package step.learning.services.hesh;

public class KupinaHashService implements HashService{
    private  final Kupina  k = new Kupina(128);
    @Override
    public String hash(String input) {
        k.update(input.getBytes());
        return k.digestHex();
    }
}
