package com.tipray.webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.MTOM;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.baseinfo.Vehicle;
import com.tipray.constant.CenterConfigConst;
import com.tipray.constant.RemoteControlConst;
import com.tipray.core.exception.ServiceException;
import com.tipray.core.exception.UdpException;
import com.tipray.net.UdpClient;
import com.tipray.pool.ThreadPool;
import com.tipray.service.ChangeRecordService;
import com.tipray.service.DistributionRecordService;
import com.tipray.service.VehicleService;
import com.tipray.webservice.client.ElockClient;

/**
 * ElockWebService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Component
@Transactional(rollbackForClassName = "Exception")
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
	private WebServiceContext context;
	@Resource
	private VehicleService vehicleService;
	@Resource
	private DistributionRecordService distributionRecordService;
	@Resource
	private ChangeRecordService changeRecordService;

	@WebMethod
	public String getInfo(@WebParam(name = "params") String params) {
		System.out.println(params);
		return "success";
	}

	/**
	 * 物流配送接口
	 * 
	 * @param Param
	 *            物流配送信息，xml文本字符串
	 * @return
	 * @return <code>&lt;Message&gt;success&lt;/Message&gt;</code>成功；<code>&lt;Message&gt;fail：错误描述&lt;/Message&gt;</code>错误
	 */
	@SuppressWarnings("unchecked")
	@WebMethod(operationName = "SetPlan")
	@WebResult(name = "SetPlanResult", targetNamespace = "http://www.cnpc.com/")
	public String SetPlan(@WebParam(name = "Param", targetNamespace = "http://www.cnpc.com/") String Param) {
		logger.debug("WebService收到物流配送报文：{}", Param);
		forword(Param);
		int beginIndex = Param.indexOf(HEADER);
		int endIndex = Param.indexOf(FOOTER) + FOOTER.length();
		String distributeXmlStr = Param.substring(beginIndex, endIndex);
		Document document;
		try {
			// 读取XML文本内容获取Document对象
			document = DocumentHelper.parseText(distributeXmlStr);
		} catch (DocumentException e) {
			return "<Message>fail：" + e.getMessage() + "</Message>";
		}
		Element root = document.getRootElement();
		Element messageNode = root.element("Message");
		Element dlistsNode = messageNode.element("dlists");
		List<Element> elements = dlistsNode.elements();
		for (Element element : elements) {
			Map<String, Object> distributionMap = new HashMap<String, Object>();
			Iterator<Element> it = element.elementIterator();
			while (it.hasNext()) {
				Element childNode = it.next();
				distributionMap.put(childNode.getName(), childNode.getText());
			}
			// 配送单所属车辆
			Vehicle vehicle = vehicleService.getByCarNo((String) distributionMap.get("vehicNo"));
			if (vehicle == null) {
				logger.warn("配送单车牌号无法匹配！");
				return "<Message>fail：配送单车牌号无法匹配！</Message>";
			}
			// 配送单所属车辆ID
			Long carId = vehicle.getId();
			distributionMap.put("carId", carId);
			Integer invoiceCount = distributionRecordService .countByInvoice((String) distributionMap.get("distributNO"));
			try {
				if (invoiceCount == null || invoiceCount == 0) {
					distributionRecordService.addDistributionRecord(distributionMap);
					logger.info("配送单：{}，新增成功!", distributionMap.get("distributNO"));
				} else {
					// 物流配送接口更改配送信息换站
					Map<String, Object> map = changeRecordService.distributionChange(distributionMap);
					ThreadPool.CACHED_THREAD_POOL.execute(() -> {
						// 车辆已绑定车载终端
						if (vehicle.getVehicleDevice() != null) {
							// 配送单所属车辆车载终端设备ID
							int terminalId = vehicle.getVehicleDevice().getDeviceId();
							// 换站ID
							long changeId = (long) map.get("changeId");
							// UDP协议数据体
							ByteBuffer dataBuffer = (ByteBuffer) map.get("dataBuffer");
							try {
								// UDP通信
								boolean udpResult = UdpClient.udpForChangeStation(terminalId, dataBuffer);
								// UDP通信结果处理
								if (!udpResult) {
									changeRecordService.updateChangeStatus(changeId, RemoteControlConst.REMOTE_PROGRESS_FAIL);
									logger.warn("物流换站：{}，UDP通信失败！", distributionMap.get("distributNO"));
								}
								changeRecordService.updateChangeStatus(changeId, RemoteControlConst.REMOTE_PROGRESS_DONE);
							} catch (UdpException e) {
								logger.error("物流换站UDP通信异常！\n{}", e.toString());
							} catch (ServiceException e) {
								logger.error("更新物流换站状态异常！\n{}", e.toString());
							}
						}
					});
					logger.info("配送单：{}，修改成功!", distributionMap.get("distributNO"));
				}
			} catch (ServiceException e) {
				logger.error("配送信息存储异常！\n{}", e.toString());
				return "<Message>fail：配送信息存储异常！</Message>";
			}
		}
		return REPLY_MESSAGE_SUCCESS;
	}

	/**
	 * 转发配送信息到瑞通系统WebService服务器
	 * 
	 * @param distributeXmlStr
	 *            {@link String} 配送信息XML文本
	 */
	private void forword(String distributeXmlStr) {
		if (CenterConfigConst.WEBSERVICE_FORWORD) {
			ThreadPool.CACHED_THREAD_POOL.execute(() -> {
				int repeatCount = 0;
				while (repeatCount < FORWORD_REPEAT_MAX) {
					try {
						String rtReply = ElockClient.setPlan(CenterConfigConst.WEBSERVICE_RT_URL, distributeXmlStr);
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

	@WebMethod
	@WebResult
	@XmlMimeType("*/*")
	public DataHandler downloadFile(@WebParam(name = "file") String filePath) {
		File downloadFile = new File(filePath);
		if (downloadFile.exists()) {
			return new DataHandler(new FileDataSource(downloadFile) {
				@Override
				public String getContentType() {
					return "application/octet-stream";
				}
			});
		}
		return null;
	}

	@WebMethod
	public void uploadFile(@WebParam(name = "fileName") String fileName,
			@XmlMimeType("*/*") @WebParam(name = "dataHandler") DataHandler dataHandler) throws Exception {
		OutputStream out = null;
		try {
			File file = new File("输出文件路径");
			if (!file.exists()) {
				out = new FileOutputStream(file);
				dataHandler.writeTo(out);
				out.flush();
			}
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}
