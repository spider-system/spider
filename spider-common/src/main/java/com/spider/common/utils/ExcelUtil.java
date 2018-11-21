package com.spider.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by wangchangpeng on 2018/11/21.
 */
public class ExcelUtil {

    public static HSSFWorkbook saveExcel(List<List<String>> data) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createSheet("sheet1");
        // 设置字体
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 16);
        // 添加样式到工作区
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);

        // 创建工作表
        HSSFSheet sheet = workbook.getSheet("sheet1");
        sheet.setAutobreaks(true);
        for (int i = 0; i < data.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < data.get(i).size(); j++) {
                String value = data.get(i).get(j);
                if (StringUtils.isNotBlank(value)) {
                    HSSFCell cell = row.createCell(j);

                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);// 设置单元格为文本类型

                    cell.setCellValue(value);
                }
            }
        }
        return workbook;
    }

    /**
     * 读取模版文件写数据
     *
     * @param data
     * @param templateStream
     * @return
     * @throws IOException
     */
    public static HSSFWorkbook saveExcel(List<List<String>> data,
                                         InputStream templateStream) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(templateStream);
        HSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i < data.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < data.get(i).size(); j++) {
                String value = data.get(i).get(j);
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(value);
            }
        }
        return workbook;
    }

    public static HSSFWorkbook saveCrmCheckbookExcel(List<List<String>> data,
                                                     List<List<String>> total,
                                                     InputStream templateStream)
            throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook(templateStream);
        HSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i < data.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < data.get(i).size(); j++) {
                String value = data.get(i).get(j);
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(value);
            }
        }
        sheet = workbook.getSheetAt(1);
        for (int i = 1; i < total.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < total.get(i).size(); j++) {
                String value = total.get(i).get(j);
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(value);
            }
        }
        return workbook;
    }

    public static HSSFWorkbook saveMealExcel(List<List<String>> data) {
        HSSFWorkbook workbook = saveExcel(data);

        HSSFRow row = workbook.getSheetAt(0).getRow(1);

        //设置列宽
        for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
            workbook.getSheetAt(0).autoSizeColumn(colNum);
        }
        return workbook;
    }

    public static HSSFWorkbook saveExcel(HSSFWorkbook workbook,
                                         List<List<String>> data) {
        workbook.createSheet("sheet1");
        // 设置字体
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 16);
        // 添加样式到工作区
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);

        //设置CELL格式为文本格式
        HSSFDataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("@"));
        // 创建工作表
        HSSFSheet sheet = workbook.getSheet("sheet1");
        sheet.setAutobreaks(true);
        for (int i = 0; i < data.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            for (int j = 0; j < data.get(i).size(); j++) {
                String value = data.get(i).get(j);
                if (StringUtils.isNotBlank(value)) {
                    HSSFCell cell = row.createCell(j);
                    cell.setCellStyle(style);
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);// 设置单元格为文本类型
                    cell.setCellValue(value);
                }
            }
        }
        return workbook;
    }

    public static List<List<String>> analyzeExcel(String filename)
            throws FileNotFoundException {
        InputStream is = new FileInputStream(new File(filename));
        Workbook workbook = null;
        try {
            workbook = new HSSFWorkbook(is);
        } catch (Exception e) {
            try {
                workbook = new XSSFWorkbook(is);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return analyzeExcel(workbook);
    }

    public static List<List<String>> analyzeExcel(InputStream is) throws FileNotFoundException {
        Workbook workbook = null;
        try {
            workbook = new HSSFWorkbook(is);
        } catch (Exception e) {
            try {
                workbook = new XSSFWorkbook(is);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return analyzeExcel(workbook);
    }

    public static List<List<String>> analyzeExcel(File file) throws InvalidFormatException, IOException {
        Workbook workbook = WorkbookFactory.create(file);
        return analyzeExcel(workbook);
    }

    /**
     * 读excel，返回List<List<String>>, 外面list表示行,里面list表示列
     *
     * @param workbook
     * @return
     */
    public static List<List<String>> analyzeExcel(Workbook workbook) {
        if (workbook == null)
            return null;
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();
        if (rows > 0) {
            List<List<String>> data = new ArrayList<List<String>>(rows);
            for (int i = 0; i < rows; i++) {
                List<String> cellData = new ArrayList<String>();
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;
                short first = row.getFirstCellNum();
                short last = row.getLastCellNum();
                if (first > 0) {
                    for (short j = 0; j < first; j++) {
                        cellData.add(null);
                    }
                }

                for (short j = first; j < last; j++) {
                    Cell cell = row.getCell(j);
                    String cellValue = null;
                    if (cell != null) {
                        cellValue = getCellStringValue(cell);
                    }
                    cellData.add(cellValue);
                }
                data.add(cellData);
            }
            return data;
        }

        return null;
    }

    private static String getCellStringValue(Cell cell) {
        String cellValue = null;
        if (cell == null)
            return null;
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case HSSFCell.CELL_TYPE_FORMULA:
                cellValue = getFormulaValue(cell);
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                cellValue = null;
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                break;
            default:
                break;
        }
        return cellValue;
    }

    // 创建Excel单元格
    public static HSSFCell createCell(HSSFRow row, int column, HSSFCellStyle style, int cellType, Object value) {
        HSSFCell cell = row.createCell(column);
        if (style != null) {
            style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            cell.setCellStyle(style);
        }
        switch (cellType) {
            case HSSFCell.CELL_TYPE_BLANK: {
                break;
            }
            case HSSFCell.CELL_TYPE_STRING: {
                cell.setCellValue(value == null ? StringUtils.EMPTY : value.toString());

                break;
            }
            case HSSFCell.CELL_TYPE_NUMERIC: {
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue(Double.parseDouble(value.toString()));
                break;
            }
            default:
                break;
        }

        return cell;
    }

    public static HSSFColor setColor(HSSFWorkbook workbook, byte r, byte g, byte b) {
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFColor hssfColor = palette.findColor(r, g, b);
        if (hssfColor == null) {
            palette.setColorAtIndex(HSSFColor.LAVENDER.index, r, g, b);
            hssfColor = palette.getColor(HSSFColor.LAVENDER.index);
        }
        return hssfColor;
    }

    /**
     * 获取函数公式值
     * @param cell 单元格
     * @return
     */
    private static String getFormulaValue(Cell cell){
        String value = null;
        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        CellValue cellValue = evaluator.evaluate(cell);
        switch (cellValue.getCellType()){
            case HSSFCell.CELL_TYPE_STRING:
                value = cellValue.getStringValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                value = String.valueOf(cellValue.getNumberValue());
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                value = null;
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                break;
            default:
                break;
        }
        return value;
    }
}
