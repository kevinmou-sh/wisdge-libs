package com.wisdge.commons.poi;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public class Result<T> {
    List<T> list;//成功列表
    int success;//成功条数
    int fail;//失败条数
    Workbook errWorkbook;//失败表单
    public List<T> getList () {
        return list;
    }
    public void setList (List<T> list) {
        this.list = list;
    }
    public int getSuccess () {
        return success;
    }

    public void setSuccess (int success) {
        this.success = success;
    }

    public int getFail () {
        return fail;
    }

    public void setFail (int fail) {
        this.fail = fail;
    }

    public Workbook getErrWorkbook () {
        return errWorkbook;
    }

    public void setErrWorkbook (Workbook errWorkbook) {
        this.errWorkbook = errWorkbook;
    }
}