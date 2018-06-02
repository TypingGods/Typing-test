package typingtest.typingtest.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import typingtest.typingtest.data.model.Text;
import typingtest.typingtest.data.model.User;
import typingtest.typingtest.data.repository.TextRepository;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

@Service
public class TextService {

    private TextRepository textRepository;

    @Autowired
    public void setTextRepository(TextRepository textRepository) {
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

    public  Map<User, Double> getScoresForText(Long textId){
        List<Object[]> result = textRepository.getScoresForText(textId);
        if(result != null && !result.isEmpty()){
            Map<User, Double> map = new HashMap<>();
            for (Object[] object : result) {
                map.put((User)object[0], (Double) object[1]);
            }
            return map.entrySet().stream()
                    .sorted(Collections.reverseOrder(comparing(Map.Entry::getValue)))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (e1,e2) -> e1, LinkedHashMap::new));
        }
        return new HashMap<>();
    }
}
