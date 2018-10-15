package com.tipray.webservice;

import com.tipray.bean.baseinfo.Device;
import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.constant.CenterConst;
import com.tipray.core.exception.ServiceException;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.net.constant.UdpBizId;
import com.tipray.pool.ThreadPool;
import com.tipray.service.ChangeRecordService;
import com.tipray.service.DistributionRecordService;
import com.tipray.service.VehicleService;
import com.tipray.webservice.client.ElockClient;
import com.tipray.webservice.constant.DistXmlNodeNameConst;
import com.tipray.webservice.job.DistUdpTask;
import com.tipray.webservice.job.DistUdpTaskCache;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.soap.MTOM;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ElockWebService
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
@Component
@WebService(name = "ElockSoap", serviceName = "Elock", portName = "ElockSoap", targetNamespace = "http://www.cnpc.com/")
@MTOM // 开启MTOM功能
public class ElockWebService {
    private static final Logger logger = LoggerFactory.getLogger(ElockWebService.class);
    private static final int FORWORD_REPEAT_MAX = 5;
    private static final long FORWORD_TIMEOUT = 60000L;
    private static final String HEADER = "<TradeData>";
    private static final String FOOTER = "</TradeData>";
    private static final String REPLY_MESSAGE_SUCCESS = "<Message>success</Message>";
    @Resource
    private VehicleService vehicleService;
    @Resource
    private DistributionRecordService distributionRecordService;
    @Resource
    private ChangeRecordService changeRecordService;
    @Resource
    private NioUdpServer udpServer;

