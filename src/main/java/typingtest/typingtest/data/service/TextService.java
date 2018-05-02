package typingtest.typingtest.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import typingtest.typingtest.data.model.Text;
import typingtest.typingtest.data.repository.TextRepository;

import java.math.BigInteger;
import java.util.*;

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

    public List<Double> getBestScoresForText(Long textId){
        return textRepository.getBestScoresForText(textId);
    }

    public  Map<Long, Double> getScoresForText(Long textId){
        List<Object[]> result = textRepository.getScoresForText(textId);
        Map<Long, Double> map = null;
        if(result != null && !result.isEmpty()){
            map = new TreeMap<>();
            for (Object[] object : result) {
                map.put(((BigInteger)object[0]).longValue(), (Double) object[1]);
            }
        }
        return map;
    }

    public void updateText(Text text) {
        textRepository.save(text);
    }

    public void deleteText(Long textId) {
        textRepository.deleteById(textId);
    }
}
