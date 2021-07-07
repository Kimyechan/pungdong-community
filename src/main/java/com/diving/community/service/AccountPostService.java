package com.diving.community.service;

import com.diving.community.domain.AccountPost;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Post;
import com.diving.community.repo.AccountPostJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountPostService {
    private final AccountPostJpaRepo accountPostJpaRepo;

    @Transactional(readOnly = true)
    public boolean checkLikePost(Account account, Long postId) {
        if (account == null) {
            return false;
        }

        List<AccountPost> accountPosts = accountPostJpaRepo.findByAccount(account.getId());
        for (AccountPost accountPost : accountPosts) {
            Post post = accountPost.getPost();
            if (post.getId().equals(postId)) {
                return true;
            }
        }

        return false;
    }

    @Transactional(readOnly = true)
    public Map<Long, Boolean> findLikePostMap(Account account) {
        Map<Long, Boolean> likeMap = new HashMap<>();

        if (account != null) {
            List<AccountPost> accountPosts = accountPostJpaRepo.findByAccount(account.getId());
            for (AccountPost accountPost : accountPosts) {
                Post post = accountPost.getPost();
                likeMap.put(post.getId(), true);
            }
        }

        return likeMap;
    }

    public AccountPost saveAccountPost(AccountPost accountPost) {
        return accountPostJpaRepo.save(accountPost);
    }

    public void deleteLikePost(Account account, Post post) {
        accountPostJpaRepo.deleteByAccountAndPost(account, post);
    }
}
