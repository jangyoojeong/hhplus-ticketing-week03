package org.hhplus.ticketing.domain.concert.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ConcertTest {
    @Test
    @DisplayName("🟢 콘서트_객체_생성_테스트_콘서트명으로_객체가_생성된다")
    void createConcertDomainTest_콘서트_객체_생성_테스트_콘서트명으로_객체가_생성된다() {
        // Given
        String concertName = "콘서트1";

        // When
        Concert concert = Concert.create(concertName);

        //Then
        assertThat(concert.getConcertName()).isEqualTo(concertName);
    }
}