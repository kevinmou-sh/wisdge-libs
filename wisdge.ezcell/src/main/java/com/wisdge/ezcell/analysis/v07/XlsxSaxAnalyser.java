package com.wisdge.ezcell.analysis.v07;

import com.wisdge.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorkbookDocument;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import com.wisdge.ezcell.analysis.BaseSaxAnalyser;
import com.wisdge.ezcell.context.AnalysisContext;
import com.wisdge.ezcell.meta.Sheet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class XlsxSaxAnalyser extends BaseSaxAnalyser {

    private XSSFReader xssfReader;
    private SharedStringsTable sharedStringsTable;
    private StylesTable stylesTable;

    private List<SheetSource> sheetSourceList = new ArrayList<SheetSource>();

    private boolean use1904WindowDate = false;

    public XlsxSaxAnalyser(AnalysisContext analysisContext) throws IOException, OpenXML4JException, XmlException {
        this.analysisContext = analysisContext;
        init();
    }
    
    private void init() throws IOException, OpenXML4JException, XmlException {
        analysisContext.setCurrentRowNo(0);
        xssfReader = new XSSFReader(OPCPackage.open(analysisContext.getInputStream()));
        sharedStringsTable = xssfReader.getSharedStringsTable();
        stylesTable = xssfReader.getStylesTable();

        InputStream workbookXml = xssfReader.getWorkbookData();
        WorkbookDocument ctWorkbook = WorkbookDocument.Factory.parse(workbookXml);
        CTWorkbook wb = ctWorkbook.getWorkbook();
        CTWorkbookPr prefix = wb.getWorkbookPr();
        if (prefix != null) {
            use1904WindowDate = prefix.getDate1904();
        }
        analysisContext.setUse1904WindowDate(use1904WindowDate);

        XSSFReader.SheetIterator ite;
        sheetSourceList = new ArrayList<>();
        ite = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        while (ite.hasNext()) {
            InputStream inputStream = ite.next();
            String sheetName = ite.getSheetName();
            log.debug("Loading sheet source {}", LogUtils.forging(sheetName));
            SheetSource sheetSource = new SheetSource(sheetName, inputStream);
            sheetSourceList.add(sheetSource);
        }
    }

    @Override
    protected void execute() throws Exception {
        Sheet sheetParam = analysisContext.getCurrentSheet();
        if (sheetParam != null && sheetParam.getSheetNo() > 0 && sheetSourceList.size() >= sheetParam.getSheetNo()) {
            SheetSource sheetSource = sheetSourceList.get(sheetParam.getSheetNo() - 1);
            log.debug("Parsing sheet {}", sheetSource.sheetName);
            InputStream sheetInputStream = sheetSource.getInputStream();
            parseXmlSource(sheetInputStream);
        } else {
            int i = 0;
            for (SheetSource sheetSource : sheetSourceList) {
                i++;
                analysisContext.setCurrentSheet(new Sheet(i));
                log.debug("Parsing sheet {}", sheetSource.sheetName);
                parseXmlSource(sheetSource.getInputStream());
            }
        }
    }
    
    private void parseXmlSource(InputStream inputStream) throws Exception {
        InputSource sheetSource = new InputSource(inputStream);
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        saxFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        saxFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        saxFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        SAXParser saxParser = saxFactory.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        ContentHandler handler = new XlsxRowHandler(this, sharedStringsTable, stylesTable, analysisContext);
        xmlReader.setContentHandler(handler);
        xmlReader.parse(sheetSource);
        inputStream.close();
    }

    @Override
    public List<Sheet> getSheets() {
        List<Sheet> sheets = new ArrayList<Sheet>();
        int i = 1;
        for (SheetSource sheetSource : sheetSourceList) {
            Sheet sheet = new Sheet(i, 0);
            sheet.setSheetName(sheetSource.getSheetName());
            i++;
            sheets.add(sheet);
        }

        return sheets;
    }

	@Override
	public AnalysisContext getContext() {
		return this.analysisContext;
	}

    class SheetSource {
        private String sheetName;
        private InputStream inputStream;

        public SheetSource(String sheetName, InputStream inputStream) {
            this.sheetName = sheetName;
            this.inputStream = inputStream;
        }

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }
    }
}
