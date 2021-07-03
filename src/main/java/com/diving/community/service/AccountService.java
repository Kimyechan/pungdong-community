package com.diving.community.service;

import com.diving.community.advice.exception.ResourceNotFoundException;
import com.diving.community.config.security.UserAccount;
import com.diving.community.domain.account.Account;
import com.diving.community.repo.AccountJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountJpaRepo accountJpaRepo;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Account account = accountJpaRepo.findById(Long.valueOf(id)).orElseThrow(ResourceNotFoundException::new);
        return new UserAccount(account);
    }
}
