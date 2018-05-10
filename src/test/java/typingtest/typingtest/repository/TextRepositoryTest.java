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

        Text text = new Text("Example text", "Example author");

        Double score1 = 13.3;
        Double score2 = 22.3;

        UserText userText1 = new UserText(user1, text, score1);
        UserText userText2 = new UserText(user2, text, score2);

        user1.getUserTexts().add(userText1);
        user2.getUserTexts().add(userText2);

        entityManager.persist(text);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // when
        List<Object[]> scoresForText = textRepository.getScoresForText(text.getId());
        List<Double> scores = new ArrayList<>();
        for (Object[] object : scoresForText) {
            scores.add((double) object[1]);
        }

        // then
        assertThat(score1).isIn(scores);
        assertThat(score2).isIn(scores);
    }

    @Test
    public void givenTextAndUsersInDB_WhenGetBestScoresForText_ThenReturnProperScores() {
        // given
        Text text = new Text("Example text", "Example author");
        entityManager.persist(text);

        final int scoresToPutInDB = 15;

        for (int i = 0; i < scoresToPutInDB; i++) {
            User user = new User("John_" + i);
            Double score = new Random().nextDouble() * 100.0;
            UserText userText = new UserText(user, text, score);
            user.getUserTexts().add(userText);
            entityManager.persist(user);
        }

        entityManager.flush();

        // when
        List<Double> bestScoresForText = textRepository.getBestScoresForText(text.getId());

        // then
        assertThat(bestScoresForText).isNotNull();
        assertThat(bestScoresForText).hasSize(10);
        assertThat(bestScoresForText.get(0)).isGreaterThanOrEqualTo(bestScoresForText.get(1));
    }

}