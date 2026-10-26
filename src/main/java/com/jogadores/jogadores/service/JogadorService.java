import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

import org.springframework.stereotype.Service;

@Service
public class JogadorService {

    private final JogadorRepository jogadorRepository;
    private final AutorizacaoService autorizacaoService;

    public JogadorService(
            JogadorRepository jogadorRepository,
            AutorizacaoService autorizacaoService) {
        this.jogadorRepository = jogadorRepository;
        this.autorizacaoService = autorizacaoService;
    }

    public Jogador cadastrarJogador(String nome, String email, String senha) {
        if (jogadorRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado: " + email);
        }

        Credencial credencial = new Credencial();
        credencial.setSenhaHash(gerarHash(senha));
        credencial.setAtiva(true);
        credencial.setDataAtualizacao(LocalDateTime.now());

        Jogador jogador = new Jogador();
        jogador.setNome(nome);
        jogador.setEmail(email);
        jogador.setCredencial(credencial);

        return jogadorRepository.save(jogador);
    }

    public boolean autenticar(String email, String senha) {
        return jogadorRepository.findByEmail(email)
                .map(Jogador::getCredencial)
                .filter(Credencial::isAtiva)
                .map(credencial -> credencial.getSenhaHash().equals(gerarHash(senha)))
                .orElse(false);
    }

    public void redefinirSenha(int jogadorId, String novaSenha) {
        if (!verificarAutorizacao(jogadorId, "REDEFINIR_SENHA")) {
            throw new SecurityException("Jogador não autorizado a redefinir a senha");
        }

        Jogador jogador = buscarJogador(jogadorId);
        Credencial credencial = jogador.getCredencial();
        credencial.setSenhaHash(gerarHash(novaSenha));
        credencial.setDataAtualizacao(LocalDateTime.now());

        jogadorRepository.save(jogador);
    }

    public DadosJogador consultarDados(int jogadorId) {
        if (!verificarAutorizacao(jogadorId, "CONSULTAR_DADOS")) {
            throw new SecurityException("Jogador não autorizado a consultar os dados");
        }

        return buscarJogador(jogadorId).getDados();
    }

    public void atualizarDados(int jogadorId, DadosJogador dados) {
        if (!verificarAutorizacao(jogadorId, "ATUALIZAR_DADOS")) {
            throw new SecurityException("Jogador não autorizado a atualizar os dados");
        }

        Jogador jogador = buscarJogador(jogadorId);
        jogador.setDados(dados);

        jogadorRepository.save(jogador);
    }

    private boolean verificarAutorizacao(int jogadorId, String acao) {
        return autorizacaoService.verificarAutorizacao((long) jogadorId, acao);
    }

    private Jogador buscarJogador(int jogadorId) {
        return jogadorRepository.findById(jogadorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Jogador não encontrado: " + jogadorId));
    }

    private String gerarHash(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash indisponível", e);
        }
    }
}
