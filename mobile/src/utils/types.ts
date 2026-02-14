export type RootStackParamList = {
  CriarGrupo: undefined;
  GrupoCriado: {
    nomeDesafio: string;
    descricao: string;
    regras: string;
    dataHora: Date;
    imagem: string | null
  };
};
