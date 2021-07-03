package com.diving.community.repo;

import com.diving.community.domain.post.Category;
import com.diving.community.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepo extends JpaRepository<Post, Long> {
    Page<Post> findByCategory(Category category, Pageable pageable);
}
