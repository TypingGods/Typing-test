package typingtest.typingtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import typingtest.typingtest.data.model.Text;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = TypingTestApplication.class
)
@AutoConfigureMockMvc
public class TypingTestApplicationIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenGetIndex_ThenReturnIndex() throws Exception{
        mockMvc.perform(get("/")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("TYPING TEST")))
                .andExpect(model().attribute("text", instanceOf(Text.class)));
    }

    @Test
    public void whenGetDatabase_ThenReturnDatabase() throws Exception{
        mockMvc.perform(get("/database")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("database"))
                .andExpect(content().string(containsString("Best scores")))
                .andExpect(model().attribute("textList", instanceOf(Map.class)));
    }

    @Test
    public void whenGetAbout_ThenReturnAbout() throws Exception{
        mockMvc.perform(get("/about")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("about"));
    }

    @Test
    public void whenGetError_ThenReturnIndex() throws Exception{
        mockMvc.perform(get("/error")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("TYPING TEST")))
                .andExpect(model().attribute("text", instanceOf(Text.class)));
    }

    @Test
    public void whenGetNotProperURL_ThenGetNotFound() throws Exception{
        mockMvc.perform(get("/giveMeMoney")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound());
    }
}
