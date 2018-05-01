package typingtest.typingtest.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import typingtest.typingtest.data.model.Text;
import typingtest.typingtest.data.model.User;
import typingtest.typingtest.data.model.UserText;

@Service
public class MainService {
    private UserService userService;
    private TextService textService;

    @Autowired
    public MainService(UserService userService, TextService textService) {
        this.userService = userService;
        this.textService = textService;
    }

    public void addScoreForUser(User user, Text text, double score) {
        UserText userText = new UserText(user, text, score);
        user.getUserTexts().add(userText);
        textService.addText(text);
        userService.addUser(user);
    }
}
