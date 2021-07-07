package com.diving.community.dto.comment;

import com.diving.community.controller.CommentController;
import com.diving.community.domain.comment.Comment;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Getter
public class CommentCommentModel extends RepresentationModel<CommentCommentModel> {
    private Long id;
    private LocalDateTime dateOfWriting;
    private String content;

    public CommentCommentModel(Comment comment) {
        this.id = comment.getId();
        this.dateOfWriting = comment.getDateOfWriting();
        this.content = comment.getContent();

        add(linkTo(CommentController.class).slash(comment.getId()).withSelfRel());
    }
}
