package com.diving.community.repo;

import com.diving.community.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentJpaRepo extends JpaRepository<Comment, Long> {
    @Query(
            value = "select c from Comment c join fetch c.writer where c.post.id = :postId and c.parent is null",
            countQuery = "select count(c) from Comment c where c.post.id = :postId and c.parent is null"
    )
    Page<Comment> findByPostIdWithWriter(Long postId, Pageable pageable);
}
