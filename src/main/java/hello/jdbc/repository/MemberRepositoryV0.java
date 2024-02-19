package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            // 시작 순서 반대로 닫아줘야한다.
            // 외부 리소스를 사용 중이니 안닫으면 계속 사용될 수 있다.
            //pstmt.close();
            //con.close();
            // 만약 Exception이 터져서 close()가 호출이 안된다면? --> 리소스 누수
            // 이에 대한 처리가 필요하다.
            close(con, pstmt, null);
        }
    }


    // PrepareStatement는 Statement에 비해서 파라미터 바인딩이 가능하도록 기능이 더 많다.(+sql injection방지)
    // Statement는 sql을 그냥 넣는 것.
    private void close(Connection con, Statement stmt, ResultSet rs) {

        if( rs != null ){
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if ( stmt != null ) {
            try{
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if( con != null ) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

    }

    private static Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }


}
