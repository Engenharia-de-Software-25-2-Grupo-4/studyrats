type StackParams = {
  Index: undefined;
  Login: undefined;
  Registro: undefined;
  RecuperarSenha: undefined;
  Home: undefined;
  Profile: undefined;
  Disciplinas: undefined;
  StudyGroupScreen: { grupoId: string };
  Feed: { grupoId: string };
  CriarGrupo: undefined; 
  GrupoCriado: { desafio: any }; // colocar tipo dps
  CriarSessao: { grupoId: string };
  Publicacao: { sessao: any };
  GroupHome: undefined
};

export { StackParams }