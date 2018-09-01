package PSM.Tool;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class ExcelToPdf {
	
//	public static void excel2pdf(String source, String target) {  
//        System.out.println("启动Excel");  
//        long start = System.currentTimeMillis();  
//        ActiveXComponent app = new ActiveXComponent("Excel.Application"); // 启动excel(Excel.Application)  
//        try {  
//        app.setProperty("Visible", false);  
//        Dispatch workbooks = app.getProperty("Workbooks").toDispatch();  
//        System.out.println("打开文档" + source);  
//        Dispatch workbook = Dispatch.invoke(workbooks, "Open", Dispatch.Method, new Object[]{source, new Variant(false),new Variant(false)}, new int[3]).toDispatch();  
//        Dispatch.invoke(workbook, "SaveAs", Dispatch.Method, new Object[] {  
//        target, new Variant(57), new Variant(false),  
//        new Variant(57), new Variant(57), new Variant(false),  
//        new Variant(true), new Variant(57), new Variant(true),  
//        new Variant(true), new Variant(true) }, new int[1]);  
//        Variant f = new Variant(false);  
//        System.out.println("转换文档到PDF " + target);  
//        Dispatch.call(workbook, "Close", f);  
//        long end = System.currentTimeMillis();  
//        System.out.println("转换完成..用时：" + (end - start) + "ms.");  
//        } catch (Exception e) { 
//        	
//        	e.printStackTrace();
//            System.out.println("========Error:文档转换失败：" + e.getMessage());
//            
//        }finally {  
//            if (app != null){  
//                app.invoke("Quit", new Variant[] {});  
//            }  
//        }  
//    }  

    private static final String SUFFIX_DOC = ".doc";
    private static final String SUFFIX_DOCX = ".docx";
    private static final String SUFFIX_XLS = ".xls";
    private static final String SUFFIX_XLSX = ".xlsx";
    private static final int wdFormatPDF = 17;
    private static final int xlTypePDF = 0;
    private static final String OFFICE_VISIBLE = "Visible";
    private static final String OFFICE_OPEN = "Open";
    private static final String OFFICE_CLOSE = "Close";
    private static final String OFFICE_EXPORT = "ExportAsFixedFormat";
    private static final String OFFICE_QUIT = "Quit";
    
	public static void excel2pdf(String inputFile, String pdfFile) {
        
		ActiveXComponent app = null;
        try {
           
            boolean isExcel = false;
            String programId = "";
            String propertyName = "";

//            if (SUFFIX_DOC.equals(suffix) || SUFFIX_DOCX.equals(suffix)) {
//                programId = "Word.Application";
//                propertyName = "Documents";
//            } else if (SUFFIX_XLS.equals(suffix) || SUFFIX_XLSX.equals(suffix)) {
//                programId = "Excel.Application";
//                propertyName = "Workbooks";
//                isExcel = true;
//            } else {
//                throw new Exception("not office file!");
//            }
            
            programId = "Excel.Application";
            propertyName = "Workbooks";
            isExcel = true;

            ComThread.InitSTA();
            app = new ActiveXComponent(programId);
            app.setProperty(OFFICE_VISIBLE, false);
            Dispatch offices = app.getProperty(propertyName).toDispatch();

            Dispatch office = Dispatch.call(offices, OFFICE_OPEN, inputFile, false, true).toDispatch();
            if (isExcel) {
                Dispatch.call(office, OFFICE_EXPORT, new Variant(0), new Variant(pdfFile), new Variant(0),
                        new Variant(false), new Variant(true));
                // Dispatch.call(office, OFFICE_EXPORT, xlTypePDF, pdfFile);
            } else {
                Dispatch.call(office, OFFICE_EXPORT, pdfFile, wdFormatPDF);
            }
            Dispatch.call(office, OFFICE_CLOSE, false);

            if (isExcel) {
                app.invoke(OFFICE_QUIT);
            } else {
                app.invoke(OFFICE_QUIT, 0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ComThread.Release();
            app = null;
        }
    }

	
}
//import java.io.BufferedInputStream;  
//import java.io.File;  
//import java.io.IOException;  
//import java.io.InputStream;  
//  
//
//
//
//import com.artofsolving.jodconverter.DocumentConverter;  
//import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;  
//import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;  
//import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;  
//public class ExcelToPdf{
//	/** 
//     * 转为PDF 
//     *  
//     * @param file 
//     */  
//    private static void doc2pdf(String inFilePath,String outFilePath) {  
//    	File docFile = new File(inFilePath);
//    	File pdfFile = new File(outFilePath);
//    	if (docFile.exists()) {  
//            if (!pdfFile.exists()) {  
//                OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);  
//                try {  
//                    connection.connect();  
//                    DocumentConverter converter = new OpenOfficeDocumentConverter(connection);  
//                    converter.convert(docFile, pdfFile);  
//                    // close the connection  
//                    connection.disconnect();  
//                    System.out.println("****pdf转换成功，PDF输出：" + pdfFile.getPath()+ "****");  
//                } catch (java.net.ConnectException e) {  
//                    e.printStackTrace();  
//                    System.out.println("****swf转换器异常，openoffice服务未启动！****");  
//                    
//                } catch (com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException e) {  
//                    e.printStackTrace();  
//                    System.out.println("****swf转换器异常，读取转换文件失败****");  
//
//                } catch (Exception e) {  
//                    e.printStackTrace();  
//                   
//                }  
//            } else {  
//                System.out.println("****已经转换为pdf，不需要再进行转化****");  
//            }  
//        } else {  
//            System.out.println("****swf转换器异常，需要转换的文档不存在，无法转换****");  
//        }  
//    }  
//    
//    public static void excel2pdf(String inFilePath,String outFilePath) {
//    	 doc2pdf(inFilePath, outFilePath);
//    }
//}