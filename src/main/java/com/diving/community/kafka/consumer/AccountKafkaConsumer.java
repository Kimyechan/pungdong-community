package com.diving.community.kafka.consumer;

import com.diving.community.domain.account.Account;
import com.diving.community.kafka.dto.account.AccountInfo;
import com.diving.community.repo.AccountJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountKafkaConsumer {
    private final AccountJpaRepo accountJpaRepo;

    @KafkaListener(topics = "account", groupId = "community")
    public void saveAccount(AccountInfo accountInfo) {
        Account account = Account.builder()
                .id(Long.valueOf(accountInfo.getId()))
                .password(accountInfo.getPassword())
                .roles(accountInfo.getRoles())
                .nickName(accountInfo.getNickName())
                .profileImageUrl(accountInfo.getProfileImageUrl())
                .build();

        accountJpaRepo.save(account);
    }
}
