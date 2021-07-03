package com.diving.community.service;

import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Post;
import com.diving.community.domain.post.PostImage;
import com.diving.community.repo.PostImageJpaRepo;
import com.diving.community.service.image.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostImageService {
    private final PostImageJpaRepo postImageJpaRepo;
    private final S3Uploader s3Uploader;
    private final PostService postService;

    public List<PostImage> saveImages(Account account, Long postId, List<MultipartFile> images) throws IOException {
        Post post = postService.findPost(postId);
        postService.checkPostCreator(account, post.getWriter());

        List<PostImage> postImages = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = s3Uploader.upload(image, "post-image", String.valueOf(account.getId()));
            PostImage postImage = PostImage.builder()
                    .post(post)
                    .imageUrl(imageUrl)
                    .build();

            PostImage savedPostImage = postImageJpaRepo.save(postImage);
            postImages.add(savedPostImage);
        }

        return postImages;
    }

    @Transactional(readOnly = true)
    public List<PostImage> findPostImages(Long postId) {
        return postImageJpaRepo.findByPostId(postId);
    }
}
