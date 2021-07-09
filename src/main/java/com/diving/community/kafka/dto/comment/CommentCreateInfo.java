package com.diving.community.kafka.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCreateInfo {
    private String postWriterId;
    private String postId;
    private String postTitle;
    private String commentWriterNickname;
}
