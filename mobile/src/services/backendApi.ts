// src/services/backendApi.ts
const API_BASE_URL = "http://191.253.18.8:6431";

import { getValidIdToken } from "./getValidIdToken";

export type CreateEstudanteBody = {
  nome: string;
  email: string;
};

export async function createEstudante(body: CreateEstudanteBody, idToken: string) {
  const res = await fetch(`${API_BASE_URL}/estudantes`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${idToken}`,
    },
    body: JSON.stringify(body),
  });

  const data = await res.json().catch(() => ({}));

  if (!res.ok) {
    const msg = data?.message ?? "ERRO_AO_CRIAR_ESTUDANTE";
    throw new Error(msg);
  }

  return data;
}

export async function authFetch(path: string, options: RequestInit = {}) {
  const token = await getValidIdToken();

  const url = `${API_BASE_URL}${path.startsWith("/") ? path : `/${path}`}`;

  console.log("AUTHFETCH:", options.method ?? "GET", url);
  console.log("AUTHFETCH token exists?", !!token, token ? token.slice(0, 16) : null);

  if (!token) throw new Error("USUARIO_NAO_LOGADO");

  const headers = new Headers(options.headers);
  headers.set("Authorization", `Bearer ${token}`);

  console.log("AUTHFETCH Authorization header set");

  const res = await fetch(url, { ...options, headers });

  // log só se for erro (pra não poluir)
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    console.log("AUTHFETCH ERROR status:", res.status, "body:", text);
    // re-cria response? não precisa, só pra debug
    // mas cuidado: você consumiu o body aqui. então só faça isso enquanto estiver debugando.
  }

  return res;
}

export async function getEstudanteByFirebaseUid(firebaseUid: string) {
  const res = await authFetch(`/estudantes/${firebaseUid}`, { method: "GET" });

  if (res.status === 404) return null;

  const data = await res.json().catch(() => ({}));

  if (!res.ok) {
    throw new Error(data?.message ?? `ERRO_AO_BUSCAR_ESTUDANTE (${res.status})`);
  }

  return data;
}