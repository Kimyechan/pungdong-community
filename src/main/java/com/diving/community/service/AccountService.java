package com.diving.community.service;

import com.diving.community.exception.ResourceNotFoundException;
import com.diving.community.config.security.UserAccount;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Post;
import com.diving.community.repo.AccountJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {
    private final AccountJpaRepo accountJpaRepo;
    private final PostService postService;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Account account = accountJpaRepo.findById(Long.valueOf(id)).orElseThrow(ResourceNotFoundException::new);
        return new UserAccount(account);
    }

    @Transactional(readOnly = true)
    public Account findWriter(Long postId) {
        Post post = postService.findPost(postId);

        return post.getWriter();
    }
}
