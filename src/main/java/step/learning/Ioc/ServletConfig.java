package step.learning.Ioc;

import com.google.inject.servlet.ServletModule;
import step.learning.servlets.*;

/**
 * Конфигурация сервлетов для Guice*/
public class ServletConfig extends ServletModule {
    @Override
    protected void configureServlets() {
        serve("/").with(HomeServlets.class);
        serve("/jsp").with(JspServlet.class);
        serve("/aboutServlet").with(AboutServlet.class);
        serve("/url").with(UrlServlet.class);
        serve("/hash").with(HashServlet.class);
        serve("/install").with(InstallServlet.class);
        serve("/signup").with(SignupServlet.class);

    }
}
