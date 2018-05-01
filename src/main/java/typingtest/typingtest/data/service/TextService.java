package typingtest.typingtest.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import typingtest.typingtest.data.model.Text;
import typingtest.typingtest.data.repository.TextRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextService {

    private TextRepository textRepository;

    @Autowired
    public TextService(TextRepository textRepository) {
        this.textRepository = textRepository;
    }

    public List<Text> getAllTexts() {
        List<Text> texts = new ArrayList<>();
        textRepository.findAll().forEach(texts::add);
        return texts;
    }

    public Text getText(Long textId) {
        return textRepository.findById(textId).orElse(null);
    }

    public void addText(Text text) {
        textRepository.save(text);
    }

    public void updateText(Text text) {
        textRepository.save(text);
    }

    public void deleteText(Long textId) {
        textRepository.deleteById(textId);
    }
}
