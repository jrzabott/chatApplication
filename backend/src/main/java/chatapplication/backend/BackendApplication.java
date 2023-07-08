package chatapplication.backend;

import chatapplication.backend.config.GlobalConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class BackendApplication {
    private BackendApplication() {
    }

    public static void main(String[] args) {
        // Create and configure the application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(GlobalConfig.class);
        context.refresh();
    }

}
