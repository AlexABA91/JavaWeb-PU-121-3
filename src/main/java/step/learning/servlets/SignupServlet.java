package step.learning.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.fileupload.FileItem;
import step.learning.services.formparse.FormParsResult;
import step.learning.services.formparse.FormParsService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class SignupServlet extends HttpServlet {
    private final FormParsService formParsService;

    @Inject
    public SignupServlet(FormParsService formParsService) {
        this.formParsService = formParsService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("pageName", "signup");
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SignupFormData formData;
        try {
            formData = new SignupFormData(req);



        } catch (Exception ex) {
            resp.getWriter().print("message There was an error: " + ex.getMessage());
            formData = null;
        }

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gen = builder.create();

        resp.getWriter().print(
                gen.toJson(formData)
        );
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
        private final SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");

        private final String uploadPath;

        public SignupFormData(HttpServletRequest req) {
            FormParsResult parsResult = formParsService.pars(req);
            Map<String, String> fields = parsResult.getFields();
            uploadPath = req.getServletContext().getRealPath("")+ "upload";

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
            String filePath;
            try {
            checkExtension(fileName);
            fileName = generateRandomName(fileName);
            while (checkFileName(fileName)) {
              fileName =  generateRandomName(fileName);
            }
            filePath = uploadPath + File.separator + fileName;
            File storeFile = new File(filePath);
             // saves the file on disk

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

        private boolean checkFileName(String fileName) {
            Set<String> fileNameInDir = new HashSet<>();
            try(Stream<Path> stream = Files.list(Paths.get(uploadPath))){
            fileNameInDir = stream.filter(file->!Files.isDirectory(file))
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.toSet());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return fileNameInDir.contains(fileName);
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
