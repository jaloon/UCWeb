package com.tipray.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.BlobMessage;
import org.junit.Test;

public class MqTset {
	private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;// 默认连接用户名
	private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;// 默认连接密码
	private static final String BROKEURL = ActiveMQConnection.DEFAULT_BROKER_URL;// 默认连接地址
	
	@Test
	public void testJMSConsumer() {
		ConnectionFactory connectionFactory;// 连接工厂
		Connection connection = null;// 连接

		Session session;// 会话 接受或者发送消息的线程
		Destination destination;// 消息的目的地

		MessageConsumer messageConsumer;// 消息的消费者

		// 实例化连接工厂
		connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKEURL);

		try {
			// 通过连接工厂获取连接
			connection = connectionFactory.createConnection();
			// 启动连接
			connection.start();
			// 创建session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// 创建一个连接HelloWorld的消息队列
			destination = session.createQueue("HelloWorld");
			// 创建消息消费者
			messageConsumer = session.createConsumer(destination);
			System.out.println("create mq consumer success!");
			while (true) {
				Message message = messageConsumer.receive(100000);
				Thread.sleep(3000);
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
						System.out.println("收到的消息: len(" + len + "), i(" + i +  "), \n\tbytes:" + Arrays.toString(bytes));
					}
					continue;
				}
				if (message instanceof BlobMessage) {
					BlobMessage blobMessage = (BlobMessage) message;
					try {
						InputStream in = blobMessage.getInputStream();
						List<Byte> list = new ArrayList<>();
						int i;
						while ((i =  in.read()) != -1) {
							list.add((byte)i);
						}
						System.out.println("收到的消息:" + list);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
