package qna.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    EntityManager em;

    @DisplayName("Question 저장 테스트")
    @Test
    void saveQuestionTest() {
        // given
        // - Question 객체를 생성한다. (제목: "제목", 내용: "내용")
        User 작성자 = userRepository.save(new User("", "", "", ""));
        Question 질문 = new Question("제목", "내용");
        질문.writeBy(작성자);
        assertThat(질문.getId()).isNull();

        // when
        // - 생성한 Question 객체를 저장한다.
        Question 저장된_질문 = questionRepository.save(질문);

        // then
        // - 저장된 Question의 ID가 null이 아닌지 검증한다.
        // - 저장된 Question의 제목이 "제목"인지 검증한다.
        // - 저장된 Question의 내용이 "내용"인지 검증한다.
        assertThat(저장된_질문.getId()).isNotNull();
        assertThat(저장된_질문.getTitle()).isEqualTo(질문.getTitle());
        assertThat(저장된_질문.getContents()).isEqualTo(질문.getContents());
    }

    @DisplayName("Question 목록 조회 테스트")
    @Test
    void findAllQuestionTest() {
        // given
        // - 2개의 Question을 생성하고 저장한다. (제목1: "제목1", 내용1: "내용1"), (제목2: "제목2", 내용2: "내용2")
        User 작성자 = userRepository.save(new User("", "", "", ""));
        Question 질문1 = new Question("제목", "내용");
        Question 질문2 = new Question("제목", "내용");
        질문1.writeBy(작성자);
        질문2.writeBy(작성자);
        questionRepository.saveAll(List.of(질문1, 질문2));

        // when
        // - 모든 Question의 목록을 조회한다.
        List<Question> questions = questionRepository.findAll();

        // then
        // - 조회된 Question의 목록 크기가 2인지 검증한다.
        assertThat(questions).hasSize(2);
        assertThat(questions.size()).isEqualTo(2);
    }

    @DisplayName("삭제되지 않은 Question 목록 조회 테스트")
    @Test
    void findByDeletedFalseTest() {
        // given
        // - 2개의 Question을 생성한다. (제목1: "제목1", 내용1: "내용1"), (제목2: "제목2", 내용2: "내용2")
        // - 두 번째 Question의 deleted를 true로 설정하고 저장한다.
        User 작성자 = userRepository.save(new User("", "", "", ""));
        Question 질문1 = new Question("제목1", "내용");
        Question 질문2 = new Question("제목2", "내용");
        질문1.writeBy(작성자);
        질문2.writeBy(작성자);
        질문2.setDeleted(true);
        questionRepository.save(질문1);
        questionRepository.save(질문2);

        // when
        // - deleted가 false인 Question의 목록을 조회한다.
        List<Question> questions = questionRepository.findByDeletedFalse();

        // then
        // - 조회된 Question의 목록 크기가 1인지 검증한다.
        // - 조회된 Question의 제목이 "제목1"인지 검증한다.
        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getTitle()).isEqualTo(질문1.getTitle());
    }

    @DisplayName("삭제되지 않은 Question 단건 조회 테스트")
    @Test
    void findByIdAndDeletedFalseTest() {
        // given
        // - Question을 생성하고 저장한다. (제목: "제목", 내용: "내용")
        User 작성자 = userRepository.save(new User("", "", "", ""));
        Question 질문 = new Question("제목", "내용");
        질문.writeBy(작성자);
        questionRepository.save(질문);

        // when
        // - 저장된 Question의 ID와 deleted가 false인 조건으로 단건 조회한다.
        Question 찾은_질문 = questionRepository.findByIdAndDeletedFalse(질문.getId())
                .orElse(null);

        // then
        // - 조회 결과가 존재하는지 검증한다.
        // - 조회된 Question의 제목이 "제목"인지 검증한다.
        assertThat(찾은_질문).isNotNull();
        assertThat(찾은_질문.getTitle()).isEqualTo(질문.getTitle());
    }

    @DisplayName("제목이 없는 Question 저장 시 예외 발생 테스트")
    @Test
    void saveQuestionWithoutTitleTest() {
        // given
        // - 제목이 null인 Question 객체를 생성한다.
        User 작성자 = userRepository.save(new User("", "", "", ""));
        Question 질문 = new Question(null, "");
        질문.writeBy(작성자);

        // when & then
        // - 제목이 null인 Question 객체를 저장하면
        // - 데이터베이스 제약 조건 위반 예외(DataIntegrityViolationException)가 발생한다.
        assertThatThrownBy(
                () -> questionRepository.save(질문)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("제목 길이가 100자를 초과하는 Question 저장 시 예외 발생 테스트")
    @Test
    void saveQuestionWithTooLongTitleTest() {
        // given
        // - 101자의 제목을 가진 Question 객체를 생성한다.
        String longTitle = "a".repeat(101);
        User 작성자 = userRepository.save(new User("", "", "", ""));
        Question 질문 = new Question(longTitle, "내용");
        질문.writeBy(작성자);

        // when & then
        // - 101자의 제목을 가진 Question 객체를 저장한다.
        // - 데이터베이스 제약 조건 위반 예외(DataIntegrityViolationException)가 발생하는지 검증한다.
        assertThatThrownBy(
                () -> questionRepository.save(질문)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }
}