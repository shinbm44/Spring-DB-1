package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    // DriverManager
    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection={], class={}", con1, con1.getClass());
        log.info("connection={], class={}", con2, con2.getClass());
    }


    // DataSource
    // 스프링이 제공하는 datasource가 적용된 드라이버 매니저
    @Test
    void dataSourceDriveManager() throws SQLException {
        //DriverManagerSource - 항상 새로운 커넥션을 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        useDataSource(dataSource);

    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }

    // 두 방법에서 커넥션을 획득하는 방법에는 큰 차이가 있다.
    /*
    DriverManager 는 커넥션을 획득할 때 마다 URL , USERNAME , PASSWORD 같은 파라미터를 계속 전달해야
    한다. 반면에 DataSource 를 사용하는 방식은 처음 객체를 생성할 때만 필요한 파리미터를 넘겨두고, 커넥션을
    획득할 때는 단순히 dataSource.getConnection() 만 호출하면 된다.

    --> 설정과 사용의 분리
     */
}
