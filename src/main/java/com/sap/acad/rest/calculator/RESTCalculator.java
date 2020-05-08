package com.sap.acad.rest.calculator;

import com.sap.acad.calculator.Calculator;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sap.acad.rest.calculator.models.Expression;
import com.sap.acad.rest.calculator.storage.StorageInterface;
import com.sap.acad.rest.calculator.storage.mysql.MySQLStorageImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;

@Path("/expressions")
public class RESTCalculator extends HttpServlet {

    private StorageInterface storage = new MySQLStorageImpl();

    @GET
    public Response getHistory() {
        try {
            JSONObject json = getJSONObjectFromExpressionArray();
            return okRequestGetHistoryGET(json);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return serverErrorToGETHistory();
        }
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
            addExpressionToStorage(expression);
            return correctExpressionResponsePOST(json);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return badRequestResponsePOST(json);
        }
    }

    public void addExpressionToStorage(String expression) throws SQLException {
        Calculator calculator = new Calculator();
        Expression expressionToSave = new Expression(expression, calculator.calculate(expression));

        this.storage.saveExpression(expressionToSave);
    }

    public Response serverErrorToGETHistory(){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .allow()
                .type(MediaType.APPLICATION_JSON)
                .entity("Couldn't connect to database")
                .build();
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

    public JSONObject getJSONObjectFromExpressionArray() throws SQLException {
        ArrayList<Expression> expressions = storage.getExpressions();
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



