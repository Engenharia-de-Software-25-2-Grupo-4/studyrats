import { authFetch } from "@/services/backendApi";

type UploadProfilePhotoResponse = any;

export async function uploadProfilePhoto(
  fileUri: string
): Promise<UploadProfilePhotoResponse> {
  if (!fileUri) throw new Error("FILE_URI_OBRIGATORIA");

  const filename = fileUri.split("/").pop() ?? "profile.jpg";
  const ext = filename.split(".").pop()?.toLowerCase();

  const mime =
    ext === "png"
      ? "image/png"
      : ext === "jpg" || ext === "jpeg"
      ? "image/jpeg"
      : "application/octet-stream";

  const form = new FormData();

  // ⚠️ O nome "imagem" precisa bater com @RequestPart/@RequestParam no backend
  form.append("imagem", {
    uri: fileUri,
    name: filename,
    type: mime,
  } as any);

  const res = await authFetch("/imagens/upload/estudante", {
    method: "POST",
    body: form,
    // ✅ NÃO setar Content-Type manualmente (deixa o fetch montar boundary)
  });

  const contentType = res.headers.get("content-type") ?? "";
  const raw = await res.text().catch(() => "");

  console.log("[uploadProfilePhoto/multipart] status:", res.status);
  console.log("[uploadProfilePhoto/multipart] content-type:", contentType);
  console.log("[uploadProfilePhoto/multipart] body:", raw);

  if (!res.ok) {
    // se vier JSON de erro, tenta extrair message
    try {
      const data = raw ? JSON.parse(raw) : {};
      throw new Error(data?.message ?? raw ?? `ERRO_AO_ENVIAR_FOTO (${res.status})`);
    } catch {
      throw new Error(raw || `ERRO_AO_ENVIAR_FOTO (${res.status})`);
    }
  }

  // backend pode responder text/plain (ex: nome do arquivo)
  if (contentType.includes("application/json")) {
    try {
      return JSON.parse(raw || "{}");
    } catch {
      return { ok: true, raw };
    }
  }

  return { ok: true, raw };
}