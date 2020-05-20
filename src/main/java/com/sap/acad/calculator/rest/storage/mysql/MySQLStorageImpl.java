package com.sap.acad.calculator.rest.storage.mysql;

import com.sap.acad.calculator.rest.exceptions.StorageException;
import com.sap.acad.calculator.rest.models.Expression;
import com.sap.acad.calculator.rest.storage.StorageInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLStorageImpl implements StorageInterface {
    private static final Logger logger = LogManager.getLogger(MySQLStorageImpl.class);

    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USERNAME = System.getenv("DB_USERNAME");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    private static final String SQL_DELETE_LAST_EXPRESSION = "DELETE FROM expressions ORDER BY id DESC LIMIT 1;";
    private static final String SQL_GET_ALL_EXPRESSIONS = "SELECT * FROM expressions;";
    private static final String SQL_SAVE_EXPRESSION = "INSERT INTO expressions(expression,answer) VALUES (?,?);";
    private static final String SQL_DELETE_EXPRESSION_WITH_ID = "DELETE FROM expressions WHERE id = ?;";


    public MySQLStorageImpl() {

    }

    @Override
    public void saveExpression(Expression expression) throws StorageException{
        logger.debug("Connecting to database...");
        try (Connection connection = getConnection();
             PreparedStatement statement = getPreparedStatement(SQL_SAVE_EXPRESSION, connection)) {
            logger.debug("Connected to database! Saving expression:" + expression);
            statement.setString(1, expression.getExpression());
            statement.setDouble(2, expression.getAnswer());
            statement.execute();
            logger.debug("Successfully saved expression: " + expression);
        } catch (SQLException | ClassNotFoundException  e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    @Override
    public List<Expression> getExpressions() throws StorageException {
        List<Expression> expressions = new ArrayList<>();
        logger.debug("Connecting to database...");
        try (Connection connection = getConnection();
             PreparedStatement statement = getPreparedStatement(SQL_GET_ALL_EXPRESSIONS, connection)) {

            try (ResultSet rs = statement.executeQuery()) {
                logger.debug("Connected to database!");
                while (rs.next()) {
                    int id = rs.getInt(1);
                    String expressionString = rs.getString(2);
                    Double answer = rs.getDouble(3);
                    Expression expression = new Expression(id, expressionString, answer);
                    expressions.add(expression);
                }
                logger.debug("Successfully received all expressions with length:" + expressions.size());
            }

        }catch (SQLException | ClassNotFoundException e) {
          throw new StorageException(e.getMessage(), e);
        }

        return expressions;
    }

    @Override
    public void deleteExpressionById(int id) throws StorageException {
        logger.debug("Connecting to database...");
        try (Connection connection = getConnection();
             PreparedStatement statement = getPreparedStatement(SQL_DELETE_EXPRESSION_WITH_ID, connection)) {
            logger.debug("Connected to database! And trying to delete expression with id:" + id);
            statement.setInt(1, id);
            statement.executeUpdate();
            logger.debug("Successfully deleted expression with id:" + id);
        } catch (SQLException | ClassNotFoundException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteLastRowExpression() throws StorageException {
        logger.debug("Connecting to database...");
        try (Connection connection = getConnection();
             PreparedStatement statement = getPreparedStatement(SQL_DELETE_LAST_EXPRESSION, connection)) {
            logger.debug("Connected to database! And trying to delete last row expression");
            statement.executeUpdate();
            logger.debug("Successfully deleted last row");
        } catch (SQLException | ClassNotFoundException e) {
            throw new StorageException(e.getMessage(), e);
        }
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);
    }

    private PreparedStatement getPreparedStatement(String sql, Connection connection) throws SQLException {
        return connection.prepareStatement(sql);
    }

}
