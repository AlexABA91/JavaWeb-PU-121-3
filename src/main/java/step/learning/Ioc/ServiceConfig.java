package step.learning.Ioc;

import com.google.inject.AbstractModule;
import step.learning.services.hesh.HashService;
import step.learning.services.hesh.KupinaHashService;

public class ServiceConfig extends AbstractModule {
    @Override
    protected void configure() {
        bind(HashService.class).to(KupinaHashService.class);
    }
}
