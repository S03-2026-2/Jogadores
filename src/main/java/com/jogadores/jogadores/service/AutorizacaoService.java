@Service
public class AutorizacaoService {

    private final TokenProvider tokenProvider;
    private final AutorizacaoRepository autorizacaoRepository;

    public AutorizacaoService(
            TokenProvider tokenProvider,
            AutorizacaoRepository autorizacaoRepository) {

        this.tokenProvider = tokenProvider;
        this.autorizacaoRepository = autorizacaoRepository;
    }

    public AutorizacaoResponse verificarAutorizacao(
            String token,
            Acao acao) {

        if (!tokenProvider.validarToken(token)) {
            return new AutorizacaoResponse(
                    0,
                    acao,
                    false
            );
        }

        int jogadorId =
                tokenProvider.extrairJogadorId(token);

        Papel papel =
                tokenProvider.extrairPapel(token);

        boolean permitida =
                autorizacaoRepository
                        .existsByPapelAndAcao(papel, acao);

        return new AutorizacaoResponse(
                jogadorId,
                acao,
                permitida
        );
    }
}
