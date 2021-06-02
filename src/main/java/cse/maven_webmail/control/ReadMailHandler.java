/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cse.maven_webmail.model.Pop3Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jongmin
 */
public class ReadMailHandler extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadMailHandler.class);

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        request.setCharacterEncoding("UTF-8");
        var select = Integer.parseInt(request.getParameter("menu"));

        switch (select) {
            case CommandTypeHelper.DELETE_MAIL_COMMAND:
                deleteMessage(request);
                response.sendRedirect("main_menu.jsp");
                break;

            case CommandTypeHelper.DOWNLOAD_COMMAND: // 파일 다운로드 처리
                download(request, response);
                break;

            default:
                try (PrintWriter out = response.getWriter()) {
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                }
                break;

        }
    }

    private void download(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/octet-stream");

        ServletOutputStream sos;

        try {

            request.setCharacterEncoding("UTF-8");
            // LJM 041203 - 아래와 같이 해서 한글파일명 제대로 인식되는 것 확인했음.
            String fileName = request.getParameter("filename");

            String userid = request.getParameter("userid");

            // download할 파일 읽기

            // 윈도우즈 환경 사용시
            var downloadDir = "C:/temp/download/";
            if (System.getProperty("os.name").equals("Linux")) {
                downloadDir = request.getServletContext().getRealPath("/WEB-INF") 
                        + File.separator + "download";
                var f = new File(downloadDir);
                if (!f.exists()) {
                    f.mkdir();
                }
            }

            response.setHeader("Content-Disposition", "attachment; filename="
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ";");

            var f = new File(downloadDir + File.separator + userid + File.separator + fileName);
            var b = new byte[(int) f.length()];
            // try-with-resource 문은 fis를 명시적으로 close해 주지 않아도 됨.
            try (var fis = new FileInputStream(f)) {
                var count = fis.read(b);
                LOGGER.info("fls.read : {}",count);
                sos = response.getOutputStream();
                sos.write(b);
                sos.flush();
                sos.close();
            }

            // 다운로드

        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }
    }

    private void deleteMessage(HttpServletRequest request) {
        var msgid = Integer.parseInt(request.getParameter("msgid"));

        var httpSession = request.getSession();
        String host = (String) httpSession.getAttribute("host");
        String userid = (String) httpSession.getAttribute("userid");
        String password = (String) httpSession.getAttribute("password");

        var pop3 = new Pop3Agent(host, userid, password);
        pop3.deleteMessage(msgid);
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
             {
                 try {
                     processRequest(request, response);
                 } catch (IOException e) {
                     LOGGER.error(e.toString());
                 }
             }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
             {
                 try {
                     processRequest(request, response);
                 } catch (IOException e) {
                     LOGGER.error(e.toString());
                 }
             }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
