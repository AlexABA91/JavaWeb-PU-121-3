package step.learning.services.formparse;

import org.apache.commons.fileupload.FileItem;

import java.util.Map;

public interface FormParsResult {
    Map<String,String> getFields();
    Map<String, FileItem> getFiles();

}
