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
  quantidadeMembros?: number;
};

export type GrupoMembro = {
  nomeEstudante: string;
  firebaseUid: string;
  role: string;
  quantidadeCheckins: number;
};

export type RankingItem = {
  nomeEstudante: string
  firebaseUid: string
  role: string
  quantidadeCheckins: number
}

export type MembroGrupo = {
  firebaseUid: string
  nomeEstudante: string
  quantidadeCheckins: number
  role: string
}
  
export type ConviteValidacao = {
  idGrupo: string;
  nomeGrupo: string;
  descricaoGrupo: string;
  jaMembro: boolean;
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
  const token = await getValidIdToken();

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

async function getSessoes(idGrupo: string) {
  const res = await authFetch(`/grupos/${idGrupo}/sessoes`, {
    method: "GET",
  })

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro ao buscar sessões")
  }

  return res.json() 
}

async function getRanking(idGrupo: string) {
  const res = await authFetch(`/grupos/${idGrupo}/ranking`, {
    method: "GET",
  })

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro ao buscar ranking")
  }

  return res.json() 
}

async function getMembros(idGrupo: string) {
  const res = await authFetch(`/grupos/${idGrupo}/membros`, {
    method: "GET",
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro ao buscar membros")
  }

  return res.json() 
}


export async function gerarLinkConvite(idGrupo: string): Promise<string> {
  const res = await authFetch(`/grupos/${idGrupo}/convites/gerar`, {
    method: "POST",
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro ao gerar convite");
  }

  return await res.text();
}

export async function listMembrosDoGrupo(idGrupo: string): Promise<GrupoMembro[]> {
  const res = await authFetch(`/grupos/${idGrupo}/membros`, { method: "GET" });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data?.message ?? "Erro ao listar membros do grupo");
  }

  return res.json() as Promise<GrupoMembro[]>;
}

const membrosCountCache = new Map<string, number>();

export async function getQuantidadeMembrosCached(idGrupo: string, forceRefresh: boolean = false ): Promise<number> {

  if (!forceRefresh) {
    const cached = membrosCountCache.get(idGrupo);
    if (cached !== undefined) return cached;
  }

  const membros = await listMembrosDoGrupo(idGrupo);
  const qtd = membros.length;

  membrosCountCache.set(idGrupo, qtd);
  return qtd;
}

export async function promisePool<T, R>(
  items: T[],
  worker: (item: T) => Promise<R>,
  concurrency = 5
): Promise<R[]> {
  const results: R[] = new Array(items.length);
  let index = 0;

  const runners = new Array(concurrency).fill(null).map(async () => {
    while (index < items.length) {
      const currentIndex = index++;
      results[currentIndex] = await worker(items[currentIndex]);
    }
  });

  await Promise.all(runners);
  return results;
}

export const grupoServer = {
  getRanking,
  getMembros, 
  getSessoes
}


export async function entrarNoGrupo(token: string): Promise<void> {
  console.log("POST /grupos/convites/:token/entrar ->", token);

  const res = await authFetch(`/grupos/convites/${token}/entrar`, { method: "POST" });

  console.log("Status:", res.status);

  const text = await res.text().catch(() => "");
  console.log("Body:", text);

  if (!res.ok) {
    let msg = "Erro ao entrar no grupo";
    try {
      const data = JSON.parse(text);
      msg = data?.message ?? msg;
    } catch {
      if (text) msg = text;
    }
    throw new Error(`${msg} (status ${res.status})`);
  }
}
export async function validarConvite(token: string): Promise<ConviteValidacao> {
  const res = await authFetch(`/grupos/convites/${token}`, {
    method: "GET",
  });

  const text = await res.text().catch(() => "");
  if (!res.ok) {
    // tenta extrair mensagem do seu padrão {"mensagem": "..."}
    try {
      const data = JSON.parse(text);
      throw new Error(data?.mensagem ?? data?.message ?? "Erro ao validar convite");
    } catch {
      throw new Error(text || "Erro ao validar convite");
    }
  }

  return JSON.parse(text) as ConviteValidacao;
}

