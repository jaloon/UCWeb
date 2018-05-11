package com.tipray.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * jqgrid分页对象
 * 
 * @author chends
 */
@SuppressWarnings("serial")
public class GridPage<T> implements Serializable {
	/** 当前页码数 */
	private int page = 1;
	/** 当前页条数 */
	private int currentRows;
	/** 每页条数 */
	private int pageRows = 10;
	/** 总页码数 */
	private int total = 1;
	/** 总条数 */
	private long records = 1;
	/** 查询条件 */
	private T t;
	/** 数据集合 */
	private List<T> dataList = new ArrayList<T>();

	public GridPage(List<T> list, int records, int page, int pageRows, int currentRows, T t) {
		setRecords(records);
		setDataList(list);
		setPage(page);
		setPageRows(pageRows);
		setCurrentRows(currentRows);
		setT(t);
		if (pageRows == 0) {
			setTotal(1);
		} else if (records % pageRows > 0) {
			setTotal((records / pageRows) + 1);
		} else {
			setTotal(records / pageRows);
		}
	}

	public GridPage(List<T> list, Long records, int page, int pageRows, int currentRows, T t) {
		setRecords(records);
		setDataList(list);
		setPage(page);
		setPageRows(pageRows);
		setCurrentRows(currentRows);
		setT(t);
		if (pageRows == 0) {
			setTotal(1);
		} else if (records % pageRows > 0) {
			setTotal((int) (records / pageRows) + 1);
		} else {
			setTotal((int) (records / pageRows));
		}
	}

	public GridPage() {
		super();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageRows() {
		return pageRows;
	}

	public void setPageRows(int pageRows) {
		this.pageRows = pageRows;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public long getRecords() {
		return records;
	}

	public void setRecords(long records) {
		this.records = records;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public T getT() {
		return t;
	}

	public void setT(T t) {
		this.t = t;
	}

	public int getCurrentRows() {
		return currentRows;
	}

	public void setCurrentRows(int currentRows) {
		this.currentRows = currentRows;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("GridPage{");
		sb.append("page=").append(page);
		sb.append(", currentRows=").append(currentRows);
		sb.append(", pageRows=").append(pageRows);
		sb.append(", total=").append(total);
		sb.append(", records=").append(records);
		sb.append(", t=").append(t);
		sb.append(", dataList=").append(dataList);
		sb.append('}');
		return sb.toString();
	}
}
