package chatapplication.backend.http;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LocalHttpServlet extends HttpServlet {

    public static final Logger log = LogManager.getLogger(LocalHttpServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info(Thread.currentThread() + " :: " + req);
        resp.getWriter().println("Hello, Tomcat!");
    }
}
