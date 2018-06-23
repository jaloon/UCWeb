package com.tipray.service.impl;

import com.tipray.bean.baseinfo.InOutReader;
import com.tipray.dao.InOutReaderDao;
import com.tipray.service.InOutReaderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 出入库读卡器管理业务层
 * 
 * @author chenlong
 * @version 1.0 2018-03-12
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "Exception" })
@Service("inOutReaderService")
public class InOutReaderServiceImpl implements InOutReaderService {
	@Resource
	private InOutReaderDao inOutReaderDao;

	@Override
	public InOutReader addInOutReader(InOutReader inOutReader) {
		if (inOutReader != null) {
			inOutReaderDao.add(inOutReader);
		}
		return inOutReader;
	}

	@Override
	public InOutReader updateInOutReader(InOutReader inOutReader) {
		if (inOutReader != null) {
			inOutReaderDao.update(inOutReader);
		}
		return inOutReader;
	}

	@Override
	public void deleteInOutReadersById(Long id) {
		inOutReaderDao.delete(id);
	}

	@Override
	public InOutReader getInOutReaderById(Long id) {
		return id == null ? null : inOutReaderDao.getById(id);
	}

	@Override
	public List<InOutReader> findByOilDepotId(Long oilDepotId) {
		return inOutReaderDao.findByOilDepotId(oilDepotId);
	}

	@Override
	public void deleteByOilDepotId(Long oilDepotId) {
		inOutReaderDao.deleteByOilDepotId(oilDepotId);
	}

	@Override
	public List<Integer> findBarrierReaderIdByDepotId(Integer oilDepotId, Integer barrierType) {
		return inOutReaderDao.findBarrierReaderIdByDepotId(oilDepotId, barrierType);
	}

}
