/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cse.maven_webmail.model.UserAdminAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jongmin
 */
public class UserAdminHandler extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminHandler.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            String userid = (String) session.getAttribute("userid");
            if (userid == null || !userid.equals("admin")) {
                out.println("현재 사용자(" + userid + ")의 권한으로 수행 불가합니다.");
                out.println("<a href=/WebMailSystem/> 초기 화면으로 이동 </a>");
            } else {

                request.setCharacterEncoding("UTF-8");
                var select = Integer.parseInt(request.getParameter("menu"));

                switch (select) {
                    case CommandTypeHelper.ADD_USER_COMMAND:
                        addUser(request, out);
                        break;

                    case CommandTypeHelper.DELETE_USER_COMMAND:
                        deleteUser(request, response);
                        break;

                    default:
                        out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                        break;
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }
    }

    private void addUser(HttpServletRequest request, PrintWriter out) {
        var server = "127.0.0.1";
        var port = 4555;
        try {
            var agent = new UserAdminAgent(server, port, this.getServletContext().getRealPath("."));
            String userid = request.getParameter("id");  // for test
            String password = request.getParameter("password");// for test
            out.println("userid = " + userid + "<br>");
            out.println("password = " + password + "<br>");
            out.flush();
            // else 사용자 등록 실패 팝업창
            if (agent.addUser(userid, password)) {
                out.println(getUserRegistrationSuccessPopUp());
            } else {
                out.println(getUserRegistrationFailurePopUp());
            }
            out.flush();
        } catch (Exception ex) {
            out.println("시스템 접속에 실패했습니다.");
        }
    }

    private String getUserRegistrationSuccessPopUp() {
        var alertMessage = "사용자 등록이 성공했습니다.";

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
                "window.location = \"admin_menu.jsp\"; " +
                "}  </script>" +
                "</body></html>";
    }

    private String getUserRegistrationFailurePopUp() {
        var alertMessage = "사용자 등록이 실패했습니다.";

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
                "window.location = \"admin_menu.jsp\"; " +
                "}  </script>" +
                "</body></html>";
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response) {
        var server = "127.0.0.1";
        var port = 4555;
        try {
            var agent = new UserAdminAgent(server, port, this.getServletContext().getRealPath("."));
            String[] deleteUserList = request.getParameterValues("selectedUsers");
            agent.deleteUsers(deleteUserList);
            response.sendRedirect("admin_menu.jsp");
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
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
