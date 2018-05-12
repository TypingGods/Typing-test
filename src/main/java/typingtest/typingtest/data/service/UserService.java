package typingtest.typingtest.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import typingtest.typingtest.data.model.User;
import typingtest.typingtest.data.repository.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository personRepository) {
        this.userRepository = personRepository;
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }
}
