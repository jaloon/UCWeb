package com.tipray.net;

import com.tipray.cache.AsynUdpCommCache;
import com.tipray.constant.CenterConfigConst;
import com.tipray.mq.MyQueue;
import com.tipray.pool.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

/**
 * 非阻塞UDP服务端
 * 
 * @author chenlong
 * @version 1.0 2018-03-20
 *
 */
public class NioUdpServer {
	private static final Logger logger = LoggerFactory.getLogger(NioUdpServer.class);
	/** 服务器数据报通道 */
	private DatagramChannel serverChannel;
	/** 客户端数据报通道 */
	private DatagramChannel clientChannel;
	/** 选择器 */
	private Selector selector;
	/** 默认数据报接收缓冲区（容量1024字节） */
	private static final ByteBuffer DEFAULT_RECEIVE_BUF = ByteBuffer.allocate(1024);
	/** 服务器默认绑定端口 */
	private static final int DEFAULT_BIND_PORT = CenterConfigConst.UDP_LOCAL_PORT;
	/** 默认数据报发送地址 */
	private static final InetSocketAddress DEFAULT_SEND_ADDRESS = new InetSocketAddress(
			CenterConfigConst.UDP_REMOTE_ADDR, CenterConfigConst.UDP_REMOTE_PORT);
	/** 接收数据是否可用标志 */
	private boolean enabled = true;

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean getEnabled() {
		return enabled;
	}

	/**
	 * 启动非阻塞UDP服务器，并将其绑定到默认端口
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		start(DEFAULT_BIND_PORT);
	}

	/**
	 * 启动非阻塞UDP服务器，并将其绑定到指定端口
	 * 
	 * @param bindPort
	 *            {@link Integer} UDP服务器绑定的端口号
	 * @throws Exception
	 */
	public void start(int bindPort) throws Exception {
		// 打开服务器数据报通道（尚未连接）
		serverChannel = DatagramChannel.open();
		// 将服务器数据报通道设置为非阻塞模式
		serverChannel.configureBlocking(false);
		// 将与服务器数据报通道关联的数据报套接字绑定到本机特定端口
		serverChannel.socket().bind(new InetSocketAddress(bindPort));
		// 打开一个选择器
		selector = Selector.open();
		// 将服务器数据报通道通道注册至selector，监听只读消息（此时服务端只能读数据，无法写数据）
		serverChannel.register(selector, SelectionKey.OP_READ);
	}

	/**
	 * 使用默认数据报接收缓冲区接收接收客户端通道发送的数据报
	 */
	public void receive() {
		receive(DEFAULT_RECEIVE_BUF);
	}

	/**
	 * 使用指定数据报接收缓冲区接收接收客户端通道发送的数据报
	 * 
	 * @param receiveBuffer
	 *            {@link ByteBuffer} 指定的数据报接收缓冲区
	 */
	public void receive(ByteBuffer receiveBuffer) {
		ThreadPool.CACHED_THREAD_POOL.execute(() -> {
			while (enabled) {
				try {
					// 选择一组键，其相应的通道已为 I/O 操作准备就绪，键的数目可能为零
					// 有通道就绪时（即select()的返回值大于0时），不阻塞；否则，阻塞，不会有任何的通道加入
					if (selector.select() == 0) {
						continue;
					}
					// 获取此选择器的已选择键集
					Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
					while (iterator.hasNext()) {
						SelectionKey key = iterator.next();
						// 此处负责把udp连接断掉，必须手动删除
						iterator.remove();
						if (key.isReadable()) {// 测试此键的通道是否已准备好进行读取。
							// 接收数据过程
							clientChannel = (DatagramChannel) key.channel();
							receiveBuffer.clear();// 固定写法
							// 接受来自客户端通道的数据
							InetSocketAddress address = (InetSocketAddress) clientChannel.receive(receiveBuffer);

							short i = receiveBuffer.getShort(0);
                            HttpServletResponse response = AsynUdpCommCache.getAndRemoveResponseCache((int)i);
                            if (response != null) {
                                PrintWriter out = response.getWriter();
                                out.write("recieve: " + i);
                                out.flush();
                                out.close();
                                System.out.println("recieve: " + i);
                                continue;
                            }

							// 1.判断是否来自预期地址和端口号
							// InetSocketAddress.getHostName()获取的主机名称是计算机名，InetAddress.getHostAddress()获取的才是IP地址
							String host = address.getAddress().getHostAddress();
							if (!host.equals(CenterConfigConst.UDP_REMOTE_HOST)) {
								// 远程主机名称非预期，抛掉
								logger.warn("远程主机地址【{}】与预期地址【{}】不符！", host, CenterConfigConst.UDP_REMOTE_HOST);
								continue;
							}
							int port = address.getPort();
							if (port != CenterConfigConst.UDP_REMOTE_PORT) {
								// 远程主机端口非预期，抛掉
								logger.warn("远程主机端口【{}】与预期端口【{}】不符！", port, CenterConfigConst.UDP_REMOTE_PORT);
								continue;
							}
							
							// 2.地址和端口正确，则为收到通信服务端的心跳，更新心跳时间
							AsynUdpCommCache.updateHeartbeat();
							
							// 3.验证数据长度，长度够放入队列，否则抛掉
							int receiveBufferLen = receiveBuffer.position();
							if (receiveBufferLen < UdpProtocol.MIN_RECEIVE_BUF_LEN) {
								// 接收数据字节数不够，不放入队列
								logger.warn("接收数据太少，只有{}个字节！", receiveBufferLen);
								continue;
							}
							byte[] bytes = Arrays.copyOf(receiveBuffer.array(), receiveBufferLen);
							// 把收到的数据放入队列进行处理
							MyQueue.queueTask(address, bytes);
						}
					}
				} catch (Exception e) {
					logger.error("UDP接收数据异常：{}", e.getMessage());
					logger.debug("UDP接收数据异常堆栈信息：", e);
				}
			}
		});
	}
	
