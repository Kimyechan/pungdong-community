package com.diving.community.controller;

import com.diving.community.advice.exception.BadRequestException;
import com.diving.community.config.security.CurrentUser;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Post;
import com.diving.community.dto.post.PostInfo;
import com.diving.community.dto.post.PostModel;
import com.diving.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/community/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

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
}
