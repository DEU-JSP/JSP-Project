package cse.maven_webmail.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBCMemberDAO {

    /**
     * 필요한 property 선언
     */
    private Connection con;
    private Statement st;
    private PreparedStatement ps;
    private ResultSet rs;
    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCMemberDAO.class);


    /**
     * 로드와 연결을 위한 생성자 작성
     */
    public JDBCMemberDAO(){

        try {
            //로드
            //MySQL
            con = getConnection();

        } catch (SQLException | NamingException e) {
            LOGGER.error(e.toString());

        }
    }//JDBC_memberDAO()

    /**
     * DB닫기 기능 메소드 작성
     */
    public void dbClose(){

        try {

            if (rs != null ) {rs.close();}
            if (ps != null ) {ps.close();}
            if (st != null ) {st.close();}

        } catch (SQLException e) {
            LOGGER.error(e.toString());
        }

    } //dbClose

    /**
     * member테이블에 insert하는 메소드 작성
     */
    public int memberInsert(MemberVO vo){
        var result = 0;

        try{
            //실행
            var sql = "insert into ADDRESSBOOK(id,name,tel,birth,addr,memo) values(?,?,?,?,?,?)";

            ps = con.prepareStatement(sql);
            ps.setString(1, vo.getId());
            ps.setString(2, vo.getName());
            ps.setString(3, vo.getTel());
            ps.setString(4, vo.getBirth());
            ps.setString(5, vo.getAddr());
            ps.setString(6, vo.getMemo());
            result = ps.executeUpdate();

        }catch (Exception e){
            LOGGER.error(e.toString());
        }finally{
            dbClose();
        }

        return result;
    }//memberInsert

    /**
     * member테이블의 모든 레코드 검색하(Select)는 메서드 작성
     */

    public List<MemberVO> getMemberlist(){

        ArrayList<MemberVO> list = new ArrayList<>();

        try{//실행
            st = con.createStatement();
            rs = st.executeQuery("select * from ADDRESSBOOK");

            while(rs.next()){
                var vo = new MemberVO();

                vo.setId(rs.getString(1));
                vo.setName(rs.getString(2));
                vo.setTel(rs.getString(3));
                vo.setBirth(rs.getString(4));
                vo.setAddr(rs.getString(5));
                vo.setMemo(rs.getString(6));

                list.add(vo);
            }
        }catch(Exception e){
            LOGGER.error(e.toString());
        }finally{
            dbClose();
        }
        return list;
    }//getMemberlist



    /**
     * member테이블의 모든 레코드 검색(Select)하는 메서드 작성
     * (검색필드와 검색단어가 들어왔을때는 where를 이용하여 검색해준다.)
     **/
    public List<MemberVO> getMemberlist(String keyField, String keyWord){

        List<MemberVO> list = new ArrayList<>();

        try{//실행

            String sql ="select * from ADDRESSBOOK ";

            if(keyWord != null && !keyWord.equals("") ){
                sql +="WHERE "+keyField.trim()+" LIKE '%"+keyWord.trim()+"%' order by id";
            }else{//모든 레코드 검색
                sql +="order by id";
            }

            System.out.println("sql = " + sql);
            st = con.createStatement();
            rs = st.executeQuery(sql);

            while(rs.next()){
                MemberVO vo = new MemberVO();

                vo.setId(rs.getString(1));
                vo.setName(rs.getString(2));
                vo.setTel(rs.getString(3));
                vo.setBirth(rs.getString(4));
                vo.setAddr(rs.getString(5));
                vo.setMemo(rs.getString(6));

                list.add(vo);
            }
        }catch(Exception e){
            System.out.println(e+"=> getMemberlist fail");
        }finally{
            dbClose();
        }
        return list;
    }


    /**
     * member테이블의 ID에 해당하는 레코드 삭제
     */
    public int delMemberlist(String id) {
        var result = 0;
        try {// 실행

            ps = con.prepareStatement("delete from ADDRESSBOOK where id = ?");
            // ?개수만큼 값 지정
            ps.setString(1, id.trim());
            result = ps.executeUpdate();  //쿼리 실행으로 삭제된 레코드 수 반환.

        } catch (Exception e) {
            LOGGER.error(e.toString());
        } finally {
            dbClose();
        }
        return result;
    }// delMemberlist

    private Connection getConnection() throws NamingException, SQLException {
        var name = "java:/comp/env/jdbc/mysqlWebmail";
        var context = new javax.naming.InitialContext();
        var  dataSource = (javax.sql.DataSource) context.lookup(name);
        return dataSource.getConnection();
    }
}//end
