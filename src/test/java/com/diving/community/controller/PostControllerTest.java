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
import org.springframework.data.domain.*;
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
                .profileImageUrl("????????? ?????? Url")
                .build();

        given(accountService.loadUserByUsername(String.valueOf(account.getId())))
                .willReturn(new UserAccount(account));

        return account;
    }

    @Test
    @DisplayName("????????? ????????????")
    public void createPost() throws Exception {
        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        PostInfo postInfo = PostInfo.builder()
                .category(Category.SHARE)
                .tags(List.of("?????? ?????????", "??? ?????? ??????"))
                .title("?????? ????????? 2????????? ??? ?????? ?????? ???????????????!!")
                .content("?????? ?????? ???????????????")
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
                                        fieldWithPath("category").description("????????? ????????????"),
                                        fieldWithPath("tags[]").description("????????? ?????? ??????"),
                                        fieldWithPath("title").description("????????? ??????"),
                                        fieldWithPath("content").description("????????? ??????")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json ??????"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token ???")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("????????? ????????? ?????? URL")
                                ),
                                responseFields(
                                        fieldWithPath("postResource.id").description("????????? ????????? ???"),
                                        fieldWithPath("postResource.dateOfRegistration").description("????????? ?????? ??????"),
                                        fieldWithPath("postResource.category").description("????????? ????????????"),
                                        fieldWithPath("postResource.tags[]").description("????????? ?????? ??????"),
                                        fieldWithPath("postResource.title").description("????????? ??????"),
                                        fieldWithPath("postResource.content").description("????????? ??????"),
                                        fieldWithPath("postResource.likeCount").description("????????? ????????? ???"),
                                        fieldWithPath("postResource.commentCount").description("????????? ?????? ???"),
                                        fieldWithPath("postResource.liked").description("????????? ????????? ??????"),
                                        fieldWithPath("_links.self.href").description("?????? ?????? URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    public void readPost() throws Exception {
        Long postId = 1L;

        Post post = Post.builder()
                .id(1L)
                .dateOfRegistration(LocalDateTime.now())
                .category(Category.SHARE)
                .tags(List.of("?????? ?????????", "??? ?????? ??????"))
                .title("?????? ????????? 2????????? ??? ?????? ?????? ???????????????!!")
                .content("?????? ?????? ???????????????")
                .likeCount(10)
                .commentCount(10)
                .build();

        given(postService.findPost(any())).willReturn(post);
        given(accountPostService.checkLikePost(any(), any())).willReturn(true);

        mockMvc.perform(get("/community/post/{id}", postId))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("post-read",
                                pathParameters(
                                    parameterWithName("id").description("????????? ????????? ???")
                                ),
                                responseFields(
                                        fieldWithPath("postResource.id").description("????????? ????????? ???"),
                                        fieldWithPath("postResource.dateOfRegistration").description("????????? ?????? ??????"),
                                        fieldWithPath("postResource.category").description("????????? ????????????"),
                                        fieldWithPath("postResource.tags[]").description("????????? ?????? ??????"),
                                        fieldWithPath("postResource.title").description("????????? ??????"),
                                        fieldWithPath("postResource.content").description("????????? ??????"),
                                        fieldWithPath("postResource.likeCount").description("????????? ????????? ???"),
                                        fieldWithPath("postResource.commentCount").description("????????? ?????? ???"),
                                        fieldWithPath("postResource.liked").description("????????? ????????? ??????"),
                                        fieldWithPath("_links.self.href").description("?????? ?????? URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("????????? ????????????")
    public void modifyPost() throws Exception {
        Long postId = 1L;

        Account account = createAccount(Role.STUDENT);
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()), account.getRoles());

        PostInfo postInfo = PostInfo.builder()
                .category(Category.SHARE)
                .tags(List.of("?????? ?????????", "??? ?????? ??????"))
                .title("?????? ????????? 2????????? ??? ?????? ?????? ???????????????!!")
                .content("?????? ?????? ???????????????")
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
        given(accountPostService.checkLikePost(any(), any())).willReturn(true);

        mockMvc.perform(put("/community/post/{id}", postId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .content(objectMapper.writeValueAsString(postInfo)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(
                        document("post-update",
                                pathParameters(
                                    parameterWithName("id").description("????????? ????????? ???")
                                ),
                                requestFields(
                                        fieldWithPath("category").description("????????? ????????????"),
                                        fieldWithPath("tags[]").description("????????? ?????? ??????"),
                                        fieldWithPath("title").description("????????? ??????"),
                                        fieldWithPath("content").description("????????? ??????")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json ??????"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token ???")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("????????? ????????? ?????? URL")
                                ),
                                responseFields(
                                        fieldWithPath("postResource.id").description("????????? ????????? ???"),
                                        fieldWithPath("postResource.dateOfRegistration").description("????????? ?????? ??????"),
                                        fieldWithPath("postResource.category").description("????????? ????????????"),
                                        fieldWithPath("postResource.tags[]").description("????????? ?????? ??????"),
                                        fieldWithPath("postResource.title").description("????????? ??????"),
                                        fieldWithPath("postResource.content").description("????????? ??????"),
                                        fieldWithPath("postResource.likeCount").description("????????? ????????? ???"),
                                        fieldWithPath("postResource.commentCount").description("????????? ?????? ???"),
                                        fieldWithPath("postResource.liked").description("????????? ????????? ??????"),
                                        fieldWithPath("_links.self.href").description("?????? ?????? URL")
                                )
                        )
                );
    }

    @Test
    @DisplayName("????????? ??????")
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
                                        parameterWithName("id").description("????????? ????????? ???")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json ??????"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token ???")
                                )
                        )
                );
    }

    @Test
    @DisplayName("??????????????? ????????? ?????? ??????")
    public void readPostsByCategory() throws Exception {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id"));
        Post post = Post.builder()
                .id(1L)
                .dateOfRegistration(LocalDateTime.now())
                .category(Category.SHARE)
                .title("?????? ????????? 2????????? ??? ?????? ?????? ???????????????!!")
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
                .param("size", String.valueOf(pageable.getPageSize()))
                .param("sort", "id,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(
                        document("post-read-list-by-category",
                                requestParameters(
                                        parameterWithName("category").description("????????? ???????????? ??????"),
                                        parameterWithName("page").description("????????? ??????"),
                                        parameterWithName("size").description("??? ???????????? ??????"),
                                        parameterWithName("sort").description("?????? ??????")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.postsModelList[].id").description("????????? ????????? ???"),
                                        fieldWithPath("_embedded.postsModelList[].dateOfRegistration").description("????????? ?????? ??????"),
                                        fieldWithPath("_embedded.postsModelList[].category").description("????????? ????????????"),
                                        fieldWithPath("_embedded.postsModelList[].title").description("????????? ??????"),
                                        fieldWithPath("_embedded.postsModelList[].likeCount").description("????????? ????????? ???"),
                                        fieldWithPath("_embedded.postsModelList[].commentCount").description("????????? ?????? ???"),
                                        fieldWithPath("_embedded.postsModelList[].writerNickname").description("????????? ????????? ?????????"),
                                        fieldWithPath("_embedded.postsModelList[].imageUrl").description("????????? ?????????"),
                                        fieldWithPath("_embedded.postsModelList[].liked").description("????????? ????????? ??????"),
                                        fieldWithPath("_embedded.postsModelList[]._links.post.href").description("????????? ?????? URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.post-images.href").description("????????? ???????????? ?????? URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.writer.href").description("????????? ????????? ?????? URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.comments.href").description("????????? ????????? ?????? URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.like.href").description("????????? ????????? ?????? ?????? URL"),
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
    @DisplayName("????????? ???????????? ??????")
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
                                    parameterWithName("id").description("????????? ????????? ???")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("multipart form data ??????"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token ???")
                                ),
                                requestParts(
                                        partWithName("images").description("????????? ????????????")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("????????? ????????? ?????? URL")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.postImageModelList[].id").description("????????? ????????? ????????? ???"),
                                        fieldWithPath("_embedded.postImageModelList[].imageUrl").description("????????? ????????? URL"),
                                        fieldWithPath("_embedded.postImageModelList[]._links.self.href").description("?????? Api Url")
                                )
                        )
                );
    }

    @Test
    @DisplayName("????????? ???????????? ??????")
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
                                        parameterWithName("id").description("????????? ????????? ???")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.postImageModelList[].id").description("????????? ????????? ????????? ???"),
                                        fieldWithPath("_embedded.postImageModelList[].imageUrl").description("????????? ????????? URL"),
                                        fieldWithPath("_embedded.postImageModelList[]._links.self.href").description("?????? ?????? ?????? Api Url"),
                                        fieldWithPath("_links.self.href").description("?????? Api Url")
                                )
                        )
                );
    }

    @Test
    @DisplayName("??? ????????? ?????? ??????")
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
                                        parameterWithName("id").description("????????? ????????? ???")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("????????? ????????? ????????? ???"),
                                        fieldWithPath("nickName").description("????????? ????????? ?????????"),
                                        fieldWithPath("profileImageUrl").description("????????? ????????? ????????? ????????? URL"),
                                        fieldWithPath("_links.self.href").description("?????? Api Url")
                                )
                        )
                );
    }

    @Test
    @DisplayName("????????? ????????? ??????")
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
                                        parameterWithName("id").description("????????? ????????? ???")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json ??????"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token ???")
                                )
                        )
                );
    }

    @Test
    @DisplayName("????????? ????????? ??????")
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
                                        parameterWithName("id").description("????????? ????????? ???")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application json ??????"),
                                        headerWithName(HttpHeaders.AUTHORIZATION).optional().description("access token ???")
                                )
                        )
                );
    }

    @Test
    @DisplayName("???????????? ????????? ?????? ??????")
    public void readMyLikePosts() throws Exception {
        Pageable pageable = PageRequest.of(0, 1);
        Post post = Post.builder()
                .id(1L)
                .dateOfRegistration(LocalDateTime.now())
                .category(Category.SHARE)
                .title("?????? ????????? 2????????? ??? ?????? ?????? ???????????????!!")
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
                                        parameterWithName("page").description("????????? ??????"),
                                        parameterWithName("size").description("??? ???????????? ??????")
                                ),
                                responseFields(
                                        fieldWithPath("_embedded.postsModelList[].id").description("????????? ????????? ???"),
                                        fieldWithPath("_embedded.postsModelList[].dateOfRegistration").description("????????? ?????? ??????"),
                                        fieldWithPath("_embedded.postsModelList[].category").description("????????? ????????????"),
                                        fieldWithPath("_embedded.postsModelList[].title").description("????????? ??????"),
                                        fieldWithPath("_embedded.postsModelList[].likeCount").description("????????? ????????? ???"),
                                        fieldWithPath("_embedded.postsModelList[].commentCount").description("????????? ?????? ???"),
                                        fieldWithPath("_embedded.postsModelList[].writerNickname").description("????????? ????????? ?????????"),
                                        fieldWithPath("_embedded.postsModelList[].imageUrl").description("????????? ?????????"),
                                        fieldWithPath("_embedded.postsModelList[].liked").description("????????? ????????? ??????"),
                                        fieldWithPath("_embedded.postsModelList[]._links.post.href").description("????????? ?????? URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.post-images.href").description("????????? ???????????? ?????? URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.writer.href").description("????????? ????????? ?????? URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.comments.href").description("????????? ????????? ?????? URL"),
                                        fieldWithPath("_embedded.postsModelList[]._links.like.href").description("????????? ????????? ?????? ?????? URL"),
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