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
import static org.mockito.ArgumentMatchers.anyLong;

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
        for (int i = 0; i < 30; ++i)
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
            if (rand > scoresList.get(29))
                assertThat(added).isTrue();
            else
                assertThat(added).isFalse();
        }
    }

    @Test
    public void givenNoText_WhenAddScoreForUser_ThenExceptionShouldBeThrown() {
        // given
        Long textId = 1L;

        Mockito.when(textService.getBestScoresForText(textId)).thenReturn(Collections.emptyList());
        Mockito.when(textService.getText(textId)).thenReturn(null);
        Mockito.doNothing().when(textService).addText(null);
        Mockito.doNothing().when(userService).addUser(new User());

        Random random = new Random();
        Double rand = random.nextDouble() * 500;

        // when
        try {
            mainService.addScoreForUser("user", Math.toIntExact(textId), rand);
        }
        // then
        catch (Exception e) {
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    public void givenNoScores_WhenAddScoreForUser_ThenScoresShouldBeAdded() {
        // given
        Text text = new Text("text", "author");
        text.setId(1L);

        Mockito.when(textService.getBestScoresForText(text.getId())).thenReturn(Collections.emptyList());
        Mockito.when(textService.getText(text.getId())).thenReturn(text);
        Mockito.doNothing().when(textService).addText(text);
        Mockito.doNothing().when(userService).addUser(new User());

        Random random = new Random();

        for (int i = 0; i < 30; ++i) {
            Double rand = random.nextDouble() * 500;
            // when
            boolean added = mainService.addScoreForUser("user", Math.toIntExact(text.getId()), rand);
            // then
            assertThat(added).isTrue();
        }
    }

    @Test
    public void givenScores_WhenGetScoresForAllText_ThenScoresShouldBeFound() {
        // given
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

        Mockito.when(textService.getAllTexts()).thenReturn(Arrays.asList(text1, text2));
        Mockito.when(textService.getScoresForText(text1.getId())).thenReturn(userMap1);
        Mockito.when(textService.getScoresForText(text2.getId())).thenReturn(userMap2);

        // when
        Map<Text, Map<User,Double>> result = mainService.getScoresForAllTexts();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty().hasSize(2);
        assertThat(result).containsExactly(entry(text1, userMap1), entry(text2, userMap2));
    }

    @Test
    public void givenNoScores_WhenGetScoresForAllText_ThenScoresShouldBeEmpty() {
        // given
        Text text1 = new Text("text1", "author1");
        Text text2 = new Text("text2", "author2");

        text1.setId(1L);
        text2.setId(2L);

        Mockito.when(textService.getAllTexts()).thenReturn(Arrays.asList(text1, text2));
        Mockito.when(textService.getScoresForText(text1.getId())).thenReturn(Collections.emptyMap());
        Mockito.when(textService.getScoresForText(text2.getId())).thenReturn(Collections.emptyMap());

        // when
        Map<Text, Map<User,Double>> result = mainService.getScoresForAllTexts();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty().hasSize(2);
        assertThat(result).containsExactly(entry(text1, Collections.emptyMap()), entry(text2, Collections.emptyMap()));
    }

    @Test
    public void givenNoTexts_WhenGetScoresForAllText_ThenReturnEmptyMap() {
        // given
        Mockito.when(textService.getAllTexts()).thenReturn(Collections.emptyList());
        Mockito.when(textService.getScoresForText(anyLong())).thenReturn(Collections.emptyMap());

        // when
        Map<Text, Map<User,Double>> result = mainService.getScoresForAllTexts();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}
