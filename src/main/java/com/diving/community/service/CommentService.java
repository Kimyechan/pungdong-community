package com.diving.community.service;

import com.diving.community.advice.exception.NoPermissionsException;
import com.diving.community.advice.exception.ResourceNotFoundException;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.comment.Comment;
import com.diving.community.domain.post.Post;
import com.diving.community.dto.comment.CommentInfo;
import com.diving.community.dto.comment.list.CommentsModel;
import com.diving.community.repo.CommentJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentJpaRepo commentJpaRepo;
    private final PostService postService;

    public Comment saveComment(Account account, Long postId, CommentInfo commentInfo) {
        Post post = postService.findPost(postId);
        postService.plusCommentCount(post);

        Comment comment = Comment.builder()
                .dateOfWriting(LocalDateTime.now())
                .content(commentInfo.getContent())
                .writer(account)
                .post(post)
                .build();

        return commentJpaRepo.save(comment);
    }

    public Comment updateComment(Account account, Long id, CommentInfo commentInfo) {
        Comment comment = commentJpaRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
        checkCommentCreator(account, comment.getWriter());

        List<Comment> childrenComment = comment.getChildren();
        commentJpaRepo.deleteAll(childrenComment);

        comment.setContent(commentInfo.getContent());
        comment.setDateOfWriting(LocalDateTime.now());

        return comment;
    }

    public void checkCommentCreator(Account account, Account writer) {
        if (account != null && !account.getId().equals(writer.getId())) {
            throw new NoPermissionsException();
        }
    }

    @Transactional(readOnly = true)
    public Comment findComment(Long id) {
        return commentJpaRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Page<CommentsModel> findComments(Long postId, Pageable pageable) {
        Page<Comment> commentPage = commentJpaRepo.findByPostIdWithWriter(postId, pageable);

        List<CommentsModel> commentsModels = new ArrayList<>();
        for (Comment comment : commentPage.getContent()) {
            CommentsModel commentsModel = new CommentsModel(comment.getWriter(), comment);
            commentsModels.add(commentsModel);
        }

        return new PageImpl<>(commentsModels, pageable, commentPage.getTotalElements());
    }
}