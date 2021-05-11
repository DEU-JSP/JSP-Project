package cse.maven_webmail.control;

import cse.maven_webmail.model.Message;
import cse.maven_webmail.model.Pop3Agent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@NoArgsConstructor
public class TrashCanHandlerImpl extends HttpServlet implements TrashCanHandler {


    @Override
    public Boolean moveToTrashCan(HttpServletRequest httpServletRequest) {
        // Http로 읽어들여 msgList 객체 생성
        boolean status = false;
        List<Optional<Message>> msgList  = Arrays.stream(httpServletRequest.getParameter("msgs").
                split(",")).mapToInt(Integer::parseInt).boxed().
                map(Message::new).map(Optional::of).
                collect(Collectors.toList());


        HttpSession httpSession = httpServletRequest.getSession();
        String host = (String) httpSession.getAttribute("host");
        String userid = (String) httpSession.getAttribute("userid");
        String password = (String) httpSession.getAttribute("password");

        Pop3Agent pop3 = new Pop3Agent(host, userid, password);
        if(!msgList.isEmpty())
            status = pop3.moveToTrashCan(msgList);

        return status;
    }

    @Override
    public Boolean restoreToTrashCan(HttpServletRequest httpServletRequest) {
        return null;
    }

}
