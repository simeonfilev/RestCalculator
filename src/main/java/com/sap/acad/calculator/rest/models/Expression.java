package com.sap.acad.calculator.rest.models;

public class Expression {
    private int id;
    private String expression;
    private Double answer;

    public Expression() {
    }

    public Expression(int id, String expression, Double answer) {
        this.id = id;
        this.expression = expression;
        this.answer = answer;
    }

    public Expression(String expression, Double answer) {
        this.expression = expression;
        this.answer = answer;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Double getAnswer() {
        return answer;
    }

    public void setAnswer(Double answer) {
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Expression{expression='" + expression + '\'' + ", answer=" + answer + '}';
    }
}
