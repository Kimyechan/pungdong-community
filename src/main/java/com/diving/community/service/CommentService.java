package com.diving.community.service;

import com.diving.community.exception.NoPermissionsException;
import com.diving.community.exception.ResourceNotFoundException;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.comment.Comment;
import com.diving.community.domain.post.Post;
import com.diving.community.dto.comment.CommentInfo;
import com.diving.community.dto.comment.list.CommentCommentsModel;
import com.diving.community.dto.comment.list.CommentsModel;
import com.diving.community.kafka.producer.CommentKafkaProducer;
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
    private final CommentKafkaProducer commentKafkaProducer;

    public Comment saveComment(Account account, Long postId, CommentInfo commentInfo) {
        Post post = postService.findPost(postId);
        postService.plusCommentCount(post);

        commentKafkaProducer.sendCommentCreateEvent(post, post.getWriter(), account);

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

    public void deleteComment(Account account, Long id) {
        Comment comment = commentJpaRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
        checkCommentCreator(account, comment.getWriter());

        commentJpaRepo.deleteById(id);
    }

    public Comment saveCommentComment(Account account, Long id, CommentInfo commentInfo) {
        Comment parentComment = commentJpaRepo.findByIdWithWriterAndPost(id).orElseThrow(ResourceNotFoundException::new);
        Post post = parentComment.getPost();

        commentKafkaProducer.sendCommentCommentCreateEvent(account, parentComment.getWriter(), post, post.getWriter());

        Comment commentComment = Comment.builder()
                .dateOfWriting(LocalDateTime.now())
                .content(commentInfo.getContent())
                .writer(account)
                .parent(parentComment)
                .post(post)
                .build();

        return commentJpaRepo.save(commentComment);
    }

    @Transactional(readOnly = true)
    public Page<CommentCommentsModel> findCommentComments(Long parentCommentId, Pageable pageable) {
        Page<Comment> commentPage = commentJpaRepo.findByParentCommentId(parentCommentId, pageable);

        List<CommentCommentsModel> commentCommentsModels = new ArrayList<>();
        for (Comment comment : commentPage.getContent()) {
            CommentCommentsModel commentsModel = new CommentCommentsModel(comment.getWriter(), comment);
            commentCommentsModels.add(commentsModel);
        }

        return new PageImpl<>(commentCommentsModels, pageable, commentPage.getTotalElements());
    }
}