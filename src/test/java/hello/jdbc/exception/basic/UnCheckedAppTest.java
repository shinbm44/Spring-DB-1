package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class UnCheckedAppTest {

    @Test
    void unchecked() {
        Controller controller = new Controller();
        assertThatThrownBy(()-> controller.request())
                .isInstanceOf(Exception.class);
    }

    @Test
    void printEx() {
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            log.info("ex", e);
        }
    }

    static class Controller {
        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkCilent networkCilent = new NetworkCilent();

        public void logic() {
            repository.call();
            networkCilent.call();
        }

    }
    static class NetworkCilent {
        public void call()  {
            throw new RuntimeConnectException("연결실패");
        }

    }
    static class Repository {
        public void call()  {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }

    }

    static class RuntimeConnectException extends RuntimeException{
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        // 기존에 runSQL에서 발생하는 SQLException의 예외(체크)를 런타임 예외로 감싸고
        // Throwable cause를 파라미터로 부모 클래스에서 호출하면
        // 스택 트레이스를 호출한다.
        // 단!, 런타임 예외로 감싸는 부분에서 기존 예외(체크)의 변수를 꼭 잊지않고 함께 넣어줘야한다.
        // 위의 코드에서
        // catch (SQLException e) {
        //                throw new RuntimeSQLException(e); <---- 'e'를 넣어야한다.
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }

}
