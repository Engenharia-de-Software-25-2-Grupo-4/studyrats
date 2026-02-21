import { ref, uploadBytes, getDownloadURL } from "firebase/storage";
import { storage } from "@/firebaseConfig";

export async function uploadImagem(uri: string, fileName: string) {
  const response = await fetch(uri);
  const blob = await response.blob();

  const storageRef = ref(storage, `images/${fileName}`);

  await uploadBytes(storageRef, blob);

  return await getDownloadURL(storageRef);
}