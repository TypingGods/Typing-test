package typingtest.typingtest.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import typingtest.typingtest.data.model.Text;
import typingtest.typingtest.data.model.User;
import typingtest.typingtest.data.model.UserText;
import typingtest.typingtest.data.repository.TextRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class TextRepositoryTest {

    @Autowired
    private TextRepository textRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void givenTextAndUsersInDB_WhenGetScoresForText_ThenReturnProperScores() {
        // given
        User user1 = new User("Adam");
        User user2 = new User("John");
        User user3 = new User("Walter");

        Text text1 = new Text("text1", "author1");
        Text text2 = new Text("text2", "author2");

        Double score1 = 13.3;
        Double score2 = 22.3;
        Double score3 = 314.2;

        UserText userText1 = new UserText(user1, text1, score1);
        UserText userText2 = new UserText(user2, text1, score2);
        UserText userText3 = new UserText(user3, text1, score3);
        UserText userText4 = new UserText(user3, text2, score1);

        user1.getUserTexts().add(userText1);
        user2.getUserTexts().add(userText2);
        user3.getUserTexts().add(userText3);
        user3.getUserTexts().add(userText4);

        entityManager.persist(text1);
        entityManager.persist(text2);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // when
        List<Object[]> scoresForText = textRepository.getScoresForText(text1.getId());
        Map<User, Double> scores = new HashMap<>();
        for (Object[] object : scoresForText) {
            scores.put((User)object[0], (Double) object[1]);
        }

        // then
        assertThat(scores).isNotEmpty().hasSize(3);
        assertThat(scores).contains(entry(user1, score1), entry(user2, score2), entry(user3, score3));
        assertThat(scores).doesNotContain(entry(user3, score1));
    }

    @Test
    public void givenNoUsersAndScoresInDB_WhenGetScoresForText_ThenReturnEmptyList() {
        // given
        Text text = new Text("text", "author");

        entityManager.persist(text);
        entityManager.flush();

        // when
        List<Object[]> scoresForText = textRepository.getScoresForText(text.getId());

        // then
        assertThat(scoresForText).isNotNull();
        assertThat(scoresForText).isEmpty();
    }

    @Test
    public void givenNoTextInDB_WhenGetScoresForText_ThenReturnEmptyList() {
        // given
        Long textId = 1L;

        // when
        List<Object[]> scoresForText = textRepository.getScoresForText(textId);

        // then
        assertThat(scoresForText).isNotNull();
        assertThat(scoresForText).isEmpty();
    }

    @Test
    public void givenTextAndUsersInDB_WhenGetBestScoresForText_ThenReturnProperScores() {
        // given
        Text text = new Text("text", "author");
        entityManager.persist(text);

        final int scoresToPutInDB = 45;

        for (int i = 0; i < scoresToPutInDB; i++) {
            User user = new User("John_" + i);
            Double score = new Random().nextDouble() * 500.0;
            UserText userText = new UserText(user, text, score);
            user.getUserTexts().add(userText);
            entityManager.persist(user);
        }

        entityManager.flush();

        // when
        List<Double> bestScoresForText = textRepository.getBestScoresForText(text.getId());

        // then
        assertThat(bestScoresForText).isNotNull();
        assertThat(bestScoresForText).isNotEmpty().hasSize(30);
        assertThat(bestScoresForText).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    public void givenNoUsersAndScoresInDB_WhenGetBestScoresForText_ThenReturnEmptyList() {
        // given
        Text text = new Text("text", "author");
        entityManager.persist(text);
        entityManager.flush();

        // when
        List<Double> bestScoresForText = textRepository.getBestScoresForText(text.getId());

        // then
        assertThat(bestScoresForText).isNotNull();
        assertThat(bestScoresForText).isEmpty();
    }

    @Test
    public void givenNoTextInDB_WhenGetBestScoresForText_ThenReturnEmptyList() {
        // given
        Long textId = 1L;

        // when
        List<Double> bestScoresForText = textRepository.getBestScoresForText(textId);

        // then
        assertThat(bestScoresForText).isNotNull();
        assertThat(bestScoresForText).isEmpty();
    }
}