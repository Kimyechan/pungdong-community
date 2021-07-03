package com.diving.community.controller;

import com.diving.community.config.security.CurrentUser;
import com.diving.community.domain.account.Account;
import com.diving.community.service.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/community/post-image")
@RequiredArgsConstructor
public class PostImageController {
    private final PostImageService postImageService;

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePostImages(@CurrentUser Account account,
                                              @PathVariable("id") Long id) {
        postImageService.deletePostImage(account, id);

        return ResponseEntity.noContent().build();
    }
}
