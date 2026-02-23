// services/disciplinas.ts (ou onde você guarda services)
import { authFetch } from "./backendApi"; // ajuste o caminho

export type DisciplinaDTO = {
  id_disciplina: string;
  nome: string;
};


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

  // quando dá certo, é json array
  return (text ? JSON.parse(text) : []) as DisciplinaDTO[];
}
