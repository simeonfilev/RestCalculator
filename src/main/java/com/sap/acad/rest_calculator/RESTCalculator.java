package com.sap.acad.rest_calculator;

import com.sap.acad.calculator.Calculator;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sap.acad.rest_calculator.models.Expression;
import com.sap.acad.rest_calculator.service.MySQLConnectionImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

@Path("/expressions")
public class RESTCalculator extends HttpServlet {
    @GET
    public Response getHistory() {
        JSONObject json = getJSONObjectFromExpressionArray();
        return okRequestGetHistoryGET(json);
    }

    @POST
    public Response calculateExpression(@QueryParam("expression") String expression) {
        Calculator calculator = new Calculator();
        if (expression == null || expression.trim().length() == 0) {
            return invalidExpressionResponsePOST();
        }
        JSONObject json = new JSONObject();
        json.put("expression", expression);
        try {
            double answer = calculator.calculate(expression);
            json.put("answer", answer);
            addExpressionToDatabase(expression);
            return correctExpressionResponsePOST(json);
        } catch (Exception e) {
            return badRequestResponsePOST(json);
        }
    }

    public void addExpressionToDatabase(String expression) {
        Calculator calculator = new Calculator();
        Expression expressionToSave = new Expression(expression, calculator.calculate(expression));
        MySQLConnectionImpl mySQLConnection = new MySQLConnectionImpl();
        mySQLConnection.saveExpression(expressionToSave);
    }

    public Response okRequestGetHistoryGET(JSONObject json) {
        return Response.status(Response.Status.OK)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .allow()
                .type(MediaType.APPLICATION_JSON)
                .entity(json.toString())
                .build();
    }

    public Response badRequestResponsePOST(JSONObject json) {
        return Response.status(Response.Status.BAD_REQUEST)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow()
                .type(MediaType.APPLICATION_JSON)
                .entity(json.toString())
                .build();
    }

    public Response correctExpressionResponsePOST(JSONObject json) {
        return Response.status(Response.Status.OK)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow()
                .type(MediaType.APPLICATION_JSON)
                .entity(json.toString())
                .build();
    }

    public Response invalidExpressionResponsePOST() {
        return Response.status(Response.Status.BAD_REQUEST)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow()
                .entity("Invalid Expression")
                .build();
    }

    public JSONObject getJSONObjectFromExpressionArray() {
        MySQLConnectionImpl mySQLConnection = new MySQLConnectionImpl();
        ArrayList<Expression> expressions = mySQLConnection.getExpressions();
        JSONObject json = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        for (Expression expression : expressions) {
            JSONObject tempExpression = new JSONObject();
            tempExpression.put("id", expression.getId());
            tempExpression.put("expression", expression.getExpression());
            tempExpression.put("answer", expression.getAnswer());
            jsonArray.put(tempExpression);
        }
        json.put("expressions", jsonArray);
        return json;
    }

}



