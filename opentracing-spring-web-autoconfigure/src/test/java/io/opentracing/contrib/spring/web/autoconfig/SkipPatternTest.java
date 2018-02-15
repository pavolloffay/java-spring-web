package io.opentracing.contrib.spring.web.autoconfig;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;

/**
 * @author Pavol Loffay
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {SkipPatternTest.SpringConfiguration.class, SkipPatternTest.Controller.class},
        properties = {"opentracing.spring.web.skipPattern=/skip"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SkipPatternTest {

    @Configuration
    @EnableAutoConfiguration
    public static class SpringConfiguration {
        @Bean
        public MockTracer tracer() {
            return new MockTracer();
        }
    }

    @RestController
    public static class Controller {
        @RequestMapping("/skip")
        public String skip() {
            return "skip";
        }
    }

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private MockTracer mockTracer;

    @Test
    public void testSkipPattern() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/skip", String.class);
        List<MockSpan> mockSpans = mockTracer.finishedSpans();
        assertEquals(0, mockSpans.size());
    }
}