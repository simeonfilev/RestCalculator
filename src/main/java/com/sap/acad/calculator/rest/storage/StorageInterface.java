package com.sap.acad.calculator.rest.storage;

import com.sap.acad.calculator.rest.exceptions.StorageException;
import com.sap.acad.calculator.rest.models.Expression;

import java.util.List;

public interface StorageInterface {

    void saveExpression(Expression expression);

    List<Expression> getExpressions();

    void deleteExpressionById(int id);

    void deleteLastRowExpression();

}
