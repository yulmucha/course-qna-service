package qna.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.Where;
import qna.exception.CannotDeleteException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Lob
    private String contents;

    @OneToMany(
            mappedBy = "question",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
    )
    @Where(clause = "deleted = false")
    private List<Answer> answers = new ArrayList<>();

    //    private Long writerId;
    @ManyToOne
    @JoinColumn(nullable = false)
    private User writer;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    protected Question() {
    }

    public Question(String title, String contents, User writer) {
        this(null, title, contents, writer);
    }

    public Question(Long id, String title, String contents, User writer) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.writer = writer;
    }

    public Question writeBy(User writer) {
        this.writer = writer;
        return this;
    }

    public boolean isOwner(User writer) {
        return this.writer.equals(writer);
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this); // Answer가 Question을 참조하게 만드는 부분
        this.answers.add(answer);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public Long getWriterId() {
        return writer.getId();
    }

    public User getWriter() {
        return writer;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public List<DeleteHistory> delete() {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(this.deleteQuestion());
        deleteHistories.addAll(this.deleteAnswers());
        return deleteHistories;
    }

    public DeleteHistory deleteQuestion() {
        this.deleted = true;
        return new DeleteHistory(
                ContentType.QUESTION,
                id,
                writer,
                LocalDateTime.now());
    }

    public List<DeleteHistory> deleteAnswers() {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for (Answer answer : answers) {
            deleteHistories.add(answer.delete());
        }
        return deleteHistories;
    }

    public void validate(User loginUser) {
        validateOwnership(loginUser);
        validateAllAnswerOwnership(loginUser);
    }

    protected void validateOwnership(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new CannotDeleteException("질문을 삭제할 권한이 없습니다.");
        }
    }

    protected void validateAllAnswerOwnership(User loginUser) {
        for (Answer answer : answers) {
            answer.validateOwnership(loginUser);
        }
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", writerId=" + writer.getId() +
                ", deleted=" + deleted +
                '}';
    }
}
