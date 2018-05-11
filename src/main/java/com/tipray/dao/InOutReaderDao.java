package com.tipray.dao;

import java.util.List;

import com.tipray.bean.baseinfo.InOutReader;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * InOutReaderDao
 * 
 * @author chenlong
 * @version 1.0 2018-01-15
 *
 */
@MyBatisAnno
public interface InOutReaderDao extends BaseDao<InOutReader> {
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
    void deleteByOilDepotId(Long oilDepotId);

	/**
	 * 根据出入库读卡器设备ID获取读卡器信息
	 * 
	 * @param devId
	 * @return
	 */
    InOutReader getByDevId(Integer devId);

	/**
	 * 获取未使用的读卡器信息
	 * 
	 * @return
	 */
    List<InOutReader> findUnusedReader();

	/**
	 * 批量添加读卡器信息
	 * 
	 * @param readers
	 */
    void addReaderList(List<InOutReader> list);
}
