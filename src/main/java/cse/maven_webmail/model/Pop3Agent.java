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
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.mail.*;
import javax.mail.Message;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author jongmin
 */
public class Pop3Agent {
    private static final Logger logger = LoggerFactory.getLogger(Pop3Agent.class);

    private String host;
    private String userid;
    private String password;
    private Store store;
    private String exceptionType;
    private HttpServletRequest request;
    final String JdbcDriver = "com.mysql.cj.jdbc.Driver";
    final String JdbcUrl = "jdbc:mysql://localhost:3308/webmail?serverTimezone=Asia/Seoul";
    final String User = "root";
    final String Password = "1234";

    public Pop3Agent() {
    }

    public Pop3Agent(String host, String userid, String password) {
        this.host = host;
        this.userid = userid;
        this.password = password;
    }

    public boolean validate() {
        boolean status = false;

        try {
            status = connectToStore();
            store.close();
        } catch (Exception ex) {
            System.out.println("Pop3Agent.validate() error : " + ex);
            status = false;  // for clarity
        } finally {
            return status;
        }
    }


    /*
     * 페이지 단위로 메일 목록을 보여주어야 함.
     */
    public String getMessageList() {
        String result = "";
        Message[] messages = null;

        if (!connectToStore()) {  // 3.1
            System.err.println("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder("INBOX");  // 3.2
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);

            MessageFormatter formatter = new MessageFormatter(userid);  //3.5
            result = formatter.getMessageTable(messages);   // 3.6

            folder.close(false);  // 3.7
            store.close();       // 3.8
        } catch (Exception ex) {
            System.out.println("Pop3Agent.getMessageList() : exception = " + ex);
            result = "Pop3Agent.getMessageList() : exception = " + ex;
        } finally {
            return result;
        }
    }

    public String getTrashList() {
        String result = "";
        Message[] messages = null;
        List<Message> filter_message = new ArrayList<>();

        if (!connectToStore()) {  // 3.1
            System.err.println("POP3 connection failed!");
            return "POP3 연결이 되지 않아 메일 목록을 볼 수 없습니다.";
        }

        try {
            // 메일 폴더 열기
            Folder folder = store.getFolder("INBOX");  // 3.2
            folder.open(Folder.READ_ONLY);  // 3.3

            // 현재 수신한 메시지 모두 가져오기
            messages = folder.getMessages();      // 3.4
            for (Message value : messages) {
                if (value.isSet(Flags.Flag.DELETED))
                    filter_message.add(value);

            }
            FetchProfile fp = new FetchProfile();
            // From, To, Cc, Bcc, ReplyTo, Subject & Date
            fp.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, fp);

            MessageFormatter formatter = new MessageFormatter(userid);  //3.5
            result = formatter.getMessageTable(filter_message.toArray(new Message[filter_message.size()]));   // 3.6
            folder.close(true);  // 3.7
            store.close();       // 3.8
        } catch (Exception ex) {
            System.out.println("Pop3Agent.getMessageList() : exception = " + ex);
            result = "Pop3Agent.getMessageList() : exception = " + ex;
        } finally {
            return result;
        }
    }

    public boolean deleteMessage(int msgid) {
        Connection connection;
        PreparedStatement preparedStatement = null;
        String sql = "SELECT num,message_name FROM(SELECT @num:=@num+1 AS num, message_name FROM (SELECT @num:=0) AS n, inbox WHERE repository_name = ?)a WHERE a.num = ?";
        String sql2 = "INSERT INTO trash SELECT * FROM inbox where message_name = ?";
        String messageName = "";
        if (!connectToStore()) {
            return false;
        }

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userid);
            preparedStatement.setInt(2, msgid);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            messageName = resultSet.getString(2);
            System.out.println("messageName"+messageName);
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setString(1,messageName);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        boolean status = false;
        logger.info("deleteMessage 값 : {}", msgid);

        try {
            // Folder 설정
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            // Message에 DELETED flag 설정
            Message msg = folder.getMessage(msgid);
            logger.info("message number : {}", msg.getMessageNumber());
            logger.info("{}", msg.getSubject());
            msg.setFlag(Flags.Flag.DELETED, true);

            // 폴더에서 메시지 삭제
            // <-- 현재 지원 안 되고 있음. 폴더를 close()할 때 expunge해야 함.
            folder.close(true);  // expunge == true
            store.close();
            status = true;
        } catch (Exception ex) {
            logger.error("deleteMessage() error: {}", ex.getMessage());
        }
        return status;
    }

//    public boolean deleteMessage(int msgid, boolean reallyDelete) {
//        boolean status = false;
//        logger.info("deleteMessage 값 : {}", msgid);
//
//        if (!connectToStore()) {
//            return false;
//        }
//
//        try {
//            // Folder 설정
//            Folder folder = store.getFolder("INBOX");
//            folder.open(Folder.READ_WRITE);
//            // Message에 DELETED flag 설정
//            Message msg = folder.getMessage(msgid);
//            logger.info("message number : {}", msg.getMessageNumber());
//            logger.info("{}", msg.getSubject());
//            msg.setFlag(Flags.Flag.DELETED, reallyDelete);
//
//            // 폴더에서 메시지 삭제
//            // <-- 현재 지원 안 되고 있음. 폴더를 close()할 때 expunge해야 함.
//            folder.close(true);  // expunge == true
//            store.close();
//            status = true;
//        } catch (Exception ex) {
//            logger.error("deleteMessage() error: {}", ex.getMessage());
//        }
//        return status;
//
//    }
    public String getMessage(int n) {
        String result = "POP3  서버 연결이 되지 않아 메시지를 볼 수 없습니다.";

        if (!connectToStore()) {
            System.err.println("POP3 connection failed!");
            return result;
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message message = folder.getMessage(n);

            MessageFormatter formatter = new MessageFormatter(userid);
            formatter.setRequest(request);  // 210308 LJM - added
            result = formatter.getMessage(message);

            folder.close(true);
            store.close();
        } catch (Exception ex) {
            System.out.println("Pop3Agent.getMessageList() : exception = " + ex);
            result = "Pop3Agent.getMessage() : exception = " + ex;
        } finally {
            return result;
        }
    }

    private boolean connectToStore() {
        boolean status = false;
        Properties props = System.getProperties();
        props.setProperty("mail.pop3.host", host);
        props.setProperty("mail.pop3.user", userid);
        props.setProperty("mail.pop3.apop.enable", "false");
        props.setProperty("mail.pop3.disablecapa", "true");  // 200102 LJM - added cf. https://javaee.github.io/javamail/docs/api/com/sun/mail/pop3/package-summary.html
        props.setProperty("mail.debug", "true");

        Session session = Session.getInstance(props);
        session.setDebug(true);

        try {
            store = session.getStore("pop3");
            store.connect(host, userid, password);
            status = true;
        } catch (Exception ex) {
            exceptionType = ex.toString();
        } finally {
            return status;
        }
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
    private Connection getConnection() throws SQLException{

        try {
            Class.forName(JdbcDriver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(JdbcUrl, User, Password);

    }

}  // class Pop3Agent

