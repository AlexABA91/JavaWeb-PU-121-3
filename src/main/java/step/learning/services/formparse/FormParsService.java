package step.learning.services.formparse;

import javax.servlet.http.HttpServletRequest;

public interface FormParsService {
    FormParsResult pars(HttpServletRequest request);
}
