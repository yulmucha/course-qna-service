package qna.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
