import { authFetch } from "@/services/backendApi";

export type UserInfo = {
  firebaseUid: string;
  nome: string;
  email: string;
};

export async function fetchUserInfo(firebaseUid: string): Promise<UserInfo | null> {
  if (!firebaseUid) return null;

  try {
    const res = await authFetch(`/estudantes/${firebaseUid}`, {
      method: "GET",
    });

    const contentType = res.headers.get("content-type") ?? "";
    const raw = await res.text().catch(() => "");

    console.log("[fetchUserInfo] status:", res.status);
    console.log("[fetchUserInfo] content-type:", contentType);
    console.log("[fetchUserInfo] body:", raw);

    if (res.status === 401) return null;
    if (res.status === 404) return null;

    if (!res.ok) {
      // tenta extrair message se vier JSON
      try {
        const data = raw ? JSON.parse(raw) : {};
        throw new Error(data?.message ?? raw ?? `ERRO_AO_BUSCAR_USUARIO (${res.status})`);
      } catch {
        throw new Error(raw || `ERRO_AO_BUSCAR_USUARIO (${res.status})`);
      }
    }

    try {
      const data = raw ? JSON.parse(raw) : null;
      if (!data) return null;

      return {
        firebaseUid: String(data.firebaseUid ?? firebaseUid),
        nome: String(data.nome ?? ""),
        email: String(data.email ?? ""),
      };
    } catch {
      return null;
    }
  } catch (e) {
    console.warn("[fetchUserInfo] erro:", e);
    return null;
  }
}