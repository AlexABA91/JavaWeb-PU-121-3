package step.learning.db.dao;

import com.google.inject.Inject;
import step.learning.services.db.DbProvider;

import java.sql.SQLException;
import java.sql.Statement;

public class UserDao {
    DbProvider provide;
    @Inject
    UserDao(DbProvider provider){
        this.provide = provider;
    }
    public boolean ensureCreated(){
        String sql = "CREATE TABLE IF NOT EXISTS JavaWeb_User (" +
                "`id`        CHAR(36)     PRIMARY KEY," +
                "`login` VARCHAR(50)," +
                "`passwordHash`   VARCHAR(256)," +
                "`name` VARCHAR(100)," +
                "`phone` VARCHAR(15)," +
                "`registrationData` VARCHAR(100)," +
                "`lastLoginData` VARCHAR(100) NULL," +
                "`confirmCode` double NULL," +
                "`email` VARCHAR(100)"+
                "`activate` boolean,"+
                ")";
        System.out.print("ensureCreate: ");
        try(Statement statement = this.provide.getConnection().createStatement()){
            // ADO.NET :  SQLCommand
            statement.executeUpdate( sql );
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
