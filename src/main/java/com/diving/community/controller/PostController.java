package com.diving.community.controller;

import com.diving.community.advice.exception.BadRequestException;
import com.diving.community.config.security.CurrentUser;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Category;
import com.diving.community.domain.post.Post;
import com.diving.community.domain.post.PostImage;
import com.diving.community.dto.post.PostInfo;
import com.diving.community.dto.post.PostModel;
import com.diving.community.dto.post.list.PostsModel;
import com.diving.community.dto.postImage.PostImageModel;
import com.diving.community.dto.postImage.PostImageModelAssembler;
import com.diving.community.service.PostImageService;
import com.diving.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/community/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostImageService postImageService;

    @GetMapping("/{id}")
    public ResponseEntity<?> readPost(@PathVariable("id") Long id) {
        Post post = postService.findPost(id);
        PostModel model = new PostModel(post);

        return ResponseEntity.ok().body(model);
    }

    @PostMapping
    public ResponseEntity<?> createPost(@CurrentUser Account account,
                                        @Valid @RequestBody PostInfo postInfo,
                                        BindingResult result) {
        if (result.hasErrors()) {
            throw new BadRequestException();
        }

        Post post = postService.savePostInfo(account, postInfo);
        PostModel model = new PostModel(post);

        return ResponseEntity.created(linkTo(PostController.class).slash(post.getId()).toUri()).body(model);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> modifyPost(@CurrentUser Account account,
                                        @PathVariable("id") Long id,
                                        @Valid @RequestBody PostInfo postInfo,
                                        BindingResult result) {
        if (result.hasErrors()) {
            throw new BadRequestException();
        }

        Post post = postService.updatePostInfo(account, id, postInfo);
        PostModel model = new PostModel(post);

        return ResponseEntity.created(linkTo(PostController.class).slash(post.getId()).toUri()).body(model);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> removePost(@CurrentUser Account account,
                                        @PathVariable("id") Long id) {
        postService.deletePost(account, id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category")
    public ResponseEntity<?> readPostsByCategory(@CurrentUser Account account,
                                                 @RequestParam Category category,
                                                 Pageable pageable,
                                                 PagedResourcesAssembler<PostsModel> assembler) {
        Page<PostsModel> postsModelPage = postService.findPostsByCategory(account, category, pageable);

        PagedModel<EntityModel<PostsModel>> model = assembler.toModel(postsModelPage);

        return ResponseEntity.ok().body(model);
    }

    @PostMapping("/{id}/post-image")
    public ResponseEntity<?> createPostImages(@CurrentUser Account account,
                                              @PathVariable("id") Long postId,
                                              @RequestParam List<MultipartFile> images,
                                              PostImageModelAssembler assembler) throws IOException {
        List<PostImage> postImages = postImageService.saveImages(account, postId, images);

        CollectionModel<PostImageModel> postImageModels = assembler.toCollectionModel(postImages);
        URI location = linkTo(PostController.class).slash(postId).slash("post-image").toUri();

        return ResponseEntity.created(location).body(postImageModels);
    }

    @GetMapping("/{id}/post-image")
    public ResponseEntity<?> readPostImages(@PathVariable("id") Long postId,
                                            PostImageModelAssembler assembler) {
        List<PostImage> postImages = postImageService.findPostImages(postId);

        CollectionModel<PostImageModel> postImageModels = assembler.toCollectionModel(postImages);
        postImageModels.add(linkTo(PostController.class).slash(postId).slash("post-image").withSelfRel());

        return ResponseEntity.ok().body(postImageModels);
    }
}
