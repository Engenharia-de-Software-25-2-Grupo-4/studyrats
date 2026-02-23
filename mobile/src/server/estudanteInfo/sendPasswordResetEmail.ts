// src/server/estudanteInfo/sendPasswordResetEmail.ts
import { sendPasswordResetEmail } from "firebase/auth";
import { auth } from "../../firebaseConfig"; // ajuste o caminho conforme seu projeto

export async function resetarSenha(email: string) {
  return sendPasswordResetEmail(auth, email);
}