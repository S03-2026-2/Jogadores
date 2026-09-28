@Entity
@Table(name = "jogador_autorizacao")
public class JogadorAutorizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "jogador_id")
    private Jogador jogador;

    @ManyToOne
    @JoinColumn(name = "autorizacao_id")
    private Autorizacao autorizacao;

    private boolean permitida;
}
