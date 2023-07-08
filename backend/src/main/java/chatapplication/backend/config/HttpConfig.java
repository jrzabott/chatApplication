package chatapplication.backend.config;

import chatapplication.backend.http.HttpServer;
import chatapplication.backend.http.LocalHttpServlet;
import chatapplication.backend.http.TomcatImpl;
import jakarta.servlet.http.HttpServlet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("http.properties")
public class HttpConfig {

    @Bean
    HttpServlet createLocalServlet() {
        return new LocalHttpServlet();
    }

    @Bean(initMethod = "start")
    public HttpServer httpServer() {
        return new TomcatImpl();
    }


}