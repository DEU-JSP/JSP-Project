/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 책임: enctype이 multipart/form-data인 HTML 폼에 있는 각 필드와 파일 정보 추출
 *
 * @author jongmin
 */
public class FormParser {

    private HttpServletRequest request;
    private String toAddress = null;
    private String ccAddress = null;
    private String subject = null;
    private String body = null;
    private String fileName = null;
    private String uploadTargetDir = "C:/temp/upload";
    private String bookDate = null;
    private String bookTime = null;
    private static final Logger logger = LoggerFactory.getLogger(FormParser.class);

    public FormParser(HttpServletRequest request) {
        this.request = request;
        if (System.getProperty("os.name").equals("Linux")) {
            uploadTargetDir = request.getServletContext().getRealPath("/WEB-INF")
                    + File.separator + "upload";
            var f = new File(uploadTargetDir);
            if (!f.exists()) {
                f.mkdir();
            }
        }
    }

    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }
    public String getBookTime() {
        return bookTime;
    }

    public void setBookTime(String bookTime) {
        this.bookTime = bookTime;
    }

    public String getBookDateTime(){
        String eBookDate;
        String eBookTime;
        if(this.bookDate.equals("")){
            var form = new Date();
            var format = new SimpleDateFormat("yyyy-MM-dd");
            eBookDate = format.format(form);
        }
        else{
            eBookDate = this.bookDate;
        }

        if(this.bookTime.equals("")){
            var form = new Date();
            var format = new SimpleDateFormat("HH:mm");
            eBookTime = format.format(form);
        }
        else{
            eBookTime = this.bookTime;
        }
        return eBookDate + " " + eBookTime;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCcAddress() {
        return ccAddress;
    }

    public void setCcAddress(String ccAddress) {
        this.ccAddress = ccAddress;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public void parse() {
        try {
            request.setCharacterEncoding("UTF-8");

            // 1. 디스크 기반 파일 항목에 대한 팩토리 생성
            var diskFactory = new DiskFileItemFactory();
            // 2. 팩토리 제한사항 설정
            diskFactory.setSizeThreshold(10 * 1024 * 1024);
            diskFactory.setRepository(new File(this.uploadTargetDir));
            // 3. 파일 업로드 핸들러 생성
            var upload = new ServletFileUpload(diskFactory);

            // 4. request 객체 파싱
            List<FileItem> fileItems = upload.parseRequest(request);
            for (FileItem fi : fileItems) {
                if (fi.isFormField()) {  // 5. 폼 필드 처리
                    var fieldName = fi.getFieldName();
                    var item = fi.getString("UTF-8");

                    switch (fieldName) {
                        case "to":
                            setToAddress(item);  // 200102 LJM - @ 이후의 서버 주소 제거

                            break;
                        case "cc":
                            setCcAddress(item);
                            break;
                        case "subj":
                            setSubject(item);
                            break;
                        case "body":
                            setBody(item);
                            break;
                        case "subdate":
                            setBookDate(item);
                            break;
                        case "subtime":
                            setBookTime(item);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + fieldName);
                    }
                } else {  // 6. 첨부 파일 처리
                    if (!(fi.getName() == null || fi.getName().equals(""))) {
                        String fieldName = fi.getFieldName();
                        logger.info("ATTACHED FILE : {} = {}", fieldName, fi.getName());

                        // 절대 경로 저장
                        setFileName(uploadTargetDir + "/" + fi.getName());
                        var fn = new File(fileName);
                        // upload 완료. 추후 메일 전송후 해당 파일을 삭제하도록 해야 함.
                        if (fileName != null) {
                            fi.write(fn);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.info("FormParser.parse() : exception = {}", ex.toString());
        }
    }  // parse()
}