package typingtest.typingtest;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import typingtest.typingtest.data.model.Text;
import typingtest.typingtest.data.model.User;
import typingtest.typingtest.data.service.MainService;

@Controller
public class MainController {

    private MainService mainService;

    @Autowired
    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    @RequestMapping("/")
    public String home(Map<String, Object> model) {
        model.put("args", "content");

        User user = new User("mike");
        Text text = new Text("very long text");

        mainService.addScoreForUser(user,text,3.2);

        return "index";
    }

    @RequestMapping("/database")
    public String database(Map<String, Object> model) {
        model.put("args", "content");
        return "database";
    }
}
