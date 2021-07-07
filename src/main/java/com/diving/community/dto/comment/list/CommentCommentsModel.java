package com.diving.community.dto.comment.list;

import com.diving.community.domain.account.Account;
import com.diving.community.domain.comment.Comment;
import com.diving.community.dto.account.AccountModel;
import com.diving.community.dto.comment.CommentCommentModel;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class CommentCommentsModel extends RepresentationModel<CommentsModel> {
    private AccountModel accountModel;
    private CommentCommentModel commentCommentModel;

    public CommentCommentsModel(Account account, Comment comment) {
        this.accountModel = new AccountModel(account);
        this.commentCommentModel = new CommentCommentModel(comment);
    }
}
