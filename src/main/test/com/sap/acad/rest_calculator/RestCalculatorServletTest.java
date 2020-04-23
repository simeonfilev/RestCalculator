package com.sap.acad.rest_calculator;

import com.sap.acad.calculator.Calculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


public class RestCalculatorServletTest {

    @Test
    public void restAPIIsWorkingCorrectly() throws IOException, ServletException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Map<String, String> headers = new HashMap<>();
        headers.put("expression", "2*3");
        Enumeration<String> headerNames = Collections.enumeration(headers.keySet());
        Mockito.when(request.getHeaderNames()).thenReturn(headerNames);
        Mockito.doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return headers.get((String) args[0]);
            }
        }).when(request).getHeader("expression");
        ArrayList<Integer> arrayList = new ArrayList<>();
         ServletOutputStream servletOutputStream = new ServletOutputStream() {
           @Override
           public boolean isReady() {
               return false;
           }

           @Override
           public void setWriteListener(WriteListener writeListener) {
           }

           @Override
           public void write(int b) throws IOException {
                arrayList.add(b);
           }
       };

        Mockito.when(response.getOutputStream()).thenReturn(servletOutputStream);
        RestCalculatorServlet testServlet = new RestCalculatorServlet();
        testServlet.doGet(request,response);
        Assertions.assertTrue(arrayList.size()>2); // more than empty response {}
    }

}
