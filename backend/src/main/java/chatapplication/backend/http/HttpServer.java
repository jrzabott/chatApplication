package chatapplication.backend.http;

public interface HttpServer {
    boolean start();

    boolean stop();

    boolean isStarted();

    boolean isStopped();

}
