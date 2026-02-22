import { authFetch } from "./backendApi";
import { getValidIdToken } from "./getValidIdToken";


export type CreateGrupoBody = {
  nome: string;
  descricao: string;
  foto_perfil: string;
  regras: string;
  data_inicio: string;
  data_fim: string;
};

export type UpdateGrupoBody = {
  nome?: string;
  descricao?: string;
  foto_perfil?: string;
  regras?: string;
  data_inicio?: string;
  data_fim?: string;
};

export type GrupoDetails = {
  id_grupo: string;
  nome: string;
  descricao: string;
  admin: {
    firebaseUid: string;
    nome: string;
    email: string;
  };
  foto_perfil: string;
  regras: string;
  data_inicio: string;
  data_fim: string;
};


export async function listGrupos(): Promise<GrupoDetails[]> {
  const res = await authFetch(`/grupos`, { method: "GET" });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro ao listar grupos");
  }

  return res.json() as Promise<GrupoDetails[]>;
}


export async function getGrupoById(idGrupo: string): Promise<GrupoDetails> {
  const res = await authFetch(`/grupos/${idGrupo}`, { method: "GET" });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro ao buscar grupo");
  }

  return res.json() as Promise<GrupoDetails>;
}

export async function createGrupo(body: CreateGrupoBody): Promise<GrupoDetails> {
  const res = await authFetch(`/grupos`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro desconhecido");
  }

  return res.json() as Promise<GrupoDetails>;
}

export async function updateGrupo(idGrupo: string, body: UpdateGrupoBody): Promise<GrupoDetails> {
  const res = await authFetch(`/grupos/${idGrupo}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro desconhecido");
  }

  return res.json() as Promise<GrupoDetails>;
}

export async function deleteGrupo(idGrupo: string): Promise<void> {
  const res = await authFetch(`/grupos/${idGrupo}`, { method: "DELETE" });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro desconhecido");
  }
}

export async function uploadImagem(idGrupo: string, uri: string): Promise<void> {
  await getValidIdToken(); 

  const formData = new FormData();
  formData.append("imagem", {
    uri,
    type: "image/jpeg",
    name: "foto.jpg",
  } as any);

  const res = await authFetch(`/imagens/upload/grupo/${idGrupo}`, {
    method: "POST",
    body: formData,
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro ao fazer upload");
  }
}