package com.tipray.service.impl;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.baseinfo.TransportCard;
import com.tipray.cache.AsynUdpCommCache;
import com.tipray.cache.SerialNumberCache;
import com.tipray.dao.TransportCardDao;
import com.tipray.net.NioUdpServer;
import com.tipray.net.SendPacketBuilder;
import com.tipray.net.TimeOutTask;
import com.tipray.net.constant.UdpBizId;
import com.tipray.service.TransportCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * 配送卡管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "Exception" })
@Service("transportCardService")
public class TransportCardServiceImpl implements TransportCardService {
	private final Logger logger = LoggerFactory.getLogger(TransportCardServiceImpl.class);
	@Resource
	private TransportCardDao transportCardDao;
	@Resource
	private NioUdpServer udpServer;

	@Override
	public TransportCard addTransportCard(TransportCard transportCard) {
		if (transportCard != null) {
			transportCardDao.add(transportCard);
		}
		return transportCard;
	}

	@Override
	public TransportCard updateTransportCard(TransportCard transportCard) {
		if (transportCard != null) {
			transportCardDao.update(transportCard);
		}
		return transportCard;
	}

	@Override
	public void deleteTransportCardById(Long id) {
		Integer terminalId = transportCardDao.getTerminalIdById(id);
		Long transportCardId = transportCardDao.getTransportCardIdById(id);
		transportCardDao.deleteVehicleCardId(transportCardId);
		transportCardDao.delete(id);
		if (terminalId != null && terminalId != 0) {
			ByteBuffer src = SendPacketBuilder.buildProtocol0x1203(terminalId, 0);
			boolean isSend = udpServer.send(src);
			if (!isSend) {
				logger.error("UDP发送数据异常！");
			}
			addTimeoutTask(src);
		}
	}

	@Override
	public TransportCard getTransportCardById(Long id) {
		return id == null ? null : transportCardDao.getById(id);
	}

	@Override
	public List<TransportCard> findAllTransportCards() {
		return transportCardDao.findAll();
	}

	@Override
	public long countTransportCard(TransportCard transportCard) {
		return transportCardDao.count(transportCard);
	}

	@Override
	public List<TransportCard> findByPage(TransportCard transportCard, Page page) {
		return transportCardDao.findByPage(transportCard, page);
	}

	@Override
	public GridPage<TransportCard> findTransportCardsForPage(TransportCard transportCard, Page page) {
		long records = countTransportCard(transportCard);
		List<TransportCard> list = findByPage(transportCard, page);
		return new GridPage<TransportCard>(list, records, page.getPageId(), page.getRows(), list.size(), transportCard);
	}

	@Override
	public List<TransportCard> findUnusedTranscards() {
		return transportCardDao.findUnusedTranscards();
	}

	@Override
	public Map<String, Object> getByTransportCardId(Long transportCardId) {
		return transportCardId == null ? null : transportCardDao.getByTransportCardId(transportCardId);
	}

	/**
	 * 添加超时任务
	 * 
	 * @param src
	 *            {@link ByteBuffer} 待发送数据包
	 * @param cacheId
	 *            {@link Integer} 缓存ID
	 */
	private void addTimeoutTask(ByteBuffer src) {
		short serialNo = SerialNumberCache.getSerialNumber(UdpBizId.TRANSPORT_CARD_UPDATE_REQUEST);
		int cacheId = AsynUdpCommCache.buildCacheId(UdpBizId.TRANSPORT_CARD_UPDATE_REQUEST, serialNo);
		new TimeOutTask(src, cacheId).executeInfoIssueTask();
	}
}
