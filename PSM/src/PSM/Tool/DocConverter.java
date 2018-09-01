package PSM.Tool;

import java.io.File;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;  
  
public class DocConverter {
//	private static final int environment = 1;// 环境 1：windows 2:linux  
//    private String fileString;// (只涉及pdf2swf路径问题)  
//    private String outputPath = "";// 输入路径 ，如果不设置就输出在默认的位置  
//    private String fileName;  
//    private File pdfFile;  
//
//    private File docFile;  
//      
//    public DocConverter(String fileString) {  
//        ini(fileString);  
//    }  
//  
//    /** 
//     * 重新设置file 
//     *  
//     * @param fileString 
//     */  
//    public void setFile(String fileString) {  
//        ini(fileString);  
//    }  
//  
//    /** 
//     * 初始化 
//     *  
//     * @param fileString 
//     */  
//    private void ini(String fileString) {  
//        this.fileString = fileString;  
//        fileName = fileString.substring(0, fileString.lastIndexOf("."));  
//        docFile = new File(fileString);  
//        pdfFile = new File(fileName + ".pdf");    
//    }  
//      
    /** 
     * 转为PDF 
     *  
     * @param file 
     */  
    public static void doc2pdf(String inFilePath,String outFilePath) throws Exception {  
    	File docFile = new File(inFilePath);
    	File pdfFile = new File(outFilePath);
    	if (docFile.exists()) {  
            if (!pdfFile.exists()) {  
                OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);  
                try {  
                    connection.connect();  
                    DocumentConverter converter = new OpenOfficeDocumentConverter(connection);  
                    converter.convert(docFile, pdfFile);  
                    // close the connection  
                    connection.disconnect();  
                    System.out.println("****pdf转换成功，PDF输出：" + pdfFile.getPath()+ "****");  
                } catch (java.net.ConnectException e) {  
                    e.printStackTrace();  
                    System.out.println("****swf转换器异常，openoffice服务未启动！****");  
                    throw e;  
                } catch (com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException e) {  
                    e.printStackTrace();  
                    System.out.println("****swf转换器异常，读取转换文件失败****");  
                    throw e;  
                } catch (Exception e) {  
                    e.printStackTrace();  
                    throw e;  
                }  
            } else {  
                System.out.println("****已经转换为pdf，不需要再进行转化****");  
            }  
        } else {  
            System.out.println("****swf转换器异常，需要转换的文档不存在，无法转换****");  
        }  
    }  
    
    public static void excel2pdf(String inFilePath,String outFilePath) throws Exception{
    	 doc2pdf(inFilePath, outFilePath);
    }
}
