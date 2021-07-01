package com.diving.community.repo;

import com.diving.community.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepo extends JpaRepository<Post, Long> {
}
