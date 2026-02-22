// src/services/changePassword.ts
import { auth } from "@/firebaseConfig";
import {
  EmailAuthProvider,
  reauthenticateWithCredential,
  updatePassword,
} from "firebase/auth";

export async function changePassword(
  currentPassword: string,
  newPassword: string
) {
  const user = auth.currentUser;
  if (!user) throw new Error("USUARIO_NAO_LOGADO");
  if (!user.email) throw new Error("PROVEDOR_SEM_SENHA");

  const cred = EmailAuthProvider.credential(user.email, currentPassword);
  await reauthenticateWithCredential(user, cred);
  await updatePassword(user, newPassword);
}