package step.learning.db.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.UUID;

public class User {
    // region fields
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String emailConfirmCode;
    private String phone;
    private String phoneConfirmCode;
    private Date birthdate;
    private String avatar;
    private String login;  // unique
    private String salt;
    private String passwordDk;  // Derived Key (RFC 2898)
    private Date registerDT;
    private Date lastLoginDT;
    private String culture;  // en-US uk-UA
    private String gender;
    private Date banDT;
    private Date deleteDT;
    private UUID roleId;
    //endregion

    // region constructors
        public User(){}

    public User(ResultSet resultSet) throws SQLException {
        setId( UUID.fromString( resultSet.getString("id") ) ) ;
        setFirstName( resultSet.getString("firstName") ) ;
        setLastName( resultSet.getString("lastName") ) ;
        setEmail( resultSet.getString("email") ) ;
        setEmailConfirmCode( resultSet.getString("emailConfirmCode") ) ;
        setPhone( resultSet.getString("phone") ) ;
        setPhoneConfirmCode( resultSet.getString("phoneConfirmCode") ) ;
        setBirthdate( resultSet.getDate( "birthdate" ) ) ;
        setAvatar( resultSet.getString( "avatar" ) ) ;
        setLogin( resultSet.getString( "login" ) ) ;
        setSalt( resultSet.getString( "salt" ) ) ;
        setPasswordDk( resultSet.getString( "passwordDk" ) ) ;
        setRegisterDT( resultSet.getDate( "registerDT" ) ) ;
        setBanDT( resultSet.getDate( "banDT" ) ) ;
        setDeleteDT( resultSet.getDate( "deleteDT" ) ) ;
        setRoleId( UUID.fromString( resultSet.getString("roleId") ) ) ;
        setCulture( resultSet.getString( "culture" ) ) ;
        setGender( resultSet.getString( "gender" ) ) ;
    }

    //endregion

    //region accessors

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailConfirmCode() {
        return emailConfirmCode;
    }

    public void setEmailConfirmCode(String emailConfirmCode) {
        this.emailConfirmCode = emailConfirmCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneConfirmCode() {
        return phoneConfirmCode;
    }

    public void setPhoneConfirmCode(String phoneConfirmCode) {
        this.phoneConfirmCode = phoneConfirmCode;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPasswordDk() {
        return passwordDk;
    }

    public void setPasswordDk(String passwordDk) {
        this.passwordDk = passwordDk;
    }

    public Date getRegisterDT() {
        return registerDT;
    }

    public void setRegisterDT(Date registerDT) {
        this.registerDT = registerDT;
    }

    public Date getLastLoginDT() {
        return lastLoginDT;
    }

    public void setLastLoginDT(Date lastLoginDT) {
        this.lastLoginDT = lastLoginDT;
    }

    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBanDT() {
        return banDT;
    }

    public void setBanDT(Date banDT) {
        this.banDT = banDT;
    }

    public Date getDeleteDT() {
        return deleteDT;
    }

    public void setDeleteDT(Date deleteDT) {
        this.deleteDT = deleteDT;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    //endregion
}
