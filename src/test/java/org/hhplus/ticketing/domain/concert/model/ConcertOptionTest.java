package org.hhplus.ticketing.domain.concert.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ConcertOptionTest {
    @Test
    @DisplayName("🟢 [콘서트옵션_객체_생성_테스트]")
    void createConcertOptionTest_콘서트옵션_정보로_객체가_생성된다() {
        // Given
        Long concertId = 1L;
        LocalDateTime concertAt = LocalDateTime.now();
        int capacity = 50;

        // When
        ConcertOption concertOption = ConcertOption.create(concertId, concertAt, capacity);

        // Then
        assertThat(concertOption.getConcertId()).isEqualTo(concertId);
        assertThat(concertOption.getConcertAt()).isEqualTo(concertAt);
        assertThat(concertOption.getCapacity()).isEqualTo(capacity);
    }
}