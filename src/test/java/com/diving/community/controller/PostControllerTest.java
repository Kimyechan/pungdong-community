package com.diving.community.controller;

import com.diving.community.config.RestDocsConfiguration;
import com.diving.community.config.security.JwtTokenProvider;
import com.diving.community.config.security.UserAccount;
import com.diving.community.domain.account.Account;
import com.diving.community.domain.account.Role;
import com.diving.community.domain.post.Category;
import com.diving.community.domain.post.Post;
import com.diving.community.domain.post.PostImage;
import com.diving.community.dto.post.PostInfo;
import com.diving.community.dto.post.list.PostsModel;
import com.diving.community.service.AccountPostService;
import com.diving.community.service.AccountService;
import com.diving.community.service.PostImageService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
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

    @MockBean
    private PostImageService postImageService;

    @MockBean
    private AccountPostService accountPostService;

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

        mockMvc.perform(get("/community/post/{id}", postId))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("post-read",
                                pathParameters(
                                    parameterWithName("id").description("게시글 식별자 값")
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
    @DisplayName("게시글 삭제")
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

    @Test
    @DisplayName("카테고리별 게시글 목록 조회")
    public void readPostsByCategory() throws Exception {
        Pageable pageable = PageRequest.of(0, 1);
        Post post = Post.builder()
                .id(1L)
                .dateOfRegistration(LocalDateTime.now())
                .category(Category.SHARE)
                .title("프리 다이빙 2분이상 숨 참는 비법 공유합니다!!")
                .likeCount(10)
                .commentCount(10)
                .build();
        String imageUrl = "main image url";
        String writerNickName = "chan";
        boolean isLiked = true;

        List<PostsModel> postsModels = new ArrayList<>();
        PostsModel postsModel = new PostsModel(post, imageUrl, writerNickName, isLiked);
        postsModels.add(postsModel);

        Page<PostsModel> postsModelPage = new PageImpl<>(postsModels, pageable, postsModels.size());

        given(postService.findPostsByCategory(any(), any(), any())).willReturn(postsModelPage);

        mockMvc.perform(get("/community/post/category")
                .param("category", Category.SHARE.toString())
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize())))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("post-read-list-by-category",
                                requestParameters(
                                        parameterWithName("category").description("게시글 카테고리 종류"),
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("한 페이지당 크기")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.postsModelList[].id").description("게시글 식별자 값"),
                                        fieldWithPath("_embedded.postsModelList[].dateOfRegistration").description("게시글 등록 일시"),
                                        fieldWithPath("_embedded.postsModelList[].category").description("게시글 카테고리"),
                                        fieldWithPath("_embedded.postsModelList[].title").description("게시글 제목"),
                                        fieldWithPath("_embedded.postsModelList[].likeCount").description("게시글 좋아요 수"),
                                        fieldWithPath("_embedded.postsModelList[].commentCount").description("게시글 댓글 수"),
                                        fieldWithPath("_embedded.postsModelList[].writerNickname").description("게시글 작성자 닉네임"),
                                        fieldWithPath("_embedded.postsModelList[].imageUrl").description("게시글 이미지"),
                                        fieldWithPath("_embedded.postsModelList[].liked").description("게시글 좋아요 여부"),
                                        fieldWithPath("_embedded.postsModelList[]._links.post.href").description("게시글 조회 URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.post-images.href").description("게시글 이미지들 조회 URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.writer.href").description("게시글 작성자 조회 URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.comments.href").description("게시글 댓글들 조회 URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.like.href").description("게시글 좋아요 여부 조회 URL"),
                                        fieldWithPath("page.size").description("페이지당 크기"),
                                        fieldWithPath("page.totalElements").description("전체 자원 갯수"),
                                        fieldWithPath("page.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("page.number").description("현재 페이지 번호"),
                                        fieldWithPath("_links.self.href").description("해당 자원 URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 이미지들 생성")
    public void createPostImages() throws Exception {
        Long postId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        MockMultipartFile file1 = new MockMultipartFile("images", "test1", "image/png", "test data".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("images", "test2", "image/png", "test data".getBytes());

        List<PostImage> postImages = new ArrayList<>();
        PostImage postImage = PostImage.builder()
                .id(1L)
                .imageUrl("post image 1")
                .build();
        postImages.add(postImage);

        given(postImageService.saveImages(any(), any(), any())).willReturn(postImages);

        mockMvc.perform(fileUpload("/community/post/{id}/post-image", postId)
                .file(file1)
                .file(file2)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "post-create-post-images",
                                pathParameters(
                                    parameterWithName("id").description("게시글 식별자 값")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("multipart form data 타입"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token 값")
                                ),
                                requestParts(
                                        partWithName("images").description("게시글 이미지들")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("생성된 자원의 조회 URL")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.postImageModelList[].id").description("게시글 이미지 식별자 값"),
                                        fieldWithPath("_embedded.postImageModelList[].imageUrl").description("게시글 이미지 URL"),
                                        fieldWithPath("_embedded.postImageModelList[]._links.self.href").description("해당 Api Url")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 이미지들 조회")
    public void readPostImages() throws Exception {
        Long postId = 1L;

        List<PostImage> postImages = new ArrayList<>();
        PostImage postImage = PostImage.builder()
                .id(1L)
                .imageUrl("post image 1")
                .build();
        postImages.add(postImage);

        given(postImageService.findPostImages(any())).willReturn(postImages);

        mockMvc.perform(get("/community/post/{id}/post-image", postId))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "post-read-post-images",
                                pathParameters(
                                        parameterWithName("id").description("게시글 식별자 값")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.postImageModelList[].id").description("게시글 이미지 식별자 값"),
                                        fieldWithPath("_embedded.postImageModelList[].imageUrl").description("게시글 이미지 URL"),
                                        fieldWithPath("_embedded.postImageModelList[]._links.self.href").description("단건 자원 조회 Api Url"),
                                        fieldWithPath("_links.self.href").description("해당 Api Url")
                                )
                        )
                );
    }

    @Test
    @DisplayName("글 작성자 정보 읽기")
    public void readPostWriter() throws Exception {
        Long postId = 1L;

        Account account = Account.builder()
                .id(1L)
                .nickName("chan")
                .profileImageUrl("profile photo image url")
                .build();

        given(accountService.findWriter(any())).willReturn(account);

        mockMvc.perform(get("/community/post/{id}/writer", postId))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "post-read-writer",
                                pathParameters(
                                        parameterWithName("id").description("게시글 식별자 값")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("게시글 작성자 식별자 값"),
                                        fieldWithPath("nickName").description("게시글 작성자 닉네임"),
                                        fieldWithPath("profileImageUrl").description("게시글 작성자 프로필 이미지 URL"),
                                        fieldWithPath("_links.self.href").description("해당 Api Url")
                                )
                        )
                );
    }

    @Test
    @DisplayName("게시글 좋아요 등록")
    public void likePost() throws Exception {
        Long postId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        mockMvc.perform(post("/community/post/{id}/like", postId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(
                        document("post-create-like",
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

    @Test
    @DisplayName("게시글 좋아요 취소")
    public void unlikePost() throws Exception {
        Long postId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        mockMvc.perform(delete("/community/post/{id}/like", postId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(
                        document("post-delete-like",
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

    @Test
    @DisplayName("좋아요한 게시글 목록 조회")
    public void readMyLikePosts() throws Exception {
        Pageable pageable = PageRequest.of(0, 1);
        Post post = Post.builder()
                .id(1L)
                .dateOfRegistration(LocalDateTime.now())
                .category(Category.SHARE)
                .title("프리 다이빙 2분이상 숨 참는 비법 공유합니다!!")
                .likeCount(10)
                .commentCount(10)
                .build();
        String imageUrl = "main image url";
        String writerNickName = "chan";
        boolean isLiked = true;

        List<PostsModel> postsModels = new ArrayList<>();
        PostsModel postsModel = new PostsModel(post, imageUrl, writerNickName, isLiked);
        postsModels.add(postsModel);

        Page<PostsModel> postsModelPage = new PageImpl<>(postsModels, pageable, postsModels.size());

        given(accountPostService.findMyLikePosts(any(), any())).willReturn(postsModelPage);

        mockMvc.perform(get("/community/post/like")
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize())))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("post-read-like-list",
                                requestParameters(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("한 페이지당 크기")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.postsModelList[].id").description("게시글 식별자 값"),
                                        fieldWithPath("_embedded.postsModelList[].dateOfRegistration").description("게시글 등록 일시"),
                                        fieldWithPath("_embedded.postsModelList[].category").description("게시글 카테고리"),
                                        fieldWithPath("_embedded.postsModelList[].title").description("게시글 제목"),
                                        fieldWithPath("_embedded.postsModelList[].likeCount").description("게시글 좋아요 수"),
                                        fieldWithPath("_embedded.postsModelList[].commentCount").description("게시글 댓글 수"),
                                        fieldWithPath("_embedded.postsModelList[].writerNickname").description("게시글 작성자 닉네임"),
                                        fieldWithPath("_embedded.postsModelList[].imageUrl").description("게시글 이미지"),
                                        fieldWithPath("_embedded.postsModelList[].liked").description("게시글 좋아요 여부"),
                                        fieldWithPath("_embedded.postsModelList[]._links.post.href").description("게시글 조회 URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.post-images.href").description("게시글 이미지들 조회 URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.writer.href").description("게시글 작성자 조회 URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.comments.href").description("게시글 댓글들 조회 URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.like.href").description("게시글 좋아요 여부 조회 URL"),
                                        fieldWithPath("page.size").description("페이지당 크기"),
                                        fieldWithPath("page.totalElements").description("전체 자원 갯수"),
                                        fieldWithPath("page.totalPages").description("전체 페이지 수"),
                                        fieldWithPath("page.number").description("현재 페이지 번호"),
                                        fieldWithPath("_links.self.href").description("해당 자원 URL")
                                )
                        )
                );
    }
}