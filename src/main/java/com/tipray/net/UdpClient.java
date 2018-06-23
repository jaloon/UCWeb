package com.tipray.net;

import com.tipray.bean.ResponseMsg;
import com.tipray.bean.upgrade.TerminalUpgradeFile;
import com.tipray.bean.baseinfo.Lock;
import com.tipray.cache.SerialNumberCache;
import com.tipray.constant.CenterConfigConst;
import com.tipray.core.exception.UdpException;
import com.tipray.net.constant.UdpBizId;
import com.tipray.util.BytesConverterByLittleEndian;
import com.tipray.util.ResponseMsgUtil;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * UDP客户端
 * 
 * @author chenlong
 * @version 1.0 2018-01-10
 *
 */
public class UdpClient {
	/** 最大重发次数 */
	private static final int REPEAT_MAX = 3;
	/** 接收数据超时15秒 */
	private static final int TIMEOUT = 15000;
	/** 默认通信服务端UDP服务器地址 */
	private static final InetAddress DEFAULT_UDP_SERVER_ADDRESS = CenterConfigConst.UDP_REMOTE_ADDR;
	/** 默认通信服务端UDP服务器端口 */
	private static final int DEFAULT_UDP_SERVER_PORT = CenterConfigConst.UDP_REMOTE_PORT;

	/** UDP客户端socket */
	private DatagramSocket clientSocket;
	/** 发送数据字节数组 */
	private byte[] sendBuf;
	/** 默认接收数据字节数组 */
	private static final byte[] DEFAULT_RECEIVE_BUF = new byte[20];
	/** 发送数据报包 */
	private DatagramPacket sendPacket;
	/** 接收数据报包 */
	private DatagramPacket receivePacket;

	public UdpClient() throws UdpException {
		try {
			this.clientSocket = new DatagramSocket();
			this.clientSocket.setSoTimeout(TIMEOUT);
		} catch (SocketException e) {
			throw new UdpException("创建UDP客户端异常！", e);
		}
	}

	public UdpClient(byte[] sendBuf) throws UdpException {
		try {
			this.clientSocket = new DatagramSocket();
			this.clientSocket.setSoTimeout(TIMEOUT);
			this.sendBuf = sendBuf;
		} catch (SocketException e) {
			throw new UdpException("创建UDP客户端异常！", e);
		}
	}

	public DatagramSocket getDatagramSocket() {
		return clientSocket;
	}

	public void setDatagramSocket(DatagramSocket datagramSocket) {
		this.clientSocket = datagramSocket;
	}

	public byte[] getSendBuf() {
		return sendBuf;
	}

	public void setSendBuf(byte[] sendBuf) {
		this.sendBuf = sendBuf;
	}

	public static InetAddress getDefaultUdpServerAddress() {
		return DEFAULT_UDP_SERVER_ADDRESS;
	}

	public static int getDefaultUdpServerPort() {
		return DEFAULT_UDP_SERVER_PORT;
	}

	public static byte[] getDefaultreceivebuf() {
		return DEFAULT_RECEIVE_BUF;
	}

	/**
	 * 向默认通信服务端UDP服务器发送数据
	 * 
	 * @throws UdpException
	 */
	public void send() throws UdpException {
		send(sendBuf);
	}

	/**
	 * 向默认通信服务端UDP服务器发送数据
	 * 
	 * @param sendBuf
	 *            发送数据字节数组
	 * @throws UdpException
	 */
	public void send(byte[] sendBuf) throws UdpException {
		sendPacket = new DatagramPacket(sendBuf, sendBuf.length, DEFAULT_UDP_SERVER_ADDRESS, DEFAULT_UDP_SERVER_PORT);
		this.send(sendPacket);
	}

	/**
	 * 向UDP服务器发送数据
	 * 
	 * @param sendPacket
	 *            {@link DatagramPacket} 发送数据报包
	 * @throws UdpException
	 */
	public void send(DatagramPacket sendPacket) throws UdpException {
		try {
			this.clientSocket.send(sendPacket);
		} catch (IOException e) {
			throw new UdpException("向UDP服务器发送数据异常！", e);
		}
	}

