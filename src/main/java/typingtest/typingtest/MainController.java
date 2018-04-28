package typingtest.typingtest;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

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
