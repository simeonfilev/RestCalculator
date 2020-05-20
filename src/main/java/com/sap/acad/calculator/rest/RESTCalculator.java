package com.sap.acad.calculator.rest;

import com.sap.acad.calculator.Calculator;
import com.sap.acad.calculator.rest.exceptions.StorageException;
import com.sap.acad.calculator.rest.models.Expression;
import com.sap.acad.calculator.rest.storage.StorageInterface;
import com.sap.acad.calculator.rest.storage.file.FileStorageImpl;
import com.sap.acad.calculator.rest.storage.mysql.MySQLStorageImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/expressions")
public class RESTCalculator extends HttpServlet {

    private static final String JSON_EXPRESSION_ID = "id";
    private static final String JSON_EXPRESSION_EXPRESSION = "expression";
    private static final String JSON_EXPRESSION_ANSWER = "answer";
    private static final String JSON_EXPRESSIONS = "expressions";

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
        json.put(JSON_EXPRESSION_EXPRESSION, expression);
        try {
            double answer = calculator.calculate(expression);
            json.put(JSON_EXPRESSION_ANSWER, answer);
            addExpressionToStorage(expression, answer);
            return correctExpressionResponsePOST(json);
        } catch (UnsupportedOperationException exception) {
            logger.error("The given expression is in incorrect format!");
            logger.error(exception.getMessage(), exception);
        } catch (StorageException exception) {
            logger.error("Couldn't connect to storage");
            logger.error(exception.getMessage(), exception);
        }

        return badRequestResponsePOST(json);
    }

    @DELETE
    public Response deleteExpression(@QueryParam("id") Integer id) {
        if (id == null || id.toString().length() == 0) {
            return noContentFoundToDeleteExpression();
        }
        try {
            storage.deleteExpressionById(id);
            return okResponseToDeleteExpression();
        } catch (StorageException e) {
            logger.error("Couldn't connect to storage");
            logger.error(e.getMessage(), e);
        }
        return noContentFoundToDeleteExpression();
    }


    public void addExpressionToStorage(String expression, Double answer) throws StorageException {
        Expression expressionToSave = new Expression(expression, answer);
        this.storage.saveExpression(expressionToSave);
    }

    public Response noContentFoundToDeleteExpression() {
        return Response.status(Response.Status.NO_CONTENT)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .allow()
                .type(MediaType.APPLICATION_JSON)
                .entity("")
                .build();
    }

    public Response okResponseToDeleteExpression() {
        return Response.status(Response.Status.OK)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .allow()
                .type(MediaType.APPLICATION_JSON)
                .entity("")
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
        List<Expression> expressions;
        try {
            expressions = storage.getExpressions();
        } catch (StorageException exception) {
            logger.error(exception.getMessage(), exception);
            return new JSONObject();
        }
        JSONObject json = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        for (Expression expression : expressions) {
            JSONObject tempExpression = new JSONObject();
            tempExpression.put(JSON_EXPRESSION_ID, expression.getId());
            tempExpression.put(JSON_EXPRESSION_EXPRESSION, expression.getExpression());
            tempExpression.put(JSON_EXPRESSION_ANSWER, expression.getAnswer());
            jsonArray.put(tempExpression);
        }
        json.put(JSON_EXPRESSIONS, jsonArray);
        return json;
    }

}



