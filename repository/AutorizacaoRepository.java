public interface AutorizacaoRepository
        extends JpaRepository<Autorizacao, Long> {

    Optional<Autorizacao> findByJogadorIdAndAcao(
        Long jogadorId,
        String acao
    );
}
