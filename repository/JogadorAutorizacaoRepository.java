import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JogadorAutorizacaoRepository
        extends JpaRepository<JogadorAutorizacao, Long> {

    Optional<JogadorAutorizacao>
            findByJogador_IdAndAutorizacao_Acao(
                    int jogadorId,
                    String acao
            );
}
