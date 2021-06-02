/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cse.maven_webmail.util.FormParser;
import cse.maven_webmail.model.SmtpAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author jongmin
 */
public class WriteMailHandler extends HttpServlet {
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WriteMailHandler.class);
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
             {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;

        try {
            request.setCharacterEncoding("UTF-8");
            var select = Integer.parseInt(request.getParameter("menu"));

            if (select == CommandTypeHelper.SEND_MAIL_COMMAND) { // 실제 메일 전송하기
                out = response.getWriter();
                boolean status = sendMessage(request);
                out.print(getMailTransportPopUp(status));
            } else {
                out = response.getWriter();
                out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
            }
        } catch (Exception ex) {
            LOGGER.info("LOGGER : {}",ex.toString());
        }
        if(out != null) {
            out.close();
        }
    }

    private boolean sendMessage(HttpServletRequest request) {
        var status = false;
        Connection connection = null;
        PreparedStatement pstmt = null;

        // 1. toAddress, ccAddress, subject, body, file1 정보를 파싱하여 추출
        var parser = new FormParser(request);
        parser.parse();

        // 2.  request 객체에서 HttpSession 객체 얻기
        HttpSession session = request.getSession();

        // 3. HttpSession 객체에서 메일 서버, 메일 사용자 ID 정보 얻기
        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");
        String fileName = parser.getFileName();

        if (!parser.getBookDate().equals("") || !parser.getBookTime().equals("")) { //예약된 메일일 경우

            try {
                InputStream fileStream = null;
                var fileLength = 0;
                //db연결하는 부분
                connection = getConnection();
                // sql 문자열 , gb_id 는 자동 등록 되므로 입력하지 않는다.
                var sql = "insert into booked_mail (host, userid, toAddress, ccAddress, subj, body, fileName, file, bookTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                if(fileName != null){
                    var file = new File(fileName);
                    fileStream = new FileInputStream(file);
                    fileLength = (int) file.length();
                }
                pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, host);
                pstmt.setString(2, userid);
                pstmt.setString(3, parser.getToAddress());
                pstmt.setString(4, parser.getCcAddress());
                pstmt.setString(5, parser.getSubject());
                pstmt.setString(6, parser.getBody());
                pstmt.setString(7, parser.getFileName());
                pstmt.setBinaryStream(8, fileStream, fileLength);
                pstmt.setString(9, parser.getBookDateTime());

                if (pstmt.executeUpdate() >= 1) {
                    status = true;
                    connection.close();
                }
            }  catch (SQLException | NamingException | FileNotFoundException throwables) {
                LOGGER.error(throwables.toString());
            }
        } else {    //예약된 메일이 아닐경우
            // 4. SmtpAgent 객체에 메일 관련 정보 설정
            var agent = new SmtpAgent(host, userid);
            agent.setTo(parser.getToAddress());
            agent.setCc(parser.getCcAddress());
            agent.setSubj(parser.getSubject());
            agent.setBody(parser.getBody());
            LOGGER.info("WriteMailHandler.sendMessage() : fileName = {}", fileName);
            if (fileName != null) {
                agent.setFile1(fileName);
            }

            // 5. 메일 전송 권한 위임
            if (agent.sendMessage()) {
                status = true;
            }
        }

        return status;
    }  // sendMessage()

    private String getMailTransportPopUp(boolean success) {
        String alertMessage = null;
        if (success) {
            alertMessage = "메일 전송이 성공했습니다.";
        } else {
            alertMessage = "메일 전송이 실패했습니다.";
        }

        var successPopUp = new StringBuilder();
        successPopUp.append("<html>");
        successPopUp.append("<head>");

        successPopUp.append("<title>메일 전송 결과</title>");
        successPopUp.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main_style.css\" />");
        successPopUp.append("</head>");
        successPopUp.append("<body onload=\"goMainMenu()\">");
        successPopUp.append("<script type=\"text/javascript\">");
        successPopUp.append("function goMainMenu() {");
        successPopUp.append("alert(\"");
        successPopUp.append(alertMessage);
        successPopUp.append("\"); ");
        successPopUp.append("window.location = \"main_menu.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }
    private Connection getConnection() throws NamingException, SQLException {
        var name = "java:/comp/env/jdbc/mysqlWebmail";
        var context = new javax.naming.InitialContext();
        var dataSource = (javax.sql.DataSource) context.lookup(name);
        return dataSource.getConnection();
    }
// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
             {
        processRequest(request, response);


    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
             {
        processRequest(request, response);


    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";

    }// </editor-fold>
}