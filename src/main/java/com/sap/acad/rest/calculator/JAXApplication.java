package com.sap.acad.rest.calculator;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("calculator")
public class JAXApplication extends ResourceConfig {
    public JAXApplication() {
        this.register(RESTCalculator.class);
    }
}

