package org.hhplus.ticketing.domain.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserInfoDomainTest {
    @Test
    @DisplayName("[성공테스트] 유저정보_객체_생성_테스트_유저이름으로_객체가_생성된다")
    void createUserInfoDomainTest_유저정보_객체_생성_테스트_유저이름으로_객체가_생성된다() {
        String userName = "홍길동";

        UserInfoDomain userInfoDomain = new UserInfoDomain(userName);

        assertThat(userInfoDomain.getUserName()).isEqualTo(userName);
    }
}