package com.diving.community.repo;

import com.diving.community.domain.post.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostImageJpaRepo extends JpaRepository<PostImage, Long> {
    @Query("select pi from PostImage pi where pi.post.id =:postId")
    List<PostImage> findByPostId(@Param("postId") Long postId);
}
