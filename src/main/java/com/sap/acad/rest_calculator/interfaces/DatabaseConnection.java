package com.sap.acad.rest_calculator.interfaces;

import com.sap.acad.rest_calculator.models.Expression;

import java.util.ArrayList;

public interface DatabaseConnection {
    void saveExpression(Expression expression);

    ArrayList<Expression> getExpressions();

    void deleteExpressionById(int id);
}
