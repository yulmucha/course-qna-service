package qna.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qna.exception.CannotDeleteException;

public class QuestionTest {
    public static final Question Q1 = new Question("title1", "contents1").writeBy(UserTest.DORAEMON);
    public static final Question Q2 = new Question("title2", "contents2").writeBy(UserTest.SPONGEBOB);


    @Test
    @DisplayName("질문 작성자가 같으면 ture를 return한다")
    void isOwnerTest1() {
        Assertions.assertThat(Q1.isOwner(UserTest.DORAEMON)).isTrue();
    }

    @Test
    @DisplayName("질문 작성자가 다르면 false를 return한다")
    void isOwnerTest2() {
        Assertions.assertThat(Q1.isOwner(UserTest.SPONGEBOB)).isFalse();
    }

    @Test
    @DisplayName("작성자 검증 함수에 작성자가 아닌 다른 유저가 전달되면 예외가 발생한다")
    void validateOwnershipTest1() {
        // given
        User 질문_작성자 = new User("a", "", "", "");
        User 다른_유저 = new User("b", "", "", "");
        Question 질문 = new Question("title1", "contents1").writeBy(질문_작성자);

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
        Question 질문 = new Question("title1", "contents1").writeBy(질문_작성자);

        // when & then
        질문.validateOwnership(질문_작성자);
    }
}
