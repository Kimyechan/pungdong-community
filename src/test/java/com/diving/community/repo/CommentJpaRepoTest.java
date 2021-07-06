package com.diving.community.repo;

import com.diving.community.domain.account.Account;
import com.diving.community.domain.comment.Comment;
import com.diving.community.domain.post.Post;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CommentJpaRepoTest {
    @Autowired
    private CommentJpaRepo commentJpaRepo;

    @Autowired
    private TestEntityManager em;

    public Post saveComments() {
        Post post = Post.builder()
                .build();
        em.persist(post);

        for (int i = 1; i < 11; i++) {
            Account account = Account.builder()
                    .id((long) i)
                    .build();
            em.persist(account);

            Comment comment = Comment.builder()
                    .writer(account)
                    .post(post)
                    .build();
            em.persist(comment);
        }

        em.flush();
        em.clear();

        return post;
    }

    @Test
    @DisplayName("댓글 작성자와 함께 댓글 목록 조회")
    public void findByPostIdWithWriter() {
        Post post = saveComments();
        Pageable pageable = PageRequest.of(0, 5);

        Page<Comment> commentPage = commentJpaRepo.findByPostIdWithWriter(post.getId(), pageable);

        for (Comment comment : commentPage.getContent()) {
            assertTrue(Hibernate.isInitialized(comment.getWriter()));
        }
    }
}