package qna.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DeleteHistoryRepositoryTest {

    @Autowired
    private DeleteHistoryRepository deleteHistoryRepository;

    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void testSaveDeleteHistory() {
        // Given
        // DeleteHistory 객체를 생성한다. (ContentType, contentId, deletedById, createDate 설정)
        User 삭제자 = userRepository.save(new User("", "", "", ""));
        DeleteHistory deleteHistory = new DeleteHistory(ContentType.ANSWER, 1L, 삭제자, LocalDateTime.now());

        // When
        // 생성한 DeleteHistory 객체를 저장한다.
        DeleteHistory saved = deleteHistoryRepository.save(deleteHistory);

        // Then
        // 저장된 DeleteHistory 객체가 null이 아닌지 확인한다.
        // 저장된 DeleteHistory 객체의 ID가 null이 아닌지 확인한다.
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    public void testFindDeleteHistoryById() {
        // Given
        // DeleteHistory 객체를 생성하고 저장한다.
        User 삭제자 = userRepository.save(new User("", "", "", ""));
        DeleteHistory saved = deleteHistoryRepository.save(new DeleteHistory(ContentType.ANSWER, 1L, 삭제자, LocalDateTime.now()));

        // When
        // 저장된 DeleteHistory의 ID로 조회한다.
        DeleteHistory deleteHistory = deleteHistoryRepository.findById(saved.getId())
                .orElse(null);

        // Then
        // 조회된 DeleteHistory 객체가 null이 아닌지 확인한다.
        // 조회된 DeleteHistory 객체의 ContentType이 기대한 값과 일치하는지 확인한다.
        // 조회된 DeleteHistory 객체의 contentId가 기대한 값과 일치하는지 확인한다.
        assertThat(deleteHistory).isNotNull();
        assertThat(deleteHistory.getContentType()).isEqualTo(saved.getContentType());
        assertThat(deleteHistory.getContentId()).isEqualTo(saved.getContentId());
    }

    @Test
    public void testFindAllDeleteHistories() {
        // Given
        // 여러 개의 DeleteHistory 객체를 생성하고 저장한다.
        User 삭제자 = userRepository.save(new User("", "", "", ""));
        DeleteHistory saved1 = deleteHistoryRepository.save(new DeleteHistory(ContentType.ANSWER, 1L, 삭제자, LocalDateTime.now()));
        DeleteHistory saved2 = deleteHistoryRepository.save(new DeleteHistory(ContentType.ANSWER, 2L, 삭제자, LocalDateTime.now()));
        DeleteHistory saved3 = deleteHistoryRepository.save(new DeleteHistory(ContentType.ANSWER, 3L, 삭제자, LocalDateTime.now()));

        // When
        // 모든 DeleteHistory 객체를 조회한다.
        List<DeleteHistory> deleteHistories = deleteHistoryRepository.findAll();

        // Then
        // 조회된 DeleteHistory 리스트가 비어있지 않은지 확인한다.
        // 조회된 DeleteHistory 리스트의 크기가 기대한 값과 일치하는지 확인한다.
        assertThat(deleteHistories).isNotEmpty();
        assertThat(deleteHistories).hasSize(3);
    }
}