    /**
     * 物流配送接口
     *
     * @param txt 物流配送信息，xml文本字符串
     * @return <code>&lt;Message&gt;success&lt;/Message&gt;</code>成功；<code>&lt;Message&gt;fail：错误描述&lt;/Message&gt;</code>错误
     */
    @WebMethod(operationName = "SetPlan")
    @WebResult(name = "SetPlanResult", targetNamespace = "http://www.cnpc.com/")
    public String setPlan(@WebParam(name = "txt", targetNamespace = "http://www.cnpc.com/") String txt) {
        logger.info("WebService收到物流配送报文：{}", txt);
        if (txt == null) {
            logger.warn("XML报文为null！");
            return "<Message>fail：XML报文为null！</Message>";
        }
        if (txt.trim().isEmpty()) {
            logger.warn("XML报文为空字符串！");
            return "<Message>fail：XML报文为空字符串！</Message>";
        }
        forword(txt);
        int beginIndex = txt.indexOf(HEADER);
        int footerIndex = txt.indexOf(FOOTER);
        int endIndex = footerIndex + FOOTER.length();
        if (beginIndex < 0 || beginIndex >= footerIndex) {
            logger.warn("XML未包含规范格式的配送信息！");
            return "<Message>fail：XML未包含规范格式的配送信息！</Message>";
        }
        String distributeXmlStr = txt.substring(beginIndex, endIndex);
        Document document;
        try {
            // 读取XML文本内容获取Document对象
            document = DocumentHelper.parseText(distributeXmlStr);
        } catch (DocumentException e) {
            return "<Message>fail：" + e.getMessage() + "</Message>";
        }
        Element root = document.getRootElement();
        Element messageNode = root.element(DistXmlNodeNameConst.NODE_1_2_MESSAGE);
        if (messageNode == null) {
            logger.warn("配送信息XML文本未包含<Message>标签！");
            return "<Message>fail：配送信息XML文本未包含“<Message>”标签！</Message>";
        }
        Element dlistsNode = messageNode.element(DistXmlNodeNameConst.NODE_2_DLISTS);
        if (dlistsNode == null) {
            logger.warn("配送信息XML文本未包含<dlists>标签！");
            return "<Message>fail：配送信息XML文本未包含“<dlists>”标签！</Message>";
        }
        List<Element> elements = dlistsNode.elements();
        if (elements.size() == 0) {
            logger.warn("配送信息XML文本未包含<dlists>标签！");
            return "<Message>fail：具体配送信息为空！</Message>";
        }
        for (Element element : elements) {
            String dlistNodeName = element.getName();
            if (!DistXmlNodeNameConst.NODE_3_DLIST.equals(dlistNodeName)) {
                logger.warn("配送信息XML文本<dlist>节点名称不匹配！{}", dlistNodeName);
                return "<Message>fail：配送信息XML文本<dlist>节点名称不匹配！</Message>";
            }
            for (String node : DistXmlNodeNameConst.DLIST_CHILD_NODES) {
                if (element.element(node) == null) {
                    logger.warn("配送信息XML节点不全，缺少<{}>标签！", node);
                    return "<Message>fail：配送信息XML节点不全，缺少“<" + node + ">”标签！</Message>";
                }
            }
            for (String node : DistXmlNodeNameConst.USEFUL_DIST_NODES) {
                if (element.element(node).getText().trim().isEmpty()) {
                    logger.warn("配送信息XML节点数据缺失，<{}>标签无数据！", node);
                    return "<Message>fail：配送信息XML节点数据缺失，“<" + node + ">”标签无数据！</Message>";
                }
            }
            Map<String, Object> distributionMap = new HashMap<>();
            Iterator<Element> it = element.elementIterator();
            while (it.hasNext()) {
                Element childNode = it.next();
                distributionMap.put(childNode.getName(), childNode.getText());
            }
            // 配送单所属车辆
            String carNumber = (String) distributionMap.get("vehicNo");
            Vehicle vehicle = vehicleService.getByCarNo(carNumber);
            if (vehicle == null) {
                logger.warn("配送单车牌号无法匹配！<vehicNo>{}</vehicNo>", carNumber);
                continue;
            }
            Device terminal = vehicle.getVehicleDevice();
            if (terminal == null) {
                logger.warn("配送单所属车辆未绑定车台！<vehicNo>{}</vehicNo>", carNumber);
                continue;
            }
            Integer terminalId = terminal.getDeviceId();
            if (terminalId == null || terminalId == 0) {
                logger.warn("配送单所属车辆未绑定车台！<vehicNo>{}</vehicNo>", carNumber);
                continue;
            }
            // 配送单所属车辆ID
            Long carId = vehicle.getId();
            distributionMap.put("carId", carId);
            distributionMap.put("terminalId", terminalId);
            try {
                long taskKey = DistUdpTaskCache.buildTaskKey(carId,
                        Integer.parseInt((String) distributionMap.get("binNum"), 10));
                DistUdpTaskCache.interrupt(taskKey);

                String invoice = (String) distributionMap.get(DistXmlNodeNameConst.NODE_4_01_DISTRIBUT_NO);
                // 配送单号代表的配送记录数目
                Integer invoiceCount = distributionRecordService.countInvoice(invoice);
                if (invoiceCount == null || invoiceCount == 0) {
                    logger.info("配送单号[{}]代表的配送记录不存在，配送新增！", invoice);
                    addDist(distributionMap, terminalId, invoice, taskKey);
                    continue;
                }
                // 配送单号代表的处于配送中状态的配送记录数目
                invoiceCount = distributionRecordService.countWaitInvoice(invoice);
                if (invoiceCount == null || invoiceCount == 0) {
                    logger.info("配送单号[{}]代表的配送记录存在，但无配送中的记录，配送新增！", invoice);
                    addDist(distributionMap, terminalId, invoice, taskKey);
                    continue;
                }

                // 相同配送信息的未完成配送记录数目
                invoiceCount = distributionRecordService.countSameWaitDistInfo(distributionMap);
                if (invoiceCount != null && invoiceCount > 0) {
                    logger.info("已存在与配送单号[{}]相同配送信息的未完成配送记录，不处理！", invoice);
                    continue;
                }

                // 物流配送接口更改配送信息换站
                Map<String, Object> map = changeRecordService.distributionChange(distributionMap);
                logger.info("配送单【{}】换站，数据录入成功！", invoice);
                ThreadPool.CACHED_THREAD_POOL.execute(() -> {
                    logger.info("配送单【{}】换站下发开始...", invoice);
                    // 换站ID
                    long changeId = (long) map.get("changeId");
                    // 原配送ID
                    long transportId = (long) map.get("transportId");
                    // 换站后的配送ID
                    long changedTransportId = (long) map.get("changedTransportId");
                    // UDP协议数据体
                    ByteBuffer dataBuffer = (ByteBuffer) map.get("dataBuffer");

                    Map<String, Object> params = new HashMap<>();
                    params.put("type", 1);
                    params.put("changeId", changeId);
                    params.put("transportId", transportId);
                    params.put("changedTransportId", changedTransportId);
                    short bizId = UdpBizId.REMOTE_CHANGE_STATION_REQUEST;
                    short serialNo = (short) (SerialNumberCache.getSerialNumber(bizId) + 1);
                    int cacheId = AsynUdpCommCache.buildCacheId(bizId, serialNo);
                    AsynUdpCommCache.putParamCache(cacheId, params);

                    ByteBuffer src = SendPacketBuilder.buildProtocol0x1401(terminalId, dataBuffer);
                    boolean isSend = udpServer.send(src);
                    if (!isSend) {
                        AsynUdpCommCache.removeParamCache(cacheId);
                        logger.warn("配送单【{}】换站下发失败：UDP发送数据异常！", invoice);
                    } else {
                        DistUdpTask task = DistUdpTask.buildAlterDistTask(invoice, taskKey, cacheId, src, params);
                        DistUdpTaskCache.add(taskKey, task);
                        // new TimeOutTask(src, cacheId).executeRemoteControlTask();
                    }
                });
            } catch (Exception e) {
                logger.error("配送信息存储异常！", e);
                return "<Message>fail：配送信息存储异常！</Message>";
            }
        }
        return REPLY_MESSAGE_SUCCESS;
    }

