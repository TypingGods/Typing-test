package typingtest.typingtest.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import typingtest.typingtest.data.model.Text;
import typingtest.typingtest.data.model.User;
import typingtest.typingtest.data.service.MainService;
import typingtest.typingtest.data.service.TextService;
import typingtest.typingtest.data.service.UserService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@RunWith(SpringRunner.class)
public class MainServiceTest {

    @TestConfiguration
    static class MainServiceTestContextConfiguration {
        @Bean
        public MainService mainService() {
            return new MainService();
        }
    }

    @Autowired
    private MainService mainService;

    @MockBean
    private UserService userService;

    @MockBean
    private TextService textService;

    @Test
    public void givenText_WhenGetText_ThenTextShouldBeFound() {
        // given
        Text text = new Text("text","author");
        text.setId(1L);
        Mockito.when(textService.getText(text.getId())).thenReturn(text);

        // when
        Text receivedText = mainService.getText(Math.toIntExact(text.getId()));

        // then
        assertThat(receivedText).isEqualTo(text);
    }

    @Test
    public void givenScores_WhenGetScoresForText_ThenScoresShouldBeFound() {
        //given
        Map<Long, Double> result = new TreeMap<>();
        result.put(1L, 23.3);
        result.put(2L, 43.2);
        result.put(3L, 11.2);

        Text text = new Text("text","author");
        text.setId(1L);

        User user1 = new User("user1");
        User user2 = new User("user2");
        User user3 = new User("user3");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        Mockito.when(textService.getScoresForText((text.getId()))).thenReturn(result);
        Mockito.when(userService.getUser(user1.getId())).thenReturn(user1);
        Mockito.when(userService.getUser(user2.getId())).thenReturn(user2);
        Mockito.when(userService.getUser(user3.getId())).thenReturn(user3);

        // when
        Map<User, Double> scoresForText = mainService.getScoresForText(Math.toIntExact(text.getId()));

        // then
        assertThat(scoresForText).containsExactly(entry(user1, 23.3), entry(user2,43.2), entry(user3, 11.2));
    }

    @Test
    public void givenScores_WhenAddScoreForUser_ThenScoresShouldBeAddedOrNot() {
        // given
        List<Double> scoresList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; ++i)
            scoresList.add(random.nextDouble() * 100);

        Text text = new Text("text", "author");
        text.setId(1L);

        Mockito.when(textService.getBestScoresForText(text.getId())).thenReturn(scoresList);
        Mockito.when(textService.getText(text.getId())).thenReturn(text);
        Mockito.doNothing().when(textService).addText(text);
        Mockito.doNothing().when(userService).addUser(new User());

        scoresList.sort(Collections.reverseOrder());

        for (int i = 0; i < 100; ++i) {
            Double rand = random.nextDouble() * 100;
            // when
            boolean added = mainService.addScoreForUser("user", Math.toIntExact(text.getId()), rand);
            // then
            if (rand > scoresList.get(9))
                assertThat(added).isTrue();
            else
                assertThat(added).isFalse();
        }
    }
}
