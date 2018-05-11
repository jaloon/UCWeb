package com.tipray.mq;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apache ActiveMq 封装（太重量！！！）
 * 
 * @author chenlong
 * @version 1.0 2018-03-20
 *
 */
public class ActiveMq {
	private static final Logger logger = LoggerFactory.getLogger(ActiveMq.class);
	/** 默认连接用户名 */
	private static final String USER_NAME = ActiveMQConnectionFactory.DEFAULT_USER;
	/** 默认连接密码 */
	private static final String PASS_WORD = ActiveMQConnectionFactory.DEFAULT_PASSWORD;
	/** 默认连接地址 */
	private static final String BROKE_URL = ActiveMQConnectionFactory.DEFAULT_BROKER_URL;
	/** 连接工厂 */
	private static final ConnectionFactory CONNECTION_FACTORY = new ActiveMQConnectionFactory(USER_NAME, PASS_WORD,
			BROKE_URL);
	/** 连接 */
	private static Connection connection;
	/** 会话接收或者发送消息的线程 */
	private static Session session;
	/** 消息的目的地 */
	private static Destination destination;
	private static final String QUEUE_NAME = "E-SEAL";
	/** 消息生产者 */
	private static MessageProducer messageProducer;
	/** 消息的消费者 */
	private static MessageConsumer messageConsumer;
	static {
		init();
	}

	/**
	 * 初始化消息队列
	 */
	public static void init() {
		try {
			// 通过连接工厂获取连接
			connection = CONNECTION_FACTORY.createConnection();
			// 启动连接
			connection.start();
			// 创建session
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			// 创建一个名称为E-SEAL的消息队列
			destination = session.createQueue(QUEUE_NAME);
			// 创建消息生产者
			messageProducer = session.createProducer(destination);
			// 创建消息消费者
			messageConsumer = session.createConsumer(destination);
		} catch (JMSException e) {
			logger.error(e.toString());
			closeConnection();
		}
	}

	/**
	 * 发送文本消息
	 * 
	 * @param text
	 *            {@link String} 消息文本
	 */
	public static void sendTextMessage(String text) {
		try {
			TextMessage message = session.createTextMessage(text);
			// 通过消息生产者发出消息
			messageProducer.send(message);
			session.commit();
		} catch (JMSException e) {
			logger.error(e.toString());
		}
	}

	/**
	 * 发送字节流消息
	 * 
	 * @param bytes
	 *            {@link byte[]} 消息字节数组
	 */
	public static void sendBytesMessage(byte[] bytes) {
		try {
			BytesMessage message = session.createBytesMessage();
			message.writeBytes(bytes);
			messageProducer.send(message);
			session.commit();
		} catch (JMSException e) {
			logger.error(e.toString());
		}
	}

	/**
	 * 接收消息
	 */
	public static void receive() {
		try {
			while (true) {
				Message message = messageConsumer.receive(100000);
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					if (textMessage != null) {
						System.out.println("收到的消息:" + textMessage.getText());
					}
					continue;
				}
				if (message instanceof BytesMessage) {
					BytesMessage bytesMessage = (BytesMessage) message;
					int len = (int) bytesMessage.getBodyLength();
					byte[] bytes = new byte[len];
					int i = bytesMessage.readBytes(bytes);
					if (i != -1) {
						// biz to do
					}
					continue;
				}
			}
		} catch (JMSException e) {
			logger.error(e.toString());
		}
	}

	/**
	 * 关闭连接
	 */
	public static void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException jmse) {
				logger.error(jmse.toString());
			}
		}
	}

	// 构造器私有化
	private ActiveMq() {
	}
}