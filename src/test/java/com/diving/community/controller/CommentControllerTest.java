package com.diving.community.controller;

import com.diving.community.config.RestDocsConfiguration;
import com.diving.community.config.security.JwtTokenProvider;
import com.diving.community.config.security.UserAccount;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.account.Role;
import com.diving.community.domain.comment.Comment;
import com.diving.community.dto.comment.CommentInfo;
import com.diving.community.service.AccountService;
import com.diving.community.service.CommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
                .profileImageUrl("프로필 사진 Url")
                .build();

        given(accountService.loadUserByUsername(String.valueOf(account.getId())))
                .willReturn(new UserAccount(account));

        return account;
    }

    @Test
    @DisplayName("게시글 댓글 작성")
    public void createComment() throws Exception {
        Long postId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        CommentInfo commentInfo = CommentInfo.builder()
                .content("도움이 되는 정보입니다")
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
                                    parameterWithName("post-id").description("게시글 식별자 값")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json 타입"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token 값")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("생성된 자원의 조회 URL")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("댓글 식별자 값"),
                                        fieldWithPath("dateOfWriting").description("댓글 작성 시기"),
                                        fieldWithPath("content").description("댓글 내용"),
                                        fieldWithPath("_links.self.href").description("해당 자원 URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 댓글 수정")
    public void updateComment() throws Exception {
        Long commentId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        CommentInfo commentInfo = CommentInfo.builder()
                .content("정말 도움이 되는 정보입니다")
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
                                        parameterWithName("id").description("댓글 식별자 값")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json 타입"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token 값")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("생성된 자원의 조회 URL")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("댓글 식별자 값"),
                                        fieldWithPath("dateOfWriting").description("댓글 작성 시기"),
                                        fieldWithPath("content").description("댓글 내용"),
                                        fieldWithPath("_links.self.href").description("해당 자원 URL")
                                )
                        )
                );
    }
}