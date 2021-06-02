/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.PrintWriter;
import java.util.Objects;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cse.maven_webmail.util.FormParser;
import cse.maven_webmail.model.SmtpAgent;

/**
 *
 * @author jongmin
 */
public class WriteMailHandler extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
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
            Objects.requireNonNull(out).println(ex);
        } finally {
            Objects.requireNonNull(out).close();
        }
    }

    private boolean sendMessage(HttpServletRequest request) {
        var status = false;

        // 1. toAddress, ccAddress, subject, body, file1 정보를 파싱하여 추출
        var parser = new FormParser(request);
        parser.parse();

        // 2.  request 객체에서 HttpSession 객체 얻기
        HttpSession session = request.getSession();

        // 3. HttpSession 객체에서 메일 서버, 메일 사용자 ID 정보 얻기
        String host = (String) session.getAttribute("host");
        String userid = (String) session.getAttribute("userid");

        // 4. SmtpAgent 객체에 메일 관련 정보 설정
        var agent = new SmtpAgent(host, userid);
        agent.setTo(parser.getToAddress());
        agent.setCc(parser.getCcAddress());
        agent.setSubj(parser.getSubject());
        agent.setBody(parser.getBody());
        String fileName = parser.getFileName();
        if (fileName != null) {
            agent.setFile1(fileName);
        }

        // 5. 메일 전송 권한 위임
        if (agent.sendMessage()) {
            status = true;
        }
        return status;
    }  // sendMessage()

    private String getMailTransportPopUp(boolean success) {
        String alertMessage;
        if (success) {
            alertMessage = "메일 전송이 성공했습니다.";
        } else {
            alertMessage = "메일 전송이 실패했습니다.";
        }

        return "<html>" +
                "<head>" +
                "<title>메일 전송 결과</title>" +
                "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main_style.css\" />" +
                "</head>" +
                "<body onload=\"goMainMenu()\">" +
                "<script type=\"text/javascript\">" +
                "function goMainMenu() {" +
                "alert(\"" +
                alertMessage +
                "\"); " +
                "window.location = \"main_menu.jsp\"; " +
                "}  </script>" +
                "</body></html>";
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);


    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);


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
