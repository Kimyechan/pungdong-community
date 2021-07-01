package com.diving.community.dto.post;

import com.diving.community.controller.PostController;
import com.diving.community.domain.post.Post;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Getter
public class PostModel extends RepresentationModel<PostModel> {
    private final PostResource postResource;

    public PostModel(Post post) {
        this.postResource = PostResource.builder()
                .id(post.getId())
                .category(post.getCategory())
                .tags(post.getTags())
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .build();

        add(linkTo(PostController.class).slash(post.getId()).withSelfRel());
    }
}
