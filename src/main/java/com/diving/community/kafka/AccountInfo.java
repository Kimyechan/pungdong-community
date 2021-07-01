package com.diving.community.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
    private String id;
    private String nickName;
    private String profileImageUrl;
}
