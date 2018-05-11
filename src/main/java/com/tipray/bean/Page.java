package com.tipray.bean;

import java.io.Serializable;

/**
 * 分页对象
 * 
 * @author chends
 *
 */
public class Page implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 当前页码 */
	private int pageId = 1;
	/** 每页条数 */
	private int rows = 15;
	/** 起始行 */
	private int startRow;
	/** 排序字段 */
	private String sidx;
	/** 排序方式: asc desc，默认正序ASC */
	private String sord = "asc";

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

	public String getSidx() {
		return sidx;
	}

	public void setSidx(String sidx) {
		this.sidx = sidx;
	}

	public String getSord() {
		return sord;
	}

	public void setSord(String sord) {
		this.sord = sord;
	}

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Page{");
        sb.append("pageId=").append(pageId);
        sb.append(", rows=").append(rows);
        sb.append(", startRow=").append(startRow);
        sb.append(", sidx='").append(sidx).append('\'');
        sb.append(", sord='").append(sord).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
