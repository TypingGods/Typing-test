package typingtest.typingtest.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import typingtest.typingtest.data.model.Text;
import typingtest.typingtest.data.model.User;
import typingtest.typingtest.data.model.UserText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MainService {

    private UserService userService;
    private TextService textService;

    @Autowired
    public MainService(UserService userService, TextService textService) {
        this.userService = userService;
        this.textService = textService;
    }

    public Map<String, String> getText(Long textId) {
        Map<String, String> map = new HashMap<>();
        map.put(textService.getText(textId).getAuthor(), textService.getText(textId).getText());
        return map;
    }

    public Map<Long,Double> getScoresForText(Long textId) {
        return textService.getScoresForText(textId);
    }

    public boolean addScoreForUser(String userName, Long textId, Double score) {
        List<Double> tmp = textService.getBestScoresForText(textId);

        if(tmp.size() < 10 || score > tmp.get(tmp.size()-1)){
            User user = new User(userName);
            Text text = textService.getText(textId);
            UserText userText = new UserText(user, text, score);
            user.getUserTexts().add(userText);
            textService.addText(text);
            userService.addUser(user);
            return true;
        }
        return false;
    }
}
