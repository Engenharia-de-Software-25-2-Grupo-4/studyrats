type StackParams = {
  Index: undefined
  Login: undefined
  Onboarding: undefined
  Registro: undefined
  RecuperarSenha: undefined
  Home: undefined
  Profile: undefined
  Disciplinas: undefined
  StudyGroupScreen: { grupoId: string }
  Feed: { grupoId: string }
  EditAcc: undefined
  CriarGrupo: undefined;
  GrupoCriado: { desafio: any };
  CriarSessao: { grupoId: string; sessao?: any }
  Publicacao: { grupoId: string; sessao: any }
  EntrarNoGrupo: { token: string }
  GroupHome: undefined
};

export { StackParams }