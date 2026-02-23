// mobile/src/server/estudanteInfo/deleteStudentAccount.ts
import { authFetch } from "@/services/backendApi";

export async function deleteStudentAccount(): Promise<{
  ok: boolean;
  status: number;
  message?: string;
}> {
  try {
    const res = await authFetch(`/estudantes`, {
      method: "DELETE",
    });

    const contentType = res.headers.get("content-type") ?? "";
    const raw = await res.text().catch(() => "");

    console.log("[deleteStudentAccount] status:", res.status);
    console.log("[deleteStudentAccount] content-type:", contentType);
    console.log("[deleteStudentAccount] body:", raw);

    // swagger: 204 (sucesso), 404 (não encontrado)
    if (res.status === 204) return { ok: true, status: 204 };
    if (res.status === 404) return { ok: false, status: 404, message: "Estudante não encontrado." };
    if (res.status === 401) return { ok: false, status: 401, message: "Não autorizado." };

    if (!res.ok) {
      try {
        const data = raw ? JSON.parse(raw) : {};
        return {
          ok: false,
          status: res.status,
          message: data?.message ?? raw ?? `ERRO_AO_EXCLUIR_CONTA (${res.status})`,
        };
      } catch {
        return {
          ok: false,
          status: res.status,
          message: raw || `ERRO_AO_EXCLUIR_CONTA (${res.status})`,
        };
      }
    }

    return { ok: true, status: res.status };
  } catch (e: any) {
    console.warn("[deleteStudentAccount] erro:", e);
    return { ok: false, status: 0, message: e?.message ?? "ERRO_AO_EXCLUIR_CONTA" };
  }
}