package org.hhplus.ticketing.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDomain {
    private Long userId;           // 유저ID (키값)
    private UUID uuid;             // uuid
    private String userName;       // 유저 이름

    public UserInfoDomain(String userName) {
        this.userName = userName;
    }
}
