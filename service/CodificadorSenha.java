public interface CodificadorSenha {

    String codificar(String senha);

    boolean confere(String senha, String senhaHash);
}
