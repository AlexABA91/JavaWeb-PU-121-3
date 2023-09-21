package step.learning.Ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.name.Names;
import step.learning.services.db.DbProvider;
import step.learning.services.db.PlanetDbProvider;
import step.learning.services.formparse.FormParsService;
import step.learning.services.formparse.MixedFormParseService;
import step.learning.services.hesh.HashService;
import step.learning.services.hesh.KupinaHashService;
import step.learning.services.kdf.HashKdfService;
import step.learning.services.kdf.KdfService;

public class ServiceConfig extends AbstractModule {
    @Override
    protected void configure()
    {
        bind(HashService.class).to(KupinaHashService.class);
        bind(DbProvider.class).to(PlanetDbProvider.class);
        bind(KdfService.class).to(HashKdfService.class);
        bind(FormParsService.class).to(MixedFormParseService.class);

        bind(String.class)
                    .annotatedWith(Names.named("DbPrefix"))
                .toInstance("JavaWeb_");
        bind(String.class)
                .annotatedWith(Names.named("UploadDir")).toInstance("JavaWeb_");
    }

}
