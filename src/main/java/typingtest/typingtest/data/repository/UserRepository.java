package typingtest.typingtest.data.repository;

import org.springframework.data.repository.CrudRepository;
import typingtest.typingtest.data.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
