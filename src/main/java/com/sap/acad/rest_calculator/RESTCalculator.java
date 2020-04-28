package com.sap.acad.rest_calculator;

import com.sap.acad.calculator.Calculator;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

@Path("/expressions")
public class RESTCalculator extends HttpServlet {

    @POST
    public Response calculateExpression(@QueryParam("expression") String expression) {
        Calculator calculator = new Calculator();
        if (expression == null || expression.trim().length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Expression").build();
        }

        JSONObject json = new JSONObject();
        json.put("expression", expression);
        json.put("answer", calculator.calculate(expression));

        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(json.toString()).build();
    }
}



