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
import static org.assertj.core.api.Assertions.entry;

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
        text.setId(1L);
        Mockito.when(textRepository.findById(text.getId())).thenReturn(java.util.Optional.of(text));

        // when
        Text receivedText = textService.getText(text.getId());

        // then
        assertThat(receivedText).isEqualTo(text);
    }

    @Test
    public void givenNoText_WhenGetText_ThenExceptionShouldBeThrown() {
        // given
        Long textId = 1L;
        Mockito.when(textRepository.findById(textId)).thenReturn(null);

        // when
        try {
            textService.getText(textId);
        }
        // then
        catch (Exception e) {
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
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
        assertThat(allTexts).isNotNull();
        assertThat(allTexts).isNotEmpty().hasSize(3);
        assertThat(allTexts).isEqualTo(texts);
    }

    @Test
    public void givenNoTexts_WhenGetAllTexts_ThenReturnEmptyList() {
        // given
        Mockito.when(textRepository.findAll()).thenReturn(new ArrayList<>());

        // when
        List<Text> allTexts = textService.getAllTexts();

        // then
        assertThat(allTexts).isNotNull();
        assertThat(allTexts).isEmpty();
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

        Double score1 = 23.4;
        object1[0] = user1;
        object1[1] = score1;

        Double score2 = 4.4;
        object2[0] = user2;
        object2[1] = score2;

        Double score3 = 65.1;
        object3[0] = user3;
        object3[1] = score3;

        List<Object[]> list = new ArrayList<>();
        list.add(object1);
        list.add(object2);
        list.add(object3);

        Long textId = 1L;

        Mockito.when(textRepository.getScoresForText(textId)).thenReturn(list);

        // when
        Map<User, Double> scoresForText = textService.getScoresForText(textId);

        // then
        assertThat(scoresForText).isNotNull();
        assertThat(scoresForText).isNotEmpty().hasSize(3);
        assertThat(scoresForText).containsExactly(entry(user3, score3), entry(user1, score1), entry(user2, score2));
    }

    @Test
    public void givenNoScores_WhenGetScoresForText_ThenReturnEmptyMap() {
        //given
        Long textId = 1L;
        Mockito.when(textRepository.getScoresForText(textId)).thenReturn(new ArrayList<>());

        // when
        Map<User, Double> scoresForText = textService.getScoresForText(textId);

        // then
        assertThat(scoresForText).isNotNull();
        assertThat(scoresForText).isEmpty();
    }

    @Test
    public void givenNoText_WhenGetScoresForText_ThenReturnEmptyMap() {
        //given
        Long textId = 1L;
        Mockito.when(textRepository.getScoresForText(textId)).thenReturn(null);

        // when
        Map<User, Double> scoresForText = textService.getScoresForText(textId);

        // then
        assertThat(scoresForText).isNotNull();
        assertThat(scoresForText).isEmpty();
    }
}
