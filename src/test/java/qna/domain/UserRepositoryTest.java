package qna.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("User를 저장하고 조회한다")
    void saveAndFindUser() {
        // given
        // User 객체를 생성한다
        User 유저 = new User("axax", "pw", "김씨", "a@gmail.com");

        // when
        // 생성한 User 객체를 저장한다
        // 저장된 User 객체를 조회한다
        User 저장된_유저 = userRepository.save(유저);
        em.clear();
        User 찾은_유저 = userRepository.findById(저장된_유저.getId())
                .orElse(null);

        // then
        // 조회된 User의 ID가 null이 아닌지 검증한다
        // 조회된 User의 속성들이 저장 시 입력한 값과 일치하는지 검증한다
        assertThat(찾은_유저.equals(유저)).isTrue();
        assertThat(찾은_유저).isEqualTo(유저);
    }

    @Test
    @DisplayName("userId로 User를 조회한다")
    void findUserByUserId() {
        // given
        // User 객체를 생성하고 저장한다
        User 저장된_유저 = userRepository.save(new User("axax", "pw", "김씨", "a@gmail.com"));

        // when
        // 저장된 User의 userId로 User를 조회한다
        User 찾은_유저 = userRepository.findByUserId(저장된_유저.getUserId())
                .orElse(null);

        // then
        // 조회된 User가 null이 아닌지 검증한다
        // 조회된 User의 userId가 저장 시 입력한 userId와 일치하는지 검증한다
        assertThat(찾은_유저).isNotNull();
        assertThat(찾은_유저.getUserId()).isEqualTo(저장된_유저.getUserId());
    }

    @Test
    @DisplayName("존재하지 않는 userId로 조회하면 null이 반환된다")
    void findUserByNonExistentUserId() {
        // when
        // 존재하지 않는 userId로 User를 조회한다
        User 찾은_유저 = userRepository.findByUserId("asfsafsadfsadfsadfsafsafsadfsd")
                .orElse(null);

        // then
        // 조회 결과가 null인지 검증한다
        assertThat(찾은_유저).isNull();
    }
}
