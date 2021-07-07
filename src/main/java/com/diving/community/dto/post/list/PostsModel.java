package com.diving.community.dto.post.list;

import com.diving.community.controller.CommentController;
import com.diving.community.controller.PostController;
import com.diving.community.domain.post.Category;
import com.diving.community.domain.post.Post;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Getter
public class PostsModel extends RepresentationModel<PostsModel> {
    private Long id;

    private LocalDateTime dateOfRegistration;

    private Category category;

    private String title;

    private Integer likeCount;

    private Integer commentCount;

    private String writerNickname;

    private String imageUrl;

    private boolean isLiked;

    public PostsModel(Post post, String imageUrl, String writerNickname, boolean isLiked) {
        this.id = post.getId();
        this.dateOfRegistration = post.getDateOfRegistration();
        this.category = post.getCategory();
        this.title = post.getTitle();
        this.likeCount = post.getLikeCount();
        this.commentCount = post.getCommentCount();
        this.writerNickname = writerNickname;
        this.imageUrl = imageUrl;
        this.isLiked = isLiked;

        add(linkTo(PostController.class).slash(post.getId()).withRel("post"));
        add(linkTo(PostController.class).slash(post.getId()).slash("post-image").withRel("post-images"));
        add(linkTo(PostController.class).slash(post.getId()).slash("writer").withRel("writer"));
        add(linkTo(CommentController.class).slash("post").slash(post.getId()).withRel("comments"));
        add(linkTo(PostController.class).slash(post.getId()).slash("like").withRel("like"));
    }
}
