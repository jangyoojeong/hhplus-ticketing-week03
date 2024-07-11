package org.hhplus.ticketing.domain.user;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.user.model.UserResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;

    /**
     * userId를 기반으로 사용자 정보를 조회합니다. (유저 정보 없을 시 예외 리턴)
     *
     * @param userId 조회할 사용자의 ID
     * @return userId에 해당하는 사용자를 포함하는 Optional 객체
     */
    public UserResult.UserInfoResult validateUser (Long userId) {
        return UserResult.UserInfoResult.from(userInfoRepository.findById(userId).orElseThrow(()
                -> new IllegalArgumentException("유저 정보가 존재하지 않습니다")));
    }
}
