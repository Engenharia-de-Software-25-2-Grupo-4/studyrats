// src/services/getValidIdToken.ts

import { auth } from "@/firebaseConfig";

export async function getValidIdToken(forceRefresh = false) {
  const user = auth.currentUser;

  if (!user) {
    throw new Error("USUARIO_NAO_LOGADO");
  }

  return await user.getIdToken(forceRefresh);
}