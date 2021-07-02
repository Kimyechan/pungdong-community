package com.diving.community.controller;

import com.diving.community.config.RestDocsConfiguration;
import com.diving.community.config.security.JwtTokenProvider;
import com.diving.community.config.security.UserAccount;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.account.Role;
import com.diving.community.domain.post.Category;
import com.diving.community.domain.post.Post;
import com.diving.community.dto.post.PostInfo;
import com.diving.community.service.AccountService;
import com.diving.community.service.PostService;
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
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureRestDocs
@Import({RestDocsConfiguration.class})
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AccountService accountService;

    @MockBean
    private PostService postService;

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
    @DisplayName("게시글 작성하기")
    public void createPost() throws Exception {
        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        PostInfo postInfo = PostInfo.builder()
                .category(Category.SHARE)
                .tags(List.of("프리 다이빙", "숨 참기 비법"))
                .title("프리 다이빙 2분이상 숨 참는 비법 공유합니다!!")
                .content("비법 공유 내용입니다")
                .build();

        Post post = Post.builder()
                .id(1L)
                .dateOfRegistration(LocalDateTime.now())
                .category(postInfo.getCategory())
                .tags(postInfo.getTags())
                .title(postInfo.getTitle())
                .content(postInfo.getContent())
                .likeCount(0)
                .commentCount(0)
                .build();

        given(postService.savePostInfo(any(), any())).willReturn(post);

        mockMvc.perform(post("/community/post")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .content(objectMapper.writeValueAsString(postInfo)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(
                        document("post-create",
                                requestFields(
                                        fieldWithPath("category").description("게시글 카테고리"),
                                        fieldWithPath("tags[]").description("게시글 태그 목록"),
                                        fieldWithPath("title").description("게시글 제목"),
                                        fieldWithPath("content").description("게시글 내용")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json 타입"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token 값")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("생성된 자원의 조회 URL")
                                ),
                                responseFields(
                                        fieldWithPath("postResource.id").description("게시글 식별자 값"),
                                        fieldWithPath("postResource.dateOfRegistration").description("게시글 등록 시기"),
                                        fieldWithPath("postResource.category").description("게시글 카테고리"),
                                        fieldWithPath("postResource.tags[]").description("게시글 태그 목록"),
                                        fieldWithPath("postResource.title").description("게시글 제목"),
                                        fieldWithPath("postResource.content").description("게시글 내용"),
                                        fieldWithPath("postResource.likeCount").description("게시글 좋아요 수"),
                                        fieldWithPath("postResource.commentCount").description("게시글 댓글 수"),
                                        fieldWithPath("_links.self.href").description("해당 자원 URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void readPost() throws Exception {
        Long postId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        Post post = Post.builder()
                .id(1L)
                .dateOfRegistration(LocalDateTime.now())
                .category(Category.SHARE)
                .tags(List.of("프리 다이빙", "숨 참기 비법"))
                .title("프리 다이빙 2분이상 숨 참는 비법 공유합니다!!")
                .content("비법 공유 내용입니다")
                .likeCount(10)
                .commentCount(10)
                .build();

        given(postService.findPost(any())).willReturn(post);

        mockMvc.perform(get("/community/post/{id}", postId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("post-read",
                                pathParameters(
                                    parameterWithName("id").description("게시글 식별자 값")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json 타입"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token 값")
                                ),
                                responseFields(
                                        fieldWithPath("postResource.id").description("게시글 식별자 값"),
                                        fieldWithPath("postResource.dateOfRegistration").description("게시글 등록 시기"),
                                        fieldWithPath("postResource.category").description("게시글 카테고리"),
                                        fieldWithPath("postResource.tags[]").description("게시글 태그 목록"),
                                        fieldWithPath("postResource.title").description("게시글 제목"),
                                        fieldWithPath("postResource.content").description("게시글 내용"),
                                        fieldWithPath("postResource.likeCount").description("게시글 좋아요 수"),
                                        fieldWithPath("postResource.commentCount").description("게시글 댓글 수"),
                                        fieldWithPath("_links.self.href").description("해당 자원 URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 수정하기")
    public void modifyPost() throws Exception {
        Long postId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        PostInfo postInfo = PostInfo.builder()
                .category(Category.SHARE)
                .tags(List.of("프리 다이빙", "숨 참기 비법"))
                .title("프리 다이빙 2분이상 숨 참는 비법 공유합니다!!")
                .content("비법 공유 내용입니다")
                .build();

        Post post = Post.builder()
                .id(1L)
                .dateOfRegistration(LocalDateTime.now())
                .category(postInfo.getCategory())
                .tags(postInfo.getTags())
                .title(postInfo.getTitle())
                .content(postInfo.getContent())
                .likeCount(0)
                .commentCount(0)
                .build();

        given(postService.updatePostInfo(any(), any(), any())).willReturn(post);

        mockMvc.perform(put("/community/post/{id}", postId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .content(objectMapper.writeValueAsString(postInfo)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(
                        document("post-update",
                                pathParameters(
                                    parameterWithName("id").description("게시글 식별자 값")
                                ),
                                requestFields(
                                        fieldWithPath("category").description("게시글 카테고리"),
                                        fieldWithPath("tags[]").description("게시글 태그 목록"),
                                        fieldWithPath("title").description("게시글 제목"),
                                        fieldWithPath("content").description("게시글 내용")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json 타입"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token 값")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("생성된 자원의 조회 URL")
                                ),
                                responseFields(
                                        fieldWithPath("postResource.id").description("게시글 식별자 값"),
                                        fieldWithPath("postResource.dateOfRegistration").description("게시글 등록 시기"),
                                        fieldWithPath("postResource.category").description("게시글 카테고리"),
                                        fieldWithPath("postResource.tags[]").description("게시글 태그 목록"),
                                        fieldWithPath("postResource.title").description("게시글 제목"),
                                        fieldWithPath("postResource.content").description("게시글 내용"),
                                        fieldWithPath("postResource.likeCount").description("게시글 좋아요 수"),
                                        fieldWithPath("postResource.commentCount").description("게시글 댓글 수"),
                                        fieldWithPath("_links.self.href").description("해당 자원 URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void removePost() throws Exception {
        Long postId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        mockMvc.perform(delete("/community/post/{id}", postId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(
                        document("post-delete",
                                pathParameters(
                                        parameterWithName("id").description("게시글 식별자 값")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json 타입"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token 값")
                                )
                        )
                );
    }
}