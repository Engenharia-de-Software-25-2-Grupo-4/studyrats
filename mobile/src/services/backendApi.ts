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

async function authFetch(path: string, options: RequestInit = {}) {
  const token = await getValidIdToken();
  if (!token) throw new Error("USUARIO_NAO_LOGADO");

  const headers = {
    ...(options.headers ?? {}),
    Authorization: `Bearer ${token}`,
  };

  return fetch(`${API_BASE_URL}${path}`, { ...options, headers });
}