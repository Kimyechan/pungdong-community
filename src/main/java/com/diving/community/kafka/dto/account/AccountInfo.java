package com.diving.community.kafka.dto.account;

import com.diving.community.domain.account.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
    private String id;
    private String password;
    private Set<Role> roles;
    private String nickName;
    private String profileImageUrl;
}
