package qna.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qna.exception.CannotDeleteException;

import java.time.LocalDateTime;

public class AnswerTest {
    public static final Answer A1 = new Answer(UserTest.DORAEMON, QuestionTest.Q1, "Answers Contents1");
    public static final Answer A2 = new Answer(UserTest.SPONGEBOB, QuestionTest.Q1, "Answers Contents2");

    @Test
    @DisplayName("답변 작성자가 같으면 ture를 return한다")
    void isOwnerTest1() {
        Assertions.assertThat(A1.isOwner(UserTest.DORAEMON)).isTrue();
        Assertions.assertThat(A2.isOwner(UserTest.SPONGEBOB)).isTrue();
    }

    @Test
    @DisplayName("답변 작성자가 다르면 false를 return한다")
    void isOwnerTest2() {
        Assertions.assertThat(A1.isOwner(UserTest.SPONGEBOB)).isFalse();
        Assertions.assertThat(A2.isOwner(UserTest.DORAEMON)).isFalse();
    }

    @Test
    void 답변삭제실패() {
        // given
        User loginUser = new User("abc", "", "", "");
        Answer answer = A1;

        // when
        Assertions.assertThatThrownBy(
                () -> answer.validateOwnership(loginUser)
        ).isInstanceOf(CannotDeleteException.class);
    }

    @Test
    @DisplayName("로그인한 유저가 답변 작성자이고 답변 삭제 시 성공 및 삭제 이력 발생")
    void 답변삭제성공() {
        // given: 답변과 그 답변의 작성자가 있는 상황에서
        User 춘식이 = new User("chunsik123", "", "춘식이", "");
        Answer 답변 = new Answer(춘식이, new Question(), "");

        // when: 답변 작성자(로그인한 유저)가 답변을 삭제했을 때
        답변.validateOwnership(춘식이);
        DeleteHistory deleteHistory = 답변.delete();

        // then: 답변이 삭제(soft delete)되고 그 히스토리가 만들어진다
        Assertions.assertThat(답변.isDeleted()).isTrue();
        Assertions.assertThat(deleteHistory)
                .isEqualTo(
                        new DeleteHistory(
                                ContentType.ANSWER,
                                답변.getId(),
                                답변.getWriter(),
                                LocalDateTime.now() // 여기는 뭐가 들어가든 상관 없음
                        )
                );
    }
}
