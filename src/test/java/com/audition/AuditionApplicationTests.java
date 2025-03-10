package com.audition;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuditionApplicationTests {

    @Test
    void contextLoads(final ApplicationContext ctx) {
        assertThat(ctx).isNotNull();
    }

}