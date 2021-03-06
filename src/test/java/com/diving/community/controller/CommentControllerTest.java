package com.diving.community.controller;

import com.diving.community.config.RestDocsConfiguration;
import com.diving.community.config.security.JwtTokenProvider;
import com.diving.community.config.security.UserAccount;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.account.Role;
import com.diving.community.domain.comment.Comment;
import com.diving.community.dto.comment.CommentInfo;
import com.diving.community.dto.comment.list.CommentCommentsModel;
import com.diving.community.dto.comment.list.CommentsModel;
import com.diving.community.service.AccountService;
import com.diving.community.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import({RestDocsConfiguration.class})
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AccountService accountService;

    @MockBean
    private CommentService commentService;

    public Account createAccount(Role role) {
        Account account = Account.builder()
                .id(1L)
                .password("1234")
                .roles(Set.of(role))
                .nickName("yechan")
                .profileImageUrl("????????? ?????? Url")
                .build();

        given(accountService.loadUserByUsername(String.valueOf(account.getId())))
                .willReturn(new UserAccount(account));

        return account;
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    public void createComment() throws Exception {
        Long postId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        CommentInfo commentInfo = CommentInfo.builder()
                .content("????????? ?????? ???????????????")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .content(commentInfo.getContent())
                .dateOfWriting(LocalDateTime.now())
                .build();

        given(commentService.saveComment(any(), any(), any())).willReturn(comment);

        mockMvc.perform(post("/community/comment/post/{post-id}", postId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .content(objectMapper.writeValueAsString(commentInfo)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(
                        document("comment-create",
                                pathParameters(
                                    parameterWithName("post-id").description("????????? ????????? ???")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json ??????"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token ???")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("????????? ????????? ?????? URL")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("?????? ????????? ???"),
                                        fieldWithPath("dateOfWriting").description("?????? ?????? ??????"),
                                        fieldWithPath("content").description("?????? ??????"),
                                        fieldWithPath("_links.self.href").description("?????? ?????? URL"),
                                        fieldWithPath("_links.comment-comments.href").description("?????? ?????? ????????? ?????? ?????? URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    public void updateComment() throws Exception {
        Long commentId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        CommentInfo commentInfo = CommentInfo.builder()
                .content("?????? ????????? ?????? ???????????????")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .content(commentInfo.getContent())
                .dateOfWriting(LocalDateTime.now())
                .build();

        given(commentService.updateComment(any(), any(), any())).willReturn(comment);

        mockMvc.perform(put("/community/comment/{id}", commentId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .content(objectMapper.writeValueAsString(commentInfo)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(
                        document("comment-update",
                                pathParameters(
                                        parameterWithName("id").description("?????? ????????? ???")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json ??????"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token ???")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("????????? ????????? ?????? URL")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("?????? ????????? ???"),
                                        fieldWithPath("dateOfWriting").description("?????? ?????? ??????"),
                                        fieldWithPath("content").description("?????? ??????"),
                                        fieldWithPath("_links.self.href").description("?????? ?????? URL"),
                                        fieldWithPath("_links.comment-comments.href").description("?????? ?????? ????????? ?????? ?????? URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    public void readPost() throws Exception {
        Long commentId = 1L;

        Comment comment = Comment.builder()
                .id(1L)
                .dateOfWriting(LocalDateTime.now())
                .content("?????? ??????")
                .build();

        given(commentService.findComment(any())).willReturn(comment);

        mockMvc.perform(get("/community/comment/{id}", commentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("comment-read",
                                pathParameters(
                                        parameterWithName("id").description("????????? ????????? ???")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("?????? ????????? ???"),
                                        fieldWithPath("dateOfWriting").description("?????? ?????? ??????"),
                                        fieldWithPath("content").description("?????? ??????"),
                                        fieldWithPath("_links.self.href").description("?????? ?????? URL"),
                                        fieldWithPath("_links.comment-comments.href").description("?????? ?????? ????????? ?????? ?????? URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("????????? ?????? ?????? ??????")
    public void readComments() throws Exception {
        Long postId = 1L;
        Pageable pageable = PageRequest.of(0, 1);

        Account account = Account.builder()
                .id(1L)
                .nickName("?????????")
                .profileImageUrl("????????? ????????? URL")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .dateOfWriting(LocalDateTime.now())
                .content("?????? ??????")
                .build();

        List<CommentsModel> commentsModels = new ArrayList<>();
        CommentsModel commentsModel = new CommentsModel(account, comment);
        commentsModels.add(commentsModel);

        Page<CommentsModel> commentsModelPage = new PageImpl<>(commentsModels, pageable, commentsModels.size());

        given(commentService.findComments(any(), any())).willReturn(commentsModelPage);

        mockMvc.perform(get("/community/comment/post/{post-id}", postId)
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize())))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("comment-read-list",
                                requestParameters(
                                        parameterWithName("page").description("????????? ??????"),
                                        parameterWithName("size").description("??? ???????????? ??????")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.commentsModelList[].accountModel.id").description("????????? ????????? ???"),
                                        fieldWithPath("_embedded.commentsModelList[].accountModel.nickName").description("????????? ?????????"),
                                        fieldWithPath("_embedded.commentsModelList[].accountModel.profileImageUrl").description("????????? ????????? ????????? URL"),
                                        fieldWithPath("_embedded.commentsModelList[].commentModel.id").description("?????? ????????? ???"),
                                        fieldWithPath("_embedded.commentsModelList[].commentModel.dateOfWriting").description("?????? ?????? ??????"),
                                        fieldWithPath("_embedded.commentsModelList[].commentModel.content").description("?????? ??????"),
                                        fieldWithPath("_embedded.commentsModelList[].commentModel._links.self.href").description("?????? ?????? ?????? URL"),
                                        fieldWithPath("_embedded.commentsModelList[].commentModel._links.comment-comments.href").description("?????? ?????? ????????? ?????? ?????? URL"),
                                        fieldWithPath("page.size").description("???????????? ??????"),
                                        fieldWithPath("page.totalElements").description("?????? ?????? ??????"),
                                        fieldWithPath("page.totalPages").description("?????? ????????? ???"),
                                        fieldWithPath("page.number").description("?????? ????????? ??????"),
                                        fieldWithPath("_links.self.href").description("?????? ?????? URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("?????? ??????")
    public void removeComment() throws Exception {
        Long commentId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        mockMvc.perform(delete("/community/comment/{id}", commentId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(
                        document("comment-delete",
                                pathParameters(
                                        parameterWithName("id").description("?????? ????????? ???")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json ??????"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token ???")
                                )
                        )
                );
    }

    @Test
    @DisplayName("????????? ??????")
    public void createCommentComment() throws Exception {
        Long commentId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        CommentInfo commentInfo = CommentInfo.builder()
                .content("????????? ?????? ???????????????")
                .build();

        Comment comment = Comment.builder()
                .id(2L)
                .content(commentInfo.getContent())
                .dateOfWriting(LocalDateTime.now())
                .build();

        given(commentService.saveCommentComment(any(), any(), any())).willReturn(comment);

        mockMvc.perform(post("/community/comment/{id}/comment", commentId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .content(objectMapper.writeValueAsString(commentInfo)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(
                        document("comment-comment-create",
                                pathParameters(
                                        parameterWithName("id").description("?????? ?????? ????????? ???")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json ??????"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token ???")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("????????? ????????? ?????? URL")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("????????? ????????? ???"),
                                        fieldWithPath("dateOfWriting").description("????????? ?????? ??????"),
                                        fieldWithPath("content").description("????????? ??????"),
                                        fieldWithPath("_links.self.href").description("?????? ?????? URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    public void readCommentComments() throws Exception {
        Long commentId = 1L;
        Pageable pageable = PageRequest.of(0, 1);

        Account account = Account.builder()
                .id(1L)
                .nickName("?????????")
                .profileImageUrl("????????? ????????? URL")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .dateOfWriting(LocalDateTime.now())
                .content("?????? ??????")
                .build();

        List<CommentCommentsModel> commentCommentsModels = new ArrayList<>();
        CommentCommentsModel commentCommentsModel = new CommentCommentsModel(account, comment);
        commentCommentsModels.add(commentCommentsModel);

        Page<CommentCommentsModel> commentsModelPage = new PageImpl<>(commentCommentsModels, pageable, commentCommentsModels.size());

        given(commentService.findCommentComments(commentId, pageable)).willReturn(commentsModelPage);

        mockMvc.perform(get("/community/comment/{id}/comment", commentId)
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize())))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("comment-comment-read-list",
                                requestParameters(
                                        parameterWithName("page").description("????????? ??????"),
                                        parameterWithName("size").description("??? ???????????? ??????")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.commentCommentsModelList[].accountModel.id").description("????????? ????????? ???"),
                                        fieldWithPath("_embedded.commentCommentsModelList[].accountModel.nickName").description("????????? ?????????"),
                                        fieldWithPath("_embedded.commentCommentsModelList[].accountModel.profileImageUrl").description("????????? ????????? ????????? URL"),
                                        fieldWithPath("_embedded.commentCommentsModelList[].commentCommentModel.id").description("????????? ????????? ???"),
                                        fieldWithPath("_embedded.commentCommentsModelList[].commentCommentModel.dateOfWriting").description("????????? ?????? ??????"),
                                        fieldWithPath("_embedded.commentCommentsModelList[].commentCommentModel.content").description("????????? ??????"),
                                        fieldWithPath("_embedded.commentCommentsModelList[].commentCommentModel._links.self.href").description("?????? ????????? ?????? URL"),
                                        fieldWithPath("page.size").description("???????????? ??????"),
                                        fieldWithPath("page.totalElements").description("?????? ?????? ??????"),
                                        fieldWithPath("page.totalPages").description("?????? ????????? ???"),
                                        fieldWithPath("page.number").description("?????? ????????? ??????"),
                                        fieldWithPath("_links.self.href").description("?????? ?????? URL")
                                )
                        )
                );
    }
}