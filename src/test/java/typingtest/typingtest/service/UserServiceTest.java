package typingtest.typingtest.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import typingtest.typingtest.data.model.User;
import typingtest.typingtest.data.repository.UserRepository;
import typingtest.typingtest.data.service.UserService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @TestConfiguration
    static class UserServiceTestContextConfiguration {
        @Bean
        public UserService userService() {
            return new UserService();
        }
    }

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void givenUser_WhenGetUser_ThenUserShouldBeFound() {
        // given
        User user = new User("user1");
        user.setId(1L);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        User receivedUser = userService.getUser(user.getId());

        // then
        assertThat(receivedUser).isEqualTo(user);
    }
}
