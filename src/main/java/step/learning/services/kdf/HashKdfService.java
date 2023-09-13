package step.learning.services.kdf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.hesh.HashService;

/**
 * Hash Based KDF
 * */
@Singleton
public class HashKdfService implements KdfService{

    @Inject
    public HashKdfService(HashService hashService) {
        this.hashService = hashService;
    }

    private final HashService hashService;
    @Override
    public String getDerivedKye(String password, String salt) {
        return hashService.hash( String.format("%s-%s",password,salt));
    }
}
