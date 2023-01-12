package ru.practicum.shareit.comments;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.comments.interfaces.CommentRepository;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.comments.model.QComment;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
public class CommentRepositoryTests {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TestEntityManager entityManager;
    private User john = new User(1L, "John", "john@doe.com");
    private Item hammer = new Item(1L,
            "Hammer",
            "Hand hammer",
            true,
            john,
            null,
            null);
    private Comment comment = new Comment(1L, "New comment", hammer, john, LocalDateTime.now());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void getAllByItem() {
        userRepository.save(john);
        itemRepository.save(hammer);
        commentRepository.save(comment);
        List<Comment> comments = commentRepository.findAllByItem(hammer);
        assertThat(comments, hasSize(1));
        assertThat(comments.get(0).getCommentId(), equalTo(comment.getCommentId()));
        QComment qComment = QComment.comment;
        BooleanExpression condition = qComment.commentId.eq(comment.getCommentId());
        Iterable<Comment> commentsFromRepo = commentRepository.findAll(condition);
        commentsFromRepo.forEach(x -> comments.add(x));
        assertThat(comments, hasSize(2));
    }
}
