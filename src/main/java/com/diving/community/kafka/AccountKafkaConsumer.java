package com.diving.community.kafka;

import com.diving.community.domain.account.Account;
import com.diving.community.repo.AccountJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountKafkaConsumer {
    private final AccountJpaRepo accountJpaRepo;

    @KafkaListener(topics = "account", groupId = "group_id")
    public void saveAccount(AccountInfo accountInfo) {
        Account account = Account.builder()
                .id(Long.valueOf(accountInfo.getId()))
                .nickName(accountInfo.getNickName())
                .profileImageUrl(accountInfo.getProfileImageUrl())
                .build();

        accountJpaRepo.save(account);
    }
}
