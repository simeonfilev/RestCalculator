package com.sap.acad.rest.calculator;

import org.glassfish.jersey.server.ResourceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("calculator")
public class JAXApplication extends ResourceConfig {

    public JAXApplication() {
        this.register(RESTCalculator.class);
    }
}

