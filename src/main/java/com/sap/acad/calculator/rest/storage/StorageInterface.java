package com.sap.acad.rest.calculator.storage;

import com.sap.acad.rest.calculator.models.Expression;

import java.sql.SQLException;
import java.util.ArrayList;

public interface StorageInterface {

    void saveExpression(Expression expression) throws SQLException;

    ArrayList<Expression> getExpressions() throws SQLException;

    void deleteExpressionById(int id) throws SQLException;

    void deleteLastRowExpression() throws SQLException;

}
