package com.diving.community.service;

import com.diving.community.exception.NoPermissionsException;
import com.diving.community.exception.ResourceNotFoundException;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Category;
import com.diving.community.domain.post.Post;
import com.diving.community.dto.post.PostInfo;
import com.diving.community.dto.post.list.PostsModel;
import com.diving.community.repo.PostJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostJpaRepo postJpaRepo;
    private final AccountPostService accountPostService;

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

    @Transactional(readOnly = true)
    public Page<PostsModel> findPostsByCategory(Account account, Category category, Pageable pageable) {
        Page<Post> postPage = postJpaRepo.findByCategory(category, pageable);
        Map<Long, Boolean> likePostMap = accountPostService.findLikePostMap(account);

        List<PostsModel> postsModels = new ArrayList<>();
        for (Post post : postPage.getContent()) {
            String mainPostImageUrl = findPostMainImageUrl(post.getId());
            String writerNickname = post.getWriter().getNickName();
            boolean isLiked = likePostMap.getOrDefault(post.getId(), false);

            PostsModel postsModel = new PostsModel(post, mainPostImageUrl, writerNickname, isLiked);
            postsModels.add(postsModel);
        }

        return new PageImpl<>(postsModels, postPage.getPageable(), postPage.getTotalElements());
    }

    public String findPostMainImageUrl(Long postId) {
        Post post = findPost(postId);

        if (post.getPostImages().isEmpty()) {
            return "";
        } else {
            return post.getPostImages().get(0).getImageUrl();
        }
    }

    public void plusCommentCount(Post post) {
        post.setCommentCount(post.getCommentCount() + 1);

        postJpaRepo.save(post);
    }

    public void enrollLikePost(Account account, Long postId) {
        Post post = findPost(postId);
        post.setLikeCount(post.getLikeCount() + 1);

        accountPostService.saveAccountPost(account, post);
    }

    public void cancelLikePost(Account account, Long postId) {
        Post post = findPost(postId);
        post.setLikeCount(post.getLikeCount() - 1);

        accountPostService.deleteLikePost(account, post);
    }
}
