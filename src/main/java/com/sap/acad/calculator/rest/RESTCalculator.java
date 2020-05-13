package com.sap.acad.calculator.rest;

import com.sap.acad.calculator.Calculator;
import com.sap.acad.calculator.rest.models.Expression;
import com.sap.acad.calculator.rest.storage.StorageInterface;
import com.sap.acad.calculator.rest.storage.mysql.MySQLStorageImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/expressions")
public class RESTCalculator extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(RESTCalculator.class);
    private StorageInterface storage = new MySQLStorageImpl();

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
            addExpressionToStorage(expression);
            storage.deleteExpressionById(0);
            return correctExpressionResponsePOST(json);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return badRequestResponsePOST(json);
        }

    }


    public void addExpressionToStorage(String expression) {
        Calculator calculator = new Calculator();
        Expression expressionToSave = new Expression(expression, calculator.calculate(expression));
        this.storage.saveExpression(expressionToSave);
    }

    public Response serverErrorToGETHistory() {
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

    public JSONObject getJSONObjectFromExpressionArray() {

        List<Expression> expressions = storage.getExpressions();
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



