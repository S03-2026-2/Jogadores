@Service
public class AutorizacaoService {

    private final AutorizacaoRepository autorizacaoRepository;

    public AutorizacaoService(
            AutorizacaoRepository autorizacaoRepository) {
        this.autorizacaoRepository = autorizacaoRepository;
    }

    public boolean verificarAutorizacao(
            Long jogadorId,
            String acao) {

        return autorizacaoRepository
                .findByJogadorIdAndAcao(jogadorId, acao)
                .map(Autorizacao::isPermitida)
                .orElse(false);
    }
}
