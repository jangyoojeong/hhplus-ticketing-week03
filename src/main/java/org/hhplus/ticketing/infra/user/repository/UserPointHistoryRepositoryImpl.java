package org.hhplus.ticketing.infra.user.repository;

import lombok.RequiredArgsConstructor;
import org.hhplus.ticketing.domain.user.UserPointHistoryRepository;
import org.hhplus.ticketing.domain.user.model.UserPointHistoryDomain;
import org.hhplus.ticketing.infra.user.entity.UserPointHistory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserPointHistoryRepositoryImpl implements UserPointHistoryRepository {

    private final UserPointHistoryJpaRepository repository;

    /**
     * userId를 기반으로 포인트 히스토리 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return userId에 해당하는 유저포인트 히스토리 도메인 객체
     */
    @Override
    public List<UserPointHistoryDomain> findByUserId(Long userId) {
        return UserPointHistory.toDomainList(repository.findByUserId(userId));
    }

    /**
     * 포인트 히스토리 정보를 저장합니다.
     *
     * @param domain 저장할 유저포인트 히스토리 도메인 객체
     * @return domain 저장된 유저포인트 히스토리 도메인 객체
     */
    @Override
    public UserPointHistoryDomain save(UserPointHistoryDomain domain) {
        return UserPointHistory.toDomain(repository.save(UserPointHistory.from(domain)));
    }
}
