package com.diving.community.domain.account;

import com.diving.community.domain.AccountPost;
import com.diving.community.domain.post.Post;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    private Long id;

    private String nickName;

    private String profileImageUrl;

    @OneToMany(mappedBy = "writer", fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountPost> likeAccountPosts;
}
