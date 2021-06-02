/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author jongmin
 */
public final class UserAdminAgent {

    private final Socket socket;
    private final InputStream is;
    private final OutputStream os;
    private final boolean isConnected;
    private String rootId;
    private String rootPassword;
    private String adminId;
    private static final String EOL = "\r";
    private final String cwd;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAdminAgent.class);


    public UserAdminAgent(String server, int port, String cwd) throws IOException {
        LOGGER.info("UserAdminAgent created: server = {} port = {}" ,server, port);

        this.cwd = cwd;

        initialize();

        socket = new Socket(server, port);
        is = socket.getInputStream();
        os = socket.getOutputStream();

        isConnected = connect();
    }

    private void initialize() {
        // property 읽는 방법 맞는지? getClass().getResourceAsStream() 사용해 보면...
        var props = new Properties();
        String propertyFile =  this.cwd + "/WEB-INF/classes/config/system.properties";
        propertyFile = propertyFile.replace("\\", "/");
        LOGGER.info("prop path = {} %n", propertyFile);

        try (var bis =
                new BufferedInputStream(
                        new FileInputStream(propertyFile))) {
            props.load(bis);
            rootId = props.getProperty("root_id");
            rootPassword = props.getProperty("root_password");
            adminId= props.getProperty("admin_id");
            LOGGER.info("ROOT_ID = {} %nROOT_PASS = {} %n", rootId, rootPassword);
        } catch (IOException ioe) {
            LOGGER.info("UserAdminAgent: 초기화 실패 - {}",ioe.getMessage());
        }

    }

    // return value:
    //   - true: addUser operation successful
    //   - false: addUser operation failed
    public boolean addUser(String userId, String password) {
        boolean status;
        var messageBuffer = new byte[1024];

        LOGGER.info("addUser() called");
        if (!isConnected) {
            return false;
        }

        try {
            // 1: "adduser" command
            String addUserCommand = "adduser " + userId + " " + password + EOL;
            os.write(addUserCommand.getBytes());

            // 2: response for "adduser" command
            java.util.Arrays.fill(messageBuffer, (byte) 0);

            var count = is.read(messageBuffer);
            LOGGER.info("is.read:{}",count);
            var recvMessage = new String(messageBuffer);

            // 3: 기존 메일사용자 여부 확인
            status = recvMessage.contains("added");
            // 4: 연결 종료
            quit();
            socket.close();
        } catch (Exception ex) {
            status = false;
        }
            // 5: 상태 반환
            return status;

    }  // addUser()

    public List<String> getUserList() {
        List<String> userList = new LinkedList<>();
        var messageBuffer = new byte[1024];

        if (!isConnected) {
            return userList;
        }

        try {
            // 1: "listusers" 명령 송신
            String command = "listusers " + EOL;
            os.write(command.getBytes());

            // 2: "listusers" 명령에 대한 응답 수신
            java.util.Arrays.fill(messageBuffer, (byte) 0);
            var count = is.read(messageBuffer);
            LOGGER.info("is.read :{}",count);
            // 3: 응답 메시지 처리
            var recvMessage = new String(messageBuffer);
            LOGGER.info(recvMessage);
            userList = parseUserList(recvMessage);

            quit();
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }
            return userList;

    }  // getUserList()

    private List<String> parseUserList(String message) {
        List<String> userList = new LinkedList<>();

        // 1: 줄 단위로 나누기
        String[] lines = message.split(EOL);
        // 2: 첫 번째 줄에는 등록된 사용자 수에 대한 정보가 있음.
        //    예) Existing accounts 7
        String[] firstLine = lines[0].split(" ");
        var numberOfUsers = Integer.parseInt(firstLine[2]);

        // 3: 두 번째 줄부터는 각 사용자 ID 정보를 보여줌.
        //    예) user: admin
        for (var i = 1; i <= numberOfUsers; i++) {
            // 3.1: 한 줄을 구분자 " "로 나눔.
            String[] userLine = lines[i].split(" ");
            // 3.2 사용자 ID가 관리자 ID와 일치하는 지 여부 확인
            if (!userLine[1].equals(adminId)) {
                userList.add(userLine[1]);
            }
        }
        return userList;
    } // parseUserList()

    public void deleteUsers(String[] userList) {
        var messageBuffer = new byte[1024];
        String command;
        String recvMessage;

        if (!isConnected) {
            return;
        }

        try {
            for (String userId : userList) {
                // 1: "deluser" 명령 송신
                command = "deluser " + userId + EOL;
                os.write(command.getBytes());
                LOGGER.info(command);

                // 2: 응답 메시지 수신
                java.util.Arrays.fill(messageBuffer, (byte) 0);
                var count = is.read(messageBuffer);
                LOGGER.info("is.read: {}",count);

                // 3: 응답 메시지 분석
                recvMessage = new String(messageBuffer);
                LOGGER.info(recvMessage);

            }
            quit();
        } catch (Exception ex) {
            LOGGER.error(String.valueOf(ex));
        }
    }  // deleteUsers()

    public boolean verify(String userid) {
        var status = false;
        var messageBuffer = new byte[1024];

        try {
            // --> verify userid
            String verifyCommand = "verify " + userid;
            os.write(verifyCommand.getBytes());

            // read the result for verify command
            // <-- User userid exists   or
            // <-- User userid does not exist
            var count = is.read(messageBuffer);
            LOGGER.info("is.read : {}",count);
            var recvMessage = new String(messageBuffer);
            if (recvMessage.contains("exists")) {
                status = true;
            }

            quit();  // quit command
        } catch (IOException ex) {
            LOGGER.error(ex.toString());
        }
            return status;

    }

    private boolean connect() throws IOException {
        var messageBuffer = new byte[1024];
        boolean returnVal;
        String sendMessage;

        LOGGER.info("UserAdminAgent.connect() called...");

        // root 인증: id, passwd - default: root
        // 1: Login Id message 수신
        var count = is.read(messageBuffer);
        LOGGER.info("is.read : {}",count);
        var recvMessage = new String(messageBuffer);
        LOGGER.info(recvMessage);
        // 2: rootId 송신
        sendMessage = rootId + EOL;
        os.write(sendMessage.getBytes());

        // 3: Password message 수신
        java.util.Arrays.fill(messageBuffer, (byte) 0);

        count = is.read(messageBuffer);
        LOGGER.info("is.read :{}",count);
        recvMessage = new String(messageBuffer);
        LOGGER.info(recvMessage);

        // 4: rootPassword 송신
        sendMessage = rootPassword + EOL;
        os.write(sendMessage.getBytes());

        // 5: welcome message 수신
        java.util.Arrays.fill(messageBuffer, (byte) 0);
        count = is.read(messageBuffer);
        LOGGER.info("is.read : {}",count);
        recvMessage = new String(messageBuffer);
        LOGGER.info(recvMessage);

        returnVal = recvMessage.contains("Welcome");
        return returnVal;
    }  // connect()

    public void quit() {
        var messageBuffer = new byte[1024];
        // quit
        try {
            // 1: quit 명령 송신
            String quitCommand = "quit" + EOL;
            os.write(quitCommand.getBytes());
            // 2: quit 명령에 대한 응답 수신
            java.util.Arrays.fill(messageBuffer, (byte) 0);
            var count = is.read(messageBuffer);
            LOGGER.info("is.read: {}",count);
            // 3: 메시지 분석
            var recvMessage = new String(messageBuffer);
            LOGGER.info(recvMessage);

        } catch (IOException ex) {
            LOGGER.error("UserAdminAgent.quit() {} ", ex.toString());
        }

    }
}
