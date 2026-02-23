// src/services/authStorage.ts
import * as SecureStore from "expo-secure-store";

const SESSION_KEY = "auth_session_v1";

export type Session = {
  idToken: string;
  refreshToken: string;
  expiresAt: number; // timestamp (ms)
  localId: string;
  email: string;
};

export async function saveSession(session: Session) {
  await SecureStore.setItemAsync(SESSION_KEY, JSON.stringify(session));
}

export async function getSession(): Promise<Session | null> {
  const raw = await SecureStore.getItemAsync(SESSION_KEY);
  return raw ? (JSON.parse(raw) as Session) : null;
}

export async function clearSession() {
  await SecureStore.deleteItemAsync(SESSION_KEY);
}

export async function getAuthenticatedUid(): Promise<string | null> {
  const session = await getSession();
  return session?.localId ?? null;
}