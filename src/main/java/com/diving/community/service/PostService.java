package com.diving.community.service;

import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Post;
import com.diving.community.dto.post.PostInfo;
import com.diving.community.repo.PostJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostJpaRepo postJpaRepo;

    public Post savePostInfo(Account account, PostInfo postInfo) {
        Post post = Post.builder()
                .category(postInfo.getCategory())
                .tags(postInfo.getTags())
                .title(postInfo.getTitle())
                .content(postInfo.getContent())
                .writer(account)
                .build();

        return postJpaRepo.save(post);
    }
}