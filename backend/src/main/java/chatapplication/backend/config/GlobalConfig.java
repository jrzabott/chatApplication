package chatapplication.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Import(HttpConfig.class)
public class GlobalConfig {
    @Bean
    ExecutorService getVirtualExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

}
