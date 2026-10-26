import org.springframework.stereotype.Service;

@Service
public class AutorizacaoService {

    private final JogadorAutorizacaoRepository jogadorAutorizacaoRepository;

    public AutorizacaoService(
            JogadorAutorizacaoRepository jogadorAutorizacaoRepository) {

        this.jogadorAutorizacaoRepository = jogadorAutorizacaoRepository;
    }

    public boolean verificarAutorizacao(
            int jogadorId,
            String acao) {

        return jogadorAutorizacaoRepository
                .findByJogador_IdAndAutorizacao_Acao(
                        jogadorId,
                        acao
                )
                .map(JogadorAutorizacao::isPermitida)
                .orElse(false);
    }
}
