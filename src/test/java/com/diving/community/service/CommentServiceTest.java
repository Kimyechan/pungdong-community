package com.diving.community.service;

import com.diving.community.domain.account.Account;
import com.diving.community.domain.comment.Comment;
import com.diving.community.dto.comment.list.CommentsModel;
import com.diving.community.repo.CommentJpaRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentJpaRepo commentJpaRepo;

    @Test
    @DisplayName("게시글 댓글 목록 조회")
    public void findComments() {
        List<Comment> commentList = new ArrayList<>();
        Account writer = Account.builder()
                .id(1L)
                .nickName("닉네임")
                .profileImageUrl("프로필 이미지 URL")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .dateOfWriting(LocalDateTime.now())
                .content("댓글 내용")
                .writer(writer)
                .build();
        commentList.add(comment);

        Pageable pageable = PageRequest.of(0, 5);

        given(commentJpaRepo.findByPostIdWithWriter(any(), any())).willReturn(new PageImpl<>(commentList, pageable, 1));


        Page<CommentsModel> commentsModelPage = commentService.findComments(1L, pageable);


        assertThat(commentsModelPage.getContent().size()).isEqualTo(1);
    }
}