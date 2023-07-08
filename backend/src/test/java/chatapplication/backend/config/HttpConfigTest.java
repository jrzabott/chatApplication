package chatapplication.backend.config;

import chatapplication.backend.http.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = {HttpConfigTest.Config.class})
class HttpConfigTest {
    @Autowired
    ApplicationContext applicationContext;

    @Test
    void testHttpConfigIsLoaded() {
        final HttpConfig httpConfig = applicationContext.getBean(HttpConfig.class);
        assertThat(httpConfig).isNotNull();
    }

    @Test
    void testHttpServerIsStarted() {
        final HttpServer httpServer = applicationContext.getBean(HttpServer.class);
        assertThat(httpServer).isNotNull();
        assertThat(httpServer.isStarted()).isTrue();
    }

    @Configuration
    @Import(HttpConfig.class)
    static class Config {
        @Bean
        ExecutorService executorService() {
            return Executors.newVirtualThreadPerTaskExecutor();
        }
    }
    //TODO - jundan - enrich tests
}