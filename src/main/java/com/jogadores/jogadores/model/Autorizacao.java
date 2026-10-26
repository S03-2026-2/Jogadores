@Entity
@Table(name = "autorizacoes")
public class Autorizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Acao acao;

    // construtores
    // getters
    // setters
}
