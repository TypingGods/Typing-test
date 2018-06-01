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

    @Test
    public void givenScores_WhenGetScoresForAllText_ThenScoresShouldBeFound() {
        // given
        Map<Text, Map<User,Double>>  textMap = new TreeMap<>(Comparator.comparing(Text::getId));
        Text text1 = new Text("text1", "author1");
        Text text2 = new Text("text2", "author2");

        text1.setId(1L);
        text2.setId(2L);

        Map<User, Double> userMap1 = new HashMap<>();
        Map<User, Double> userMap2 = new HashMap<>();

        User user1 = new User("user1");
        User user2 = new User("user2");

        Double score1 = 12.3;
        Double score2 = 78.5;

        userMap1.put(user1, score1);
        userMap1.put(user2, score2);

        userMap2.put(user1, score2);
        userMap2.put(user2, score1);

        textMap.put(text1, userMap1);
        textMap.put(text2, userMap2);

        Mockito.when(textService.getAllTexts()).thenReturn(Arrays.asList(text1, text2));
        Mockito.when(textService.getScoresForText(text1.getId())).thenReturn(userMap1);
        Mockito.when(textService.getScoresForText(text2.getId())).thenReturn(userMap2);

        // when
        Map<Text, Map<User,Double>> result = mainService.getScoresForAllTexts();

        // then
        assertThat(result).isEqualTo(textMap);
    }
}
