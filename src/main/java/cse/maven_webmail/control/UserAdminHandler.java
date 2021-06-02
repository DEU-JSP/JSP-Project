/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cse.maven_webmail.model.UserAdminAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 관리자- 사용자 추가/ 사용자- 회원가입
 * @author jongmin
 */
public class UserAdminHandler extends HttpServlet {
    private static String userId;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminHandler.class);
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = null;

        HttpSession session = request.getSession();
        userId = (String)session.getAttribute("userid");


        try {
            request.setCharacterEncoding("UTF-8");
            var select = Integer.parseInt(request.getParameter("menu"));

            switch (select) {
                case CommandTypeHelper.ADD_USER_COMMAND:
                    out = response.getWriter();
                    addUser(request, response, out);
                    break;

                case CommandTypeHelper.DELETE_USER_COMMAND:
                    out = response.getWriter();
                    deleteUser(request, response, out);
                    break;

                default:
                    out = response.getWriter();
                    out.println("없는 메뉴를 선택하셨습니다. 어떻게 이 곳에 들어오셨나요?");
                    break;
            }
        } catch (Exception ex) {
            LOGGER.info("LOGGER : {}",ex.toString());
        }
        if(out != null)
            out.close();

    }

    private void addUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        var server = "localhost";
        var port = 4555;
        try {
            var agent = new UserAdminAgent(server, port, this.getServletContext().getRealPath("."));
            String userid = request.getParameter("id");  // for test
            String password = request.getParameter("password");// for test


            if (agent.addUser(userid, password)) {
                if (userId == null || !userId.equals("admin")) {
                    out.println(getUserJoinSuccessPopUp());
                }else{
                    out.println(getUserRegistrationSuccessPopUp());
                }
            } else {
                if (userId == null || !userId.equals("admin")) {
                    out.println(getUserJoinFailurePopUp());
                }else{
                    out.println(getUserRegistrationFailurePopUp());
                }
            }
        } catch (Exception ex) {
            out.println("시스템 접속에 실패했습니다.");
        }
    }
    private String getUserJoinSuccessPopUp() {
        var alertMessage = "회원가입을 성공했습니다.";
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
        successPopUp.append("window.location = \"index.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    private String getUserRegistrationSuccessPopUp() {
        var alertMessage = "사용자 등록이 성공했습니다.";
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
        successPopUp.append("window.location = \"admin_menu.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }
    private String getUserJoinFailurePopUp() {
        var alertMessage = "회원가입에 실패했습니다.";
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
        successPopUp.append("window.location = \"index.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    private String getUserRegistrationFailurePopUp() {
        var alertMessage = "사용자 등록이 실패했습니다.";
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
        successPopUp.append("window.location = \"admin_menu.jsp\"; ");
        successPopUp.append("}  </script>");
        successPopUp.append("</body></html>");
        return successPopUp.toString();
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        var server = "localhost";
        var port = 4555;
        try {
            var agent = new UserAdminAgent(server, port, this.getServletContext().getRealPath("."));
            String[] deleteUserList = request.getParameterValues("selectedUsers");
            agent.deleteUsers(deleteUserList);
            response.sendRedirect("admin_menu.jsp");
        } catch (Exception ex) {
            LOGGER.info(" UserAdminHandler.deleteUser : exception = {}",ex.toString());
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            {
        try {
            processRequest(request, response);
        } catch (ServletException | IOException e) {
            LOGGER.info(e.toString());
        }
            }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
             {
                 try {
                     processRequest(request, response);
                 } catch (ServletException | IOException e) {
                     LOGGER.info(e.toString());
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