package com.example.studyrats.util;

public class Mensagens {

    // Mensagens de sucesso

    // Mensagens de falha

    // Mensagens de exceções customizadas
    public static final String FIREBASE_JSON_NAO_ENCONTRADO = "Não foi possível autenticar o firebase (JSON)";
    public static final String MENSAGEM_EXCEPTION_EXEMPLO = "Exemplo de mensagem para exceção";
    public static final String FIREBASE_IO = "Falha do IO do firebase";

    // Mensagens de exceções de Estudante
    public static final String ESTUDANTE_NAO_AUTENTICADO = "Não foi possível identificar um usuário autenticado para esta operação.";
    public static final String ESTUDANTE_NAO_ENCONTRADO = "O estudante solicitado não foi encontrado no sistema.";
    public static final String UID_JA_CADASTRADO = "O id de usuário já está vinculado a uma conta ativa";
    public static final String EMAIL_JA_CADASTRADO = "Este e-mail já está vinculado a uma conta ativa.";

    // Mensagens dos tests
    public static final String NAO_RETORNOU_CONFLICT = "A rota não retornou 409 - Conflict - ";
    public static final String NAO_RETORNOU_UNAUTHORIZED = "A rota não retornou 401 - Unauthorized - ";
    public static final String NAO_RETORNOU_NOT_FOUND = "A rota não retornou 404 - Not found - ";
    public static final String NAO_RETORNOU_NO_CONTENT = "A rota não retornou 204 - Not content - ";

}