	/**
	 * 向指定UDP服务器发送数据
	 * 
	 * @param sendBuf
	 *            发送数据字节数组
	 * @param destinationAddress
	 *            指定目标UDP服务器地址
	 * @param destinationPort
	 *            指定目标UDP服务器端口
	 * @throws UdpException
	 */
	public void send(byte[] sendBuf, InetAddress destinationAddress, int destinationPort) throws UdpException {
		this.sendBuf = sendBuf;
		sendPacket = new DatagramPacket(sendBuf, sendBuf.length, destinationAddress, destinationPort);
		this.send(sendPacket);
	}

	/**
	 * 按默认接收数据字节数组接收UDP服务器回复的数据
	 * 
	 * @return byte[] UDP服务器回复的数据字节数组
	 * @throws UdpException
	 */
	public byte[] receive() throws UdpException {
		return receive(DEFAULT_RECEIVE_BUF);
	}

	/**
	 * 指定接收数据字节数组接收UDP服务器回复的数据
	 * 
	 * @param receiveBuf
	 *            指定接收数据字节数组
	 * @return byte[] UDP服务器回复的数据字节数组
	 * @throws UdpException
	 */
	public byte[] receive(byte[] receiveBuf) throws UdpException {
		receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
		this.receive(receivePacket);
		return Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
	}

	/**
	 * 接收UDP服务器回复的数据
	 * 
	 * @param receivePacket
	 *            {@link DatagramPacket} 接收数据报包
	 * @throws UdpException
	 */
	public void receive(DatagramPacket receivePacket) throws UdpException {
		try {
			this.clientSocket.receive(receivePacket);
		} catch (IOException e) {
			throw new UdpException("接收UDP服务器回复数据异常！", e);
		}
	}

	/**
	 * 收发数据及超时处理
	 * 
	 * @return byte[] 收到的应答数据，若为null，则未收到数据
	 * @throws UdpException
	 */
	public byte[] sendAndReceive() throws UdpException {
		sendPacket = new DatagramPacket(sendBuf, sendBuf.length, DEFAULT_UDP_SERVER_ADDRESS, DEFAULT_UDP_SERVER_PORT);
		receivePacket = new DatagramPacket(DEFAULT_RECEIVE_BUF, DEFAULT_RECEIVE_BUF.length);
		return dealTimeout();
	}

	/**
	 * 收发数据及超时处理
	 * 
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public ResponseMsg sendAndReceiveToMsg() throws UdpException {
		sendPacket = new DatagramPacket(sendBuf, sendBuf.length, DEFAULT_UDP_SERVER_ADDRESS, DEFAULT_UDP_SERVER_PORT);
		receivePacket = new DatagramPacket(DEFAULT_RECEIVE_BUF, DEFAULT_RECEIVE_BUF.length);
		return dealTimeoutToMsg();
	}

	/**
	 * 收发数据及超时处理
	 * 
	 * @param sendBuf
	 *            发送数据字节数组
	 * @param receiveBuf
	 *            指定接收数据字节数组
	 * @return byte[] 收到的应答数据，若为null，则未收到数据
	 * @throws UdpException
	 */
	public byte[] sendAndReceive(byte[] sendBuf, byte[] receiveBuf) throws UdpException {
		sendPacket = new DatagramPacket(sendBuf, sendBuf.length, DEFAULT_UDP_SERVER_ADDRESS, DEFAULT_UDP_SERVER_PORT);
		receivePacket = new DatagramPacket(receiveBuf, receiveBuf.length);
		return dealTimeout();
	}

	/**
	 * 处理超时重发
	 * 
	 * @return byte[] 收到的应答数据，若为null，则未收到数据
	 * @throws UdpException
	 */
	private byte[] dealTimeout() throws UdpException {
		// 重发数据的次数统计
		int repeatCount = 0;
		try {
			while (repeatCount < REPEAT_MAX) {
				try {
					// 发送数据
					this.clientSocket.send(sendPacket);
					// 接收数据
					this.clientSocket.receive(receivePacket);
					// 按接收数据报包的长度返回应答数据字节数组
					return Arrays.copyOf(receivePacket.getData(), receivePacket.getLength());
				} catch (InterruptedIOException e) {
					// 如果接收数据时阻塞超时，重发并将重发数据的次数统计+1
					repeatCount++;
				} catch (IOException e) {
					throw new UdpException("UDP通信异常！", e);
				}
			}
		} finally {
			// 关闭UDP客户端
			this.close();
		}
		return null;
	}

