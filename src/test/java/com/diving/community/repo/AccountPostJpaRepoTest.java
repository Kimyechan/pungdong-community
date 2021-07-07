package com.diving.community.repo;

import com.diving.community.domain.AccountPost;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Post;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AccountPostJpaRepoTest {
    @Autowired
    private AccountPostJpaRepo accountPostJpaRepo;

    @Autowired
    private TestEntityManager em;

    public Account saveAccountPosts() {
        Account account = Account.builder().id(1L).build();
        Account savedAccount = em.persist(account);

        for (int i = 0; i < 5; i++) {
            Post post = Post.builder().build();
            Post savedPost = em.persist(post);
            AccountPost accountPost = AccountPost.builder()
                    .post(savedPost)
                    .account(savedAccount)
                    .build();
            em.persist(accountPost);
        }

        em.flush();
        em.clear();

        return account;
    }

    @Test
    @DisplayName("회원의 좋아요 목록 전체 조회")
    public void findLikePostByAccount() {
        Account account = saveAccountPosts();

        List<AccountPost> accountPostList = accountPostJpaRepo.findByAccount(account.getId());

        for (AccountPost accountPost : accountPostList) {
            assertTrue(Hibernate.isInitialized(accountPost.getPost()));
        }
    }
}