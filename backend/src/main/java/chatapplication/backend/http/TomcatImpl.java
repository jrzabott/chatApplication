package chatapplication.backend.http;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class TomcatImpl implements HttpServer {
    public static final Logger log = LogManager.getLogger(TomcatImpl.class);
    public static final int DEFAULT_PORT = 8080;
    public static final String LOCAL_SERVLET_NAME = "localServlet";
    public static final int MIN_TCP_PORT = 1;
    public static final int MAX_TCP_PORT = 65_535;
    @Value("${http.server.port}")
    private int serverPort;
    @Autowired
    private ExecutorService executor;
    @Autowired
    private LocalHttpServlet localHttpServlet;
    private Tomcat tomcat;
    private boolean started = false;

    private static String getListeningPorts(Tomcat tomcatServer) {
        return Arrays.stream(tomcatServer.getServer().findServices())
                .map(Service::findConnectors)
                .flatMap(Arrays::stream)
                .map(Connector::getPort)
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));
    }

    @Override
    public boolean start() throws HttpServerInitializationException {
        boolean result;
        try {
            Tomcat tomcatServer = new Tomcat();
            tomcatServer.setConnector(createConnector());
            configureTomcatContext(tomcatServer);
            tomcatServer.start();

            final String logMsg = "Tomcat is started and listening in " +
                    tomcatServer.getHost().getName() +
                    ":" +
                    getListeningPorts(tomcatServer);
            log.info(logMsg);

            this.tomcat = tomcatServer;
            this.started = tomcatServer.getConnector() != null && tomcatServer.getConnector().getState().isAvailable();
            result = started;

        } catch (LifecycleException e) {
            log.error("Tomcat server failed to start.");
            result = false;
        }
        return result;
    }

    private Connector createConnector() {
        Connector connector = new Connector(new Http11NioProtocol());
        connector.setPort(getPortOrDefault());
        connector.getProtocolHandler().setExecutor(executor);
        return connector;
    }

    private int getPortOrDefault() {
        return serverPort >= MIN_TCP_PORT && serverPort <= MAX_TCP_PORT ? serverPort : DEFAULT_PORT;
    }

    private void configureTomcatContext(Tomcat tomcatServer) {
        final Context context = tomcatServer.addContext("", null);
        Tomcat.addServlet(context, LOCAL_SERVLET_NAME, localHttpServlet);
        context.addServletMappingDecoded("/*", LOCAL_SERVLET_NAME);
    }

    @Override
    public boolean stop() {

        try {
            stopTomcat();
            destoryTomcat();
            this.started = false;

        } catch (HttpServerStoppingException e) {
            log.warn("Tomcat failed to stop... Trying to release resources...");

        } catch (HttpServerDestructionException e) {
            log.warn("Tomcat failed to release resources due to: %s".formatted(e));
            return false;
        }
        return true;
    }

    private void destoryTomcat() {
        try {
            tomcat.destroy();
            log.info("Tomcat resources fred successfully.");
        } catch (LifecycleException e) {
            log.warn("Tomcat Failed to release resources.");
            throw new HttpServerDestructionException(e);
        }
    }

    private void stopTomcat() {
        try {
            tomcat.stop();
            log.info("Tomcat stopped successfully.");
        } catch (LifecycleException e) {
            log.warn("Tomcat Failed to stop.");
            throw new HttpServerStoppingException(e);
        }
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isStopped() {
        return !started;
    }

    public static class HttpServerInitializationException extends RuntimeException {
        public HttpServerInitializationException(Exception eexception) {
            super(eexception);
        }
    }

    public static class HttpServerStoppingException extends RuntimeException {
        public HttpServerStoppingException(LifecycleException exception) {
            super(exception);
        }
    }

    public static class HttpServerDestructionException extends RuntimeException {
        public HttpServerDestructionException(LifecycleException exception) {
            super(exception);
        }
    }
}
