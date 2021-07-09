package com.diving.community.repo;

import com.diving.community.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentJpaRepo extends JpaRepository<Comment, Long> {
    @Query(
            value = "select c from Comment c join fetch c.writer where c.post.id = :postId and c.parent is null",
            countQuery = "select count(c) from Comment c where c.post.id = :postId and c.parent is null"
    )
    Page<Comment> findByPostIdWithWriter(@Param("postId") Long postId, Pageable pageable);

    @Query(
            value = "select c from Comment c join fetch c.writer where c.parent.id = :parentCommentId",
            countQuery = "select count(c) from Comment c where c.parent.id = :parentCommentId"
    )
    Page<Comment> findByParentCommentId(@Param("parentCommentId") Long parentCommentId, Pageable pageable);

    @Query("select c from Comment c join fetch c.writer join fetch c.post where c.id = :id")
    Optional<Comment> findByIdWithWriterAndPost(@Param("id") Long id);
}
