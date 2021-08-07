package com.diving.community.repo;

import com.diving.community.domain.AccountPost;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountPostJpaRepo extends JpaRepository<AccountPost, Long> {
    @Query("select ap from AccountPost ap join fetch ap.post where ap.account.id = :accountId")
    List<AccountPost> findByAccount(@Param("accountId") Long accountId);

    void deleteByAccountAndPost(Account account, Post post);

    @Query(
            value = "select ap from AccountPost ap join fetch ap.post where ap.account.id = :accountId",
            countQuery = "select count(ap) from AccountPost ap where ap.account.id = :accountId")
    Page<AccountPost> findByAccount(@Param("accountId") Long accountId, Pageable pageable);

    Optional<AccountPost> findByAccountAndPost(Account account, Post post);
}