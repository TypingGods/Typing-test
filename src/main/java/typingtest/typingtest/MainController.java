package typingtest.typingtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import typingtest.typingtest.data.model.Text;
import typingtest.typingtest.data.service.MainService;

import java.util.List;
import java.util.Random;

@Controller
public class MainController implements ErrorController {

    private MainService mainService;
    private static final String PATH = "/error";

    @Autowired
    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    @RequestMapping(value = {"/", PATH})
    public String home(Model model) {
        boolean textsInDB;
        List<Text> texts = mainService.getAllTexts();

        if(texts.isEmpty()) {
            textsInDB = false;
        }
        else {
            textsInDB = true;
            Random random = new Random();
            Text text = texts.get(random.nextInt(texts.size()));
            model.addAttribute("text", text);
        }

        model.addAttribute("textsInDB", textsInDB);
        return "index";
    }

    @RequestMapping(value = "/database", method = RequestMethod.GET)
    public String database(Model model) {
        model.addAttribute("textList", mainService.getScoresForAllTexts());
        return "database";
    }

    @RequestMapping(value = "/database", method = RequestMethod.POST)
    public String addToDatabase(Model model, String userName, int textId, Double score) {
        mainService.addScoreForUser(userName, textId, score);
        model.addAttribute("textList", mainService.getScoresForAllTexts());
        return "database";
    }

    @RequestMapping("/about")
    public String about() {
        return "about";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
