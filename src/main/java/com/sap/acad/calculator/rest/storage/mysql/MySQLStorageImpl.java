package com.sap.acad.rest.calculator.storage.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.sap.acad.rest.calculator.models.Expression;
import com.sap.acad.rest.calculator.storage.StorageInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLStorageImpl implements StorageInterface {
    private MysqlDataSource dataSource;

    public MySQLStorageImpl() {
        this.dataSource = new MysqlDataSource();
        this.dataSource.setUser(System.getenv("db_username"));
        this.dataSource.setPassword(System.getenv("db_password"));
        this.dataSource.setServerName(System.getenv("server_name"));
        this.dataSource.setDatabaseName(System.getenv("db_name"));
    }

    @Override
    public void saveExpression(Expression expression) throws SQLException {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = saveExpressionStatement(connection, expression)) {
            statement.execute();
        } catch (SQLException e) {
            throw new SQLException("database access error");
        }
    }

    @Override
    public ArrayList<Expression> getExpressions() throws SQLException {
        ArrayList<Expression> expressions = new ArrayList<>();
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = getExpressionsStatement(connection);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Expression expression = new Expression();
                expression.setId(rs.getInt(1));
                expression.setExpression(rs.getString(2));
                expression.setAnswer(rs.getDouble(3));
                expressions.add(expression);
            }
        } catch (SQLException e) {
            throw new SQLException("database access error");
        }
        return expressions;
    }

    @Override
    public void deleteExpressionById(int id) throws SQLException {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = deleteExpressionByIdStatement(connection, id)) {
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException("database access error");
        }
    }

    @Override
    public void deleteLastRowExpression() throws SQLException {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = deleteLastExpression(connection)) {
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException("database access error");
        }
    }

    public PreparedStatement deleteLastExpression(Connection connection) throws SQLException {
        String sql = "DELETE FROM expressions ORDER BY id desc limit 1";
        PreparedStatement statement = connection.prepareStatement(sql);
        return statement;
    }

    public PreparedStatement getExpressionsStatement(Connection connection) throws SQLException {
        String sql = "SELECT * FROM expressions";
        PreparedStatement statement = connection.prepareStatement(sql);
        return statement;
    }

    public PreparedStatement saveExpressionStatement(Connection connection, Expression expression) throws SQLException {
        String sql = "insert into expressions(expression,answer) Values (?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, expression.getExpression());
        statement.setDouble(2, expression.getAnswer());
        return statement;
    }

    public PreparedStatement deleteExpressionByIdStatement(Connection connection, int id) throws SQLException {
        String sql = "DELETE FROM expressions WHERE id = ?;";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        return statement;
    }

}
