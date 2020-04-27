package com.sap.acad.rest_calculator;

import com.sap.acad.calculator.Calculator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/")
public class RestCalculatorServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Calculator calculator = new Calculator();
        String expression = request.getParameter("expression");
        String json = "";
        if (!expression.equals("")) {
            json = "{\n";
            json += "\"expression\": " + expression + ",\n";
            json += "\"answer\": " + calculator.calculate(expression) + ",\n";
            json += "}";
        } else {
            json = "{}";
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getOutputStream().println(json);
    }
}
