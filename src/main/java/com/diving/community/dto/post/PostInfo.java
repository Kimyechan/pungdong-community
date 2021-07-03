package com.diving.community.dto.post;

import com.diving.community.domain.post.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostInfo {
    @NotNull
    private Category category;

    private List<String> tags;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;
}
