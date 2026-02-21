package com.example.studyrats.exceptions;

import com.example.studyrats.util.Mensagens;

public class ImagemVazia extends RuntimeException {
    public ImagemVazia() {super(Mensagens.IMAGEM_VAZIA);}
}
