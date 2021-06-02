package cse.maven_webmail.model;


import cse.maven_webmail.control.ReservationHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReservationMail implements Job {
    private static final String delsql = "delete from booked_mail where bookTime = (select (date_format(now(), '%Y-%m-%d %H:%i:00')))";
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationMail.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try(var connection = getConnection(); var stmt = connection.createStatement();
            var pstmt = connection.prepareStatement(delsql);) {

            var sql = "SELECT * From booked_mail";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
                var form = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                var dateTime = form.parse(rs.getString("bookTime"));
                if(dateTime.compareTo(new Date()) > 0){
                    continue;
                }
                //SmtpAgent 객체에 메일 관련 정보 설정
                var agent = new SmtpAgent(rs.getString("host"), rs.getString("userid"));
                agent.setTo(rs.getString("toAddress"));
                agent.setCc(rs.getString("ccAddress"));
                agent.setSubj(rs.getString("subj"));
                agent.setBody(rs.getString("body"));
                var fileName = rs.getString("fileName");

                if (fileName != null) {
                    InputStream is = rs.getBinaryStream("file");
                    agent.setFile2(fileName);
                    agent.setFile(is);
                }

                //메일 전송 권한 위임
                if (agent.sendMessage()) {
                    //보낸 후 db 메시지 삭제
                    // 실행
                    pstmt.executeUpdate();
                }
            }
        }  catch (SQLException | NamingException | ParseException throwables) {
            LOGGER.info(throwables.toString());
        }
    }
    private Connection getConnection() throws NamingException, SQLException {
        var name = "java:/comp/env/jdbc/mysqlWebmail";
        var context = new javax.naming.InitialContext();
        var dataSource = (javax.sql.DataSource) context.lookup(name);
        return dataSource.getConnection();
    }
}