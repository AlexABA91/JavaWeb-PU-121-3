package step.learning.Ioc;

import com.google.inject.servlet.ServletModule;
import step.learning.filters.CharsetFilter;
import step.learning.filters.FileWriterFilter;

/**
 * Конфигурация фильтров для Guice*/
public class FilterConfig  extends ServletModule {
    @Override
    protected void configureServlets() {
       // filter("/*").through(FileWriterFilter.class);
        filter("/*").through(CharsetFilter.class);

    }
}
