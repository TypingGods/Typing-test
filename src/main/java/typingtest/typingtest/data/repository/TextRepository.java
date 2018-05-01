package typingtest.typingtest.data.repository;

import org.springframework.data.repository.CrudRepository;
import typingtest.typingtest.data.model.Text;

public interface TextRepository extends CrudRepository<Text, Long> {
}
