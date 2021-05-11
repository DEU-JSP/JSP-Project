<%--
  Created by IntelliJ IDEA.
  User: hwangjeongho
  Date: 2021/05/11
  Time: 11:41 오후
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"  errorPage="addrbook_error.jsp" import ="cse.maven_webmail.*, java.util.*"%>
<%@ page import="cse.maven_webmail.control.AddrBook" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<% request.setCharacterEncoding("utf-8"); %>

<jsp:useBean id="ab" scope="page" class="cse.maven_webmail.control.AddrBean"/>
<jsp:useBean id="addrbook" class="cse.maven_webmail.control.AddrBook"/>
<jsp:setProperty name="addrbook" property="*"/>
<%
    // 컨트롤러 요청 파라미터
    String action = request.getParameter("action");

    // 파라미터에 따른 요청 처리
    // 주소록 목록 요청인 경우

    if(action.equals("list")) {

		 ArrayList<AddrBook> datas = ab.getDBList();
		request.setAttribute("datas", datas);
		pageContext.forward("addrbook_list2.jsp");

    }
    // 주소록 등록 요청인 경우
    else if(action.equals("insert")) {
	 	if(ab.insertDB(addrbook)) {
			response.sendRedirect("addrbook_control.jsp?action=list");
		}
		else
			throw new Exception("DB 입력오류");

    }
    // 주소록 수정 페이지 요청인 경우
    else if(action.equals("edit")) {
	 	AddrBook abook = ab.getDB(addrbook.getAb_id());
		if(!request.getParameter("upasswd").equals("1234")) {
			out.println("<script>alert('비밀번호가 틀렸습니다.!!');history.go(-1);</script>");
		}
		else {
			request.setAttribute("ab",abook);
			pageContext.forward("addrbook_edit_form.jsp");
		}

    }
    // 주소록 수정 등록 요청인 경우
    else if(action.equals("update")) {
			if(ab.updateDB(addrbook)) {
				response.sendRedirect("addrbook_control.jsp?action=list");
			}
			else
				throw new Exception("DB 갱신오류");

    }
    // 주소록 삭제 요청인 경우
    else if(action.equals("delete")) {
		if(ab.deleteDB(addrbook.getAb_id())) {
			response.sendRedirect("addrbook_control.jsp?action=list");
		}
		else
			throw new Exception("DB 삭제 오류");
	}

	else {
            out.println("<script>alert('action 파라미터를 확인해 주세요!!!')</script>");

        }
%>