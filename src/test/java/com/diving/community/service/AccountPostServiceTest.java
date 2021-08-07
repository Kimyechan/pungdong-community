package com.diving.community.service;

import com.diving.community.domain.AccountPost;
import com.diving.community.exception.BadRequestException;
import com.diving.community.repo.AccountPostJpaRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AccountPostServiceTest {
    @InjectMocks
    private AccountPostService accountPostService;

    @Mock
    private AccountPostJpaRepo accountPostJpaRepo;

    @Test
    @DisplayName("이미 좋아요한 게시물인지 확인 - 이미 좋아요한 게시물")
    public void checkAlreadyLikePostException() {
        // given
        given(accountPostJpaRepo.findByAccountAndPost(any(), any())).willReturn(Optional.of(AccountPost.builder().build()));

        // when
        // then
        assertThrows(BadRequestException.class, () -> accountPostService.checkAlreadyLikePost(any(), any()));
    }

    @Test
    @DisplayName("이미 좋아요한 게시물인지 확인 - 좋아요하지 않은 게시물")
    public void checkAlreadyLikePostNotException() {
        // given
        given(accountPostJpaRepo.findByAccountAndPost(any(), any())).willReturn(Optional.empty());

        // when
        // then
        assertDoesNotThrow(() -> accountPostService.checkAlreadyLikePost(any(), any()));
    }
}