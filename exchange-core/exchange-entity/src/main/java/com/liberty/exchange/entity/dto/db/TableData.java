package com.liberty.exchange.entity.dto.db;

import java.util.List;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 4:13
 * Description: TableData
 */
public class TableData<T> {
    private long total;
    private List<T> rows;

    public TableData(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public TableData() {
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
