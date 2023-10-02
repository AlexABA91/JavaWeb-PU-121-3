package step.learning.db.dao;

import com.google.inject.Inject;
import step.learning.db.dto.User;
import step.learning.db.dto.WebToken;
import step.learning.services.db.DbProvider;
import sun.security.util.AuthResources_de;

import javax.inject.Named;
import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public WebToken get (User user ){
        if(user == null) return  null;
        String sql ="SELECT w.`id`,w.`sub`,w.`exp`,w.`iat` FROM " +dbPrefix+"WebTokens w " +
                "WHERE w.`sub` = ? AND w`exp` > CURRENT_TIMESTAMP";
        try(PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)){
            prep.setString (1,user.getId().toString());
            ResultSet res = prep.executeQuery();
            if(res.next()){
            return new WebToken(res);}
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage()+"--"+sql);
        }
        return null;
    }

    public WebToken create(User user ){
        if(user == null) return  null;
        WebToken activeToken;
        try{
            activeToken = this.get(user);
        }
        catch (RuntimeException ignored){activeToken = null;}
        String sql = "";

        if(activeToken == null) {
            // генерируем новый токен
            sql = "INSERT INTO " + dbPrefix + "WebTokens(`id`,`sub`,`exp`,`iat`)VALUES(?,?,?,?)";
            try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
                WebToken webToken = new WebToken();
                webToken.setId(UUID.randomUUID());
                webToken.setSub(user.getId());
                webToken.setIat(new Date());
                Calendar c = Calendar.getInstance();
                c.setTime(webToken.getIat());
                c.add(Calendar.DATE, 1);
                webToken.setExp(
                        c.getTime()
                );
                prep.setString(1, webToken.getId().toString());
                prep.setString(2, webToken.getSub().toString());
                prep.setTimestamp(3, new Timestamp(webToken.getExp().getTime()));
                prep.setTimestamp(4, new Timestamp(webToken.getIat().getTime()));
                prep.executeUpdate();
                return webToken;

            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage() + "--" + sql);
                throw new RuntimeException(e);
            }
        }else { // обновляем термин
            sql = "UPDATE "+ dbPrefix + "WebTokens SET `ext` = DATE_ADD(CURRENT_TIMESTAMP,INTERVAL 1 DAY)" +
                    "WHERE `id` = '"+activeToken.getId()+"'";
            try(Statement statement = dbProvider.getConnection().createStatement()){
                statement.executeUpdate(sql);
                activeToken.setExp(new Date(new Date().getTime() +24*60*60*1000));
                return activeToken;
            } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage() + "--" + sql);
            throw new RuntimeException(e);
        }

        }

    }
    public User getSubject( String header ) {
        Matcher matches = Pattern.compile( "Bearer (.+)$" ).matcher( header ) ;
        if( matches.find() ) {
            try {
                return this.getSubject( new WebToken( matches.group(1) ) ) ;
            }
            catch( ParseException ignored ) { }
        }
        return null ;
    }

    public User getSubject(WebToken token){
        if(token == null ||
                token.getId() == null ||
                token.getSub() == null||
                token.getExp() == null||
                token.getExp().before(new Date())) return null;

        String sql = "SELECT u.* FROM "+dbPrefix+"Users u" +
                " JOIN " +dbPrefix + "WebTokens w ON u.`id` = w.`sub`" +
                " WHERE w.`id` = ? AND w.`exp` > CURRENT_TIMESTAMP";

        try(PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)){
            prep.setString(1, token.getId().toString());
            ResultSet res = prep.executeQuery();
            if(res.next()){
                return new User(res);
            }
        }catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage() + "--" + sql);
            return null;
        }
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
