package step.learning.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.fileupload.FileItem;
import step.learning.db.dao.UserDao;
import step.learning.db.dao.WebTokenDao;
import step.learning.db.dto.User;
import step.learning.db.dto.WebToken;
import step.learning.services.formparse.FormParsResult;
import step.learning.services.formparse.FormParsService;
import step.learning.services.kdf.KdfService;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class SignupServlet extends HttpServlet {
    private final FormParsService formParsService;
    private final KdfService kdfService;
    private final String uploadPath;
    private final UserDao userDao;
    private  final Logger logger;
    private final WebTokenDao webTokenDao;
    @Inject
    public SignupServlet(@Named ("UploadDir") String uploadPath, KdfService kdfService,
                         FormParsService formParsService, UserDao userDao, Logger logger,
                         WebTokenDao webTokenDao) {
        this.formParsService = formParsService;
        this.uploadPath = uploadPath;
        this.kdfService = kdfService;
        this.userDao = userDao;
        this.logger = logger;
        this.webTokenDao = webTokenDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("pageName", "signup");
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResponseData responseData;
        WebToken webToken = null;
         try {
             FormParsResult parsResult = formParsService.pars(req);
             String login = parsResult.getFields().get("aut-login");
             String password = parsResult.getFields().get("aut-password");
//              Geson
//            JsonObject json = JsonParser.parseReader(req.getReader()).getAsJsonObject();
//            String login = json.get("aut-login").getAsString();
//             String password = json.get("aut-password").getAsString();
                User user = userDao.aunthenticate(login,password);
               if(user!= null) {
                   //Генерируем WbToken
                    webToken = webTokenDao.create(user);
//                   Д.З. Перевірити та налаштувати алгоритм формування токенів,
//                   перевірити правильність дат створення та закінчення токену,
//                           вивести одержаний токен у повідомленні модального вікна.
//                   Повторити теорію про window.localStorage
                   responseData = new ResponseData(200, "OK");
               }else {
                   responseData = new ResponseData(401, "Unauthorized");
               }

         }catch (Exception ex){
             logger.log(Level.SEVERE, ex.getMessage());

             responseData = new ResponseData(500, "There was an error 500. Look at server`s log");
         }
        Gson gen = new GsonBuilder().setPrettyPrinting().create();

        ResponseDataAll rep = new ResponseDataAll(responseData, webToken);
        resp.getWriter().print(
                gen.toJson(rep)
        );


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SignupFormData formData;
        ResponseData responseData;
        String uploadDir = req.getServletContext().getRealPath("./") + "upload" ;
        try {
            formData = new SignupFormData(uploadDir,req);
            User user = formData.toUserDto();
            //  userDao.addUser(user);
            // TODO: send confirm codes
            responseData = new ResponseData(200, "OK");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            responseData = new ResponseData(500, "There was an error 500. Look at server`s log");
            formData = null;
        }

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gen = builder.create();

        resp.getWriter().print(
                gen.toJson(responseData)
        );
    }
    static class ResponseDataAll{
        ResponseData responseData;
        WebToken webToken;

        public ResponseData getResponseData() {
            return responseData;
        }

        public void setResponseData(ResponseData responseData) {
            this.responseData = responseData;
        }

        public WebToken getWebToken() {
            return webToken;
        }

        public void setWebToken(WebToken webToken) {
            this.webToken = webToken;
        }

        public ResponseDataAll(ResponseData responseData, WebToken webToken) {
            this.responseData = responseData;
            this.webToken = webToken;
        }
    }
    static class ResponseData{
        int statusCode;
        String messageStr;
        public ResponseData() {
        }

        public ResponseData(int statusCode, String messageStr) {
            this.statusCode = statusCode;
            this.messageStr = messageStr;

        }
        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessageStr() {
            return messageStr;
        }

        public void setMessageStr(String messageStr) {
            this.messageStr = messageStr;
        }
    }
    class SignupFormData {
        // region Fields
        private String name;
        private String lastName;
        private String email;
        private String phone;
        private Date birthdate;
        private String login;
        private String password;
        private String culture;
        private String gender;
        private String avatar;
        //endregion

        //region Accessors
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Date getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(Date birthdate) {
            this.birthdate = birthdate;
        }
        public void setBirthdate(String birthdate){
            if (birthdate != null && ! birthdate.isEmpty()) {
                try {
                    setBirthdate(dateParser.parse(birthdate));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }else {
                setBirthdate((Date) null);
            }
        }
        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
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

        public String getAvatar() {
            return avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
    //endregion
        private final transient SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");

         public User toUserDto(){
             User user = new User() ;
             user.setId(UUID.randomUUID());
             user.setAvatar( this.getAvatar() ) ;
             user.setFirstName( this.getName() ) ;
             user.setLastName( this.getLastName() ) ;
             user.setLogin( this.getLogin() ) ;
             user.setGender( this.getGender() ) ;
             user.setCulture( this.getCulture() ) ;
             user.setBirthdate( this.getBirthdate() ) ;
             user.setPhone( this.getPhone() ) ;

             if(user.getPhone() != null){
                 // генерируем
                 String phoneCode = UUID.randomUUID().toString().substring(0,6);

                 user.setPhoneConfirmCode(phoneCode);
                 //  TODO: отправляем
             }

             user.setEmail( this.getEmail() );

             if(user.getEmail() != null){
                 // генерируем
                 String emailCode = UUID.randomUUID().toString().substring(0,6);

                 user.setEmailConfirmCode(emailCode);
                 //  TODO: отправляем
             }
             user.setId( UUID.randomUUID() ) ;

             user.setDeleteDT( null ) ;
             user.setBanDT( null ) ;
             user.setRegisterDT( new Date() ) ;
             user.setLastLoginDT( null ) ;

             user.setSalt( user.getId().toString().substring(0, 8) ) ;
             user.setPasswordDk( kdfService.getDerivedKye( this.getPassword(), user.getSalt() ) ) ;


             return user ;
         };
        private final String uploadPath;

            public SignupFormData(String uploadPath, HttpServletRequest req) {
            FormParsResult parsResult = formParsService.pars(req);
            Map<String, String> fields = parsResult.getFields();
            this.uploadPath = uploadPath;

            setName(fields.get("reg-name"));
            setLastName(fields.get("reg-lastname"));
            setEmail(fields.get("reg-email"));
            setPhone(fields.get("reg-phone"));
            setLogin(fields.get("reg-login"));
            setPassword(fields.get("reg-password"));
            setCulture(fields.get("reg-culture"));
            setGender(fields.get("reg-gender"));
            setBirthdate(fields.get("reg-birthdate"));

            Map<String, FileItem> files = parsResult.getFiles();

            if(files.containsKey("reg-avatar")){
                setAvatar(files.get("reg-avatar"));
            }else
                setAvatar((String) null);
        }
        public void setAvatar(FileItem avatar)  {
            String fileName = new File(avatar.getName()).getName();
            try {
            checkExtension(fileName);
                File storeFile;
            do{
                fileName =  generateRandomName(fileName);
                storeFile = new File( uploadPath + File.separator + fileName);
            }while (storeFile.exists());
                avatar.write(storeFile);

            }catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.avatar = fileName;
        }

        private String generateRandomName(String fileName) {
                String ext = fileName.substring(fileName.lastIndexOf("."));
                String tempFileName = UUID.randomUUID().toString().substring(0, 10);
                return tempFileName + ext;
        }

        private void checkExtension(String fileName) {
            List<String>validExtension = Arrays.asList(".png",".jpg",".jpeg",".svg",".webp");
            String ext = fileName.substring(fileName.lastIndexOf("."));
            if(!validExtension.contains(ext)){
                throw new InvalidParameterException();
            };

        }

        public void setAvatar(String avatar) {

            this.avatar = avatar;
        }
    }
}
