package org.hhplus.ticketing.integration;

import org.hhplus.ticketing.application.concert.facade.ConcertFacade;
import org.hhplus.ticketing.utils.TestDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
// @DirtiesContext 컨텍스트의 상태를 초기화
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ConcertIntegration {

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private TestDataInitializer testDataInitializer;

    @BeforeEach
    void setUp() {
        testDataInitializer.initializeTestData();
    }

    //@Test


}