	/**
	 * 处理超时重发
	 * 
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	private ResponseMsg dealTimeoutToMsg() throws UdpException {
		// 重发数据的次数统计
		int repeatCount = 0;
		try {
			while (repeatCount < REPEAT_MAX) {
				try {
					// 发送数据
					this.clientSocket.send(sendPacket);
					// 接收数据
					this.clientSocket.receive(receivePacket);
					// 按接收数据报包的长度返回应答数据字节数组
					return ResponseMsgUtil.success(Arrays.copyOf(receivePacket.getData(), receivePacket.getLength()));
				} catch (InterruptedIOException e) {
					// 如果接收数据时阻塞超时，重发并将重发数据的次数统计+1
					repeatCount++;
				} catch (IOException e) {
					throw new UdpException("UDP通信异常！", e);
				}
			}
		} finally {
			// 关闭UDP客户端
			this.close();
		}
		return null;// 应答超时
	}

	/**
	 * 关闭UDP客户端
	 */
	public void close() {
		this.clientSocket.close();
	}

	/**
	 * 执行一次UDP通信，包含超时处理
	 * 
	 * @param sendBuf
	 *            发送数据字节数组
	 * @return byte[] 收到的应答数据，若为null，则未收到数据
	 * @throws UdpException
	 */
	public static byte[] executeUdp(byte[] sendBuf) throws UdpException {
		UdpClient client = new UdpClient(sendBuf);
		return client.sendAndReceive();
	}

	/**
	 * 执行一次UDP通信，包含超时处理
	 * 
	 * @param sendBuf
	 *            发送数据字节数组
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg executeUdpToMsg(byte[] sendBuf) throws UdpException {
		UdpClient client = new UdpClient(sendBuf);
		return client.sendAndReceiveToMsg();
	}

	/**
	 * 执行一次UDP通信，包含超时处理
	 * 
	 * @param sendBuf
	 *            发送数据字节数组
	 * @param receiveBuf
	 *            指定接收数据字节数组
	 * @return byte[] 收到的应答数据，若为null，则未收到数据
	 * @throws UdpException
	 */
	public static byte[] executeUdp(byte[] sendBuf, byte[] receiveBuf) throws UdpException {
		UdpClient client = new UdpClient();
		return client.sendAndReceive(sendBuf, receiveBuf);
	}

