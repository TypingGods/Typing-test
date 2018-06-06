package typingtest.typingtest.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import typingtest.typingtest.MainController;
import typingtest.typingtest.data.model.Text;
import typingtest.typingtest.data.model.User;
import typingtest.typingtest.data.service.MainService;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MainService mainService;

    @Test
    public void whenGetIndex_ThenReturnIndex() throws Exception{
        Text text1 = new Text("To be, or not to be, that is the question 1", "William Shakespeare");
        Text text2 = new Text("To be, or not to be, that is the question 2", "William Shakespeare");

        text1.setId(1L);
        text2.setId(2L);

        Mockito.when(mainService.getAllTexts()).thenReturn(Arrays.asList(text1, text2));

        mockMvc.perform(get("/")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("To be, or not to be, that is the question")))
                .andExpect(content().string(containsString("TYPING TEST")))
                .andExpect(model().attribute("text", instanceOf(Text.class)));
    }

    @Test
    public void whenGetDatabase_ThenReturnDatabase() throws Exception{
        Text text1 = new Text("To be, or not to be, that is the question", "William Shakespeare");
        Text text2 = new Text("I do know some things", "Jon Snow");

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

        Map<Text, Map<User, Double>> scores = new HashMap<>();
        scores.put(text1, userMap1);
        scores.put(text2, userMap2);

        Mockito.when(mainService.getScoresForAllTexts()).thenReturn(scores);

        mockMvc.perform(get("/database")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("database"))
                .andExpect(content().string(containsString("Best scores")))
                .andExpect(content().string(containsString("To be, or not to be, that is the question")))
                .andExpect(content().string(containsString("William Shakespeare")))
                .andExpect(content().string(containsString("I do know some things")))
                .andExpect(content().string(containsString("Jon Snow")))
                .andExpect(content().string(containsString(String.valueOf(score1))))
                .andExpect(content().string(containsString(String.valueOf(score2))))
                .andExpect(model().attribute("textList", equalTo(scores)));
    }

    @Test
    public void whenAddToDatabase_ThenReturnDatabaseAndMessage() throws Exception{
        int textId = 1;
        Double score = 314.15;
        String userName = "Jack Sparrow";

        Mockito.when(mainService.addScoreForUser(userName, textId, score)).thenReturn(true);
        Mockito.when(mainService.getScoresForAllTexts()).thenReturn(Collections.emptyMap());

        mockMvc.perform(post("/database")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("userName", userName)
                .param("textId", String.valueOf(textId))
                .param("score", String.valueOf(score))
        )
                .andExpect(status().isOk())
                .andExpect(view().name("database"))
                .andExpect(content().string(containsString("Best scores")))
                .andExpect(content().string(containsString("Your score is good enough to be added to database!")))
                .andExpect(model().attribute("added", is(true)))
                .andExpect(model().attribute("textList", Collections.emptyMap()));
    }

    @Test
    public void whenNotAddingToDatabase_ThenReturnDatabaseAndMessage() throws Exception{
        int textId = 3;
        Double score = 4.01;
        String userName = "Will Turner";

        Mockito.when(mainService.addScoreForUser(userName, textId, score)).thenReturn(false);
        Mockito.when(mainService.getScoresForAllTexts()).thenReturn(Collections.emptyMap());

        mockMvc.perform(post("/database")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("userName", userName)
                .param("textId", String.valueOf(textId))
                .param("score", String.valueOf(score))
        )
                .andExpect(status().isOk())
                .andExpect(view().name("database"))
                .andExpect(content().string(containsString("Best scores")))
                .andExpect(content().string(containsString("Your score is too low to be added to database. Try again!")))
                .andExpect(model().attribute("added", is(false)))
                .andExpect(model().attribute("textList", Collections.emptyMap()));
    }

    @Test
    public void whenGetAbout_ThenReturnAbout() throws Exception{
        mockMvc.perform(get("/about")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("about"));
    }

    @Test
    public void whenGetError_ThenReturnIndex() throws Exception{
        Text text = new Text("To be, or not to be, that is the question", "William Shakespeare");
        text.setId(1L);

        Mockito.when(mainService.getAllTexts()).thenReturn(Collections.singletonList(text));

        mockMvc.perform(get("/error")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("TYPING TEST")))
                .andExpect(content().string(containsString("To be, or not to be, that is the question")))
                .andExpect(model().attribute("text", equalTo(text)));
    }

    @Test
    public void whenGetNotProperURL_ThenGetNotFound() throws Exception{
        mockMvc.perform(get("/giveMeMoney")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound());
    }
}
