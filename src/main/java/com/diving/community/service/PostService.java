package com.diving.community.service;

import com.diving.community.advice.exception.NoPermissionsException;
import com.diving.community.advice.exception.ResourceNotFoundException;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Post;
import com.diving.community.dto.post.PostInfo;
import com.diving.community.repo.PostJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostJpaRepo postJpaRepo;

    public Post savePostInfo(Account account, PostInfo postInfo) {
        Post post = Post.builder()
                .dateOfRegistration(LocalDateTime.now())
                .category(postInfo.getCategory())
                .tags(postInfo.getTags())
                .title(postInfo.getTitle())
                .content(postInfo.getContent())
                .writer(account)
                .build();

        return postJpaRepo.save(post);
    }

    @Transactional(readOnly = true)
    public Post findPost(Long id) {
        return postJpaRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public Post updatePostInfo(Account account, Long id, PostInfo postInfo) {
        Post post = findPost(id);
        checkPostCreator(account, post.getWriter());

        post.setCategory(postInfo.getCategory());
        post.setTags(postInfo.getTags());
        post.setTitle(postInfo.getTitle());
        post.setContent(postInfo.getContent());

        return postJpaRepo.save(post);
    }

    @Transactional(readOnly = true)
    public void checkPostCreator(Account account, Account writer) {
        if (!account.getId().equals(writer.getId())) {
            throw new NoPermissionsException();
        }
    }

    public void deletePost(Account account, Long id) {
        Post post = findPost(id);
        checkPostCreator(account, post.getWriter());

        postJpaRepo.deleteById(id);
    }
}
