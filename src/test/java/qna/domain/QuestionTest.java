package qna.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qna.exception.CannotDeleteException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QuestionTest {
    public static final Question Q1 = new Question("title1", "contents1", UserTest.DORAEMON);
    public static final Question Q2 = new Question("title2", "contents2", UserTest.SPONGEBOB);


    @Test
    @DisplayName("질문 작성자가 같으면 ture를 return한다")
    void isOwnerTest1() {
        assertThat(Q1.isOwner(UserTest.DORAEMON)).isTrue();
    }

    @Test
    @DisplayName("질문 작성자가 다르면 false를 return한다")
    void isOwnerTest2() {
        assertThat(Q1.isOwner(UserTest.SPONGEBOB)).isFalse();
    }

    @Test
    @DisplayName("작성자 검증 함수에 작성자가 아닌 다른 유저가 전달되면 예외가 발생한다")
    void validateOwnershipTest1() {
        // given
        User 질문_작성자 = new User("a", "", "", "");
        User 다른_유저 = new User("b", "", "", "");
        Question 질문 = new Question("title1", "contents1", 질문_작성자);

        // when & then
        Assertions.assertThatThrownBy(
                () -> 질문.validateOwnership(다른_유저)
        ).isInstanceOf(CannotDeleteException.class);
    }

    @Test
    @DisplayName("작성자 검증 함수에 작성자 유저가 전달되면 예외가 발생하지 않는다")
    void validateOwnershipTest2() {
        // given
        User 질문_작성자 = new User("a", "", "", "");
        Question 질문 = new Question("title1", "contents1", 질문_작성자);

        // when & then
        질문.validateOwnership(질문_작성자);
    }

    @Test
    @DisplayName("질문 작성자가 아닌 사용자의 답변이 있을 경우 질문 삭제 시 예외 발생")
    void validateAllAnswerOwnership1() {
        // given
        // (로그인한)유저가 한 명 있고, 그 유저가 작성한 질문이 하나 있음.
        // 그리고 그 유저가 자기가 작성한 질문에 대해 삭제 요청을 한 상황.
        // 그런데 그 질문에 대한 답변이 한 개 이상 있는데,
        // 한 답변의 작성자가 질문 작성자와 다른 유저임.
        User 질문_작성자 = new User("doraemon", "", "", "");
        User 다른_유저 = new User("sponge_bob", "", "", "");
        Question 질문 = new Question("DI는 왜 중요한가요?", "제곧내", 질문_작성자);
        Answer 답변1 = new Answer(질문_작성자, 질문, "결합도를 낮춰 줍니다");
        Answer 답변2 = new Answer(다른_유저, 질문, "유연성을 높여 줍니다");
        질문.addAnswer(답변1);
        질문.addAnswer(답변2);

        // when & then
        Assertions.assertThatThrownBy(
                () -> 질문.validateAllAnswerOwnership(질문_작성자)
        ).isInstanceOf(CannotDeleteException.class);
    }

    @Test
    @DisplayName("모든 답변이 질문 작성자의 것일 경우 질문 삭제 가능")
    void validateAllAnswerOwnership2() {
        // given
        User 질문_작성자 = new User("doraemon", "", "", "");
        Question 질문 = new Question("DI는 왜 중요한가요?", "제곧내", 질문_작성자);
        Answer 답변1 = new Answer(질문_작성자, 질문, "결합도를 낮춰 줍니다");
        Answer 답변2 = new Answer(질문_작성자, 질문, "유연성을 높여 줍니다");
        질문.addAnswer(답변1);
        질문.addAnswer(답변2);

        // when & then
        질문.validateAllAnswerOwnership(질문_작성자);
    }

    @Test
    void deleteQuestionTest() {
        User 질문_작성자 = new User("doraemon", "", "", "");
        Question question = new Question(3L, "", "", 질문_작성자);

        DeleteHistory deleteHistory = question.deleteQuestion();

        assertThat(question.isDeleted()).isTrue();
        assertThat(deleteHistory).isEqualTo(new DeleteHistory(
                ContentType.QUESTION,
                3L,
                질문_작성자,
                LocalDateTime.now()
        ));
    }

    @Test
    @DisplayName("질문에 답변이 없을 때, 로그인한 사용자 본인이 작성한 질문이라면 삭제 성공 (히스토리 생성됨)")
    void 답변없는_질문삭제_성공() {
        User 질문_작성자 = new User("yul", "", "", "");
        Question 질문 = new Question(1L, "눈이 떨리는 이유는 뭔가요?", "", 질문_작성자);

        질문.validate(질문_작성자);
        List<DeleteHistory> deleteHistories = 질문.delete();

        assertThat(질문.isDeleted()).isTrue(); // 질문이 삭제된 상태인지 체크
        assertThat(deleteHistories).containsExactly( // 삭제 이력이 일치하는지 체크ㄴ
                new DeleteHistory(
                        ContentType.QUESTION,
                        질문.getId(),
                        질문.getWriter(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    @DisplayName("질문에 답변이 없을 때, 로그인한 사용자 본인이 작성한 질문이 아니라면 질문 삭제 실패")
    void 답변없는_질문삭제_실패() {
        User 질문_작성자 = new User("yul", "", "", "");
        User 질문_작성자가_아닌_유저 = new User("dora", "", "", "");
        Question 질문 = new Question(1L, "눈이 떨리는 이유는 뭔가요?", "", 질문_작성자);

        assertThatThrownBy(
                () -> 질문.validate(질문_작성자가_아닌_유저)
        ).isInstanceOf(CannotDeleteException.class);
    }

    @Test
    @DisplayName("질문에 답변이 있을 때, 로그인한 사용자가 작성한 질문이고 본인이 작성한 답변만 존재한다면, 질문 및 답변 삭제 성공 (히스토리 생성됨)")
    void 본인답변_질문삭제_성공() {
        User 질문_작성자 = new User("yul", "", "", "");
        Question 질문 = new Question(1L, "눈이 떨리는 이유는 뭔가요?", "", 질문_작성자);
        Answer 답변 = new Answer(10L, 질문_작성자, 질문, "마그네슘이 부족할 수도");
        질문.addAnswer(답변);
        
        질문.validate(질문_작성자);
        List<DeleteHistory> 삭제이력목록 = 질문.delete();

        assertThat(질문.isDeleted()).isTrue();
        assertThat(삭제이력목록).containsExactlyInAnyOrder(
                new DeleteHistory(ContentType.QUESTION, 질문.getId(), 질문.getWriter(), LocalDateTime.now()),
                new DeleteHistory(ContentType.ANSWER, 답변.getId(), 답변.getWriter(), LocalDateTime.now())
        );
    }

    @Test
    @DisplayName("질문에 답변이 있을 때, 로그인한 사용자 본인이 작성한 질문이라도 타인이 작성한 답변이 존재한다면, 삭제 실패")
    void 타인답변_질문삭제_실패() {
        User 질문_작성자 = new User("yul", "", "", "");
        User 답변_작성자 = new User("dora", "", "", "");
        Question 질문 = new Question(1L, "눈이 떨리는 이유는 뭔가요?", "", 질문_작성자);
        질문.addAnswer(new Answer(10L, 답변_작성자, 질문, "마그네슘이 부족할 수도"));

        assertThatThrownBy(
                () -> 질문.validate(질문_작성자)
        ).isInstanceOf(CannotDeleteException.class);
    }
}
