package com.diving.community.service;

import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Spy
    @InjectMocks
    private PostService postService;

    @Mock
    private AccountPostService accountPostService;

    @Test
    @DisplayName("게시글 좋아요 등록")
    public void enrollLikePost() {
        Account account = Account.builder().build();
        Post post = Post.builder().id(1L).likeCount(0).build();

        doReturn(post).when(postService).findPost(any());

        postService.enrollLikePost(account, post.getId());

        assertThat(post.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 좋아요 취소")
    public void cancelLikePost() {
        Account account = Account.builder().build();
        Post post = Post.builder().id(1L).likeCount(2).build();

        doReturn(post).when(postService).findPost(any());

        postService.cancelLikePost(account, post.getId());

        assertThat(post.getLikeCount()).isEqualTo(1);
    }
}