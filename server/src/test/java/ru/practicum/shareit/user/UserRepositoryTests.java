package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;
    private User john = new User(1L, "John", "john@doe.com");

    @Test
    void findById() {
        entityManager.merge(john);
        entityManager.flush();
        User user = userRepository.findById(john.getId()).get();
        assertThat(user.getName(), equalTo(john.getName()));
        assertThat(user.getId(), equalTo(john.getId()));
        assertThat(user.getEmail(), equalTo(john.getEmail()));
    }

    @Test
    void saveUser() {
        User user = userRepository.save(john);
        assertThat(user.getName(), equalTo(john.getName()));
        assertThat(user.getId(), equalTo(john.getId()));
        assertThat(user.getEmail(), equalTo(john.getEmail()));
    }

    @Test
    void existsByEmail() {
        entityManager.merge(john);
        entityManager.flush();
        Boolean result = userRepository.existsByEmailIgnoreCase(john.getEmail());
        assertThat(result, equalTo(true));
    }
}
