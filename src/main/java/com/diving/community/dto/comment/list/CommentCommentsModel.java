package com.diving.community.dto.comment.list;

import com.diving.community.domain.account.Account;
import com.diving.community.domain.comment.Comment;
import com.diving.community.dto.account.AccountModel;
import com.diving.community.dto.comment.CommentModel;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class CommentCommentsModel extends RepresentationModel<CommentsModel> {
    private AccountModel accountModel;
    private CommentModel commentModel;

    public CommentCommentsModel(Account account, Comment comment) {
        this.accountModel = new AccountModel(account);
        this.commentModel = new CommentModel(comment);
    }
}
