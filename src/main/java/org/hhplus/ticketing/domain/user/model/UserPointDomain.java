package org.hhplus.ticketing.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPointDomain {
    private Long userPointId;      // 포인트ID (키값)
    private Long userId;           // 유저ID
    private int point;             // 포인트

    public UserPointDomain(Long userId, int point) {
        this.userId = userId;
        this.point = point;
    }

    public static UserPointDomain defaultUserPointDomain(Long userId) {
        return UserPointDomain.builder()
                .userId(userId)
                .point(0)
                .build();
    }

    // 포인트 추가
    public void increasePoint(int amount) {
        this.point += amount;
    }

    // 포인트 차감
    public void decreasePoint(int amount) {
        if (this.point < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.point -= amount;
    }

    public static UserPointDomain from (UserCommand.AddPointCommand command) {
        return UserPointDomain.builder()
                .userId(command.getUserId())
                .point(command.getAmount())
                .build();
    }

}