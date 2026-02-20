// src/services/firebaseAuth.ts
const FIREBASE_API_KEY = "AIzaSyAQMQwsYBE_j6jpm8r7tvxupFB2kuhrMvs";

type FirebaseAuthResponse = {
  idToken: string;
  email: string;
  refreshToken: string;
  expiresIn: string; // segundos (string)
  localId: string;   // UID do Firebase
};

async function firebasePost<T>(url: string, body: any): Promise<T> {
  const res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  const data = await res.json();

  if (!res.ok) {
    const msg = data?.error?.message ?? "FIREBASE_AUTH_ERROR";
    throw new Error(msg);
  }

  return data as T;
}

export async function firebaseSignUp(email: string, password: string) {
  const url = `https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=${FIREBASE_API_KEY}`;
  return firebasePost<FirebaseAuthResponse>(url, {
    email,
    password,
    returnSecureToken: true,
  });
}

export async function firebaseSignIn(email: string, password: string) {
  const url = `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${FIREBASE_API_KEY}`;
  return firebasePost<FirebaseAuthResponse>(url, {
    email,
    password,
    returnSecureToken: true,
  });
}

/**
 * Renova idToken usando refreshToken (quando expirar).
 */
export async function firebaseRefreshIdToken(refreshToken: string) {
  const url = `https://securetoken.googleapis.com/v1/token?key=${FIREBASE_API_KEY}`;

  const form = new URLSearchParams();
  form.append("grant_type", "refresh_token");
  form.append("refresh_token", refreshToken);

  const res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: form.toString(),
  });

  const data = await res.json();

  if (!res.ok) {
    throw new Error(data?.error?.message ?? "FIREBASE_REFRESH_ERROR");
  }

  return {
    idToken: data.id_token as string,
    refreshToken: data.refresh_token as string,
    expiresIn: Number(data.expires_in), // segundos
    localId: data.user_id as string,
  };
}

export async function firebaseDeleteAccount(idToken: string) {
  const url = `https://identitytoolkit.googleapis.com/v1/accounts:delete?key=${FIREBASE_API_KEY}`;
  return firebasePost<{ kind: string }>(url, { idToken });
}
