// src/services/getValidIdToken.ts
import { getSession, saveSession } from "./authStorage";
import { firebaseRefreshIdToken } from "./firebaseAuth";

export async function getValidIdToken(): Promise<string | null> {
  const session = await getSession();
  if (!session) return null;

  // 1 minuto de folga
  const needsRefresh = Date.now() > session.expiresAt - 60_000;
  if (!needsRefresh) return session.idToken;

  const refreshed = await firebaseRefreshIdToken(session.refreshToken);
  const expiresAt = Date.now() + refreshed.expiresIn * 1000;

  await saveSession({
    ...session,
    idToken: refreshed.idToken,
    refreshToken: refreshed.refreshToken,
    localId: refreshed.localId,
    expiresAt,
  });

  return refreshed.idToken;
}
