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
  CriarSessao: { grupoId: string; sessao?: any };
  Publicacao: { grupoId: string; sessao: any };
  EntrarNoGrupo: { token: string }
};

export { StackParams }