package com.diving.community.kafka.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCommentCreateInfo {
    private String postWriterId;
    private String commentWriterId;
    private String postId;
    private String commentCommentWriterNickname;
}
