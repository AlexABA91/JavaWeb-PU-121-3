package step.learning.services.kdf;
/**
 *  Key Derived Function (RFC 2898)
 * */
public interface KdfService {
   String getDerivedKye( String password,String Salt);
}
