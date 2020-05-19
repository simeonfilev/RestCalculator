package com.sap.acad.calculator.rest.storage.file;

import com.sap.acad.calculator.rest.exceptions.StorageException;
import com.sap.acad.calculator.rest.models.Expression;
import com.sap.acad.calculator.rest.storage.StorageInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorageImpl implements StorageInterface {

    private static final Logger logger = LogManager.getLogger(FileStorageImpl.class);
    private final String fileName;

    public FileStorageImpl(){
        fileName = "storage.txt";
        File file = new File(fileName);
        try {
            file.createNewFile();
        }catch (SecurityException e) {
            logger.error(e.getMessage(), e);
        }catch(IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public FileStorageImpl(String name) {
        fileName = name;
        File file = new File(name);
        try {
            file.createNewFile();
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
        }catch(IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void saveExpression(Expression expression) throws StorageException {
        try {
            FileWriter myWriter = new FileWriter(fileName, true);
            myWriter.write(expression.getExpression() + "," + expression.getAnswer() + System.lineSeparator());
            myWriter.close();
            logger.debug("Successfully added expression: " + expression);
        } catch (IOException e) {
            logger.error("File not found!");
            throw new StorageException(e.getMessage(),e);
        }
    }

    @Override
    public List<Expression> getExpressions() throws StorageException{
        List<Expression> expressions = new ArrayList<>();
        int counter = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            for (String line; (line = br.readLine()) != null; ) {
                String[] arguments = line.split(",");
                int id = counter++;
                String expression = arguments[0];
                Double answer = Double.parseDouble(arguments[1]);
                expressions.add(new Expression(id, expression, answer));
            }
            logger.debug("Successfully receive all expressions with count:" + expressions.size());
        } catch (IOException e) {
            logger.error("File not found!");
           throw new StorageException("File not found or can't be opened!");
        }
        return expressions;
    }

    @Override
    public void deleteExpressionById(int id) throws StorageException{
        File inputFile = new File(fileName);
        File tempFile = new File("myTempFile.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String currentLine;
            int counter = 0;
            while ((currentLine = reader.readLine()) != null) {
                if (counter == id) {
                    counter++;
                    continue;
                }
                writer.write(currentLine + System.lineSeparator());
                counter++;
            }
            logger.debug("Successfully deleted expression with id:" + id);
        } catch (IOException e) {
            logger.error("File not found!");
            throw new StorageException("File not found or can't be opened!");
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    @Override
    public void deleteLastRowExpression() throws StorageException {
        deleteExpressionById(getExpressions().size() - 1);
    }

    public void deleteStorageFile() {
        try {
            File inputFile = new File(fileName);
            inputFile.delete();
        } catch (NullPointerException e) {
            logger.error("File not found!");
            logger.error(e.getMessage(), e);
        }
    }
}
