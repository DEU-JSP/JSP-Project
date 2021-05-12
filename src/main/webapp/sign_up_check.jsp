<%-- 
   sign_up -> sign_up_check.jsp 로 action 전달 (회원가입 프로세스)
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="user.UserDAO"%>
<%@page import="java.io.PrintWriter"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>회원가입 확인</title>
    </head>
    <body>
        <%
            request.setCharacterEncoding("UTF-8"); //이거 없으면 한글 이름 오류나서 DB들어감
        %>
        <% //form태그로 전송한 값 받기
            String userId = request.getParameter("userId");
            String userPwd = request.getParameter("userPwd");
            String email = request.getParameter("email");
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");

            //컬럼 모두 notnull로 설정되어 있으므로 빈칸이 있으면 경고
            if(userId == null || userPwd == null || email == null || name == null || phone == null){
                PrintWriter script = response.getWriter();
                script.println("<script>");
                script.println("alert('모두 입력해주세요')");
                script.println("history.back()"); //뒤로가기
                script.println("</script>");
            }else{
           
                UserDAO userDAO = new UserDAO();
                
                userDAO.setUserId(userId);
                userDAO.setUserPwd(userPwd);
                userDAO.setEmail(email);
                userDAO.setName(name);
                userDAO.setPhone(phone);
                
                int result = userDAO.signup(userDAO); //회원가입 결과 가져온 정수
                // 1: 성공, 0: 중복되는 아이디 -1: 오류
                if(result == -1){
                    PrintWriter script = response.getWriter();
                    script.println("<script>");
                    script.println("alert('서버오류')");
                    script.println("history.back()");
                    script.println("</script>");
                }else if(result == 0){
                    PrintWriter script = response.getWriter();
                    script.println("<script>");
                    script.println("alert('이미 존재하는 아이디입니다.')");
                    script.println("history.back()");
                    script.println("</script>");
                }else{
                    PrintWriter script = response.getWriter();
                    script.println("<script>");
                     script.println("alert('회원가입에 성공하였습니다.')");
                    script.println("location.href='index.jsp'");
                    script.println("</script>");
                }
            }
         %>
    </body>
</html>
