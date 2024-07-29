package qna.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
public class AnswerRepositoryTest {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @PersistenceContext
    EntityManager em;

    private User user;
    private Question question;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("user1", "password", "name", "email@test.com"));
        question = questionRepository.save(new Question("제목", "내용"));
        question.writeBy(user);
    }

    @DisplayName("Answer 엔티티 저장 시 ID 생성 및 내용 일치 여부 검증")
    @Test
    void 답변_저장_및_조회() {
        // given
        // 사용자와 질문이 주어져 있다.
        // 새로운 답변 내용이 준비되어 있다.
        Answer 저장할_답변 = new Answer(user, question, "내용");
        assertThat(저장할_답변.getId()).isNull(); // 저장하기 전에는 null

        // when
        // 답변을 저장한다.
        Answer 저장된_답변 = answerRepository.save(저장할_답변);

        // then
        // 저장된 답변의 ID가 null이 아니어야 한다.
        // 저장된 답변의 내용이 원래 입력한 내용과 일치해야 한다.
        assertAll(() -> assertThat(저장된_답변.getId()).isNotNull(), () -> assertThat(저장된_답변.getContents()).isEqualTo(저장할_답변.getContents()), () -> assertThat(저장할_답변.getId()).isNotNull(), // 저장할_답변의 id 값도 null에서 바뀌었음
                () -> assertThat(저장할_답변 == 저장된_답변).isTrue());
    }

    @Test
    void 질문_ID로_삭제되지_않은_답변_목록_조회() {
        // given
        // 하나의 질문에 대해 여러 개의 답변이 저장되어 있다.
        // 일부 답변은 삭제 처리되어 있다.
        Answer answer1 = new Answer(user, question, "");
        Answer answer2 = new Answer(user, question, "");
        Answer answer3 = new Answer(user, question, "");
        answer2.setDeleted(true);
        answerRepository.saveAll(List.of(answer1, answer2, answer3));

        // when
        // 해당 질문의 ID로 삭제되지 않은 답변 목록을 조회한다.
        List<Answer> answers = answerRepository.findByQuestion_IdAndDeletedFalse(question.getId());

        // then
        // 조회된 답변 목록의 크기가 삭제되지 않은 답변의 수와 일치해야 한다.
        // 조회된 답변 목록에는 삭제되지 않은 답변들의 내용만 포함되어 있어야 한다.
        assertThat(answers.size()).isEqualTo(2);
        assertThat(
                answers.stream()
                        .allMatch(answer -> answer.isDeleted() == false)
        ).isTrue();
    }

    @DisplayName("저장된 Answer 엔티티를 ID로 조회 시 정확한 엔티티 반환 검증")
    @Test
    void ID로_삭제되지_않은_답변_조회() {
        // given
        // 삭제되지 않은 상태의 답변이 저장되어 있다.
//        Answer 저장된_답변 = answerRepository.save(new Answer(user, question, ""));
        Answer 답변 = new Answer(user, question, "");
        answerRepository.save(답변);
        em.clear();

        // when
        // 해당 답변의 ID로 조회를 수행한다.
        Answer 찾은_답변 = answerRepository.findByIdAndDeletedFalse(답변.getId()).orElse(null);

        // then
        // 조회된 답변이 null이 아니어야 한다.
        // 조회된 답변의 내용이 원래 저장한 내용과 일치해야 한다.
        assertAll(() -> assertThat(찾은_답변).isNotNull(), () -> assertThat(찾은_답변.getId()).isEqualTo(답변.getId()), () -> assertThat(찾은_답변.getContents()).isEqualTo(답변.getContents()), () -> assertThat(찾은_답변.getQuestionId()).isEqualTo(답변.getQuestionId()), () -> assertThat(찾은_답변.getWriterId()).isEqualTo(답변.getWriterId()));
    }

    @Test
    void 삭제된_답변_조회_실패() {
        // given
        // 삭제 처리된 상태의 답변이 저장되어 있다.
        Answer 답변 = new Answer(user, question, "");
        답변.setDeleted(true);
        answerRepository.save(답변);

        // when
        // 해당 답변의 ID로 조회를 수행한다.
        Answer 찾은_답변 = answerRepository.findByIdAndDeletedFalse(답변.getId()).orElse(null);

        // then
        // 조회 결과가 null이어야 한다.
        assertThat(찾은_답변).isNull();
    }
}
