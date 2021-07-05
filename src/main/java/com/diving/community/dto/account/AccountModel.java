package com.diving.community.dto.account;

import com.diving.community.controller.PostController;
import com.diving.community.domain.account.Account;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Getter
public class AccountModel extends RepresentationModel<AccountModel> {
    private Long id;
    private String nickName;
    private String profileImageUrl;

    public AccountModel(Account account) {
        this.id = account.getId();
        this.nickName = account.getNickName();
        this.profileImageUrl = account.getProfileImageUrl();
    }
}
