package com.diving.community.repo;

import com.diving.community.domain.AccountPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountPostJpaRepo extends JpaRepository<AccountPost, Long> {
    @Query("select ap from AccountPost ap join fetch ap.post where ap.account.id = :accountId")
    List<AccountPost> findByAccount(@Param("accountId") Long accountId);
}
