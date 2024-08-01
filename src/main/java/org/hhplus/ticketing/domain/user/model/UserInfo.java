package org.hhplus.ticketing.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    private Long userId;           // 유저ID (키값)
    private String userName;       // 유저 이름

    public static UserInfo create(String userName) {
        return UserInfo.builder()
                .userName(userName)
                .build();
    }
}