	/**
	 * 向客户端发送数据
	 * 
	 * @param src
	 *            {@link byte[]} 待发送数据
	 * @param target
	 *            {@link InetSocketAddress} 目标客户端地址（IP + port）
	 * @return <code>true</code>成功；<code>false</code>失败
	 */
	public boolean send(byte[] src, InetSocketAddress target) {
		ByteBuffer buf = ByteBuffer.allocate(src.length);
		buf.put(src);
		return send(buf, target);
	}

	/**
	 * 向客户端发送数据
	 * 
	 * @param src
	 *            {@link String} 待发送数据
	 * @param target
	 *            {@link InetSocketAddress} 目标客户端地址（IP + port）
	 * @return <code>true</code>成功；<code>false</code>失败
	 */
	public boolean send(String src, InetSocketAddress target) {
		ByteBuffer buf = StandardCharsets.UTF_8.encode(src);
		return send(buf, target);
	}

	/**
	 * 向客户端发送数据
	 * 
	 * @param src
	 *            {@link ByteBuffer} 待发送数据
	 * @param target
	 *            {@link InetSocketAddress} 目标客户端地址（IP + port）
	 * @return <code>true</code>成功；<code>false</code>失败
	 */
	public boolean send(ByteBuffer src, InetSocketAddress target) {
		try {
			// !!! position: 表示在哪个位置开始往缓冲区中写数据或是读数据
			// !!! 发送数据前需要先从缓冲区读取数据写入通道
			// !!! 如果不设置position，则从缓冲区读取的数据可能不是全部数据，甚至可能读不到数据（向客户端发送0字节）
			src.position(0);

			// 发送数据方式一：若向客户端发送了0字节，客户端不会收到信息
			// serverChannel.connect(target);
			// int sendBytesNum = serverChannel.write(src);

			// 发送数据方式二：若向客户端发送了0字节，客户端会收到0字节信息
			int sendBytesNum = serverChannel.send(src, target);

			if (sendBytesNum == 0) {
				logger.warn("向客户端{}发送了0字节！", target);
				return false;
			}
			// 发送数据后必须断开连接，否则下次发送数据会报异常：
			// java.lang.IllegalStateException: Connect already invoked
			serverChannel.disconnect();
			return true;
		} catch (Exception e) {
			logger.error("向客户端{}发送数据异常：{}", target, e.toString());
			logger.debug("UDP发送数据异常堆栈信息：", e);
			return false;
		}
	}

	/**
	 * 向默认客户端发送数据
	 * 
	 * @param src
	 *            {@link byte[]} 待发送数据
	 * @return <code>true</code>成功；<code>false</code>失败
	 */
	public boolean send(byte[] src) {
		return send(src, DEFAULT_SEND_ADDRESS);
	}

	/**
	 * 向默认客户端发送数据
	 * 
	 * @param src
	 *            {@link String} 待发送数据
	 * @return <code>true</code>成功；<code>false</code>失败
	 */
	public boolean send(String src) {
		return send(src, DEFAULT_SEND_ADDRESS);
	}

	/**
	 * 向默认客户端发送数据
	 * 
	 * @param src
	 *            {@link ByteBuffer} 待发送数据
	 * @return <code>true</code>成功；<code>false</code>失败
	 */
	public boolean send(ByteBuffer src) {
		return send(src, DEFAULT_SEND_ADDRESS);
	}

	/**
	 * 获取本地地址
	 * 
	 * @return {@link SocketAddress}
	 */
	public SocketAddress getLocalAddress() {
		try {
			return serverChannel.getLocalAddress();
		} catch (Exception e) {
			logger.error("获取本地地址异常：{}", e.getMessage());
		}
		return null;
	}

	/**
	 * 关闭非阻塞UDP服务器
	 */
	public void stop() {
		if (serverChannel != null) {
			try {
				serverChannel.close();
			} catch (IOException e) {
				logger.error("关闭服务器数据报通道异常：{}", e.getMessage());
			}
		}
		if (clientChannel != null) {
			try {
				clientChannel.close();
			} catch (IOException e) {
				logger.error("关闭客户端数据报通道异常：{}", e.getMessage());
			}
		}
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				logger.error("关闭选择器异常：{}", e.getMessage());
			}
		}
	}
	
}
