import { initializeApp } from 'firebase/app';
import { getStorage } from "firebase/storage";

const firebaseConfig = {
  apiKey: "AIzaSyAQMQwsYBE_j6jpm8r7tvxupFB2kuhrMvs",
  authDomain: "gymratsauth.firebaseapp.com",
  projectId: "gymratsauth",
  storageBucket: "gymratsauth.firebasestorage.app",
  messagingSenderId: "909298886886",
  appId: "1:909298886886:web:86c0712dc539785d06f327"
};

export const app = initializeApp(firebaseConfig);
export const storage = getStorage(app);