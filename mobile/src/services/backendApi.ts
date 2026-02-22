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
  if (!token) throw new Error("USUARIO_NAO_LOGADO");

  const url = `${API_BASE_URL}${path.startsWith("/") ? path : `/${path}`}`;

  const isFormData =
    typeof FormData !== "undefined" && options.body instanceof FormData;

  // ✅ normaliza headers em objeto simples
  const base: Record<string, string> = {};

  // pega headers existentes
  const inputHeaders = options.headers ?? {};
  const h = new Headers(inputHeaders as any);
  h.forEach((v, k) => (base[k] = v));

  // injeta auth
  base["authorization"] = `Bearer ${token}`;

  // ✅ se não for FormData, garantimos content-type (principalmente em GET)
  if (!isFormData) {
    // força uma escolha válida (pra não ficar null)
    base["content-type"] = base["content-type"] || "application/octet-stream";
    // também garante accept
    base["accept"] = base["accept"] || "*/*";
  } else {
    // multipart: não setar content-type
    delete base["content-type"];
  }

  console.log("[authFetch] URL:", url);
  console.log("[authFetch] method:", options.method ?? "GET");
  console.log("[authFetch] isFormData:", isFormData);
  console.log("[authFetch] final headers:", base);

  return fetch(url, {
    ...options,
    headers: base,
  });
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