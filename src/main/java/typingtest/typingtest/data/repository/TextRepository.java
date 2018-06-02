package typingtest.typingtest.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import typingtest.typingtest.data.model.Text;

import java.util.List;

public interface TextRepository extends CrudRepository<Text, Long> {

    @Query(value = "SELECT score FROM person_text pt JOIN text t ON pt.text_id=t.id WHERE t.id=?1" +
            " ORDER BY score DESC LIMIT 30", nativeQuery = true)
    List<Double> getBestScoresForText(Long textId);

    @Query("SELECT u, ut.score FROM User u JOIN UserText ut ON u.id=ut.person.id WHERE" +
            " ut.text.id=?1 ORDER BY ut.score DESC")
    List<Object[]> getScoresForText(Long textId);
}
