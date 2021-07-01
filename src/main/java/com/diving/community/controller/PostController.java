package com.diving.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/post")
public class PostController {
    @PostMapping
    public ResponseEntity<?> createPost() {
        return ResponseEntity.ok().build();
    }
}
