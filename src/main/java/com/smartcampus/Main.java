package com.smartcampus;

import com.smartcampus.config.AppConfig;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Main {
    public static void main(String[] args) throws Exception {
        URI uri = URI.create("http://0.0.0.0:8080/api/v1/");
        ResourceConfig config = new ResourceConfig();
        config.registerClasses(new AppConfig().getClasses().toArray(new Class<?>[0]));
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, config);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
        System.out.println("SmartCampus API running at http://localhost:8080/api/v1");
        Thread.currentThread().join();
    }
}
