import java.time.LocalDateTime;

public class DadosJogador {
    private int id;
    private int mmr;
    private String preferencias;
    private LocalDateTime dataCriacao;

    public DadosJogador(int id, int mmr, String preferencias) {
        this.id = id;
        this.mmr = mmr;
        this.preferencias = preferencias;
        this.dataCriacao = LocalDateTime.now();
    }
}