package com.sap.acad.rest_calculator;

import com.sap.acad.calculator.Calculator;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.json.JSONObject;

@WebServlet("/")
public class RestCalculatorServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Calculator calculator = new Calculator();

        if (!request.getParameterMap().containsKey("expression")
                || request.getParameter("expression").trim().equals("")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String expression = request.getParameter("expression");
        JSONObject json = new JSONObject();
        json.put("expression", expression);
        json.put("answer", calculator.calculate(expression));

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getOutputStream().println(json.toString());
    }
}
