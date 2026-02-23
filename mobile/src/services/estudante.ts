import { getSession } from "./authStorage";
import { getEstudanteByFirebaseUid } from "./backendApi";

export type Estudante = {
  nome: string;
  email?: string;
  firebaseUid: string;
};

export async function getEstudanteAtual(): Promise<Estudante | null> {
  const session = await getSession();
  if (!session?.localId) return null;

  const estudante = await getEstudanteByFirebaseUid(session.localId);
  return estudante;
}