
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