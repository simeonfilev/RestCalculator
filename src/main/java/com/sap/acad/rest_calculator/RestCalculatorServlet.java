package com.sap.acad.rest_calculator;

import com.sap.acad.calculator.Calculator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@WebServlet("/")
public class RestCalculatorServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Calculator calculator = new Calculator();

        Map<String, String> headers = Collections
                .list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, request::getHeader));

        String expression="";

        try{
            expression = headers.get("expression");
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            response.getOutputStream().println("{}");
        }
        if(!expression.equals("") ){
            String json = "{\n";
            json += "\"expression\": " + expression + ",\n";
            json += "\"answer\": " + calculator.calculate(expression) + ",\n";
            json += "}";
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().println(json);
        }
        else{
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            response.getOutputStream().println("{}");
        }
    }



}
