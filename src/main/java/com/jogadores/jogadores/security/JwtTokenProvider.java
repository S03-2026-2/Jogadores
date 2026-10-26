@Component
public class JwtTokenProvider implements TokenProvider {

    private final String chaveSecreta;
    private final long expiracaoMinutos;

    @Override
    public String gerarToken(int jogadorId, Papel papel) {
        // cria JWT
    }

    @Override
    public boolean validarToken(String token) {
        // verifica assinatura + expiração
    }

    @Override
    public int extrairJogadorId(String token) {
        // pega jogadorId do JWT
    }

    @Override
    public Papel extrairPapel(String token) {
        // pega papel do JWT
    }
}
