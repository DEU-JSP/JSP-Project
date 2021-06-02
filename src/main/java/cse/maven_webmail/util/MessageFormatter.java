/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.util;

import cse.maven_webmail.control.CommandTypeHelper;
import javax.mail.Message;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author jongmin
 */
public final class MessageFormatter {

    private final String userid;  // 파일 임시 저장 디렉토리 생성에 필요
    private HttpServletRequest request = null;

    public MessageFormatter(String userid) {
        this.userid = userid;
    }

    public String getMessageTable(Message[] messages) {
        var buffer = new StringBuilder();

        // 메시지 제목 보여주기
        buffer.append("<table>");  // table start
        buffer.append("<tr> "
                + " <th> No. </td> "
                + " <th> 보낸 사람 </td>"
                + " <th> 제목 </td>     "
                + " <th> 보낸 날짜 </td>   "
                + " <th> 삭제 </td>   "
                + " </tr>");

        for (int i = messages.length - 1; i >= 0; i--) {
            var parser = new MessageParser(messages[i], userid);
            parser.parse(false);  // envelope 정보만 필요
            // 메시지 헤더 포맷
            // 추출한 정보를 출력 포맷 사용하여 스트링으로 만들기
            buffer.append("<tr> " + " <td id=no>").append(i + 1).append(" </td> ").append(" <td id=sender>").append(parser.getFromAddress()).append("</td>").append(" <td id=subject> ").append(" <a href=show_message.jsp?msgid=").append(i + 1).append(" title=\"메일 보기\"> ").append(parser.getSubject()).append("</a> </td>").append(" <td id=date>").append(parser.getSentDate()).append("</td>").append(" <td id=delete>").append("<a href=ReadMail.do?menu=").append(CommandTypeHelper.DELETE_MAIL_COMMAND).append("&msgid=").append(i + 1).append("> 삭제 </a>").append("</td>").append(" </tr>");
        }
        buffer.append("</table>");

        return buffer.toString();
    }

    public String getMessage(Message message) {
        var buffer = new StringBuilder();

        var parser = new MessageParser(message, userid, request);
        parser.parse(true);

        buffer.append("보낸 사람: ").append(parser.getFromAddress()).append("<br>");
        buffer.append("받은 사람: ").append(parser.getToAddress()).append("<br>");
        buffer.append("Cc &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; : ").append(parser.getCcAddress()).append(" <br>");
        buffer.append("보낸 날짜: ").append(parser.getSentDate()).append(" <br>");
        buffer.append("제 &nbsp;&nbsp;&nbsp;  목: ").append(parser.getSubject()).append(" <br> <hr>");

        buffer.append(parser.getBody());

        String attachedFile = parser.getFileName();
        if (attachedFile != null) {
            buffer.append("<br> <hr> 첨부파일: <a href=ReadMail.do?menu=" + CommandTypeHelper.DOWNLOAD_COMMAND + "&userid=").append(this.userid).append("&filename=").append(attachedFile.replaceAll(" ", "%20")).append(" target=_top> ").append(attachedFile).append("</a> <br>");
        }

        return buffer.toString();
    }
    
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
