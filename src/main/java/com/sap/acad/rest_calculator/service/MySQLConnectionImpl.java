package com.sap.acad.rest_calculator.service;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.sap.acad.calculator.Calculator;
import com.sap.acad.rest_calculator.interfaces.DatabaseConnection;
import com.sap.acad.rest_calculator.models.Expression;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySQLConnectionImpl implements DatabaseConnection {
    private MySQLDatabase database;
    private Calculator calculator;

    public MySQLConnectionImpl() {
        this.database = new MySQLDatabase();
        this.calculator = new Calculator();
    }

    @Override
    public void saveExpression(Expression expression) {
        Connection connection = null;
        try {
            connection = this.database.getDatabase().getConnection();
            Statement stmt = connection.createStatement();
            String params = "(\"" + expression.getExpression() + "\"" + "," + "\"" + this.calculator.calculate(expression.getExpression()) + "\")";
            String sql = "insert into expressions(expression,answer) Values " + params + ";";
            var query = connection.nativeSQL(sql);
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Expression> getExpressions() {
        ArrayList<Expression> expressions = new ArrayList<>();
        MysqlDataSource dataSource = this.database.getDatabase();
        try {
            var connection = dataSource.getConnection();
            var query = connection.nativeSQL("SELECT * FROM expressions");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Expression tempExpression = new Expression();
                tempExpression.setId(rs.getInt(1));
                tempExpression.setExpression(rs.getString(2));
                tempExpression.setAnswer(rs.getDouble(3));
                expressions.add(tempExpression);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expressions;
    }

    @Override
    public void deleteExpressionById(int id) {
        try {
            var connection = this.database.getDatabase().getConnection();
            String sql = "DELETE FROM expressions " + "WHERE id = " + id + ";";
            Statement stmt = connection.createStatement();
            var query = connection.nativeSQL(sql);
            stmt.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
