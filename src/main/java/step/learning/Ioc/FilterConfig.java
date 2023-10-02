package step.learning.Ioc;

import com.google.inject.servlet.ServletModule;
import step.learning.filters.CharsetFilter;
import step.learning.filters.DbFilter;
import step.learning.filters.AuthorizationFilter;

/**
 * Конфигурация фильтров для Guice*/
public class FilterConfig  extends ServletModule {
    @Override
    protected void configureServlets() {
       // filter("/*").through(FileWriterFilter.class);
        filter("/*").through(CharsetFilter.class);
        filter("/*").through(DbFilter.class);
        filter("/*").through(AuthorizationFilter.class);

    }
}
