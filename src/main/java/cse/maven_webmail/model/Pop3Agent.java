/*
 * File: Pop3Agent.java
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import cse.maven_webmail.util.MessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.mail.*;
import javax.mail.Message;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author jongmin
 */
public final class Pop3Agent {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pop3Agent.class);

    private String host;
    private String userid;
    private String password;
    private Store store;
    private HttpServletRequest request;
    private static final String INBOX = "INBOX";

    public Pop3Agent() {
    }

    public Pop3Agent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }

    public boolean validate() {
        var status = false;

        try {
            status = connectToStore();
            store.close();
        } catch (Exception ex) {
            LOGGER.info("Pop3Agent.validate() error : {}",ex.toString());
            status = false;  // for clarity
        }
        return status;
    }


    /*
     * 페이지 단위로 메일 목록을 보여주어야 함.
     */
    public String getMessageList() {
        String result;
        Message[] messages;

        if (!connectToStore()) {  // 3.1
            LOGGER.error("POP3 connection failed!!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            var folder = store.getFolder(INBOX);  // 3.2
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            var fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);

            var formatter = new MessageFormatter(userid);  //3.5
            result = formatter.getMessageTable(messages);   // 3.6

            folder.close(false);  // 3.7
            store.close();       // 3.8
        } catch (Exception ex) {
            LOGGER.info("Pop3Agent.getMessageList() :: exception = {}" ,ex.toString());
            result = "Pop3Agent.getMessageList() ::: exception = " + ex;
        }

        return result;

    }

    public String getTrashList() {
        var result = "";
        Message[] messages;
        List<Message> filterMessage = new ArrayList<>();

        if (!connectToStore()) {  // 3.1
            LOGGER.info("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            var folder = store.getFolder(INBOX);  // 3.2
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            for (Message value : messages) {
                if (value.isSet(Flags.Flag.DELETED)) {
                    filterMessage.add(value);
                }

            }
            var fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);

            var formatter = new MessageFormatter(userid);  //3.5
            result = formatter.getMessageTable(filterMessage.toArray(new Message[0]));   // 3.6
            folder.close(true);  // 3.7
            store.close();       // 3.8
        } catch (Exception ex) {
            LOGGER.info("Pop3Agent.getMessageList() : exception = {}", ex.toString());
            result = "Pop3Agent.getMessageList() : exception = " + ex;
        }
        return result;
    }

    public boolean deleteMessage(int msgid) {
        var sql = "SELECT num,message_name FROM(SELECT @num:=@num+1 AS num, message_name FROM (SELECT @num:=0) AS n, inbox WHERE repository_name = ?)a WHERE a.num = ?";
        var sql2 = "INSERT INTO trash SELECT * FROM inbox where message_name = ?";
        String messageName;
        if (!connectToStore()) {
            return false;
        }

        try( var connection = getConnection(); PreparedStatement preparedStatement1 = connection.prepareStatement(sql);
             var preparedStatement2 = connection.prepareStatement(sql2);) {
            preparedStatement1.setString(1, userid);
            preparedStatement1.setInt(2, msgid);
            var resultSet = preparedStatement1.executeQuery();
            resultSet.next();
            messageName = resultSet.getString(2);
            LOGGER.info("messageName {}",messageName);
            preparedStatement2.setString(1,messageName);
            preparedStatement2.executeUpdate();

        } catch (SQLException | NamingException throwables) {
            LOGGER.error(throwables.toString());
        }

        var status = false;
        LOGGER.info("deleteMessage 값 : {}", msgid);

        try {
            // Folder 설정
            var folder = store.getFolder(INBOX);
            folder.open(Folder.READ_WRITE);
            // Message에 DELETED flag 설정
            var msg = folder.getMessage(msgid);
            LOGGER.info("message number : {}", msg.getMessageNumber());
            LOGGER.info("{}", msg.getSubject());
            msg.setFlag(Flags.Flag.DELETED, true);

            // 폴더에서 메시지 삭제
            // <-- 현재 지원 안 되고 있음. 폴더를 close()할 때 expunge해야 함.
            folder.close(true);  // expunge == true
            store.close();
            status = true;
        } catch (Exception ex) {
            LOGGER.error("deleteMessage() error: {}", ex.getMessage());
        }
        return status;
    }

    public String getMessage(int n) {
        var result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!connectToStore()) {
            LOGGER.error("POP3 connection failed!");
            return result;
        }

        try {
            var folder = store.getFolder(INBOX);
            folder.open(Folder.READ_ONLY);

            var message = folder.getMessage(n);

            var formatter = new MessageFormatter(userid);
            formatter.setRequest(request);  // 210308 LJM - added
            result = formatter.getMessage(message);

            folder.close(true);
            store.close();
        } catch (Exception ex) {
            LOGGER.info("Pop3Agent.getMessageList() : exception = {}", ex.toString());
            result = "Pop3Agent.getMessage() : exception = " + ex;
        }
        return result;
    }

    private boolean connectToStore() {
        var status = false;
        var props = System.getProperties();
        props.setProperty("mail.pop3.host", host);
        props.setProperty("mail.pop3.user", userid);
        props.setProperty("mail.pop3.apop.enable", "false");
        props.setProperty("mail.pop3.disablecapa", "true");  // 200102 LJM - added cf. https://javaee.github.io/javamail/docs/api/com/sun/mail/pop3/package-summary.html
        props.setProperty("mail.debug", "true");

        var session = Session.getInstance(props);
        session.setDebug(true);

        try {
            store = session.getStore("pop3");
            store.connect(host, userid, password);
            status = true;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }
        return status;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    private Connection getConnection() throws NamingException, SQLException {
        var name = "java:/comp/env/jdbc/mysqlWebmail";
        var context = new javax.naming.InitialContext();
        var dataSource = (javax.sql.DataSource) context.lookup(name);
        return dataSource.getConnection();
    }

}  // class Pop3Agent

