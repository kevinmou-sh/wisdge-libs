package com.wisdge.ezcell.analysis.v07;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import com.wisdge.ezcell.annotation.FieldType;
import com.wisdge.ezcell.context.AnalysisContext;
import com.wisdge.ezcell.event.AnalysisEventRegisterCenter;
import com.wisdge.ezcell.exception.SAXTerminatorException;
import com.wisdge.ezcell.utils.PositionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.wisdge.ezcell.analysis.v07.ExcelXmlConstants.*;

@Slf4j
public class XlsxRowHandler extends DefaultHandler {
	private final DataFormatter formatter = new DataFormatter();

	private SharedStringsTable sharedStringsTable;
	private StylesTable stylesTable;
	private AnalysisContext analysisContext;
	private AnalysisEventRegisterCenter registerCenter;
	private int curRow;
	private int curCol;
	private List<Object> curRowContent = new ArrayList<>();
	private String curPosition;
	private String curContent;
	private FieldType curType;
	private short formatIndex;
	private String formatString;
	private int realRows = 0;

	public XlsxRowHandler(AnalysisEventRegisterCenter registerCenter, SharedStringsTable sharedStringsTable, StylesTable stylesTable, AnalysisContext analysisContext) {
		this.registerCenter = registerCenter;
		this.analysisContext = analysisContext;
		this.sharedStringsTable = sharedStringsTable;
		this.stylesTable = stylesTable;
	}

	@Override
	public void startElement(String uri, String localName, String tagName, Attributes attributes) throws SAXException {
		// 获得SHEET的内容区域
		if (DIMENSION.equals(tagName)) {
			String[] items = attributes.getValue(DIMENSION_REF).split(":");
			if (items.length > 1) {
				// 空的Sheet中没有dimension的完整信息，通常为"A1"
				String endCell = items[1].toUpperCase();
				// 计算Sheet的行数
				analysisContext.setTotalRows(PositionUtils.getRow(endCell));
				// 计算Sheet的总列数
				analysisContext.setTotalCols(PositionUtils.getCol(endCell));
			}
		} else if (CELL_TAG.equals(tagName)) {
			// 获取当前列坐标
			curPosition = attributes.getValue(POSITION);
			int nextRow = PositionUtils.getRow(curPosition) - 1;
			if (nextRow > curRow) {
				curRow = nextRow;
			}
			analysisContext.setCurrentRowNo(curRow);

			// 添加空列值
			curCol = PositionUtils.getCol(curPosition) - 1;
			for(int i = curRowContent.size(); i < curCol; i ++) {
				curRowContent.add(null);
			}

			// 获取当前单元格格式
			setCellType(attributes);
		} else if (tagName.equals(CELL_VALUE_TAG) || tagName.equals(CELL_VALUE_TAG_1)) {
			// initialize current cell value
			curContent = "";
		}
	}

	@Override
	public void endElement(String uri, String localName, String tagName) throws SAXException {
		if (ROW_TAG.equals(tagName)) {
			// 当前行数据读取完毕
			registerCenter.notifyListeners(curRowContent);
			curRowContent.clear();

			realRows ++;
			Map<String, Object> custom = analysisContext.getCustom();
			if (custom != null && custom.containsKey("previewSize")) {
				int previewSize = (int) custom.get("previewSize");
				if (previewSize > 0 && realRows >= previewSize)
					throw new SAXTerminatorException();
			}

		} else if (CELL_VALUE_TAG.equals(tagName)) {
			// Process the last contents as required.
			curRowContent.add(getDataValue(curContent.trim()));
		} else if (CELL_VALUE_TAG_1.equals(tagName)) {
			curRowContent.add(curContent.trim());
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		curContent += new String(ch, start, length);
	}

	/**
	 * 根据element属性设置数据类型
	 * @param attributes
	 */
	public void setCellType(Attributes attributes){
		curType = FieldType.NUMERIC;
		formatIndex = -1;
		formatString = null;
		String cellTypeStr = attributes.getValue("t");
		String cellStyleStr = attributes.getValue("s");
		if (cellTypeStr != null) {
			switch(cellTypeStr) {
			case "b":
				curType = FieldType.BOOLEAN;
				break;
			case "e":
				curType = FieldType.ERROR;
				break;
			case "inlineStr":
				curType = FieldType.INLINESTR;
				break;
			case "s":
				curType = FieldType.SSTINDEX;
				break;
			case "str":
				curType = FieldType.FORMULA;
				break;
			}
		}

		if (curType != FieldType.SSTINDEX && cellStyleStr != null){
			XSSFCellStyle style = stylesTable.getStyleAt(Integer.parseInt(cellStyleStr));
			formatIndex = style.getDataFormat();
			formatString = style.getDataFormatString();
			if (formatString == null){
				curType = FieldType.NULL;
				formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
			} else {
				if (formatString.contains("yy") || formatString.contains("mm")) {
					// 日期时间格式
					curType = FieldType.DATE;
					//System.out.println(curPosition + ": " + formatString);
					formatString = formatString.replace("m/d/yy", "yyyy-MM-dd");
				} else {
					// 数字格式
				}
			}
		}
	}

	/**
	 * 根据数据类型获取数据
	 * @param value
	 * @return Object
	 */
	public Object getDataValue(String value) {
		Object result = value;
		switch (curType) {
		//这几个的顺序不能随便交换，交换了很可能会导致数据错误
		case BOOLEAN:
			char first = value.charAt(0);
			result = (first != '0');
			break;
		case ERROR:
			result = "\"ERROR:" + value.toString() + '"';
			break;
		case FORMULA:
			// formula存在在<f/>标签内
			break;
		case INLINESTR:
			result = new XSSFRichTextString(value).toString();
			break;
		case SSTINDEX:
			result = sharedStringsTable.getItemAt(Integer.parseInt(value)).toString();
			break;
		case NUMERIC:
//			if (formatString != null) {
//				value = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
//			}
//			//value = value.replace("_", "");
			if (value.indexOf(".") == -1)
				result = Long.parseLong(value);
			else
				result = Double.parseDouble(value);
			break;
		case DATE:
			//System.out.println(curPosition + ": " + value);
			try {
				if (isNumeric(value))
					result = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
				else
					result = value;
			} catch(NumberFormatException e) {
				log.error(e.getMessage(), e);
				result = value;
			}
			// result = value.replace(" ", "");
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * 数据格式校验
	 *
	 * @param str
	 *            验证的字符串对象
	 * @return true or false
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("(\\-|)\\d+(\\.\\d+|)");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

}
