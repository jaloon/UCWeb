package com.tipray.net;

import com.tipray.bean.RC4Key;
import com.tipray.bean.ResponseMsg;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.RC4KeyCache;
import com.tipray.cache.VehicleCompanyRelationCache;
import com.tipray.cache.VehicleTrackTimeCache;
import com.tipray.constant.AlarmBitMarkConst;
import com.tipray.constant.reply.ErrorTagConst;
import com.tipray.mq.MyQueue;
import com.tipray.mq.MyQueueElement;
import com.tipray.net.constant.UdpBizId;
import com.tipray.net.constant.UdpProtocolParseResultEnum;
import com.tipray.net.constant.UdpReplyCommonErrorEnum;
import com.tipray.net.constant.UdpReplyWorkErrorEnum;
import com.tipray.util.ArraysUtil;
import com.tipray.util.BytesConverterByLittleEndian;
import com.tipray.util.BytesUtil;
import com.tipray.util.CRCUtil;
import com.tipray.util.EmptyObjectUtil;
import com.tipray.util.RC4Util;
import com.tipray.util.ResponseMsgUtil;
import com.tipray.websocket.handler.TrackCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * UDP协议
 *
 * @author chenlong
 * @version 1.0 2017-12-22
 */
public class UdpProtocol {
    private static final Logger logger = LoggerFactory.getLogger(UdpProtocol.class);
    /**
     * Web服务端ID
     */
    public static final int WEB_SERVER_ID = 0x00000002;
    /**
     * 通信服务端ID
     */
    public static final int COMM_SERVER_ID = 0x00000001;
    /**
     * 中心rc4密钥
     */
    private byte[] key;
    /**
     * 中心rc4秘钥版本
     */
    private byte keyVer;
    /**
     * 随机数
     */
    private byte random;
    /**
     * 协议号
     */
    private static final byte PROTOCOL_ID = 0x01;
    /**
     * 目的地址(通信服务端地址/车台设备ID)
     */
    private int remoteAddress;
    /**
     * 业务ID
     */
    private short bizId;
    /**
     * 序列号
     */
    private short serialNo;
    /**
     * 数据体版本号
     */
    private static final short DATA_VER = 0x0101;
    /**
     * 数据体
     */
    private ByteBuffer data;
    /**
     * 接收数据最小长度(心跳包)
     */
    public static final int MIN_RECEIVE_BUF_LEN = 16;
    /**
     * 应答数据最小长度
     */
    public static final int MIN_REPLY_BUF_LEN = 28;
    /**
     * 应答数据体最小长度
     */
    public static final int MIN_REPLY_DATA_LEN = 4;

    /**
     * UdpProtocol无参构造方法
     */
    public UdpProtocol() {
        setRc4();
        initRandom();
    }

    /**
     * UdpProtocol有参构造方法
     *
     * @param remoteAddress {@link int} 目的地址(通信服务端地址/车台设备ID)
     * @param bizId         {@link short} 业务ID
     * @param serialNo      {@link short} 序列号
     * @param data          {@link ByteBuffer} 数据体
     */
    public UdpProtocol(int remoteAddress, short bizId, short serialNo, ByteBuffer data) {
        setRc4();
        initRandom();
        this.remoteAddress = remoteAddress;
        this.bizId = bizId;
        this.serialNo = serialNo;
        this.data = data;
    }

    /**
     * UdpProtocol有参构造方法
     *
     * @param random        {@link byte} 随机数
     * @param remoteAddress {@link int} 目的地址(通信服务端地址/车台设备ID)
     * @param bizId         {@link short} 业务ID
     * @param serialNo      {@link short} 序列号
     * @param data          {@link ByteBuffer} 数据体
     */
    public UdpProtocol(byte random, int remoteAddress, short bizId, short serialNo, ByteBuffer data) {
        setRc4();
        this.random = random;
        this.remoteAddress = remoteAddress;
        this.bizId = bizId;
        this.serialNo = serialNo;
        this.data = data;
    }

    /**
     * 设置RC4秘钥和版本号
     */
    private void setRc4() {
        RC4Key rc4Key = RC4KeyCache.getRC4Key();
        key = rc4Key.getKey();
        keyVer = rc4Key.getVer();
    }

    /**
     * 初始化随机数
     */
    public void initRandom() {
        Random rand = new Random();
        byte[] random = new byte[1];
        rand.nextBytes(random);
        this.random = random[0];
    }

    public byte[] getKey() {
        return key;
    }

