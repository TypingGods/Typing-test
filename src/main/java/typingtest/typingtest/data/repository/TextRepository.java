package typingtest.typingtest.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import typingtest.typingtest.data.model.Text;

import java.util.List;

public interface TextRepository extends CrudRepository<Text, Long> {

    @Query(value = "SELECT score FROM person_text pt JOIN text t ON pt.text_id=t.id WHERE t.id=?1" +
            " ORDER BY score DESC LIMIT 10", nativeQuery = true)
    List<Double> getBestScoresForText(Long textId);

    @Query(value = "SELECT p.id, score FROM person p JOIN person_text pt ON p.id=pt.person_id WHERE" +
            " pt.text_id=?1 ORDER BY score DESC",nativeQuery = true)
    List<Object[]> getScoresForText(Long textId);
}
