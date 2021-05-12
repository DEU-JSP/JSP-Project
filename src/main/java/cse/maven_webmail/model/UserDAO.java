/*
회원가입 및 user DB Table과 연결
 */
package cse.maven_webmail.model;

/**
 *
 * 실질적으로 db로 전송하기위한 dao(CRUD처리하는 부분)
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

public class UserDAO {

    //user column 명과 동일하게 선언
    private String userId;
    private String userPwd;
    private String email;
    private String name;
    private String phone;

    private Connection conn;
    private ResultSet rs;
    //DB연결 위해 추가
    private static UserDAO userDAO = null; //로그인

    public UserDAO() {
        try {
            String URL = "jdbc:mysql://localhost:3308/webmail?serverTimezone=Asia/Seoul";
            String ID = "jdbctester";
            String Password = "chaechae";
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, ID, Password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static UserDAO getInstance() {
        if (userDAO == null) {
            userDAO = new UserDAO();
        }
        return userDAO;
    }

    //회원가입시 아이디 중복확인
    public boolean Id_Check(String userId) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM user WHERE userId = ?");
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //로그인
    //1:성공 0:비밀번호 다름 -1:서버오류 -2:아이디없음 관리자일 경우 2 리턴
    public int login(String userId, String userPwd) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT userPwd FROM user WHERE userId =?");
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1).equals(userPwd) ? 1 : 0; //같으면 1, 다르면 0
            } else {
                return -2; //존재하지 않는 아이디
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //user table 정보 가져오기
    public UserDAO getUser(String userID) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM user WHERE userId =?");
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                UserDAO userDAO = new UserDAO();
                userDAO.setUserId(rs.getString(1));
                userDAO.setUserPwd(rs.getString(2));
                userDAO.setEmail(rs.getString(3));
                userDAO.setName(rs.getString(4));
                userDAO.setPhone(rs.getString(5));
                return userDAO;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null; //정보 가져올게 없으면 null
    }

    //회원가입 체크
    /*
    1: 성공
    0: 중복되는 아이디
    -1: 이외의 오류
     */
    public int signup(UserDAO userDAO) {
        if (!Id_Check(userDAO.getUserId())) {
            return 0; //아이디 존재하면 0
        }
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO user VALUES(?,?,?,?,?)");
            pstmt.setString(1, userDAO.getUserId());
            pstmt.setString(2, userDAO.getUserPwd());
            pstmt.setString(3, userDAO.getEmail());
            pstmt.setString(4, userDAO.getName());
            pstmt.setString(5, userDAO.getPhone());

            return pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return -1; //오류
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
