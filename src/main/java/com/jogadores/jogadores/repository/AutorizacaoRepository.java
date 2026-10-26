public interface AutorizacaoRepository
        extends JpaRepository<Autorizacao, Long> {

    boolean existsByPapelAndAcao(Papel papel, Acao acao);
}
