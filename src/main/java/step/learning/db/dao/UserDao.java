package step.learning.db.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.db.dto.User;
import step.learning.services.db.DbProvider;
import step.learning.services.kdf.KdfService;

import javax.inject.Named;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class UserDao {
    private final DbProvider dbProvider;
    private final KdfService kdfService;
    private final Logger logger;
    private final String dbPrefix;
    @Inject
    UserDao(DbProvider provider, KdfService kdfService, Logger logger,
            @Named("DbPrefix") String dbPrefix){
        this.dbProvider = provider;
        this.kdfService = kdfService;
        this.logger = logger;
        this.dbPrefix = dbPrefix;
    }

    public boolean confirmEmailCoder(User user, String code){
        if( user == null || code == null  || !code.equals(user.getEmailConfirmCode()))
            return false;
        String sql = "UPDATE " + dbPrefix + "Users SET emailConfirmCode = null WHERE id =?";
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)){
                prep.setString(1,user.getId().toString());
                prep.executeUpdate();
                return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage()+"--"+sql);
            return false;
        }

    }
    /**
     * CREATE TABLE and INSERT first user
     * */

    public void install(){

        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + dbPrefix + "Users (" +
                "id               CHAR(36)    PRIMARY KEY," +
                "firstName        VARCHAR(50) NULL," +
                "lastName         VARCHAR(50) NULL," +
                "email            VARCHAR(128) NOT NULL," +
                "emailConfirmCode CHAR(6)     NULL," +
                "phone            VARCHAR(16) NULL," +
                "phoneConfirmCode VARCHAR(6)  NULL," +
                "birthdate        DATETIME    NULL," +
                "avatar           VARCHAR(512)NULL," +
                "`login`          VARCHAR(64) NOT NULL UNIQUE," +
                "salt             CHAR(8)     NOT NULL," +
                "passwordDk       VARCHAR(64) NOT NULL COMMENT 'Derived Key (RFC 2898)'," +
                "registerDT       DATETIME    DEFAULT  CURRENT_TIMESTAMP," +
                "lastLoginDT      DATETIME    NULL," +
                "culture          VARCHAR(5)  NULL COMMENT 'uk-UA'," +
                "gender           VARCHAR(64) NULL," +
                "banDT            DATETIME    NULL," +
                "deleteDT         DATETIME    NULL," +
                "roleId           CHAR(36)    NULL" +
                ") Engine = InnoDB  DEFAULT CHARSET = utf8";

           String id ="3476262b-5145-11ee-8782-5ed5d7e7214b";
           String salt = id.substring(0,8);
           String defaultPassword ="admin123";
           String passwordDK = kdfService.getDerivedKye(defaultPassword,salt);

           String insetSQL = String.format(
                   "INSERT INTO %1$sUsers (id,email,`login`,salt,passwordDk) "+
                    "VALUES('%2$s','admin@some.mail.com','admin','%3$s','%4$s') "+
                   "ON DUPLICATE KEY UPDATE salt ='%3$s',passwordDk ='%4$s'",
                   dbPrefix,id,salt,passwordDK);

        try(Statement statement = this.dbProvider.getConnection().createStatement()){
            // ADO.NET :  SQLCommand
            statement.executeUpdate( sqlCreateTable );
            statement.executeUpdate(insetSQL);

        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage()+"--"+sqlCreateTable+"--"+insetSQL);
            throw new RuntimeException(e);
        }
    }

    public void addUser(User user){
      String sql = "INSERT INTO "+dbPrefix+"Users (id,firstName,lastName,email,emailConfirmCode,phone" +
              ",phoneConfirmCode,birthdate,avatar,`login`,salt" +
              ",passwordDk,culture,gender,roleId,`registerDT`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)){
          prep.setString(1,  user.getId().toString());
          prep.setString(2,  user.getFirstName());
          prep.setString(3,  user.getLastName());
          prep.setString(4,  user.getEmail());
          prep.setString(5,  user.getEmailConfirmCode());
          prep.setString(6,  user.getPhone());
          prep.setString(7,  user.getPhoneConfirmCode());
          prep.setDate (8, new java.sql.Date(user.getBirthdate().getTime()));
          prep.setString(9,  user.getAvatar());
          prep.setString(10, user.getLogin());
          prep.setString(11, user.getSalt());
          prep.setString(12, user.getPasswordDk());
          prep.setString(13, user.getCulture());
          prep.setString(14, user.getGender());
          prep.setString(15, user.getRoleId()!= null? user.getRoleId().toString():null);
          prep.setTimestamp(16, new java.sql.Timestamp(user.getRegisterDT().getTime()));
          prep.executeUpdate();

//          Properties emailProperties = new Properties();
//          emailProperties.put("mail.smtp.auth","true");
//          emailProperties.put("mail.smtp.starttls.enable","true");
//          emailProperties.put("mail.smtp.port","587");
//          emailProperties.put("mail.smtp.ssl.protocols","TLSv1.2");
//          emailProperties.put("mail.smtp.ssl.trust","smtp.gmail.com");
//
//          javax.mail.Session mailSession = Session.getInstance(emailProperties);
//          mailSession.setDebug(true); // выводить в консоль процесс отправки почты
//
//          try(Transport emailTransport = mailSession.getTransport("smtp")) {
//              emailTransport.connect("smtp.gmail.com","alex1991020480@gmail.com","zzemwvgtouowjjud");
//              // настраиваем сообщение
//              javax.mail.internet.MimeMessage message = new MimeMessage( mailSession);
//              message.setFrom(new InternetAddress( "alex1991020480@gmail.com"));
//              message.setSubject("Код подтверждения регистрации JavaWeb");
//              message.setContent("<p><b>код регистрации --- "+user.getEmailConfirmCode()+" --- </b> с регистрацией! <a href='http://localhost:8080/JavaWeb_PU_121_3/'>на Сайте</a></p> ","text/html; charset=UTF-8");
//              emailTransport.sendMessage(message, InternetAddress.parse(user.getEmail()));
//              // отправляем его
//          } catch (MessagingException e) {
//              throw new RuntimeException(e.getMessage());
//          }
      } catch (SQLException e) {
          logger.log(Level.SEVERE,e.getMessage()+"--"+sql);
          throw new RuntimeException(e);
      }
    }

    public User aunthenticate(String login, String password){
        String sql = "SELECT u.* FROM "+dbPrefix+"Users u WHERE u.`login` = ?";
        try (PreparedStatement statement = dbProvider.getConnection().prepareStatement(sql)) {
            statement.setString(1,login);
            ResultSet res =statement.executeQuery();
            if(res.next()){ // есть данные
                User user = new User(res);
                if(kdfService.
                        getDerivedKye(password, user.getSalt())
                        .equals(user.getPasswordDk())){
                    return user;
                }

            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage()+"--"+sql);
            throw new RuntimeException(e);
        }
        return null;
    }

    public void SetLastLoginTime(User user) {
        String sqlDataUpdate = "UPDATE "+dbPrefix+"Users SET `lastLoginDT` = CURRENT_TIMESTAMP " +
                "WHERE `id` = '"+user.getId()+"'";
        try(Statement statement = dbProvider.getConnection().createStatement()) {
            statement.execute(sqlDataUpdate);
            user.setLastLoginDT(new Date());
        }catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage()+"--"+sqlDataUpdate);
            throw new RuntimeException(e);
        }
    }
}
