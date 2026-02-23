import { authFetch } from "@/services/backendApi";

type ChangeUsernamePayload = {
  nome: string;
  email: string;
};

export type ChangeUsernameResponse = {
  firebaseUid: string;
  nome: string;
  email: string;
};

export async function changeUsername(
  nome: string,
  email: string
): Promise<ChangeUsernameResponse> {
  if (!nome?.trim()) throw new Error("NOME_OBRIGATORIO");
  if (!email?.trim()) throw new Error("EMAIL_OBRIGATORIO");

  const payload: ChangeUsernamePayload = {
    nome: nome.trim(),
    email: email.trim(),
  };

  const res = await authFetch("/estudantes", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  const contentType = res.headers.get("content-type") ?? "";
  const raw = await res.text().catch(() => "");

  console.log("[changeUsername] status:", res.status);
  console.log("[changeUsername] content-type:", contentType);
  console.log("[changeUsername] body:", raw);

  if (res.status === 401) throw new Error("USUARIO_NAO_LOGADO");
  if (res.status === 404) throw new Error("ESTUDANTE_NAO_ENCONTRADO");

  if (!res.ok) {
    try {
      const data = raw ? JSON.parse(raw) : {};
      throw new Error(data?.message ?? raw ?? `ERRO_AO_ATUALIZAR_USUARIO (${res.status})`);
    } catch {
      throw new Error(raw || `ERRO_AO_ATUALIZAR_USUARIO (${res.status})`);
    }
  }

  if (contentType.includes("application/json")) {
    try {
      const data = raw ? JSON.parse(raw) : {};
      return {
        firebaseUid: String(data.firebaseUid ?? ""),
        nome: String(data.nome ?? payload.nome),
        email: String(data.email ?? payload.email),
      };
    } catch {
      return { firebaseUid: "", nome: payload.nome, email: payload.email };
    }
  }

  return { firebaseUid: "", nome: payload.nome, email: payload.email };
}