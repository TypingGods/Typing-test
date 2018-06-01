package typingtest.typingtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import typingtest.typingtest.data.service.MainService;

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
        Random random = new Random();
        int textId = random.nextInt(5) + 1;
        model.addAttribute("text", mainService.getText(textId));
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
        model.addAttribute("scores", mainService.getScoresForText(textId));
        model.addAttribute("myText", mainService.getText(textId));
        return "database";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
