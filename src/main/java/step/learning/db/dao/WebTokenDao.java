package step.learning.db.dao;

import com.google.inject.Inject;
import step.learning.db.dto.User;
import step.learning.db.dto.WebToken;
import step.learning.services.db.DbProvider;
import sun.security.util.AuthResources_de;

import javax.inject.Named;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebTokenDao {
    private final DbProvider dbProvider;
    private final Logger logger;
    private final String dbPrefix;

    @Inject
    public WebTokenDao(DbProvider dbProvider, Logger logger,@Named("DbPrefix") String dbPrefix) {
        this.dbProvider = dbProvider;
        this.logger = logger;
        this.dbPrefix = dbPrefix;
    }

    public WebToken create(User user ){
        if(user == null) return  null;
        String sql = "INSERT INTO " +dbPrefix+"WebTokens(`id`,`sub`,`exp`,`iat`)VALUES(?,?,?,?)";
        try(PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)){
            WebToken webToken = new WebToken();
            webToken.setId(UUID.randomUUID());
            webToken.setSub(user.getId());
            webToken.setIat(new Date());
            Calendar c = Calendar.getInstance();
             c.setTime(webToken.getIat());
             c.add(Calendar.DATE,1);
            webToken.setExp(
                    c.getTime()
            );
            prep.setString (1,webToken.getId().toString());
            prep.setString (2,webToken.getSub().toString());
            prep.setTimestamp (3,new Timestamp( webToken.getExp().getTime()));
            prep.setTimestamp (4,new Timestamp( webToken.getIat().getTime()));
            prep.executeUpdate();
            return webToken;

        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage()+"--"+sql);
            throw new RuntimeException(e);
        }
    }

    public User getSubject(WebToken token){
        return null;
    };
    public void Install(){
        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + dbPrefix + "WebTokens (" +
                "`id`  CHAR(36) PRIMARY KEY,"+
                "`sub`  CHAR(36) COMMENT 'User ID',"+
                "`exp`  DATETIME NOT NULL,"+
                "`iat`  DATETIME DEFAULT CURRENT_TIMESTAMP"+
                ") Engine = InnoDB  DEFAULT CHARSET = utf8";
        try(Statement statement = dbProvider.getConnection().createStatement()){
            statement.executeUpdate(sqlCreateTable);
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage()+"--"+sqlCreateTable);
            throw new RuntimeException(e);
        }
    }
}
