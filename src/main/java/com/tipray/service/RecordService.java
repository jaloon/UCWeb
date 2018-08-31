package com.tipray.service;

import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.bean.Record;

import java.util.List;

/**
 * RecordService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface RecordService<T extends Record> {
	/**
	 * 根据Id获取记录
	 * 
	 * @param id
	 * @return
	 */
    T getRecordById(Long id);

	/**
	 * 查询所有的记录列表
	 * 
	 * @param
	 * @return
	 */
    List<T> findAllRecords();

	/**
	 * 获取记录数目
	 * 
	 * @return
	 */
    long countRecord(T record);

	/**
	 * 分页查询记录
	 * 
	 * @param record
	 * @param page
	 * @return
	 */
    List<T> findByPage(T record, Page page);

	/**
	 * 分页查询记录
	 * 
	 * @param record
	 * @param page
	 * @return
	 */
    GridPage<T> findRecordsForPage(T record, Page page);
}
