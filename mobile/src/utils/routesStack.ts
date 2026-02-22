type StackParams = {
  Index: undefined;
  Login: undefined;
  Registro: undefined;
  RecuperarSenha: undefined;
  Home: undefined;
  Profile: undefined;
  Disciplinas: undefined;
  StudyGroupScreen: { grupoId: string };
  Feed: undefined;
  CriarGrupo: undefined; 
  GrupoCriado: { desafio: any }; // colocar tipo dps
  CriarSessao: undefined;
  Publicacao: { sessao: any }
};

export { StackParams }