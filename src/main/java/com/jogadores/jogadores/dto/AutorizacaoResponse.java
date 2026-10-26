public record AutorizacaoResponse(
        int jogadorId,
        Acao acao,
        boolean permitida
) {}
