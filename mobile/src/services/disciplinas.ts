
import { authFetch } from "@/services/backendApi"

export type DisciplinaDTO = {
  id_disciplina: string
  nome: string
}

export async function listMinhasDisciplinas(): Promise<DisciplinaDTO[]> {
  const res = await authFetch("/disciplinas/minhas", { method: "GET" })

  if (res.status === 204) return []

  const data = await res.json().catch(() => ([]))

  if (!res.ok) {
    const msg = data?.message ?? `ERRO_AO_BUSCAR_DISCIPLINAS (${res.status})`
    throw new Error(msg)
  }

  return Array.isArray(data) ? (data as DisciplinaDTO[]) : []
}

export async function listarDisciplinasDoGrupo(idGrupo: string): Promise<DisciplinaDTO[]> {
  const res = await authFetch(`/disciplinas/grupo/${idGrupo}`, { method: "GET" });

  const text = await res.text().catch(() => "");
  if (!res.ok) {
    try {
      const data = JSON.parse(text);
      throw new Error(data?.mensagem ?? data?.message ?? "Erro ao buscar disciplinas");
    } catch {
      throw new Error(text || "Erro ao buscar disciplinas");
    }
  }

  return (text ? JSON.parse(text) : []) as DisciplinaDTO[];
}
