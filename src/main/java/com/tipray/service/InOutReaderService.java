package com.tipray.service;

import com.tipray.bean.baseinfo.InOutReader;
import com.tipray.core.exception.ServiceException;

import java.util.List;

/**
 * InOutReaderService
 *
 * @author chenlong
 * @version 1.0 2018-01-15
 */
public interface InOutReaderService {
    /**
     * 新增出入库读卡器
     *
     * @param inOutReader 出入库读卡器
     * @throws ServiceException
     */
    InOutReader addInOutReader(InOutReader inOutReader) throws ServiceException;

    /**
     * 修改出入库读卡器
     *
     * @param inOutReader 出入库读卡器
     * @throws ServiceException
     */
    InOutReader updateInOutReader(InOutReader inOutReader) throws ServiceException;

    /**
     * 根据Id删除出入库读卡器
     *
     * @param id 读卡器
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
     * @param oilDepotId 油库ID
     * @return
     */
    List<InOutReader> findByOilDepotId(Long oilDepotId);

    /**
     * 根据油库ID删除出入库读卡器信息
     *
     * @param oilDepotId 油库ID
     */
    void deleteByOilDepotId(Long oilDepotId) throws ServiceException;

    /**
     * 根据油库ID查找由于转发道闸通知的读卡器ID
     *
     * @param oilDepotId  油库ID
     * @param barrierType 道闸类型（1 进，2 出）
     * @return 读卡器ID
     */
    List<Integer> findBarrierReaderIdByDepotId(Integer oilDepotId, Integer barrierType);
}
