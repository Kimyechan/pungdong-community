package com.diving.community.dto.postImage;

import com.diving.community.domain.post.PostImage;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class PostImageModelAssembler implements RepresentationModelAssembler<PostImage, PostImageModel> {
    @Override
    public PostImageModel toModel(PostImage entity) {
        return new PostImageModel(entity);
    }

    @Override
    public CollectionModel<PostImageModel> toCollectionModel(Iterable<? extends PostImage> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
