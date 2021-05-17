/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.control;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * UserAdminAgent를 이용한 조회와 삭제의 오류로, 
 * Beans/JSTL을 사용하여 해결하기 위하여 새로운 클래스 생성 (Admin)
 *
 * @author chae
 */
//deleteusers.tag에서 사용하는 액션
@WebServlet(name = "Delete", urlPatterns = {"/Delete"}) //삭제 액션
public class DeleteHandler extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            final String JdbcDriver = "com.mysql.cj.jdbc.Driver";
            final String JdbcUrl = "jdbc:mysql://172.20.16.1:3306/webmail?serverTimezone=Asia/Seoul";
            final String User = "james";
            final String Password = "chaechae";

            try {
                // 1. JDBC 드라이버 적재
                Class.forName(JdbcDriver);

                // 2. Connection 객체 생성
                Connection conn = DriverManager.getConnection(JdbcUrl, User, Password);

                // 3. PreparedStatement 객체 생성
                String sql = "delete from users where username=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);

                // 4. SQL 문 완성
                request.setCharacterEncoding("UTF-8");  // 한글 인식

                String username = request.getParameter("username");
                if (!(username == null) && !username.equals("")) {
                    pstmt.setString(1, username);

                    // 5. 실행 
                    pstmt.executeUpdate();
                }
                // 6. 자원 해제
                pstmt.close();
                conn.close();

                response.sendRedirect("delete_user.jsp");

            } catch (IOException | ClassNotFoundException | SQLException ex) {
                out.println("오류가 발생했습니다. (발생 오류 : " + ex.getMessage()
                        + ")");
                out.println("<br/><a href=\"index.jsp\">초기 화면</a>");
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
