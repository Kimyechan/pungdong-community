package com.diving.community.dto.post;

import com.diving.community.domain.post.Category;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResource {
    private Long id;

    private Category category;

    private List<String> tags;

    private String title;

    private String content;

    private Integer likeCount;

    private Integer commentCount;
}