	/**
	 * 执行一次UDP通信，包含超时处理
	 * 
	 * @param remoteAddress
	 *            {@link int} 目的地址(通信服务端地址/设备ID)
	 * @param biz
	 *            {@link short} 业务ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return UDP服务器应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean executeUdp(int remoteAddress, short biz, ByteBuffer dataBuffer) throws UdpException {
		// 序列号2个字节
		short serialNo = SerialNumberCache.getNextSerialNumber(biz);
		// 协议
		UdpProtocol udpProtocol = new UdpProtocol(remoteAddress, biz, serialNo, dataBuffer);
		byte[] sendBuf = udpProtocol.dealProtocolOfRequest();
		// UDP通信
		byte[] receiveBuf = executeUdp(sendBuf);
		// UDP应答结果
		return udpProtocol.dealProtocolOfResponse(receiveBuf);
	}

	/**
	 * 执行一次UDP通信，包含超时处理
	 * 
	 * @param remoteAddress
	 *            {@link int} 目的地址(通信服务端地址/设备ID)
	 * @param biz
	 *            {@link short} 业务ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg executeUdpToMsg(int remoteAddress, short biz, ByteBuffer dataBuffer) throws UdpException {
		// 序列号2个字节
		short serialNo = SerialNumberCache.getNextSerialNumber(biz);
		// 协议
		UdpProtocol udpProtocol = new UdpProtocol(remoteAddress, biz, serialNo, dataBuffer);
		byte[] sendBuf = udpProtocol.dealProtocolOfRequest();
		// UDP通信
		ResponseMsg receiveMsg = executeUdpToMsg(sendBuf);
		// UDP应答结果
		return udpProtocol.dealProtocolOfResponseToMsg(receiveMsg);
	}

	/**
	 * UDP通信(车辆公共参数更新业务)
	 * 
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForCommenParamUpdate(ByteBuffer dataBuffer) throws UdpException {
		return executeUdp(UdpProtocol.COMM_SERVER_ID, UdpBizId.TERMINAL_COMMON_CONFIG_UPDATE_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(车辆公共参数更新业务)
	 * 
	 * @param commonParamList
	 *            {@link byte} 公共配置文件列表，用位标识下列更新文件：<br>
	 *            1 emergency_card_info.db<br>
	 *            2 management_card_info.db<br>
	 *            3 in_out_oildepot_card_info.db<br>
	 *            4 in_out_oildepot_dev_info.db<br>
	 *            5 oildepot_info.db
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForCommenParamUpdate(byte commonParamList) throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForCommonConfigUpdate(commonParamList);
		return udpForCommenParamUpdate(dataBuffer);
	}

	/**
	 * UDP通信(车辆轨迹信息定时上传间隔参数更新业务)
	 *
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForTrackIntervalUpdate(int terminalId) throws UdpException {
		// 无数据体
		return executeUdp(terminalId, UdpBizId.TERMINAL_TRACK_CONFIG_UPDATE_REQUEST, null);
	}

	/**
	 * UDP通信(配送卡信息更新下发业务，车载终端设备ID为空时不发)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForTransportCardUpdate(int terminalId, ByteBuffer dataBuffer) throws UdpException {
		return executeUdp(terminalId, UdpBizId.TRANSPORT_CARD_UPDATE_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(配送卡信息更新下发业务，车载终端设备ID为空时不发)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param transportCard
	 *            {@link long} 配送卡ID
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForTransportCardUpdate(int terminalId, long transportCard) throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForTranscardUpdate(transportCard);
		return udpForTransportCardUpdate(terminalId, dataBuffer);
	}

	/**
	 * UDP通信(车辆绑定业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForCarBind(int terminalId, ByteBuffer dataBuffer) throws UdpException {
		return executeUdp(terminalId, UdpBizId.CAR_BIND_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(车辆绑定业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param carNumber
	 *            {@link String} 车牌号
	 * @param storeNum
	 *            {@link byte} 仓数
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForCarBind(int terminalId, String carNumber, byte storeNum) throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForCarBind(carNumber, storeNum);
		return udpForCarBind(terminalId, dataBuffer);
	}

	/**
	 * UDP通信(车辆绑定业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForCarBindToMsg(int terminalId, ByteBuffer dataBuffer) throws UdpException {
		return executeUdpToMsg(terminalId, UdpBizId.CAR_BIND_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(车辆绑定业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param carNumber
	 *            {@link String} 车牌号
	 * @param storeNum
	 *            {@link byte} 仓数
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForCarBindToMsg(int terminalId, String carNumber, byte storeNum) throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForCarBind(carNumber, storeNum);
		return udpForCarBindToMsg(terminalId, dataBuffer);
	}

	/**
	 * UDP通信(监听待绑定锁业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForLockListen(int terminalId, ByteBuffer dataBuffer) throws UdpException {
		return executeUdp(terminalId, UdpBizId.LOCK_LISTEN_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(监听待绑定锁业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param listenState
	 *            {@link byte} 监听状态<br>
	 *            1 开始监听<br>
	 *            2 结束监听
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForLockListen(int terminalId, byte listenState) throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForLockListen(listenState);
		return udpForLockListen(terminalId, dataBuffer);
	}

	/**
	 * UDP通信(监听待绑定锁业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForLockListenToMsg(int terminalId, ByteBuffer dataBuffer) throws UdpException {
		return executeUdpToMsg(terminalId, UdpBizId.LOCK_LISTEN_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(监听待绑定锁业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param listenState
	 *            {@link byte} 监听状态<br>
	 *            1 开始监听<br>
	 *            2 结束监听
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForLockListenToMsg(int terminalId, byte listenState) throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForLockListen(listenState);
		return udpForLockListenToMsg(terminalId, dataBuffer);
	}

	/**
	 * UDP通信(锁待绑定列表清除业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForLockClear(int terminalId) throws UdpException {
		// 无数据体
		return executeUdp(terminalId, UdpBizId.LOCK_CLEAR_REQUEST, null);
	}

	/**
	 * UDP通信(锁待绑定列表清除业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForLockClearToMsg(int terminalId) throws UdpException {
		// 无数据体
		return executeUdpToMsg(terminalId, UdpBizId.LOCK_CLEAR_REQUEST, null);
	}

	/**
	 * UDP通信(锁绑定列表下发业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForLockBind(int terminalId, ByteBuffer dataBuffer) throws UdpException {
		return executeUdp(terminalId, UdpBizId.LOCK_BIND_MODIFY_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(锁绑定列表下发业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param bindType
	 *            {@link byte} 变更类型（1 增加；2删除；3修改）
	 * @param locks
	 *            {@link Lock} 绑定的锁列表
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForLockBind(int terminalId, byte bindType, List<Lock> locks) throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForLockBind(bindType, locks);
		return udpForLockBind(terminalId, dataBuffer);
	}

	/**
	 * UDP通信(锁绑定列表下发业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForLockBindToMsg(int terminalId, ByteBuffer dataBuffer) throws UdpException {
		return executeUdpToMsg(terminalId, UdpBizId.LOCK_BIND_MODIFY_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(锁绑定列表下发业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param bindType
	 *            {@link byte} 变更类型（1 增加；2删除；3修改）
	 * @param locks
	 *            {@link Lock} 绑定的锁列表
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForLockBindToMsg(int terminalId, byte bindType, List<Lock> locks) throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForLockBind(bindType, locks);
		return udpForLockBindToMsg(terminalId, dataBuffer);
	}

	/**
	 * UDP通信(车台软件更新下发业务)
	 * 
	 * dataBuffer {@link ByteBuffer} 数据体
	 * 
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForTerminalSoftwareUpdateToMsg(ByteBuffer dataBuffer) throws UdpException {
		return executeUdpToMsg(UdpProtocol.COMM_SERVER_ID, UdpBizId.TERMINAL_SOFTWARE_UPGRADE_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(车台软件更新下发业务)
	 * 
	 * @param terminalIds
	 *            {@link String} 车台设备ID，英文逗号“,”分隔
	 * @param upgradeType {@link byte} 升级类型
	 * @param ftpPath
	 *            {@link String} ftp更新路径，UTF-8编码
	 * @param files
	 *            {@link TerminalUpgradeFile} 升级文件列表
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForTerminalSoftwareUpdateToMsg(String terminalIds, byte upgradeType, String ftpPath,
			List<TerminalUpgradeFile> files) throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForTerminalSoftwareUpgrade(terminalIds, upgradeType, ftpPath, files);
		return udpForTerminalSoftwareUpdateToMsg(dataBuffer);
	}

	/**
	 * UDP通信(实时监控业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForRealtimeMonitor(int terminalId, ByteBuffer dataBuffer) throws UdpException {
		return executeUdp(terminalId, UdpBizId.REALTIME_MONITOR_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(实时监控业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param sessionId
	 *            {@link WebSocketSession#getId()}
	 * @param interval
	 *            {@link short} 监控间隔（秒）
	 * @param duration
	 *            {@link short} 监控时长（分）
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForRealtimeMonitor(int terminalId, long sessionId, short interval, short duration)
			throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForRealtimeMonitor(sessionId, interval, duration);
		return udpForRealtimeMonitor(terminalId, dataBuffer);
	}

	/**
	 * UDP通信(重点监控业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 数据体
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForFocusMonitor(int terminalId, ByteBuffer dataBuffer) throws UdpException {
		return executeUdp(terminalId, UdpBizId.FOCUS_MONITOR_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(重点监控业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param sessionId
	 *            {@link WebSocketSession#getId()}
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForFocusMonitor(int terminalId, long sessionId) throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForFocusMonitor(sessionId);
		return udpForFocusMonitor(terminalId, dataBuffer);
	}

	/**
	 * UDP通信(轨迹监控取消业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param sessionId
	 *            {@link WebSocketSession#getId()}
	 * @param udpBizId
	 *            {@link short}
	 *            UDP业务ID（FOCUS_MONITOR_CANCEL_REQUEST，REAL_TIME_MONITOR_CANCEL_REQUEST）
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForMonitorCancel(int terminalId, long sessionId, short udpBizId) throws UdpException {
		ByteBuffer dataBuffer = SendPacketBuilder.buildDataBufForMonitorCancel(sessionId);
		return executeUdp(terminalId, udpBizId, dataBuffer);
	}

	/**
	 * UDP通信(远程换站业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 协议数据体
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForChangeStation(int terminalId, ByteBuffer dataBuffer) throws UdpException {
		return executeUdp(terminalId, UdpBizId.REMOTE_CHANGE_STATION_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(远程换站业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param dataBuffer
	 *            {@link ByteBuffer} 协议数据体
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForChangeStationToMsg(int terminalId, ByteBuffer dataBuffer) throws UdpException {
		return executeUdpToMsg(terminalId, UdpBizId.REMOTE_CHANGE_STATION_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(远程报警消除业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param deviceType
	 *            {@link byte} 设备类型<br>
	 *            1 车台<br>
	 *            2 锁
	 * @param deviceId
	 *            {@link int} 设备ID
	 * @param alarmType
	 *            {@link byte} 报警类型（用位标识）<br>
	 *            车台：<br>
	 *            1 未施封越界<br>
	 *            锁：<br>
	 *            1 通讯异常<br>
	 *            2 电池低电压报警<br>
	 *            3 异常开锁报警<br>
	 *            4 进入应急
	 * @param userName
	 *            {@link String} 操作员姓名，UTF-8编码
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForAlarmEliminate(int terminalId, byte deviceType, int deviceId, byte alarmType,
			String userName) throws UdpException {
		byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
		int userNameBufLen = userNameBuf.length;
		// 缓冲区容量
		int capacity = 7 + userNameBufLen;
		// 构建协议数据体
		ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
		// 添加设备类型，1个字节
		dataBuffer.put(deviceType);
		// 添加设备ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(deviceId));
		// 添加报警类型，1个字节
		dataBuffer.put(alarmType);
		// 添加姓名长度，1个字节
		dataBuffer.put((byte) userNameBufLen);
		// 添加操作员姓名，UTF-8编码
		dataBuffer.put(userNameBuf);
		return executeUdp(terminalId, UdpBizId.REMOTE_ALARM_ELIMINATE_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(远程报警消除业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param deviceType
	 *            {@link byte} 设备类型<br>
	 *            1 车台<br>
	 *            2 锁
	 * @param deviceId
	 *            {@link int} 设备ID
	 * @param alarmType
	 *            {@link byte} 报警类型（用位标识）<br>
	 *            车台：<br>
	 *            1 未施封越界<br>
	 *            锁：<br>
	 *            1 通讯异常<br>
	 *            2 电池低电压报警<br>
	 *            3 异常开锁报警<br>
	 *            4 进入应急
	 * @param userName
	 *            {@link String} 操作员姓名，UTF-8编码
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForAlarmEliminateToMsg(int terminalId, byte deviceType, int deviceId, byte alarmType,
			String userName) throws UdpException {
		byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
		int userNameBufLen = userNameBuf.length;
		// 缓冲区容量
		int capacity = 7 + userNameBufLen;
		// 构建协议数据体
		ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
		// 添加设备类型，1个字节
		dataBuffer.put(deviceType);
		// 添加设备ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(deviceId));
		// 添加报警类型，1个字节
		dataBuffer.put(alarmType);
		// 添加姓名长度，1个字节
		dataBuffer.put((byte) userNameBufLen);
		// 添加操作员姓名，UTF-8编码
		dataBuffer.put(userNameBuf);
		return executeUdpToMsg(terminalId, UdpBizId.REMOTE_ALARM_ELIMINATE_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(远程车辆进出请求业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param remoteControlId
	 *            {@link int} 远程控制ID
	 * @param controlType
	 *            {@link byte} 操作类型<br>
	 *            1 入库<br>
	 *            2 出库<br>
	 *            3 入加油站<br>
	 *            4 出加油站<br>
	 * @param stationId
	 *            {@link int} 站点ID（加油站ID、油库ID）
	 * @param userName
	 *            {@link String} 操作员姓名，UTF-8编码
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForCarInOut(int terminalId, int remoteControlId, byte controlType, int stationId,
			String userName) throws UdpException {
		byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
		int userNameBufLen = userNameBuf.length;
		// 缓冲区容量
		int capacity = 10 + userNameBufLen;
		// 构建协议数据体
		ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
		// 远程控制ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(remoteControlId));
		// 添加操作类型，1个字节
		dataBuffer.put(controlType);
		// 添加站点ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(stationId));
		// 添加姓名长度，1个字节
		dataBuffer.put((byte) userNameBufLen);
		// 添加操作员姓名，UTF-8编码
		dataBuffer.put(userNameBuf);
		return executeUdp(terminalId, UdpBizId.REMOTE_CAR_IN_OUT_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(远程车辆进出请求业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param remoteControlId
	 *            {@link int} 远程控制ID
	 * @param controlType
	 *            {@link byte} 操作类型<br>
	 *            1 入库<br>
	 *            2 出库<br>
	 *            3 入加油站<br>
	 *            4 出加油站<br>
	 * @param stationId
	 *            {@link int} 站点ID（加油站ID、油库ID）
	 * @param userName
	 *            {@link String} 操作员姓名，UTF-8编码
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForCarInOutToMsg(int terminalId, int remoteControlId, byte controlType, int stationId,
			String userName) throws UdpException {
		byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
		int userNameBufLen = userNameBuf.length;
		// 缓冲区容量
		int capacity = 10 + userNameBufLen;
		// 构建协议数据体
		ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
		// 远程控制ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(remoteControlId));
		// 添加操作类型，1个字节
		dataBuffer.put(controlType);
		// 添加站点ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(stationId));
		// 添加姓名长度，1个字节
		dataBuffer.put((byte) userNameBufLen);
		// 添加操作员姓名，UTF-8编码
		dataBuffer.put(userNameBuf);
		return executeUdpToMsg(terminalId, UdpBizId.REMOTE_CAR_IN_OUT_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(远程车辆状态强制变更业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param remoteControlId
	 *            {@link int} 远程控制ID
	 * @param carStatus
	 *            {@link byte} 车辆状态<br>
	 *            1 在途<br>
	 *            2 返程<br>
	 *            3 在油库<br>
	 *            4 在加油站<br>
	 *            5 应急
	 * @param stationId
	 *            {@link int} 站点ID（加油站ID、油库ID）
	 * @param userName
	 *            {@link String} 操作员姓名，UTF-8编码
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForCarStatusAlter(int terminalId, int remoteControlId, byte carStatus, int stationId,
			String userName) throws UdpException {
		byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
		int userNameBufLen = userNameBuf.length;
		// 缓冲区容量
		int capacity = 10 + userNameBufLen;
		// 构建协议数据体
		ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
		// 远程控制ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(remoteControlId));
		// 添加车辆状态，1个字节
		dataBuffer.put(carStatus);
		// 添加站点ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(stationId));
		// 添加姓名长度，1个字节
		dataBuffer.put((byte) userNameBufLen);
		// 添加操作员姓名，UTF-8编码
		dataBuffer.put(userNameBuf);
		return executeUdp(terminalId, UdpBizId.REMOTE_CAR_STATUS_ALTER_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(远程车辆状态强制变更业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param remoteControlId
	 *            {@link int} 远程控制ID
	 * @param carStatus
	 *            {@link byte} 车辆状态<br>
	 *            1 在途<br>
	 *            2 返程<br>
	 *            3 在油库<br>
	 *            4 在加油站<br>
	 *            5 应急
	 * @param stationId
	 *            {@link int} 站点ID（加油站ID、油库ID）
	 * @param userName
	 *            {@link String} 操作员姓名，UTF-8编码
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForCarStatusAlterToMsg(int terminalId, int remoteControlId, byte carStatus,
			int stationId, String userName) throws UdpException {
		byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
		int userNameBufLen = userNameBuf.length;
		// 缓冲区容量
		int capacity = 10 + userNameBufLen;
		// 构建协议数据体
		ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
		// 远程控制ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(remoteControlId));
		// 添加车辆状态，1个字节
		dataBuffer.put(carStatus);
		// 添加站点ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(stationId));
		// 添加姓名长度，1个字节
		dataBuffer.put((byte) userNameBufLen);
		// 添加操作员姓名，UTF-8编码
		dataBuffer.put(userNameBuf);
		return executeUdpToMsg(terminalId, UdpBizId.REMOTE_CAR_STATUS_ALTER_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(开锁重置业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param lockIds
	 *            {@link Integer} 锁设备ID列表
	 * @param userId
	 *            {@link int} 操作员ID
	 * @param userName
	 *            {@link String} 操作员姓名，UTF-8编码
	 * @return 应答结果：<code>true</code>成功；<code>false</code>失败
	 * @throws UdpException
	 */
	public static boolean udpForLockOpenReset(int terminalId, List<Integer> lockIds, int userId, String userName)
			throws UdpException {
		int lockNum = lockIds.size();
		byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
		int userNameBufLen = userNameBuf.length;
		// 缓冲区容量
		int capacity = 6 + 4 * lockNum + userNameBufLen;
		// 构建协议数据体
		ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
		// 添加锁数量，1个字节
		dataBuffer.put((byte) lockNum);
		if (lockNum > 0) {
			for (Integer lockId : lockIds) {
				// 添加锁设备ID，4个字节
				dataBuffer.put(BytesConverterByLittleEndian.getBytes(lockId));
			}
		}
		// 添加操作员ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(userId));
		// 添加姓名长度，1个字节
		dataBuffer.put((byte) userNameBufLen);
		// 添加操作员姓名，UTF-8编码
		dataBuffer.put(userNameBuf);
		return executeUdp(terminalId, UdpBizId.LOCK_OPEN_RESET_REQUEST, dataBuffer);
	}

