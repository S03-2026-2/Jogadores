import jakarta.persistence.*;

@Entity
@Table(name = "jogador_autorizacao")
public class JogadorAutorizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jogador_id", nullable = false)
    private Jogador jogador;

    @ManyToOne
    @JoinColumn(name = "autorizacao_id", nullable = false)
    private Autorizacao autorizacao;

    @Column(nullable = false)
    private boolean permitida;

    public boolean isPermitida() {
        return permitida;
    }

    public void setPermitida(boolean permitida) {
        this.permitida = permitida;
    }

    public Jogador getJogador() {
        return jogador;
    }

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
    }

    public Autorizacao getAutorizacao() {
        return autorizacao;
    }

    public void setAutorizacao(Autorizacao autorizacao) {
        this.autorizacao = autorizacao;
    }
}
