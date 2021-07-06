package com.diving.community.controller;

import com.diving.community.advice.exception.BadRequestException;
import com.diving.community.config.security.CurrentUser;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.comment.Comment;
import com.diving.community.dto.comment.CommentInfo;
import com.diving.community.dto.comment.CommentModel;
import com.diving.community.dto.comment.list.CommentsModel;
import com.diving.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/community/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<?> readComment(@PathVariable("id") Long id) {
        Comment comment = commentService.findComment(id);
        CommentModel model = new CommentModel(comment);

        return ResponseEntity.ok().body(model);
    }

    @GetMapping("/post/{post-id}")
    public ResponseEntity<?> readComments(@PathVariable("post-id") Long postId,
                                          Pageable pageable,
                                          PagedResourcesAssembler<CommentsModel> assembler) {
        Page<CommentsModel> commentsModelPage = commentService.findComments(postId, pageable);
        PagedModel<EntityModel<CommentsModel>> commentsModels = assembler.toModel(commentsModelPage);

        return ResponseEntity.ok().body(commentsModels);
    }

    @PostMapping("/post/{post-id}")
    public ResponseEntity<?> createComment(@CurrentUser Account account,
                                           @PathVariable("post-id") Long postId,
                                           @Valid @RequestBody CommentInfo commentInfo,
                                           BindingResult result) {
        if (result.hasErrors()) {
            throw new BadRequestException();
        }

        Comment comment = commentService.saveComment(account, postId, commentInfo);
        CommentModel model = new CommentModel(comment);

        return ResponseEntity.created(linkTo(CommentController.class).slash(comment.getId()).toUri()).body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyComment(@CurrentUser Account account,
                                           @PathVariable("id") Long id,
                                           @Valid @RequestBody CommentInfo commentInfo,
                                           BindingResult result) {
        if (result.hasErrors()) {
            throw new BadRequestException();
        }

        Comment comment = commentService.updateComment(account, id, commentInfo);
        CommentModel model = new CommentModel(comment);

        return ResponseEntity.created(linkTo(CommentController.class).slash(comment.getId()).toUri()).body(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeComment(@CurrentUser Account account,
                                           @PathVariable("id") Long id) {
        commentService.deleteComment(account, id);

        return ResponseEntity.noContent().build();
    }
}
