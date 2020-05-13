package com.sap.acad.calculator.rest.storage.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.sap.acad.calculator.rest.models.Expression;
import com.sap.acad.calculator.rest.storage.StorageInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLStorageImpl implements StorageInterface {
    private static final Logger logger = LogManager.getLogger(MySQLStorageImpl.class);
    private static final String SQL_DELETE_LAST_EXPRESSION = "DELETE FROM expressions ORDER BY id DESC LIMIT 1;";
    private static final String SQL_GET_ALL_EXPRESSIONS = "SELECT * FROM expressions;";
    private static final String SQL_SAVE_EXPRESSION = "INSERT INTO expressions(expression,answer) VALUES (?,?);";
    private static final String SQL_DELETE_EXPRESSION_WITH_ID = "DELETE FROM expressions WHERE id = ?;";
    private MysqlDataSource dataSource;


    public MySQLStorageImpl() {
        this.dataSource = new MysqlDataSource();
        this.dataSource.setUser(System.getenv("DB_USERNAME"));
        this.dataSource.setPassword(System.getenv("DB_PASSWORD"));
        this.dataSource.setServerName(System.getenv("SERVER_NAME"));
        this.dataSource.setDatabaseName(System.getenv("DB_NAME"));
    }

    @Override
    public void saveExpression(Expression expression) {
        logger.debug("Connecting to database...");
        try (Connection connection = getConnection();
             PreparedStatement statement = getPreparedStatement(SQL_SAVE_EXPRESSION, connection)) {
            logger.debug("Connected to database! Saving expression:" + expression);
            statement.setString(1, expression.getExpression());
            statement.setDouble(2, expression.getAnswer());
            statement.execute();
            logger.debug("Successfully saved expression: " + expression);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public ArrayList<Expression> getExpressions() {
        ArrayList<Expression> expressions = new ArrayList<>();
        logger.debug("Connecting to database...");
        try (Connection connection = getConnection();
             PreparedStatement statement = getPreparedStatement(SQL_GET_ALL_EXPRESSIONS, connection);
             ResultSet rs = statement.executeQuery()) {
            logger.debug("Connected to database!");
            while (rs.next()) {
                Expression expression = new Expression();
                expression.setId(rs.getInt(1));
                expression.setExpression(rs.getString(2));
                expression.setAnswer(rs.getDouble(3));
                expressions.add(expression);
            }
            logger.debug("Successfully received all expressions with length:" + expressions.size());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return expressions;
    }

    @Override
    public void deleteExpressionById(int id) {
        logger.debug("Connecting to database...");
        try (Connection connection = getConnection();
             PreparedStatement statement = getPreparedStatement(SQL_DELETE_EXPRESSION_WITH_ID, connection)) {
            logger.debug("Connected to database! And trying to delete expression with id:" + id);
            statement.setInt(1, id);
            statement.executeUpdate();
            logger.debug("Successfully deleted expression with id:" + id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteLastRowExpression() {
        logger.debug("Connecting to database...");
        try (Connection connection = getConnection();
             PreparedStatement statement = getPreparedStatement(SQL_DELETE_LAST_EXPRESSION, connection)) {
            logger.debug("Connected to database! And trying to delete last row expression");
            statement.executeUpdate();
            logger.debug("Successfully deleted last row");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    private PreparedStatement getPreparedStatement(String sql, Connection connection) throws SQLException {
        return connection.prepareStatement(sql);
    }

}
