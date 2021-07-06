package com.diving.community.service;

import com.diving.community.domain.account.Account;
import com.diving.community.domain.comment.Comment;
import com.diving.community.domain.post.Post;
import com.diving.community.dto.comment.CommentInfo;
import com.diving.community.repo.CommentJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentJpaRepo commentJpaRepo;
    private final PostService postService;

    public Comment saveComment(Account account, Long postId, CommentInfo commentInfo) {
        Post post = postService.findPost(postId);

        Comment comment = Comment.builder()
                .dateOfWriting(LocalDateTime.now())
                .content(commentInfo.getContent())
                .writer(account)
                .post(post)
                .build();

        return commentJpaRepo.save(comment);
    }
}
