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
import typingtest.typingtest.data.service.MainService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MainService service;

    @Test
    public void whenGetIndex_ThenReturnIndex() throws Exception{
        Text text1 = new Text("text1", "author1");
        Text text2 = new Text("text2", "author2");

        text1.setId(1L);
        text2.setId(2L);

        List<Text> texts = Arrays.asList(text1, text2);

        Mockito.when(service.getAllTexts()).thenReturn(texts);

        mvc.perform(get("/")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetDatabase_ThenReturnDatabase() throws Exception{
        mvc.perform(get("/database")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }
}
