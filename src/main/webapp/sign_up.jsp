<%-- 
    Document   : sign_up
    Created on : 2021.05.05
    Author     : chae
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>회원가입</title>
        
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
<script>
        function check_pw(){
            var pw = document.getElementById('userPwd').value;
            var SC = ["!","@","#","$","%","^","&","*"];
            var check_SC = 0;
 
            if(pw.length < 6 || pw.length>16){
                window.alert('비밀번호는 6글자 이상, 16글자 이하만 이용 가능합니다.');
                document.getElementById('userPwd').value='';
            }
            for(var i=0;i<SC.length;i++){
                if(pw.indexOf(SC[i]) != -1){
                    check_SC = 1;
                }
            }
            if(check_SC == 0){
                window.alert('특수문자가 포함되어 있지 않습니다.')
                document.getElementById('userPwd').value='';
            }
            if(document.getElementById('userPwd').value !='' && document.getElementById('repwd').value!=''){
                if(document.getElementById('userPwd').value==document.getElementById('repwd').value){
                    document.getElementById('check').innerHTML='비밀번호가 일치합니다.'
                    document.getElementById('check').style.color='blue';
                }
                else{
                    document.getElementById('check').innerHTML='비밀번호가 일치하지 않습니다.';
                    document.getElementById('check').style.color='red';
                }
            }
        }
    </script>

     
    </head>
    <body>
        <jsp:include page="header.jsp" />
        <div align="center">
            <br />
            <form method="post" action="sign_up_check.jsp">
            <table align="center" border="0">
                <tr>
                    <td align="center" valign="middle">
                        <table border="1" align="center" width ="600">
                               
                            <tr align="center">
                                <td colspan="3"><font color="#000000"><b>회원 가입</b></font></td>
                            </tr>


                            <tr>
                                <td width="20%">아이디</td>
                                <td width="50%"><input name="userId" id="userId" size="15">
                                 
                            </tr>

                            <tr>
                                <td>비밀번호</td>
                                <td><input type="password" name="userPwd" id="userPwd" size="20" onchange="check_pw()"></td>
                            </tr>

                            <tr>
                                <td>비밀번호 확인</td>
                                <td><input type="password" name="repwd" id="repwd" size="20" onchange="check_pw()">&nbsp;<span id="check"></span></td>
                            </tr>

                            <tr>
                                <td>이름</td>
                                <td><input name="name" size="20"></td>
                            </tr>

                            <tr>
                                <td>연락처</td>
                                <td><input name="phone" size="15"></td>
                            </tr>

                            <tr>
                                <td>이메일</td>
                                <td><input name="email" size="50"></td>
                            </tr>
                            <tr><!-- inputCheck() -> script.js 연결해서 여러 오류 검사-->
                                <td colspan="3" align="center"><input type="submit"
                                                                      value="회원가입"> &nbsp; &nbsp; <input
                                                                      type="reset" value="다시쓰기"> &nbsp; &nbsp; <input
                                                                      type="button" value="취소"
                                                                      onClick="javascript:location.href = 'index.jsp'"></td>
                            </tr>
                            <tr>
                                회원가입시 모든 정보란에 입력 바랍니다. <br/>
                                ※비밀번호 주의사항: 6글자 이상, 16글자 이하 / 특수문자 필수 포함<br/>
                       
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </form>
    </div>
    <jsp:include page="footer.jsp" />
</body>
</html>

