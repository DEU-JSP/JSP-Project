/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cse.maven_webmail.model.Pop3Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jongmin
 */
public final class LoginHandler extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     */
    private static final String ADMINISTRATOR = "admin";
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        var selectedMenu = Integer.parseInt(request.getParameter("menu"));


        try {
            switch (selectedMenu) {
                case CommandTypeHelper.LOGIN:
                    final var host = (String) request.getSession().getAttribute("host");
                    final var userid = request.getParameter("userid");
                    final var password = request.getParameter("passwd");

                    // Check the login information is valid using <<model>>Pop3Agent.
                    var pop3Agent = new Pop3Agent(host, userid, password);
                    boolean isLoginSuccess = pop3Agent.validate();

                    // Now call the correct page according to its validation result.
                    if (isLoginSuccess) {
                        if (isAdmin(userid)) {
                            // HttpSession 객체에 userid를 등록해 둔다.
                            session.setAttribute("userid", userid);
                            response.sendRedirect("admin_menu.jsp");
                        } else {
                            // HttpSession 객체에 userid와 password를 등록해 둔다.
                            session.setAttribute("userid", userid);
                            session.setAttribute("password", password);
                            response.sendRedirect("main_menu.jsp");
                        }
                    } else {
                        var view = request.getRequestDispatcher("login_fail.jsp");
                        view.forward(request, response);
                    }
                    break;
                case CommandTypeHelper.LOGOUT:
                    out = response.getWriter();
                    session.invalidate();
                    response.sendRedirect(getServletContext().getInitParameter("HomeDirectory"));
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        } finally {
            out.close();
        }
    }

    protected boolean isAdmin(String userid) {

        return userid.equals(ADMINISTRATOR);
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
