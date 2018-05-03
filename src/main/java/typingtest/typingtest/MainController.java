package typingtest.typingtest;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
        return "index";
    }

    @RequestMapping("/database")
    public String database(Map<String, Object> model) {
        model.put("args", "content");
        return "database";
    }
}
