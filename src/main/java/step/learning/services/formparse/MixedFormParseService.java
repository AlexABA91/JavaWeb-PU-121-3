package step.learning.services.formparse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 Parser for both multipart and urlencoded forms
 */
@Singleton
public class MixedFormParseService implements FormParsService {
  // private final String uploadDir;
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB
    private final ServletFileUpload fileUpload;

    @Inject
    public MixedFormParseService() {
    //    this.uploadDir = uploadDir;
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        fileUpload = new ServletFileUpload(factory);
        fileUpload.setFileSizeMax(MAX_FILE_SIZE);
        fileUpload.setSizeMax(MAX_REQUEST_SIZE);
    }

    @Override
    public FormParsResult pars(HttpServletRequest request) {
  //     String uploadPath = request.getServletContext().getRealPath("")
 //               + File.separator + uploadDir;
//
//        // creates the directory if it does not exist
//        File uploadDir = new File(uploadPath);
//        if (!uploadDir.exists()) {
//            uploadDir.mkdir();
//        }
        // готовим коллекции для результата
        Map<String, String> fields = new HashMap<>();
        Map<String, FileItem> files = new HashMap<>();
        // разделяем работу в зависимости от типа запроса
        boolean isMultipart = request.getHeader("Content-Type").startsWith("multipart/form-data");
        if ( isMultipart /*ServletFileUpload.isMultipartContent(request)*/) { // multipart
            try {
               // используя созданный в конструкторе паркер
                  // разбираем параметры запроса
                       for(FileItem fileItem : fileUpload.parseRequest(request))  // проверяем каждую часть этого запроса (part of multipart)
                        {
                            if (fileItem.isFormField()) { // если ето поле формы -
                                    fields.put(                // то добавляем до коллекции fields
                                            fileItem.getFieldName(),
                                            fileItem.getString("UTF-8")
                                    );
                            } else {// иначе это файловая часть
                                files.put(   // добавляем ведомости к ней
                                        fileItem.getFieldName(),
                                        fileItem
                                );
                            }
                        }

            } catch (FileUploadException | UnsupportedEncodingException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        } else { // urlencoded. Используем стандартный сервлет API
            Enumeration<String> parametersNames = request.getParameterNames();
            // перебираем имена всех параметров запроса и перемещаем в коллекцию
            while (parametersNames.hasMoreElements()) {
                String name = parametersNames.nextElement();
                fields.put(name, request.getParameter(name));
            }
        }
        return new FormParsResult() {
            @Override
            public Map<String, String> getFields() {
                return fields;
            }

            @Override
            public Map<String, FileItem> getFiles() {
                return files;
            }
        };
    }
}
