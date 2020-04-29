package com.sap.acad.rest_calculator;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.apache.catalina.startup.Tomcat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class IntegrationTest {
    private Tomcat tomcat;

    @Before
    public void setUp() throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(8085);
        tomcat.getConnector();
        tomcat.setBaseDir("temp");
        String contextPath = "/RestCalculator";
        String warFilePath = new File("").getAbsolutePath()+ "\\target\\WebCalculator.war";
        tomcat.getHost().setAppBase(".");
        tomcat.addWebapp(contextPath, warFilePath);
        tomcat.init();
        tomcat.start();
        Assertions.assertTrue(tomcat.getServer().getState().isAvailable());
    }

    @After
    public void tearDown() throws Exception {
        tomcat.stop();
        Assertions.assertFalse(tomcat.getServer().getState().isAvailable());
        tomcat.destroy();
    }

    @Test
    public void verifyRESTServiceCorrectness() throws IOException, InterruptedException {
        String url = "http://localhost:8085/RestCalculator/calculator/expressions?expression=5";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
    }
}
