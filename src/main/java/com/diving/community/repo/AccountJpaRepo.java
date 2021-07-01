package com.diving.community.repo;

import com.diving.community.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountJpaRepo extends JpaRepository<Account, Long> {
}
