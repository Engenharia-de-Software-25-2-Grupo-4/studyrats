import { authFetch } from "../../services/backendApi";
import { Buffer } from "buffer";
import { ImageSourcePropType } from "react-native";

const DEFAULT_IMG = require("../../assets/default_profile.jpg");

export async function fetchProfilePhoto(firebaseUid: string): Promise<ImageSourcePropType> {
  if (!firebaseUid) return DEFAULT_IMG;

  try {
    const url = `/imagens/estudante/${firebaseUid}?t=${Date.now()}`;

    const res = await authFetch(url, {
      method: "GET",
    });

    console.log("[fetchProfilePhoto] status:", res.status);
    console.log("[fetchProfilePhoto] content-type:", res.headers.get("content-type"));

    if (res.status === 401) {
      const raw = await res.text().catch(() => "");
      console.log("[fetchProfilePhoto] 401 body:", raw);
      return DEFAULT_IMG;
    }

    if (res.status === 404) return DEFAULT_IMG;

    if (!res.ok) {
      const raw = await res.text().catch(() => "");
      console.log("[fetchProfilePhoto] !ok body:", raw);
      return DEFAULT_IMG;
    }

    const contentType = res.headers.get("content-type") ?? "image/jpeg";

    const arrayBuffer = await res.arrayBuffer();
    console.log("[fetchProfilePhoto] bytes:", arrayBuffer.byteLength);

    if (!arrayBuffer.byteLength) return DEFAULT_IMG;

    const base64 = Buffer.from(arrayBuffer).toString("base64");

    return { uri: `data:${contentType};base64,${base64}` };
  } catch (e) {
    console.warn("[fetchProfilePhoto] erro:", e);
    return DEFAULT_IMG;
  }
}