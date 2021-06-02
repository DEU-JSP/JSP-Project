package cse.maven_webmail.model;

import cse.maven_webmail.control.CommandTypeHelper;
import cse.maven_webmail.util.TrashMessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public final class TrashAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrashAgent.class);
    private String userId;
    private String messageName;
    private String dir;
    private String fileName;

    public TrashAgent() {
        //빈생성자
    }

    public String getFileName() {
        return fileName;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMessageName(String messageName) {
        LOGGER.info("messageName : {}", messageName);
        this.messageName = messageName;
    }

    /**
     * 휴지통에서 제목을 클릭했을 때 자세히 나오게 하는 메소드입니다.
     * @return 휴지통 결과
     */
    public String getResult() {
        var sql = "SELECT message_body FROM trash WHERE message_name = ?";
        String result;
        try (var connection = getConnection();
             var preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, messageName);
            LOGGER.info("{}", preparedStatement);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    var stringBuilder = new StringBuilder();
                    var trashMessageFormatter = new TrashMessageFormatter();

                    try (var inputStream = resultSet.getBinaryStream(1)) {
                        trashMessageFormatter.setMailStream(inputStream);
                        trashMessageFormatter.parse();
                    }
                    stringBuilder.append("보낸 사람 : ").append(trashMessageFormatter.getFromAddress()).append("<br>");
                    stringBuilder.append("받은 사람 : ").append(trashMessageFormatter.getToAddress()).append("<br>");
                    stringBuilder.append("Cc : ").append(trashMessageFormatter.getCc() == null ? "" : trashMessageFormatter.getCc()).append("<br>");
                    stringBuilder.append("보낸 날짜 : ").append(trashMessageFormatter.getDate()).append("<br>");
                    stringBuilder.append("제목 : ").append(trashMessageFormatter.getSubject()).append("<br>");
                    stringBuilder.append("<hr>");
                    stringBuilder.append(trashMessageFormatter.getBody());
                    stringBuilder.append("<hr>");
                    if (trashMessageFormatter.getFileName() != null) {
                        stringBuilder.append("<form action=\"trash.do\" method=\"POST\">");
                        stringBuilder.append("<input type=\"hidden\" name=\"menu\" value=\"").append(CommandTypeHelper.DOWNLOAD_COMMAND).append("\"/>");
                        stringBuilder.append("<input type=\"hidden\" name=\"messageName\" value=\"").append(messageName).append("\"/>");
                        stringBuilder.append("파일 : <input type=\"submit\" value=\"");
                        stringBuilder.append(trashMessageFormatter.getFileName());
                        stringBuilder.append("\"/>");
                        stringBuilder.append("</form>");
                    }
                    result = stringBuilder.toString();
                } else {
                    return "데이터베이스 오류가 발생했습니다.";
                }
            }
        } catch (SQLException | IOException | NamingException throwables) {
            LOGGER.error(throwables.getMessage());
            return "오류가 발생했습니다." + throwables.getMessage();
        }
        return result;
    }

    /**
     * 파일 다운로드를 처리합니다.
     */
    public void download() {

        var sql = "SELECT message_body FROM trash WHERE message_name = ?";
        try (var connection = getConnection();
             var preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, messageName);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    try (var inputStream = resultSet.getBinaryStream(1)) {
                        var trashMessageFormatter = new TrashMessageFormatter();
                        trashMessageFormatter.setMailStream(inputStream);
                        trashMessageFormatter.parse();
                        fileName = trashMessageFormatter.getFileName();
                        try (var fileStream = trashMessageFormatter.getFileStream();
                             var fileOutputStream = new FileOutputStream(dir + "/" + fileName)
                        ) {
                            byte[] decoded = fileStream.readAllBytes();
                            fileOutputStream.write(decoded);
                        }
                    }
                } else {
                    LOGGER.error("messageName을 못찾았음 : {}", messageName);
                }
            }
        } catch (SQLException | IOException throwables) {
            LOGGER.error(throwables.getMessage());
        } catch (NamingException e) {
            LOGGER.error(e.toString());
        }
    }

    /**
     * 휴지통 내의 모든 결과를 가져오는 메소드입니다.
     * @return 휴지통 조회 결과 table
     */
    public List<String> getResults() {
        List<String> results = new LinkedList<>();
        var sql = "SELECT message_name, message_body FROM trash WHERE repository_name = ?";
        try (var connection = getConnection();
             var preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, userId);
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    var stringBuilder = new StringBuilder();
                    var messageName = resultSet.getString(1);
                    var trashMessageFormatter = new TrashMessageFormatter();
                    try (var inputStream = resultSet.getBinaryStream(2)) {
                        trashMessageFormatter.setMailStream(inputStream);
                        trashMessageFormatter.parse();
                    }
                    stringBuilder.append("<tr>");
                    stringBuilder.append("<td>");
                    stringBuilder.append(resultSet.getRow());
                    stringBuilder.append("</td>");
                    stringBuilder.append("<td>");
                    stringBuilder.append(trashMessageFormatter.getFromAddress());
                    stringBuilder.append("</td>");
                    stringBuilder.append("<td>");
                    stringBuilder.append("<form action=\"show_trash_message.jsp\" method=\"POST\">");
                    stringBuilder.append("<input type=\"hidden\" name=\"messageName\" value=\"");
                    stringBuilder.append(URLEncoder.encode(messageName, StandardCharsets.UTF_8)).append("\"/>");
                    stringBuilder.append("<input type=\"submit\" class=\"submitLink\" value=\"");
                    stringBuilder.append(trashMessageFormatter.getSubject());
                    stringBuilder.append("\"/>");
                    stringBuilder.append("</form>");
                    stringBuilder.append("</td>");
                    stringBuilder.append("<td>");
                    stringBuilder.append(trashMessageFormatter.getDate());
                    stringBuilder.append("</td>");
                    stringBuilder.append("<td>");
                    stringBuilder.append("<form action=\"trash.do\" method=\"POST\">");
                    stringBuilder.append("<input type=\"hidden\" name=\"menu\" value=\"");
                    stringBuilder.append(CommandTypeHelper.DELETE_MAIL_COMMAND).append("\"/>");
                    stringBuilder.append("<input type=\"hidden\" name=\"messageName\" value=\"");
                    stringBuilder.append(messageName).append("\"/>");
                    stringBuilder.append("<input type=\"submit\" class=\"submitLink\" value=\"");
                    stringBuilder.append("삭제");
                    stringBuilder.append("\"/>");
                    stringBuilder.append("</form>");
                    stringBuilder.append("</td>");
                    stringBuilder.append("<td>");
                    stringBuilder.append("<form action=\"trash.do\" method=\"POST\">");
                    stringBuilder.append("<input type=\"hidden\" name=\"menu\" value=\"");
                    stringBuilder.append(CommandTypeHelper.RESTORE_MAIL_COMMAND).append("\"/>");
                    stringBuilder.append("<input type=\"hidden\" name=\"messageName\" value=\"");
                    stringBuilder.append(messageName).append("\"/>");
                    stringBuilder.append("<input type=\"submit\" class=\"submitLink\" value=\"");
                    stringBuilder.append("복구");
                    stringBuilder.append("\"/>");
                    stringBuilder.append("</form>");
                    stringBuilder.append("</td>");
                    stringBuilder.append("</tr>");
                    results.add(stringBuilder.toString());
                }
            }
        } catch (SQLException | IOException | NamingException throwables) {
            LOGGER.error(throwables.getMessage());
        }
        return results;
    }

    /**
     * 휴지통 내의 모든 메일을 완전 삭제하는 메소드입니다.
     * @return 삭제 성공 여부
     */
    public boolean deleteAll() {
        boolean status;
        var sql = "DELETE FROM trash WHERE repository_name = ?";
        try (var connection = getConnection();
             var preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, userId);
            preparedStatement.executeUpdate();
            status = true;
        } catch (SQLException | NamingException throwables) {
            status = false;
            LOGGER.error(throwables.getMessage());
        }
        return status;
    }

    /**
     * 휴지통에서 메일을 복구하는 메소드입니다.
     * @return 복구 성공 여부
     */
    public boolean restore() {
        var status = false;
        var firstSql = "INSERT INTO inbox SELECT * FROM trash WHERE message_name= ?";
        var second = "DELETE FROM trash WHERE message_name = ?";
        try (var connection = getConnection();
             var preparedStatement = connection.prepareStatement(firstSql);
             var secondStatement = connection.prepareStatement(second)
        ) {
            preparedStatement.setString(1, messageName);
            int rows = preparedStatement.executeUpdate();
            if (rows == 1) {
                secondStatement.setString(1, messageName);
                int secondRow = secondStatement.executeUpdate();
                status = secondRow == 1;
            }
        } catch (SQLException | NamingException throwables) {
            status = false;
            LOGGER.error(throwables.getMessage());
        }
        return status;
    }

    /**
     * 휴지통에서 메일을 완전 삭제하는 메소드입니다.
     * @return 완전 삭제 성공 여부
     */
    public boolean delete() {
        boolean status;
        var sql = "DELETE FROM trash WHERE message_name = ?";
        try (var connection = getConnection();
             var preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, messageName);
            int rows = preparedStatement.executeUpdate();
            status = rows == 1;
        } catch (SQLException | NamingException throwables) {
            status = false;
            LOGGER.error(throwables.getMessage());
        }
        return status;
    }

    private Connection getConnection() throws NamingException, SQLException {
        var name = "java:/comp/env/jdbc/mysqlWebmail";
        var context = new javax.naming.InitialContext();
        var dataSource = (javax.sql.DataSource) context.lookup(name);
        return dataSource.getConnection();
    }

}
