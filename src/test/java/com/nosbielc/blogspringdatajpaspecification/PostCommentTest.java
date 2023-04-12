package com.nosbielc.blogspringdatajpaspecification;

import com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.entities.Post;
import com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.entities.PostComment;
import com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.entities.User;
import com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.enums.CommentStatus;
import com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.repositories.PostCommentRepository;
import com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.repositories.PostRepository;
import com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.nosbielc.blogspringdatajpaspecification.infrastructure.persistence.repositories.PostCommentRepository.Specs.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
class PostCommentTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;

    private List<User> userList = new ArrayList<>();
    private List<Post> postList = new ArrayList<>();
    private List<PostComment> postCommentList = new ArrayList<>();


    @BeforeEach
    void setUp() throws InterruptedException {
        var random = new Random();
        var user1 = User.builder()
                .withFirstName("John")
                .withLastName("Snow")
                .withEmail("j_snow@email.com")
                .withAge(76)
                .build();

        var user2 = User.builder()
                .withFirstName("Mario")
                .withLastName("Pizza")
                .withEmail("m_pizza@email.com")
                .withAge(12)
                .build();

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        userList.add(user1);
        userList.add(user2);

        for (int i = 0; i < 56; i++) {

            var post = Post.builder()
                    .withTitle(format("Post title %s", i))
                    .withContent(format("Post Content %s", i))
                    .withCreatedAt(LocalDateTime.now().plusSeconds(i))
                    .withUser(i % 2 == 0 ? user1 : user2 )
                    .build();
            post = postRepository.save(post);

            postList.add(post);

            for (int j = 0; j < 10; j++) {

                var postcomment = postCommentRepository.save(PostComment.builder()
                        .withPost(post)
                        .withUser(j % 2 == 0 ? user1 : user2)
                        .withReview(format("Review %s", j))
                        .withVotes(random.nextInt(21))
                        .withCreatedAt(LocalDateTime.now().plusSeconds(j))
                        .withStatus(CommentStatus.fromCode(random.nextInt(5)))
                        .build());

                postCommentList.add(postcomment);

            }

        }

    }

    @AfterEach
    void exit() {
        userRepository.findAll().forEach(System.out::println);
        postRepository.findAll().forEach(System.out::println);
        postCommentRepository.findAll().forEach(System.out::println);
    }

    @ParameterizedTest
    @CsvSource({"1", "2", "3", "4", "5"})
    void testTotalCommentsPost(Long idPost) {

        var post = postRepository.findById(idPost);

        assertTrue(post.isPresent());

        var comments = postCommentRepository.findAll(
                byPost(post.get())
        );

        assertNotNull(comments);
        System.out.printf("Post tem %s comentarios.%n", comments.size());
        assertEquals(10, comments.size());
    }

    @ParameterizedTest
    @CsvSource({"6", "7", "8", "9", "10"})
    void testTotalCommentsPostOrderByCreatedOn(Long idPost) {

        var post = postRepository.findById(idPost);

        assertTrue(post.isPresent());

        var comments = postCommentRepository.findAll(
                orderByCreatedAt(
                        byPost(post.get())
                )
        );

        assertNotNull(comments);
        System.out.printf("Post tem %s comentarios.%n", comments.size());
        assertEquals(10, comments.size(), "A lista não tem 10 cometarios");

        assertOrderByCreated(comments, "A lista não está ordenada pela data de criação");
    }

    @ParameterizedTest
    @CsvSource({"0", "1", "2", "3", "4"})
    void testTotalCommentsPostOrderByCreatedOnAndStatus(int codeCommentStatus) {

        var postComment = postCommentList.stream().filter( f -> f.getStatus().equals(CommentStatus.fromCode(codeCommentStatus))).findFirst();

        if (postComment.isEmpty()) {
            System.out.println("Sem posts para avaliar");
            return;
        }

        var idPost = postComment.get().getPost().getId();
        var post = postRepository.findById(idPost);

        assertTrue(post.isPresent());

        var comments = postCommentRepository.findAll(
                orderByCreatedAt(
                        byPost(post.get())
                                .and(byStatus(CommentStatus.fromCode(codeCommentStatus)))
                )
        );

        var verifyData = postCommentList.stream().filter( f -> Objects.equals(f.getPost().getId(), idPost) && f.getStatus().equals(CommentStatus.fromCode(codeCommentStatus))).count();

        System.out.printf("Post tem %s comentarios com o status %s.%n", comments.size(), CommentStatus.fromCode(codeCommentStatus).getDisplayName());
        assertEquals(verifyData, comments.size(), format("A lista não tem %s cometarios com o status %s", comments.size(), CommentStatus.fromCode(codeCommentStatus).getDisplayName()));

        assertOrderByCreated(comments, "A lista por status não está ordenada pela data de criação");
    }

    @ParameterizedTest
    @CsvSource({"5", "12", "25", "15", "20"})
    void testTotalCommentsPostOrderByCreatedOnAndVotes(int minVotes) {

        var postComment = postCommentList.stream().filter( f -> f.getVotes() >= minVotes).findFirst();

        if (postComment.isEmpty()) {
            System.out.println("Sem posts para avaliar");
            return;
        }

        var idPost = postComment.get().getPost().getId();
        var post = postRepository.findById(idPost);

        assertTrue(post.isPresent());

        var comments = postCommentRepository.findAll(
                orderByCreatedAt(
                        byPost(post.get())
                                .and(byVotesGreaterThanEqual(minVotes))
                )
        );

        var verifyData = postCommentList.stream().filter( f -> Objects.equals(f.getPost().getId(), idPost) && f.getVotes() >= minVotes).count();

        System.out.printf("Post tem %s comentarios com votos maiores ou iguais a %s.%n", comments.size(), minVotes);
        assertEquals(verifyData, comments.size(), format("A lista não tem %s cometarios com votos maiores ou iguais a %s", comments.size(), minVotes));

        assertOrderByCreated(comments, "A lista por votes não está ordenada pela data de criação");
    }

    @ParameterizedTest
    @CsvSource({"5, 0, 12, Content", "12, 1, 10, Content", "25, 2, 5, Content", "15, 3, 18, Content", "20, 4, 1, Content"})
    void testTotalCommentsPostCombiningMultiple(Long idPost, int codeCommentStatus, int minVotes, String content) {

        var post = postRepository.findById(idPost);

        assertTrue(post.isPresent());

        var comments = postCommentRepository.findAll(
                orderByCreatedAt(
                        byPost(post.get())
                                .and(byStatus(CommentStatus.fromCode(codeCommentStatus)))
                                .and(byReviewLike(content))
                                .and(byVotesGreaterThanEqual(minVotes))
                )
        );

        var verifyData = postCommentList.stream().filter( f -> Objects.equals(f.getPost().getId(), idPost) && f.getVotes() >= minVotes && f.getStatus().equals(CommentStatus.fromCode(codeCommentStatus))
                && f.getReview().contains(content)).count();

        assertEquals(verifyData, comments.size(), format("A lista combinada não tem %s cometarios com votos maiores ou iguais a (%s) e status (%s) e que contenha (%s)",
                comments.size(), minVotes, CommentStatus.fromCode(codeCommentStatus).getDisplayName(), content));

        assertOrderByCreated(comments, "A lista combinada não está ordenada pela data de criação");
    }

    private static void assertOrderByCreated(List<PostComment> comments, String message) {
        boolean isSortedByAge = true;
        for (int i = 1; i < comments.size(); i++) {
            if (comments.get(i).getCreatedAt().isBefore(comments.get(i - 1).getCreatedAt())) {
                isSortedByAge = false;
                break;
            }
        }

        assertTrue(isSortedByAge, message);
    }

}