	/**
	 * UDP通信(开锁重置业务)
	 * 
	 * @param terminalId
	 *            {@link int} 车载终端设备ID
	 * @param lockIds
	 *            {@link Integer} 锁设备ID列表
	 * @param userId
	 *            {@link int} 操作员ID
	 * @param userName
	 *            {@link String} 操作员姓名，UTF-8编码
	 * @return {@link ResponseMsg}
	 * @throws UdpException
	 */
	public static ResponseMsg udpForLockOpenResetToMsg(int terminalId, List<Integer> lockIds, int userId,
			String userName) throws UdpException {
		int lockNum = lockIds.size();
		byte[] userNameBuf = userName.getBytes(StandardCharsets.UTF_8);
		int userNameBufLen = userNameBuf.length;
		// 缓冲区容量
		int capacity = 6 + 4 * lockNum + userNameBufLen;
		// 构建协议数据体
		ByteBuffer dataBuffer = ByteBuffer.allocate(capacity);
		// 添加锁数量，1个字节
		dataBuffer.put((byte) lockNum);
		if (lockNum > 0) {
			for (Integer lockId : lockIds) {
				// 添加锁设备ID，4个字节
				dataBuffer.put(BytesConverterByLittleEndian.getBytes(lockId));
			}
		}
		// 添加操作员ID，4个字节
		dataBuffer.put(BytesConverterByLittleEndian.getBytes(userId));
		// 添加姓名长度，1个字节
		dataBuffer.put((byte) userNameBufLen);
		// 添加操作员姓名，UTF-8编码
		dataBuffer.put(userNameBuf);
		return executeUdpToMsg(terminalId, UdpBizId.LOCK_OPEN_RESET_REQUEST, dataBuffer);
	}
}
