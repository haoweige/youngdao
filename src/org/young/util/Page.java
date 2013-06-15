package org.young.util;

public class Page {

	private static final long serialVersionUID = 1L;

	private int pageIndex;
	private int recCount;
	private int pageSize;
	private int pageCount;
	private int pageStart;
	private int pageEnd;

	public Page() {
		pageSize = 10;
		pageIndex = 1;
	}

	public int getPageStart() {
		pageStart = Math.max(1, getPageIndex() - 4);
		return pageStart;
	}

	public int getPageEnd() {
		pageEnd = Math.min(getPageCount(), getPageIndex() + 4);
		return pageEnd;
	}

	public void setRecCount(int recCount) {
		this.recCount = recCount;
		if (recCount == 0) {
			pageCount = 1;
			setPageIndex(1);
			return;
		}
		if (recCount % pageSize == 0) {
			pageCount = recCount / pageSize;
		} else {
			pageCount = recCount / pageSize + 1;
		}
	}

	public int getRecCount() {
		return recCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageCount() {
		return pageCount;
	}
}
