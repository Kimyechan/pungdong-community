package com.diving.community.dto.postImage;

import com.diving.community.controller.PostImageController;
import com.diving.community.domain.post.PostImage;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Getter
public class PostImageModel extends RepresentationModel<PostImageModel> {
    private Long id;
    private String imageUrl;

    public PostImageModel(PostImage postImage) {
        this.id = postImage.getId();
        this.imageUrl = postImage.getImageUrl();

        add(linkTo(PostImageController.class).slash(postImage.getId()).withSelfRel());
    }
}