    public byte getKeyVer() {
        return keyVer;
    }

    public byte getRandom() {
        return random;
    }

    public void setRandom(byte random) {
        this.random = random;
    }

    public static byte getProtocolId() {
        return PROTOCOL_ID;
    }

    public int getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(int remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public short getBizId() {
        return bizId;
    }

    public void setBizId(short bizId) {
        this.bizId = bizId;
    }

    public short getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(short serialNo) {
        this.serialNo = serialNo;
    }

    public static short getDataVer() {
        return DATA_VER;
    }

    public ByteBuffer getData() {
        return data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    /**
     * 处理请求业务
     *
     * @return RC4加密并添加CRC校验的请求数据字节数组
     * @throws IllegalArgumentException if <code>key</code> is null.
     */
    public byte[] dealProtocolOfRequest() {
        return dealProtocolOfRequestToByteBuffer().array();
    }

    /**
     * 处理请求业务
     *
     * @return {@link ByteBuffer}
     * @throws IllegalArgumentException if <code>key</code> is null.
     */
    public ByteBuffer dealProtocolOfRequestToByteBuffer() {
        if (key == null) {
            throw new IllegalArgumentException("获取RC4密钥失败");
        }
        // 业务体打包
        // 获取ByteBuffer新缓冲区的容量
        int capacity = 14;
        if (bizId != UdpBizId.LINK_HEARTBEAT_DETECTION) {
            capacity += 2;
        }
        if (data != null && data.capacity() != 0) {
            capacity += data.capacity();
        }
        // 业务体buffer
        ByteBuffer bizBuffer = ByteBuffer.allocate(capacity);
        // 添加随机数
        bizBuffer.put(random);
        // 添加协议号
        bizBuffer.put(PROTOCOL_ID);
        // 添加源地址
        bizBuffer.put(BytesConverterByLittleEndian.getBytes(WEB_SERVER_ID));
        // 添加目的地址
        bizBuffer.put(BytesConverterByLittleEndian.getBytes(remoteAddress));
        // 添加业务ID
        bizBuffer.put(BytesConverterByLittleEndian.getBytes(bizId));
        // 添加序列号
        bizBuffer.put(BytesConverterByLittleEndian.getBytes(serialNo));
        if (bizId != UdpBizId.LINK_HEARTBEAT_DETECTION) {
            // 添加数据体版本号（心跳包不带数据体版本号）
            bizBuffer.put(BytesConverterByLittleEndian.getBytes(DATA_VER));
        }
        if (data != null && data.capacity() != 0) {
            // 添加数据体
            bizBuffer.put(data.array());
        }

        // 对业务体RC4加密
        byte[] encryptBizBuf = RC4Util.rc4(bizBuffer.array(), key);

        // 数据报打包
        // 数据包容量比业务体多2个字节
        capacity += 2;
        // 数据包
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        // 添加密钥版本
        buffer.put(keyVer);
        // 添加加密业务体
        buffer.put(encryptBizBuf);
        // 密钥版本号和加密数据通过 CRC校验算法得出CRC校验码
        byte crc = CRCUtil.getCRC(Arrays.copyOf(buffer.array(), capacity - 1));
        buffer.put(crc);
        if (logger.isDebugEnabled()) {
            logger.debug("UDP Client send: byte{}", Arrays.toString(buffer.array()));
        }
        return buffer;
    }

    /**
     * 处理UDP服务器接收业务
     *
     * @param element {@link MyQueueElement}
     */
    public void dealProtocolOfReceive(MyQueueElement element) {
        InetSocketAddress clientAddr = element.getClientAddr();
        byte[] receiveBuf = element.getReceiveBuf();
        if (logger.isDebugEnabled()) {
            logger.debug("UDP Server receive: {} ==> byte{}", clientAddr, Arrays.toString(receiveBuf));
        }
        // 接收数据是否为空
        if (EmptyObjectUtil.isEmptyArray(receiveBuf)) {
            logger.error("接收数据为空！");
            return;
        }

        // 接收数据太短
        if (receiveBuf.length < UdpProtocol.MIN_RECEIVE_BUF_LEN) {
            logger.error("数据长度：{}，长度不够！", receiveBuf.length);
            return;
        }

        // NioUdpServer udpServer = (NioUdpServer) SpringBeanUtil.getBean("udpServer");

        // CRC校验
        boolean valid = CRCUtil.checkCrc(receiveBuf, 0, receiveBuf.length - 1);
        if (!valid) {
            logger.error("接收数据CRC校验失败！");
            return;
        }

        // 密钥版本检查
        byte rc4Ver = receiveBuf[0];
        if (rc4Ver != this.keyVer) {
            logger.error("密钥版本：{}，不符！", rc4Ver);
            return;
        }

        // RC4解密
        // 获取加密部分数据（随机数和业务体，去除第1个字节密钥版本号和最后一个字节CRC校验码）
        byte[] encryptBuf = Arrays.copyOfRange(receiveBuf, 1, receiveBuf.length - 1);
        // 解密（随机数和业务体）
        byte[] decryptBuf = RC4Util.rc4(encryptBuf, key);
        // 业务体（去除第1个字节随机数）
        byte[] receiveBizBuf = Arrays.copyOfRange(decryptBuf, 1, decryptBuf.length);

        // 业务ID
        byte[] bizIdWord = Arrays.copyOfRange(receiveBizBuf, 9, 11);
        short bizId = BytesConverterByLittleEndian.getShort(bizIdWord);
        if (logger.isDebugEnabled()) {
            logger.debug("receive udp biz id: ", bizId);
        }

        if (bizId == UdpBizId.LINK_HEARTBEAT_DETECTION) {
            // 链路维护心跳业务
            return;
        }
        if (bizId == UdpBizId.UDP_TRACK_UPLOAD) {
            // 轨迹上报业务
            MyQueue.queueTaskForTrack(receiveBizBuf);
            return;
        }
        if (Arrays.binarySearch(UdpBizId.RESPONSE_BIZ_IDS, bizId) >= 0) {
            // 应答业务
            dealProtocolReply(receiveBizBuf, bizId);
            return;
        }
        logger.error("业务ID：0x{}，无效！", BytesUtil.bytesToHex(ArraysUtil.reverse(bizIdWord), false));
    }


    /**
     * 处理UDP应答业务
     *
     * @param replyBizBuf {@link byte[]} 应答业务体
     * @param replyBizId  {@link short} 应答业务ID
     */
    private void dealProtocolReply(byte[] replyBizBuf, short replyBizId) {
        int index = 0;
        // 协议号
        byte protocolId = replyBizBuf[index];
        if (protocolId != UdpProtocol.PROTOCOL_ID) {
            logger.error("协议号：{}，错误！", "0x" + BytesUtil.byteToHex(protocolId, false));
            return;
        }
        // index += 1;
        //
        // // 源地址(不做验证)
        // byte[] localAddrDWord = Arrays.copyOfRange(replyBizBuf, index, index + 4);
        // int localAddr = BytesConverterByLittleEndian.getInt(localAddrDWord);
        // index += 4;

        index += 5;
        // 目的地址
        byte[] remoteAddrDWord = Arrays.copyOfRange(replyBizBuf, index, index + 4);
        int remoteAddress = BytesConverterByLittleEndian.getInt(remoteAddrDWord);
        if (remoteAddress != UdpProtocol.WEB_SERVER_ID) {
            logger.error("目的地址：0x{}，错误！", BytesUtil.bytesToHex(ArraysUtil.reverse(remoteAddrDWord), false));
            return;
        }
        // index += 4;
        //
        // // 业务ID
        // index += 2;

        index += 6;

        // 序列号
        byte[] serialNoWord = Arrays.copyOfRange(replyBizBuf, index, index + 2);
        short serialNo = BytesConverterByLittleEndian.getShort(serialNoWord);
        index += 2;

        // 应答数据体
        byte[] data = Arrays.copyOfRange(replyBizBuf, index, replyBizBuf.length);
        ResponseMsg msg = dealReplyDataForCommen(data);
        int cacheId = AsynUdpCommCache.buildCacheId(replyBizId ^ 128, serialNo);
        if (replyBizId == UdpBizId.TERMINAL_COMMEN_CONFIG_UPDATE_RESPONSE
                || replyBizId == UdpBizId.TRANSPORT_CARD_UPDATE_RESPONSE) {
            AsynUdpCommCache.putResultCache(cacheId, msg);
        } else {
            // 处理应答结果
            UdpReceiveResultHandler.handleReplyForRemoteControl(replyBizId, cacheId, msg);
        }
    }

    /**
     * 通用应答数据体处理
     *
     * @param data {@link byte[]} 应答数据体
     * @return {@link ResponseMsg}
     */
    private ResponseMsg dealReplyDataForCommen(byte[] data) {
        if (data.length != UdpProtocol.MIN_REPLY_DATA_LEN) {
            logger.error("应答数据体字节数【{}】不符！", data.length);
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.DATA_LEN_INVALID);
        }
        int index = 0;
        // 通用错误
        byte[] commonErrorWord = Arrays.copyOfRange(data, index, index + 2);
        short commonError = BytesConverterByLittleEndian.getShort(commonErrorWord);
        if (commonError != 0) {
            logger.error("应答数据体通用错误：[{}]{}", commonError, UdpReplyCommonErrorEnum.codeToMsg(commonError));
        }
        index += 2;
        // 业务错误
        byte[] bizErrorWord = Arrays.copyOfRange(data, index, index + 2);
        short bizError = BytesConverterByLittleEndian.getShort(bizErrorWord);
        if (bizError != 0) {
            logger.error("应答数据体业务错误：[{}]{}", bizError, UdpReplyWorkErrorEnum.codeToMsg(bizError));
        }
        if (commonError + bizError != 0) {
            int errorId = (int) commonError << 16 | bizError;
            // String msg = "{\"commonError\":" + commonError + ",\"workError\":" + bizError + "}";
            Map<String, Short> map = new HashMap<>();
            map.put("commonError", commonError);
            map.put("workError", bizError);
            return ResponseMsgUtil.error(ErrorTagConst.UDP_REPLY_ERROR_TAG, errorId, map);
        }
        return ResponseMsgUtil.success();
    }

    /**
     * 处理UDP轨迹上报业务
     *
     * @param receiveDataBuf {@link byte[]} 接收到的轨迹数据
     */
    public void dealProtocolForTrackUpload(byte[] receiveDataBuf) {
        int index = 0;
        // 协议号
        byte protocolId = receiveDataBuf[index];
        if (protocolId != UdpProtocol.PROTOCOL_ID) {
            logger.error("协议号：0x{}，错误！", BytesUtil.byteToHex(protocolId, false));
            return;
        }
        index += 1;

        // 源地址（车台设备ID）
        byte[] localAddrDWord = Arrays.copyOfRange(receiveDataBuf, index, index + 4);
        int terminalId = BytesConverterByLittleEndian.getInt(localAddrDWord);
        index += 4;

        parseTrackBuf(index, terminalId, receiveDataBuf);

    }

    /**
     * 处理UDP轨迹上报业务
     *
     * @param element {@link Map.Entry} 轨迹队列元素
     */
    public void dealProtocolForTrackUpload(Map.Entry<Integer, byte[]> element) {
        byte[] receiveDataBuf = element.getValue();
        int index = 0;
        // 协议号
        byte protocolId = receiveDataBuf[index];
        if (protocolId != UdpProtocol.PROTOCOL_ID) {
            logger.error("协议号：{}，错误！", "0x" + BytesUtil.byteToHex(protocolId, false));
            return;
        }
        index += 1;

        // 源地址（车台设备ID）
        int terminalId = element.getKey();
        index += 4;

        parseTrackBuf(index, terminalId, receiveDataBuf);

    }

    /**
     * 解析轨迹数据
     *
     * @param index          {@link int} 轨迹数据索引
     * @param terminalId     {@link int} 车台设备ID
     * @param receiveDataBuf {@link byte[]} 接收到的轨迹数据
     */
    private void parseTrackBuf(int index, int terminalId, byte[] receiveDataBuf) {
        // 目的地址
        byte[] remoteAddrDWord = Arrays.copyOfRange(receiveDataBuf, index, index + 4);
        int remoteAddress = BytesConverterByLittleEndian.getInt(remoteAddrDWord);
        if (remoteAddress != UdpProtocol.WEB_SERVER_ID) {
            logger.error("目的地址：0x{}，错误！", BytesUtil.bytesToHex(ArraysUtil.reverse(remoteAddrDWord), false));
            return;
        }
        // index += 4;

        // // 业务ID（不必再次验证）
        // byte[] bizIdWord = Arrays.copyOfRange(receiveDataBuf, index, index + 2);
        // short bizId = BytesConverterByLittleEndian.getShort(bizIdWord);
        // if (bizId != UdpBizId.UDP_TRACK_UPLOAD) {
        //     logger.error("应答业务ID：0x{}，无效！",  BytesUtil.bytesToHex(ArraysUtil.reverse(bizIdWord), false));
        //     return;
        // }
        // index += 2;

        // // 序列号（暂时不做验证）
        // byte[] serialNoWord = Arrays.copyOfRange(receiveDataBuf, index, index + 2);
        // short serialNo = BytesConverterByLittleEndian.getShort(serialNoWord);
        // index += 2;

        index += 8;

        // 数据体版本号
        byte[] dataVerWord = Arrays.copyOfRange(receiveDataBuf, index, index + 2);
        short dataVer = BytesConverterByLittleEndian.getShort(dataVerWord);
        if (dataVer != UdpProtocol.DATA_VER) {
            logger.error("数据体版本号：0x{}，错误！", BytesUtil.bytesToHex(ArraysUtil.reverse(dataVerWord), false));
            return;
        }
        index += 2;

        // 应答数据体
        // byte[] dataResult = Arrays.copyOfRange(decryptBuf, index, decryptBuf.length);
        // 车辆ID
        byte[] carIdDWord = Arrays.copyOfRange(receiveDataBuf, index, index + 4);
        int intCarId = BytesConverterByLittleEndian.getInt(carIdDWord);
        index += 4;
        long carId = intCarId;
        if (!UdpReceiveResultHandler.checkCarOnline(carId)) {
            logger.warn("车辆【{}】已离线！", carId);
            return;
        }
        // 运输公司
        String carCom = VehicleCompanyRelationCache.getComOfCar(carId);
        // 车牌号长度
        byte carNumberLen = receiveDataBuf[index];
        index++;
        // 车牌号，UTF-8编码
        byte[] carNumberBytes = Arrays.copyOfRange(receiveDataBuf, index, index + carNumberLen);
        String carNumber = new String(carNumberBytes, StandardCharsets.UTF_8);
        index += carNumberLen;
        // 轨迹信息条数
        byte trackNum = receiveDataBuf[index];
        index++;
        if (trackNum < 1) {
            logger.warn("无轨迹信息");
            return;
        }
        List<String> trackList = new ArrayList<>();
        StringBuffer trackBuf = null;
        // 轨迹相关信息
        for (int i = 0; i < trackNum; i++) {
            byte[] positionTimeDWord = Arrays.copyOfRange(receiveDataBuf, index + 13, index + 17);
            int positonTimeSecond = BytesConverterByLittleEndian.getInt(positionTimeDWord);
            boolean isUp = VehicleTrackTimeCache.isUpdate(intCarId, positonTimeSecond);
            if (!isUp) {
                // 轨迹时间太旧，抛掉
                continue;
            }
            Long dbTrackMillis = TrackCache.INSTANCE.getTrackTimeMillis(carId);
            if (dbTrackMillis != null && dbTrackMillis > positonTimeSecond * 1000L) {
                // 数据库轨迹时间较新，使用数据库轨迹缓存
                trackList.add(TrackCache.INSTANCE.cacheTrack(carId));
                VehicleTrackTimeCache.updateTime(intCarId, (int) (dbTrackMillis / 1000));
                continue;
            }
            trackBuf = new StringBuffer();
            trackBuf.append('{');
            trackBuf.append("\"biz\":\"track\",");
            trackBuf.append("\"carId\":").append(carId).append(',');
            // trackBuf.append("\"terminalId\":").append(terminalId).append(',');
            trackBuf.append("\"carNumber\":\"").append(carNumber).append('\"').append(',');
            trackBuf.append("\"carCom\":\"").append(carCom).append('\"').append(',');
            // 经纬度是否有效
            byte isLocationValid = receiveDataBuf[index];
            trackBuf.append("\"gpsValid\":").append(isLocationValid).append(',');
            index++;
            // 经度
            byte[] longitudeDword = Arrays.copyOfRange(receiveDataBuf, index, index + 4);
            float longitude = BytesConverterByLittleEndian.getFloat(longitudeDword);
            trackBuf.append("\"longitude\":").append(longitude).append(',');
            index += 4;
            // 纬度
            byte[] latitudeDword = Arrays.copyOfRange(receiveDataBuf, index, index + 4);
            float latitude = BytesConverterByLittleEndian.getFloat(latitudeDword);
            trackBuf.append("\"latitude\":").append(latitude).append(',');
            index += 4;
            // 角度
            byte[] angleWord = Arrays.copyOfRange(receiveDataBuf, index, index + 2);
            short angle = BytesConverterByLittleEndian.getShort(angleWord);
            trackBuf.append("\"angle\":").append(angle).append(',');
            index += 2;
            // 速度
            byte speed = receiveDataBuf[index];
            trackBuf.append("\"velocity\":").append(speed).append(',');
            // index++;
            // 定位时间
            // byte[] positionTimeDWord = Arrays.copyOfRange(receiveDataBuf, index, index + 4);
            // int positonTimeSecond = BytesConverterByLittleEndian.getInt(positionTimeDWord);
            // Date date = DateUtil.getDateByIntTime(positonTimeSecond);
            // String positonTime = DateUtil.formatDate(date, DateUtil.FORMAT_DATETIME);
            // trackBuf.append("\"trackTime\":\"").append(positonTime).append('\"').append(',');
            // index += 4;
            index += 5;
            // 车辆状态[ 1在途2返程3在油库4在加油站5应急]
            byte carStatus = receiveDataBuf[index];
            trackBuf.append("\"carStatus\":\"").append(getCarStatus(carStatus)).append('\"').append(',');
            index++;
            // 车台报警信息
            byte terminalAlarm = receiveDataBuf[index];
            index++;
            // 是否报警
            char alarm = isAlarm(terminalAlarm);
            // 锁数量
            byte lockNum = receiveDataBuf[index];
            index++;
            if (lockNum > 0) {
                // 锁报警信息
                byte[] lockAlarms = Arrays.copyOfRange(receiveDataBuf, index, index + lockNum);
                index += lockNum;
                alarm = isAlarm(terminalAlarm, lockNum, lockAlarms);
            }
            trackBuf.append("\"alarm\":\"").append(alarm).append('\"');
            trackBuf.append('}');
            trackList.add(trackBuf.toString());
        }
        if (trackList.size() > 0) {
            UdpReceiveResultHandler.handleTrack(carId, trackList);
        }
    }

    /**
     * 车辆状态
     *
     * @param carStatus 状态值（0：未知 | 1：在油库 | 2：在途中 | 3：在加油站 | 4：返程中 | 5：应急 | 6: 待入油区 | 7：在油区)
     * @return 车辆状态名称
     */
    private String getCarStatus(byte carStatus) {
        switch (carStatus) {
            case 0:
                return "未知";
            case 1:
                return "在油库";
            case 2:
                return "在途中";
            case 3:
                return "在加油站";
            case 4:
                return "返程中";
            case 5:
                return "应急";
            case 6:
                return "待入油区";
            case 7:
                return "在油区";
            default:
                break;
        }
        return "未知";
    }

    /**
     * 是否报警
     *
     * @param terminalAlarm 车台报警信息
     * @return 是/否
     */
    private char isAlarm(byte terminalAlarm) {
        if ((terminalAlarm & AlarmBitMarkConst.VALID_TERMINAL_ALARM_BITS) > 0) {
            return '是';
        }
        return '否';
    }

    /**
     * 是否报警
     *
     * @param terminalAlarm 车台报警信息
     * @param lockNum       锁数量
     * @param lockAlarms    锁报警信息数组
     * @return 是/否
     */
    private char isAlarm(byte terminalAlarm, byte lockNum, byte[] lockAlarms) {
        // 车台报警信息位说明：以下位序从低位开始
        // ------------------------------------------------------------------------------
        // |8		|7 		|6 		|5 		|4 		|3 		|2 				|1 			|
        // ------------------------------------------------------------------------------
        // |保留		|保留 	|保留 	|保留 	|保留 	|保留 	|时钟电池报警 	|未施封越界 	|
        // ------------------------------------------------------------------------------

        if ((terminalAlarm & AlarmBitMarkConst.VALID_TERMINAL_ALARM_BITS) > 0) {
            return '是';
        }
        // 锁报警信息位说明：以下位序从低位开始
        // --------------------------------------------------------------------------------------
        // |8 			|7			|6		|5		|4			|3 			|2 		|1			|
        // --------------------------------------------------------------------------------------
        // |开关状态		|是否可用	|保留	|异常移动|进入应急	|异常开锁	|低电压	|通讯异常	|
        // --------------------------------------------------------------------------------------
        for (int i = 0; i < lockNum; i++) {
            byte lockAlarm = lockAlarms[i];
            if ((lockAlarm & AlarmBitMarkConst.LOCK_ALARM_BIT_7_ENABLE) == 0) {
                // 无效锁提前排除
                continue;
            }
            if ((lockAlarm & AlarmBitMarkConst.VALID_LOCK_ALARM_BITS) > 0) {
                return '是';
            }
        }
        return '否';
    }
}