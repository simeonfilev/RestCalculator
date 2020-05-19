package com.sap.acad.calculator.rest.storage;

import com.sap.acad.calculator.rest.exceptions.StorageException;
import com.sap.acad.calculator.rest.models.Expression;

import java.util.List;

public interface StorageInterface {

    void saveExpression(Expression expression) throws StorageException;

    List<Expression> getExpressions() throws StorageException;

    void deleteExpressionById(int id) throws StorageException;

    void deleteLastRowExpression() throws StorageException;

}
