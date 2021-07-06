package com.diving.community.controller;

import com.diving.community.advice.exception.BadRequestException;
import com.diving.community.config.security.CurrentUser;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.comment.Comment;
import com.diving.community.dto.comment.CommentInfo;
import com.diving.community.dto.comment.CommentModel;
import com.diving.community.service.CommentService;
import lombok.RequiredArgsConstructor;
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
}
