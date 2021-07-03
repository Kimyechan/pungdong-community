package com.diving.community.repo;

import com.diving.community.domain.post.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageJpaRepo extends JpaRepository<PostImage, Long> {
}
