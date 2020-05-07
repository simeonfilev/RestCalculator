package com.sap.acad.rest_calculator;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.sap.acad.calculator.Calculator;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Path("/expressions")
public class RESTCalculator extends HttpServlet {
    @GET
    public Response getHistory() {
        JSONObject json = new JSONObject();
        MysqlDataSource dataSource = ConnectionDatabase.getDatabase();
        try {
            var connection = dataSource.getConnection();
            var query =connection.nativeSQL("SELECT * FROM expressions");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            JSONArray expressions = new JSONArray();
            while (rs.next()){
                JSONObject tempExpression = new JSONObject();
                tempExpression.put("id",rs.getInt(1));
                tempExpression.put("expression",rs.getString(2));
                tempExpression.put("answer",rs.getDouble(3));
                expressions.put(tempExpression);
            }
            json.put("expressions",expressions);
        } catch (SQLException e) {
            System.out.println("COULDN'T CONNECT TO DB");
        }

        return Response.status(Response.Status.OK)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods","GET")
                .allow()
                .type(MediaType.APPLICATION_JSON)
                .entity(json.toString())
                .build();

    }

    @POST
    public Response calculateExpression(@QueryParam("expression") String expression) {
        Calculator calculator = new Calculator();
        if (expression == null || expression.trim().length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods","POST")
                    .allow()
                    .entity("Invalid Expression")
                    .build();
        }
        JSONObject json = new JSONObject();
        json.put("expression", expression);
        try{
            double answer = calculator.calculate(expression);
            json.put("answer", answer);
            ConnectionDatabase.addExpression(expression,String.valueOf(answer));
            return Response.status(Response.Status.OK)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods","POST")
                    .allow()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(json.toString())
                    .build();
        }catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods","POST")
                    .allow()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(json.toString())
                    .build();
        }
    }
}