    /**
     * 新增配送单
     *
     * @param distributionMap 配送信息
     * @param terminalId      车台设备ID
     * @param invoice         配送单号
     * @param taskKey         任务key
     * @throws ServiceException
     */
    private void addDist(Map<String, Object> distributionMap, int terminalId, String invoice, long taskKey)
            throws ServiceException {
        Long transportId = distributionRecordService.addDistributionRecord(distributionMap);
        logger.info("配送单【{}】新增，数据录入成功！", invoice);
        ThreadPool.CACHED_THREAD_POOL.execute(() -> {
            logger.info("配送单【{}】新增下发开始...", invoice);
            ByteBuffer dataBuffer = distributionRecordService.buildNewDistDataBuffer(
                    (String) distributionMap.get(DistXmlNodeNameConst.NODE_4_06_DEPT_ID),
                    transportId.intValue(),
                    Byte.parseByte((String) distributionMap.get(DistXmlNodeNameConst.NODE_4_05_BIN_NUM), 10));
            Map<String, Object> params = new HashMap<>();
            params.put("type", 2);
            params.put("transportId", transportId);
            short bizId = UdpBizId.REMOTE_CHANGE_STATION_REQUEST;
            short serialNo = (short) (SerialNumberCache.getSerialNumber(bizId) + 1);
            int cacheId = AsynUdpCommCache.buildCacheId(bizId, serialNo);
            AsynUdpCommCache.putParamCache(cacheId, params);

            ByteBuffer src = SendPacketBuilder.buildProtocol0x1401(terminalId, dataBuffer);
            boolean isSend = udpServer.send(src);
            if (!isSend) {
                logger.warn("配送单【{}】新增下发失败：UDP发送数据异常！", invoice);
            } else {
                DistUdpTask task = DistUdpTask.buildNewDistTask(invoice, taskKey, cacheId, src, params);
                DistUdpTaskCache.add(taskKey, task);
            }
        });
    }

    /**
     * 转发配送信息到瑞通系统WebService服务器
     *
     * @param distributeXmlStr {@link String} 配送信息XML文本
     */
    private void forword(String distributeXmlStr) {
        if (CenterConst.WEBSERVICE_FORWORD) {
            ThreadPool.CACHED_THREAD_POOL.execute(() -> {
                int repeatCount = 0;
                while (repeatCount < FORWORD_REPEAT_MAX) {
                    try {
                        String rtReply = ElockClient.setPlan(CenterConst.WEBSERVICE_RT_URL, distributeXmlStr);
                        if (rtReply.equals(REPLY_MESSAGE_SUCCESS)) {
                            return;
                        }
                        logger.error("第{}次转发配送信息回复失败：{}", ++repeatCount, rtReply);
                        Thread.sleep(FORWORD_TIMEOUT);
                    } catch (Exception e) {
                        logger.error("第{}次转发配送信息异常：{}", ++repeatCount, e.getMessage());
                    }
                }
                logger.error("转发配送信息失败！");
            });
        }
    }
}
