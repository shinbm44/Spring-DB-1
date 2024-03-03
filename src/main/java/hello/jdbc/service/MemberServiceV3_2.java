package hello.jdbc.service;


import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */

// 계좌 이체 로직
@Slf4j

public class MemberServiceV3_2 {

//    private final DataSource dataSource;
//    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        txTemplate.executeWithoutResult((status)-> {
            // 비즈니스 로직 시작
            try {
                bizLogic( fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }

        });

    }

    private void bizLogic( String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById( fromId);
        Member toMember = memberRepository.findById( toId);

        memberRepository.update( fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update( toId, toMember.getMoney() + money);
        // 비즈니스 로직 끝
    }

    //jdbcutils를 사용해도 좋으나 다른 클라이언트가 돌아간 커넥션 풀의 커넥션을 획득할 시,
    // autocommit이 false인 상태이다. 본래 autocommit은 true가 디폴트, 그래서 true로 바꿔준다.
    private static void release(Connection con) {
        if (con != null ) {
            try {
                con.setAutoCommit(true); // 커넥션 풀로 돌아가면 autocommit을 디폴트로..
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private void validation(Member toMember) {
        if ( toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
