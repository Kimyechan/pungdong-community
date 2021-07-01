package com.diving.community.domain.account;

import com.diving.community.domain.AccountPost;
import com.diving.community.domain.post.Post;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    private Long id;

    private String password;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<Role> roles;
    private String nickName;

    private String profileImageUrl;

    @OneToMany(mappedBy = "writer", fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountPost> likeAccountPosts;
}
