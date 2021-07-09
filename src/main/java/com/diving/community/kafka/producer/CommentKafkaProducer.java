package com.diving.community.kafka.producer;

import com.diving.community.domain.account.Account;
import com.diving.community.domain.post.Post;
import com.diving.community.kafka.dto.comment.CommentCommentCreateInfo;
import com.diving.community.kafka.dto.comment.CommentCreateInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentKafkaProducer {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public void sendCommentCreateEvent(Post post, Account postWriter, Account commentWriter) {
        CommentCreateInfo commentCreateInfo = CommentCreateInfo.builder()
                .postWriterId(String.valueOf(postWriter.getId()))
                .postId(String.valueOf(post.getId()))
                .postTitle(post.getTitle())
                .commentWriterNickname(commentWriter.getNickName())
                .build();

        kafkaTemplate.send("create-comment", commentCreateInfo);
    }

    public void sendCommentCommentCreateEvent(Account postWriter, Account commentWriter, Post post, Account commentCommentWriter) {
        CommentCommentCreateInfo info = CommentCommentCreateInfo.builder()
                .postWriterId(String.valueOf(postWriter.getId()))
                .commentWriterId(String.valueOf(commentWriter.getId()))
                .postId(String.valueOf(post.getId()))
                .commentCommentWriterNickname(commentCommentWriter.getNickName())
                .build();

        kafkaTemplate.send("create-comment-comment", info);
    }
}
