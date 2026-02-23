import { authFetch } from "./backendApi";
import { getValidIdToken } from "./getValidIdToken";

export type CreateSessaoBody = {
  titulo: string;
  descricao: string;
  horario_inicio: string;
  duracao_minutos: number;
  url_foto: string;
  disciplina: string;
  topico: string;
};

export type UpdateSessaoBody = {
  titulo?: string;
  descricao?: string;
  horario_inicio?: string;
  duracao_minutos?: number;
  url_foto?: string;
  disciplina?: string;
  topico?: string;
};

export type SessaoDetails = {
  id_sessao: string,
  id_criador: string,
  nome_criador: string,
  titulo: string,
  descricao: string,
  horario_inico: string,
  duracao_minutos: number,
  url_foto: string,
  disciplina: string,
  topico: string,
  total_comentarios: number,
  total_reacoes: number,
  reagiu: boolean
}

export type CreateReacao = {
  reagiu: boolean,
  total_reacoes: number
}

export type CreateComentario = {
  texto: string
}

export type ComentarioDetails = {
  id_comentario: string,
  firebaseUid_autor: string,
  nome_autor: string,
  texto: string,
  horario_comentario: string
}

export type Upload = {
  imagem: string
}

export async function createSessao(body: CreateSessaoBody, idGrupo: string): Promise<SessaoDetails> {
  console.log("CREATE SESSAO grupoId:", idGrupo);
  console.log("CREATE SESSAO payload:", body);

  const res = await authFetch(`/sessaoDeEstudo/${idGrupo}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  console.log("CREATE SESSAO status:", res.status);

  const text = await res.text().catch(() => "");
  console.log("CREATE SESSAO body:", text);

  if (!res.ok) {
    // seu backend provavelmente usa "mensagem"
    try {
      const data = JSON.parse(text);
      throw new Error(data?.mensagem ?? data?.message ?? "Erro ao criar sessão");
    } catch {
      throw new Error(text || "Erro ao criar sessão");
    }
  }

  // como já consumimos o body com text(), converte aqui
  return JSON.parse(text) as SessaoDetails;
}

export async function updateSessao(idSessao: string, body: UpdateSessaoBody): Promise<SessaoDetails> {
  const res = await authFetch(`/sessaoDeEstudo/${idSessao}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro desconhecido");
  }

  return res.json() as Promise<SessaoDetails>;
}

export async function deleteSessao(idSessao: string): Promise<void> {
  const res = await authFetch(`/sessaoDeEstudo/${idSessao}`, {
    method: "DELETE",
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro desconhecido");
  }
}
export async function getSessao(idSessao: string): Promise<SessaoDetails> {
  const res = await authFetch(`/sessaoDeEstudo/${idSessao}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json"
    },
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro desconhecido");
  }

  return res.json() as Promise<SessaoDetails>;
}
export async function reactSessao(idSessao: string): Promise<CreateReacao> {
  const res = await authFetch(`/sessaoDeEstudo/${idSessao}/reacoes/toggle`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro desconhecido");
  }

  return res.json() as Promise<CreateReacao>;
}
export async function comentarSessao(idSessao: string, body: CreateComentario): Promise<ComentarioDetails> {
  const res = await authFetch(`/sessaoDeEstudo/${idSessao}/comentarios`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro desconhecido");
  }

  return res.json() as Promise<ComentarioDetails>;
}

export async function uploadImagem(idSessaoDeEstudo: string, uri: string): Promise<void> {

  const formData = new FormData();
  formData.append("imagem", {
    uri,
    type: "image/jpeg",
    name: "foto.jpg",
  } as any);

  const res = await authFetch(`/imagens/upload/sessaoDeEstudo/${idSessaoDeEstudo}`, {
    method: "POST",
    body: formData,
  });

  console.log("status upload:", res.status);

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro ao fazer upload");
  }
}