package com.tipray.net;

import com.tipray.bean.ResponseMsg;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.VehicleCompanyRelationCache;
import com.tipray.constant.AlarmBitMarkConst;
import com.tipray.constant.CenterConfigConst;
import com.tipray.constant.reply.ErrorTagConst;
import com.tipray.mq.MyQueueElement;
import com.tipray.net.constant.UdpBizId;
import com.tipray.net.constant.UdpProtocolParseResultEnum;
import com.tipray.net.constant.UdpReplyErrorTag;
import com.tipray.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    private static byte[] RC4_KEY;
    /**
     * 中心rc4秘钥版本
     */
    private static byte RC4_VER;
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
        initRc4();
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
        initRc4();
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
        initRc4();
        this.random = random;
        this.remoteAddress = remoteAddress;
        this.bizId = bizId;
        this.serialNo = serialNo;
        this.data = data;
    }

    /**
     * 初始化RC4秘钥和版本号
     */
    private static synchronized void initRc4() {
        try {
            String url = new StringBuffer(CenterConfigConst.PLTONE_URL).append("/api/getCenterRc4.do").toString();
            String param = new StringBuffer("id=").append(CenterConfigConst.CENTER_ID).append("&ver=")
                    .append(CenterConfigConst.CENTER_VER).toString();
            String msgJson = HttpRequestUtil.sendGet(url, param);
            ResponseMsg responseMsg = JSONUtil.parseToObject(msgJson, ResponseMsg.class);
            if (responseMsg.getId() > 0) {
                throw new IllegalArgumentException(responseMsg.getMsg() + param);
            }
            String rc4Hex = (String) responseMsg.getMsg();
            byte[] encryptedData = BytesUtil.hexStringToBytes(rc4Hex);
            byte[] key = RC4Util.getKeyByDeviceId(CenterConfigConst.CENTER_ID);
            byte[] decryptedData = RC4Util.rc4(encryptedData, key);
            RC4_KEY = Arrays.copyOf(decryptedData, decryptedData.length - 1);
            RC4_VER = decryptedData[decryptedData.length - 1];
        } catch (Exception e) {
            logger.error("获取RC4密钥异常：\n{}", e.toString());
            throw new IllegalArgumentException("获取RC4密钥失败");
        }
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

    public byte[] getRc4Key() {
        return RC4_KEY;
    }

    public byte getRc4Ver() {
        return RC4_VER;
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
     * @throws IllegalArgumentException if <code>RC4_KEY</code> is null.
     */
    public byte[] dealProtocolOfRequest() {
        return dealProtocolOfRequestToByteBuffer().array();
    }

    /**
     * 处理请求业务
     *
     * @return {@link ByteBuffer}
     * @throws IllegalArgumentException if <code>RC4_KEY</code> is null.
     */
    public ByteBuffer dealProtocolOfRequestToByteBuffer() {
        if (RC4_KEY == null) {
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
        byte[] encryptBizBuf = RC4Util.rc4(bizBuffer.array(), RC4_KEY);

        // 数据报打包
        // 数据包容量比业务体多2个字节
        capacity += 2;
        // 数据包
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        // 添加密钥版本
        buffer.put(RC4_VER);
        // 添加加密业务体
        buffer.put(encryptBizBuf);
        // 密钥版本号和加密数据通过 CRC校验算法得出CRC校验码
        byte crc = CRCUtil.getCRC(Arrays.copyOf(buffer.array(), capacity - 1));
        buffer.put(crc);
        logger.debug("UDP Client send: byte{}", Arrays.toString(buffer.array()));
        return buffer;
    }

    /**
     * 处理应答业务
     *
     * @param receiveBuf 应答数据字节数组
     * @return <code>true</code> 请求成功，<code>false</code> 请求失败
     */
    public boolean dealProtocolOfResponse(byte[] receiveBuf) {
        logger.debug("UDP Client receive: byte{}", Arrays.toString(receiveBuf));
        // 应答数据是否为空
        if (EmptyObjectUtil.isEmptyArray(receiveBuf)) {
            logger.error("未收到应答数据或应答数据为空！");
            return false;
        }

        // 应答数据字节数组长度
        if (receiveBuf.length < UdpProtocol.MIN_RECEIVE_BUF_LEN) {
            logger.error("应答数据字节数过少！");
            return false;
        }

        // CRC校验
        boolean valid = CRCUtil.checkCrc(receiveBuf, 0, receiveBuf.length - 1);
        if (!valid) {
            logger.error("应答数据CRC校验失败！");
            return false;
        }

        // 密钥版本检查
        byte rc4Ver = receiveBuf[0];
        if (rc4Ver != this.RC4_VER) {
            initRc4();
            if (rc4Ver != this.RC4_VER) {
                logger.error("密钥版本：{}，不符！", rc4Ver);
                return false;
            }
        }

        // RC4解密
        // 获取加密业务体
        byte[] encryptBuf = Arrays.copyOfRange(receiveBuf, 1, receiveBuf.length - 1);
        // 获取解密业务体
        byte[] decryptBuf = RC4Util.rc4(encryptBuf, RC4_KEY);

        int index = 1;
        // 协议号
        byte[] protocolByte = Arrays.copyOfRange(decryptBuf, index, index + 1);
        byte protocol = protocolByte[0];
        if (protocol != UdpProtocol.PROTOCOL_ID) {
            logger.error("应答协议号：{}，错误！", "0x" + BytesUtil.bytesToHex(protocolByte, false));
            return false;
        }
        index += 1;

        // 源地址
        byte[] localAddrDWord = Arrays.copyOfRange(decryptBuf, index, index + 4);
        int localAddress = BytesConverterByLittleEndian.getInt(localAddrDWord);
        if (localAddress != this.remoteAddress) {
            logger.error("应答源地址：{}，错误！", "0x" + BytesUtil.bytesToHex(ArraysUtil.reverse(localAddrDWord), false));
            return false;
        }
        index += 4;

        // 目的地址
        byte[] remoteAddrDWord = Arrays.copyOfRange(decryptBuf, index, index + 4);
        int remoteAddress = BytesConverterByLittleEndian.getInt(remoteAddrDWord);
        if (remoteAddress != UdpProtocol.WEB_SERVER_ID) {
            logger.error("应答目的地址：{}，错误！", "0x" + BytesUtil.bytesToHex(ArraysUtil.reverse(remoteAddrDWord), false));
            return false;
        }
        index += 4;

        // 业务ID
        byte[] replyBizIdWord = Arrays.copyOfRange(decryptBuf, index, index + 2);
        short replyBizId = BytesConverterByLittleEndian.getShort(replyBizIdWord);
        if ((replyBizId ^ this.bizId) != 128) {
            logger.error("应答业务ID：{}，错误！", "0x" + BytesUtil.bytesToHex(ArraysUtil.reverse(replyBizIdWord), false));
            return false;
        }
        index += 2;

        // 序列号
        byte[] serialNoWord = Arrays.copyOfRange(decryptBuf, index, index + 2);
        short serialNo = BytesConverterByLittleEndian.getShort(serialNoWord);
        if (serialNo != this.serialNo) {
            logger.error("应答序列号：{}，错误！", "0x" + BytesUtil.bytesToHex(ArraysUtil.reverse(serialNoWord), false));
            return false;
        }
        index += 2;

        // 应答数据体
        if (replyBizId == UdpBizId.TERMINAL_SOFTWARE_UPGRADE_RESPONSE) {
            // return;
        }
        if (decryptBuf.length - index < UdpProtocol.MIN_REPLY_DATA_LEN) {
            logger.error("应答数据体字节数过少！");
            return false;
        }
        byte[] dataErrorDWord = Arrays.copyOfRange(decryptBuf, index, index + 4);
        int result = BytesConverterByLittleEndian.getInt(dataErrorDWord);
        if (result != 0) {
            logger.error("请求错误！{}", "0x" + BytesUtil.bytesToHex(ArraysUtil.reverse(dataErrorDWord), false));
            return false;
        }
        return true;
    }

    /**
     * 处理应答业务
     *
     * @param receiveMsg {@link ResponseMsg} 应答信息
     * @return {@link ResponseMsg}
     */
    public ResponseMsg dealProtocolOfResponseToMsg(ResponseMsg receiveMsg) {
        if (receiveMsg == null) {
            logger.error("UDP回复超时！");
            return ResponseMsgUtil.error(ErrorTagConst.UDP_COMMUNICATION_ERROR_TAG, 1, "UDP回复超时！");
        }
        byte[] receiveBuf = (byte[]) receiveMsg.getMsg();
        logger.debug("UDP Client receive: byte{}, length={}", Arrays.toString(receiveBuf), receiveBuf.length);
        // 应答数据是否为空
        if (EmptyObjectUtil.isEmptyArray(receiveBuf)) {
            logger.error("未收到应答数据或应答数据为空！");
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.RECEIVE_NULL);
        }

        // 应答数据字节数组长度
        if (receiveBuf.length < UdpProtocol.MIN_RECEIVE_BUF_LEN) {
            logger.error("应答数据字节数组长度{}太短！", receiveBuf.length);
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.RECEIVE_LEN_INVALID);
        }

        // CRC校验
        boolean valid = CRCUtil.checkCrc(receiveBuf, 0, receiveBuf.length - 1);
        if (!valid) {
            logger.error("应答数据CRC校验失败！");
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.CRC_INVALID);
        }

        // 密钥版本检查
        byte rc4Ver = receiveBuf[0];
        if (rc4Ver != this.RC4_VER) {
            initRc4();
            if (rc4Ver != this.RC4_VER) {
                logger.error("密钥版本：{}，不符！", rc4Ver);
                return ResponseMsgUtil.error(UdpProtocolParseResultEnum.RC4_VER_INCONSISTENT);
            }
        }

        // RC4解密
        // 获取加密业务体
        byte[] encryptBuf = Arrays.copyOfRange(receiveBuf, 1, receiveBuf.length - 1);
        // 获取解密业务体
        byte[] decryptBuf = RC4Util.rc4(encryptBuf, RC4_KEY);

        int index = 1;
        // 协议号
        byte[] protocolByte = Arrays.copyOfRange(decryptBuf, index, index + 1);
        byte protocol = protocolByte[0];
        if (protocol != UdpProtocol.PROTOCOL_ID) {
            logger.error("应答协议号：{}，错误！", "0x" + BytesUtil.bytesToHex(protocolByte, false));
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.PROTOCOL_ID_ERROR);
        }
        index += 1;

        // 源地址（ 暂时不做验证）
        byte[] localAddrDWord = Arrays.copyOfRange(decryptBuf, index, index + 4);
        int localAddress = BytesConverterByLittleEndian.getInt(localAddrDWord);
        if (localAddress != this.remoteAddress) {
            logger.error("应答源地址：{}，错误！", "0x" + BytesUtil.bytesToHex(ArraysUtil.reverse(localAddrDWord), false));
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.SRC_TERM_ADDR_ERROR);
        }

        index += 4;

        // 目的地址（ 暂时不做验证）
        byte[] remoteAddrDWord = Arrays.copyOfRange(decryptBuf, index, index + 4);
        int remoteAddress = BytesConverterByLittleEndian.getInt(remoteAddrDWord);
        if (remoteAddress != UdpProtocol.WEB_SERVER_ID) {
            logger.error("应答目的地址：{}，错误！", "0x" + BytesUtil.bytesToHex(ArraysUtil.reverse(remoteAddrDWord), false));
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.DEST_TERM_ADDR_ERROR);
        }

        index += 4;

        // 业务ID
        byte[] replyBizIdWord = Arrays.copyOfRange(decryptBuf, index, index + 2);
        short replyBizId = BytesConverterByLittleEndian.getShort(replyBizIdWord);
        if ((replyBizId ^ this.bizId) != 128) {
            logger.error("业务{}应答业务ID：{}，错误！",
                    "0x" + BytesUtil.bytesToHex(BytesConverterByBigEndian.getBytes(bizId), false),
                    "0x" + BytesUtil.bytesToHex(ArraysUtil.reverse(replyBizIdWord), false));
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.BIZ_ID_INCONSISTENT);
        }
        index += 2;

        // 序列号
        byte[] serialNoWord = Arrays.copyOfRange(decryptBuf, index, index + 2);
        short serialNo = BytesConverterByLittleEndian.getShort(serialNoWord);
        if (serialNo != this.serialNo) {
            logger.error("应答序列号：{}，错误！", "0x" + BytesUtil.bytesToHex(ArraysUtil.reverse(serialNoWord), false));
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.SERIAL_NO_INCONSISTENT);
        }
        index += 2;

        // 应答数据体
        int dataLen = decryptBuf.length - index;
        if (replyBizId != UdpBizId.TERMINAL_SOFTWARE_UPGRADE_RESPONSE && dataLen != UdpProtocol.MIN_REPLY_DATA_LEN) {
            logger.error("业务：{}，应答数据体字节数不符！",
                    "0x" + BytesUtil.bytesToHex(BytesConverterByBigEndian.getBytes(bizId), false));
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.DATA_LEN_INVALID);
        }
        // 通用错误
        byte[] commonErrorWord = Arrays.copyOfRange(decryptBuf, index, index + 2);
        short commonError = BytesConverterByLittleEndian.getShort(commonErrorWord);
        if (commonError != UdpReplyErrorTag.NONE_ERROR) {
            logger.error("应答数据体通用错误！{}", commonError);
        }
        index += 2;
        // 业务错误
        byte[] bizErrorWord = Arrays.copyOfRange(decryptBuf, index, index + 2);
        short bizError = BytesConverterByLittleEndian.getShort(bizErrorWord);
        if (bizError != UdpReplyErrorTag.NONE_ERROR) {
            logger.error("应答数据体业务错误！{}", bizError);
        }
        if (commonError + bizError == 0) {
            return ResponseMsgUtil.success(); // 无错
        }
        String msg = new StringBuffer("{\"commonError\":").append(commonError).append(",\"workError\":")
                .append(bizError).append('}').toString();
        if (replyBizId == UdpBizId.TERMINAL_SOFTWARE_UPGRADE_RESPONSE) {
            if (dataLen < 6) {
                logger.error("业务：0x1208，应答数据体字节数不符！");
                return ResponseMsgUtil.error(UdpProtocolParseResultEnum.DATA_LEN_INVALID);
            }
            index += 2;
            // 通知失败的设备数量
            byte devNum = decryptBuf[index];
            if (dataLen != UdpProtocol.MIN_REPLY_DATA_LEN + 1 + devNum) {
                logger.error("业务：0x1208，应答数据体字节数不符！");
                return ResponseMsgUtil.error(UdpProtocolParseResultEnum.DATA_LEN_INVALID);
            }
            index += 1;
            // 通知失败的设备位序（值：0到n-1）
            byte[] devSorts = Arrays.copyOfRange(decryptBuf, index, index + devNum);
            msg = new StringBuffer("{\"commonError\":").append(commonError).append(",\"workError\":").append(bizError)
                    .append(",\"devNum\":").append(devNum).append(",\"devSorts\":").append(Arrays.toString(devSorts))
                    .append('}').toString();
        }
        int errorId = (int) commonError << 16 | bizError;
        return ResponseMsgUtil.error(ErrorTagConst.UDP_REPLY_ERROR_TAG, errorId, msg);
    }

    /**
     * 处理UDP服务器接收业务
     *
     * @param element {@link MyQueueElement}
     */
    public void dealProtocolOfReceive(MyQueueElement element) {
        InetSocketAddress clientAddr = element.getClientAddr();
        byte[] receiveBuf = element.getReceiveBuf();
        logger.debug("UDP Server receive: {} ==> byte{}", clientAddr, Arrays.toString(receiveBuf));
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
        if (rc4Ver != this.RC4_VER) {
            initRc4();
            if (rc4Ver != this.RC4_VER) {
                logger.error("密钥版本：{}，不符！", rc4Ver);
                return;
            }
        }

        // RC4解密
        // 获取加密部分数据（随机数和业务体，去除第1个字节密钥版本号和最后一个字节CRC校验码）
        byte[] encryptBuf = Arrays.copyOfRange(receiveBuf, 1, receiveBuf.length - 1);
        // 解密（随机数和业务体）
        byte[] decryptBuf = RC4Util.rc4(encryptBuf, RC4_KEY);
        // 业务体（去除第1个字节随机数）
        byte[] receiveBizBuf = Arrays.copyOfRange(decryptBuf, 1, decryptBuf.length);

        // 业务ID
        byte[] bizIdWord = Arrays.copyOfRange(receiveBizBuf, 9, 11);
        short bizId = BytesConverterByLittleEndian.getShort(bizIdWord);
        String hexBizId = BytesUtil.bytesToHex(ArraysUtil.reverse(bizIdWord), false);
        logger.debug("receive udp biz id: 0x{}", hexBizId);

        if (bizId == UdpBizId.LINK_HEARTBEAT_DETECTION) {
            // 链路维护心跳业务
            return;
        }
        if (bizId == UdpBizId.UDP_TRACK_UPLOAD) {
            // 轨迹上报业务
            // MyQueue.queueTaskForTrack(receiveBizBuf);
            return;
        }
        if (Arrays.binarySearch(UdpBizId.RESPONSE_BIZ_IDS, bizId) >= 0) {
            // 应答业务
            dealProtocolReply(receiveBizBuf, bizId);
            return;
        }
        logger.error("业务ID：0x{}，无效！", hexBizId);
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
        ResponseMsg msg;
        if (replyBizId == UdpBizId.TERMINAL_SOFTWARE_UPGRADE_RESPONSE) {
            msg = dealReplyDataForTerminalSoftwareUpdate(data);
        } else {
            msg = dealReplyDataForCommen(data);
        }
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
        if (commonError != UdpReplyErrorTag.NONE_ERROR) {
            logger.error("应答数据体通用错误！{}", commonError);
        }
        index += 2;
        // 业务错误
        byte[] bizErrorWord = Arrays.copyOfRange(data, index, index + 2);
        short bizError = BytesConverterByLittleEndian.getShort(bizErrorWord);
        if (bizError != UdpReplyErrorTag.NONE_ERROR) {
            logger.error("应答数据体业务错误！{}", bizError);
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
     * 车台软件更新业务应答数据体处理
     *
     * @param data {@link byte[]} 应答数据体
     * @return {@link ResponseMsg}
     */
    private ResponseMsg dealReplyDataForTerminalSoftwareUpdate(byte[] data) {
        if (data.length < UdpProtocol.MIN_REPLY_DATA_LEN) {
            logger.error("车台软件更新下发业务：应答数据体长度：{}，太短！", data.length);
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.DATA_LEN_INVALID);
        }
        int index = 0;
        // 通用错误
        byte[] commonErrorWord = Arrays.copyOfRange(data, index, index + 2);
        short commonError = BytesConverterByLittleEndian.getShort(commonErrorWord);
        if (commonError != UdpReplyErrorTag.NONE_ERROR) {
            logger.error("应答数据体通用错误！{}", commonError);
        }
        index += 2;
        // 业务错误
        byte[] bizErrorWord = Arrays.copyOfRange(data, index, index + 2);
        short bizError = BytesConverterByLittleEndian.getShort(bizErrorWord);
        if (bizError != UdpReplyErrorTag.NONE_ERROR) {
            logger.error("应答数据体业务错误！{}", bizError);
        }
        if (commonError + bizError == 0) {
            return ResponseMsgUtil.success(); // 无错
        }

        if (data.length < 6) {
            logger.error("车台软件更新下发业务：应答数据体通知失败后剩余字节数：{}，不够！", data.length - UdpProtocol.MIN_REPLY_DATA_LEN);
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.DATA_LEN_INVALID);
        }
        index += 2;
        // 通知失败的设备数量
        byte devNum = data[index];
        if (data.length != UdpProtocol.MIN_REPLY_DATA_LEN + 1 + 4 * devNum) {
            logger.error("车台软件更新下发业务：实际设备ID数目[n*4={}]与报文中指定的设备数量[{}]不符！", data.length - 5, devNum);
            return ResponseMsgUtil.error(UdpProtocolParseResultEnum.DATA_LEN_INVALID);
        }
        index += 1;

        StringBuffer msg = new StringBuffer("{\"commonError\":").append(commonError).append(",\"workError\":")
                .append(bizError).append(",\"devIds\":[");
        // 通知失败的设备ID
        for (int i = 0; i < devNum; i++) {
            byte[] devIdDWord = Arrays.copyOfRange(data, index, index + 4);
            int devId = BytesConverterByLittleEndian.getInt(devIdDWord);
            index += 4;
            msg.append(devId).append(',');
        }
        msg.deleteCharAt(msg.length() - 1);
        msg.append(']').append('}');
        int errorId = (int) commonError << 16 | bizError;
        return ResponseMsgUtil.error(ErrorTagConst.UDP_REPLY_ERROR_TAG, errorId, msg.toString());
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
            logger.debug("车辆【{}】已离线！", carId);
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
            logger.error("无轨迹信息");
            return;
        }
        List<String> trackList = new ArrayList<>();
        StringBuffer trackBuf = null;
        // 轨迹相关信息
        for (int i = 0; i < trackNum; i++) {
            trackBuf = new StringBuffer();
            trackBuf.append('{');
            trackBuf.append("\"biz\":\"track\",");
            trackBuf.append("\"carId\":").append(carId).append(',');
            // trackBuf.append("\"terminalId\":").append(terminalId).append(',');
            trackBuf.append("\"carNumber\":\"").append(carNumber).append('\"').append(',');
            trackBuf.append("\"carCom\":\"").append(carCom).append('\"').append(',');
            // 经纬度是否有效
            // byte isLocationValid = receiveDataBuf[index];
            // trackBuf.append("\"coorValid\":").append(isLocationValid).append(',');
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
            // 锁数量
            byte lockNum = receiveDataBuf[index];
            index++;
            // 锁报警信息
            byte[] lockAlarms = Arrays.copyOfRange(receiveDataBuf, index, index + lockNum);
            index += lockNum;
            // 是否报警
            char alarm = isAlarm(terminalAlarm, lockNum, lockAlarms);
            trackBuf.append("\"alarm\":\"").append(alarm).append('\"');
            // trackBuf.append("\"alarm\":\"").append(alarm).append('\"').append(',');
            // trackBuf.append("\"online\":1"); // online：0.离线；1.在线
            trackBuf.append('}');
            trackList.add(trackBuf.toString());
        }
        UdpReceiveResultHandler.handleTrack(carId, trackList);
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
        // |开关状态		|是否可用	|保留	|保留	|进入应急	|异常开锁	|低电压	|通讯异常	|
        // --------------------------------------------------------------------------------------
        for (int i = 0; i < lockNum; i++) {
            byte lockAlarm = lockAlarms[i];
            if ((lockAlarm & AlarmBitMarkConst.VALID_LOCK_ALARM_BITS) > 0) {
                return '是';
            }
        }
        return '否';
    }

}
