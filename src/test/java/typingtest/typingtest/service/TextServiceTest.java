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
import typingtest.typingtest.data.repository.TextRepository;
import typingtest.typingtest.data.service.TextService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class TextServiceTest {

    @TestConfiguration
    static class TextServiceTestContextConfiguration {
        @Bean
        public TextService textService() {
            return new TextService();
        }
    }

    @Autowired
    private TextService textService;

    @MockBean
    private TextRepository textRepository;


    @Test
    public void givenText_WhenGetText_ThenTextShouldBeFound() {
        // given
        Text text = new Text("text","author");
        Mockito.when(textRepository.findById(text.getId())).thenReturn(java.util.Optional.of(text));

        // when
        Text receivedText = textService.getText(text.getId());

        // then
        assertThat(receivedText).isEqualTo(text);
    }

    @Test
    public void givenTexts_WhenGetAllTexts_ThenReturnAllTexts() {
        // given
        List<Text> texts = new ArrayList<>();
        texts.add(new Text("text1","author1"));
        texts.add(new Text("text2","author2"));
        texts.add(new Text("text3","author3"));

        Mockito.when(textRepository.findAll()).thenReturn(texts);

        // when
        List<Text> allTexts = textService.getAllTexts();

        // then
        assertThat(allTexts).isEqualTo(texts);
    }

    @Test
    public void givenScores_WhenGetScoresForText_ThenReturnScores() {
        //given
        Object[] object1 = new Object[2];
        Object[] object2 = new Object[2];
        Object[] object3 = new Object[2];

        User user1 =  new User("user1");
        User user2 =  new User("user2");
        User user3 =  new User("user3");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);

        object1[0] = user1;
        object1[1] = 23.4;

        object2[0] = user2;
        object2[1] = 4.4;

        object3[0] = user3;
        object3[1] = 65.1;

        List<Object[]> list = new ArrayList<>();
        list.add(object1);
        list.add(object2);
        list.add(object3);

        Long textId = 1L;

        Mockito.when(textRepository.getScoresForText(textId)).thenReturn(list);

        // when
        Map<User, Double> scoresForText = textService.getScoresForText(textId);

        // then
        Map<User, Double> map = new HashMap<>();
        map.put(user1, 23.4);
        map.put(user2, 4.4);
        map.put(user3, 65.1);

        assertThat(scoresForText).isEqualTo(map);
    }

    @Test
    public void givenScores_WhenGetBestScoresForText_ThenReturnProperScores() {
        // given
        List<Double> list = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < 10; i++)
            list.add(random.nextDouble() * 100);

        list.sort(Collections.reverseOrder());

        Long textId = 1L;

        Mockito.when(textRepository.getBestScoresForText(textId)).thenReturn(list);

        // when
        List<Double> bestScoresForText = textService.getBestScoresForText(textId);

        // then
        assertThat(bestScoresForText).isEqualTo(list);
    }
}
