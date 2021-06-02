package cse.maven_webmail.model;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.naming.NamingException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReservationMail implements Job {
    PreparedStatement pstmt = null;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try(Connection connection = getConnection(); Statement stmt = connection.createStatement();) {

            String sql = "SELECT * From booked_mail";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
                SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date dateTime = form.parse(rs.getString("bookTime"));
                if(dateTime.compareTo(new Date()) > 0){
                    continue;
                }
                //SmtpAgent 객체에 메일 관련 정보 설정
                SmtpAgent agent = new SmtpAgent(rs.getString("host"), rs.getString("userid"));
                agent.setTo(rs.getString("toAddress"));
                agent.setCc(rs.getString("ccAddress"));
                agent.setSubj(rs.getString("subj"));
                agent.setBody(rs.getString("body"));
                String fileName = rs.getString("fileName");
                System.out.println("Write MailHandler.sendMessage() : fileName = " + fileName);
                if (fileName != null) {
                    InputStream is = rs.getBinaryStream("file");
                    agent.setFile2(fileName);
                    agent.setFile(is);
                }

                //메일 전송 권한 위임
                if (agent.sendMessage()) {
                    //보낸 후 db 메시지 삭제
                    String delsql = "delete from booked_mail where bookTime = (select (date_format(now(), '%Y-%m-%d %H:%i:00')))";
                    PreparedStatement pstmt = connection.prepareStatement(delsql);
                    // 실행
                    pstmt.executeUpdate();
                }
            }
            connection.close();
        }  catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    private Connection getConnection() throws NamingException, SQLException {
        var name = "java:/comp/env/jdbc/mysqlWebmail";
        var context = new javax.naming.InitialContext();
        var dataSource = (javax.sql.DataSource) context.lookup(name);
        return dataSource.getConnection();
    }
}