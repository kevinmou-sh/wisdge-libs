package com.wisdge.commons.poi;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
    private static Map<String, Integer> getCellIndexs (Row rowheader) {
        int totalCells = rowheader.getPhysicalNumberOfCells();//列数
        Map<String, Integer> filedIndexMap = new HashMap<>();
        for (int c = 0; c < totalCells; c++) {
            org.apache.poi.ss.usermodel.Cell cell = rowheader.getCell(c);
            HSSFDataFormatter hSSFDataFormatter = new HSSFDataFormatter();
            String cellValue = hSSFDataFormatter.formatCellValue(cell);
            if (!StringUtils.isEmpty(cellValue)) {
                filedIndexMap.put(cellValue, c);
            }
        }
        return filedIndexMap;
    }

    private static <T> List<T> getObjList (Map<String, List<KeyValue<Row, T>>> successRows) {
        List<T> objList = new ArrayList<>();
        for (Map.Entry<String, List<KeyValue<Row, T>>> entry : successRows.entrySet()) {
            for (KeyValue<Row, T> obj : entry.getValue()) {
                objList.add(obj.getV());
            }
        }
        return objList;
    }

    private static <T> boolean isErrorOrder (List<KeyValue<Row, T>> list) {
        boolean iserr = false;
        for (KeyValue<Row, T> obj : list) {
            if (obj.getV() == null) {
                iserr = true;
                break;
            }
        }
        return iserr;
    }

    private static void delNullRows (Sheet sheet) {
        int i = sheet.getLastRowNum();
        Row tempRow;
        while (i > 0) {
            i--;
            tempRow = sheet.getRow(i);
            if (tempRow == null) {
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
            }
        }
    }

    private static <T> int getErrWorkbook (Map<String, List<KeyValue<Row, T>>> successRows) {
        int errcount=0;
        List<T> objList = new ArrayList<>();
        for (Iterator<Map.Entry<String, List<KeyValue<Row, T>>>> it = successRows.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, List<KeyValue<Row, T>>> item = it.next();
            boolean iserr = isErrorOrder(item.getValue());
            if (iserr) {
                //移除错误的记录
                it.remove();
                errcount=errcount+item.getValue().size();
            } else {
                //在excel中删除正确的记录,只保留错误的记录
                for (int j = 0; j < item.getValue().size(); j++) {
                    Row row = item.getValue().get(j).getK();
                    row.getSheet().removeRow(row);
                }
            }
        }
        return errcount;
    }

    public static <T> Result<T> adapter(Class<T> t, Workbook wb) throws IllegalAccessException, InstantiationException {
        Result<T> result = new Result<>();
        //region 设置错误提示样式
        Sheet sheet = wb.getSheetAt(0);
        Font font = wb.createFont();
        font.setColor(Font.COLOR_RED);
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(font);
        //endregion 样式设置结束
        int totalRows = sheet.getLastRowNum();
        boolean sethint = false;
        if (totalRows >= 1) {
            Row rowheader = sheet.getRow(0);//第一行必须是列名行
            int lastCellNum = rowheader.getLastCellNum();//列数
            Map<String, Integer> filedIndexMap = getCellIndexs(rowheader);
            Field[] fields = t.getDeclaredFields();
            Map<String, List<KeyValue<Row, T>>> successRows = new HashMap<>();
            for (int r = 1; r <= totalRows; r++) {
                Row row = sheet.getRow(r);
                T obj = t.newInstance();
                String primarykey = "";
                String errorMsg = "";
                boolean error = false;
                if (row == null)
                    continue;
                for (Field field : fields) {
                    ExportEntityMap cell = field.getAnnotation(ExportEntityMap.class);
                    if (cell != null) {
                        String cellValue = null;
                        Object indexObj = filedIndexMap.get(cell.CnName());
                        if (indexObj != null) {
                            int index = (int) indexObj;
                            org.apache.poi.ss.usermodel.Cell cell1 = row.getCell(index);
                            HSSFDataFormatter hSSFDataFormatter = new HSSFDataFormatter();
                            cellValue = hSSFDataFormatter.formatCellValue(cell1);
                        }
                        if (cell.primarykey()) {
                            primarykey = primarykey + cellValue;
                        }
                        if ((cell.primarykey() || cell.required()) &&
                                StringUtils.isEmpty(cellValue)
                                ) {
                            errorMsg = errorMsg + cell.CnName() + ",不能为空;";
                            error = true;
                        } else if (cellValue != null) {
                            field.setAccessible(true);
                            field.set(obj, cellValue);
                        }
                    }
                }
                KeyValue<Row, T> keyValue = new KeyValue<>();
                keyValue.setK(row);
                if (!error)
                    keyValue.setV(obj);
                else {
                    sethint = true;
                    org.apache.poi.ss.usermodel.Cell errorCell = row.createCell(lastCellNum);
                    errorCell.setCellValue(errorMsg);
                    errorCell.setCellStyle(cellStyle);
                }
                List<KeyValue<Row, T>> list = successRows.get(primarykey);
                if (list == null) {
                    list = new ArrayList<>();
                    list.add(keyValue);
                    successRows.put(primarykey, list);
                } else {
                    list.add(keyValue);
                }
            }
            if (sethint) {
                //如果存在失败的记录执行
                org.apache.poi.ss.usermodel.Cell errorMsgCell = rowheader.createCell(lastCellNum);
                sheet.setColumnWidth(lastCellNum, 30 * 256);
                errorMsgCell.setCellValue("提示");
                int errcount=getErrWorkbook(successRows);//wb中只保留错误数据
                delNullRows(sheet);//删除空行
                result.setErrWorkbook(wb);//保存错误的wb
                result.setFail(errcount);
            }
            List<T> objList = getObjList(successRows);
            result.setList(objList);
            result.setSuccess(objList.size());
        }
        return result;
    }
    public static <T> Result<T> adapter(Class<T> t, InputStream is) throws IOException, InvalidFormatException, InstantiationException, IllegalAccessException {
        Workbook wb = WorkbookFactory.create(is);
        return adapter(t,wb);
    }
}
