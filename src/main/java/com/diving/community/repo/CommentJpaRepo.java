package com.diving.community.repo;

import com.diving.community.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepo extends JpaRepository<Comment, Long> {
}
