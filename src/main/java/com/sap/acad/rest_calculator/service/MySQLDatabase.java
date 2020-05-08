package com.sap.acad.rest_calculator.service;

import com.mysql.cj.jdbc.MysqlDataSource;


public class MySQLDatabase {

    private ConfigProperties properties = new ConfigProperties();

    public MysqlDataSource getDatabase() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(this.properties.getPropertyWithKey("user"));
        dataSource.setPassword(this.properties.getPropertyWithKey("password"));
        dataSource.setServerName(this.properties.getPropertyWithKey("servername"));
        dataSource.setDatabaseName(this.properties.getPropertyWithKey("databaseName"));
        return dataSource;
    }


}
