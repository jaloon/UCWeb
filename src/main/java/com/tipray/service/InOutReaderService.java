package com.tipray.service;

import java.util.List;

import com.tipray.bean.baseinfo.InOutReader;
import com.tipray.core.exception.ServiceException;

/**
 * InOutReaderService
 * 
 * @author chenlong
 * @version 1.0 2018-01-15
 *
 */
public interface InOutReaderService {
	/**
	 * 新增出入库卡
	 * 
	 * @param inOutReader
	 * @throws ServiceException
	 */
    InOutReader addInOutReader(InOutReader inOutReader) throws ServiceException;

	/**
	 * 修改出入库卡
	 * 
	 * @param inOutReader
	 * @throws ServiceException
	 */
    InOutReader updateInOutReader(InOutReader inOutReader) throws ServiceException;

	/**
	 * 根据Id删除出入库卡
	 * 
	 * @param id
	 * @throws ServiceException
	 */
    void deleteInOutReadersById(Long id) throws ServiceException;

	/**
	 * 根据Id获取出入库卡
	 * 
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
    InOutReader getInOutReaderById(Long id);

	/**
	 * 根据油库ID查询出入库读卡器信息
	 * 
	 * @param oilDepotId
	 * @return
	 */
    List<InOutReader> findByOilDepotId(Long oilDepotId);

	/**
	 * 根据油库ID删除出入库读卡器信息
	 * 
	 * @param oilDepotId
	 */
    void deleteByOilDepotId(Long oilDepotId) throws ServiceException;
}
