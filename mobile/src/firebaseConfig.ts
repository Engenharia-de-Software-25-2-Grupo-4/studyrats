// src/firebaseConfig.ts
import { initializeApp, getApps, getApp } from "firebase/app";
import { initializeAuth, getAuth } from "firebase/auth";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { getStorage } from "firebase/storage";

const firebaseConfig = {
  apiKey: "AIzaSyAQMQwsYBE_j6jpm8r7tvxupFB2kuhrMvs",
  authDomain: "gymratsauth.firebaseapp.com",
  projectId: "gymratsauth",
  storageBucket: "gymratsauth.firebasestorage.app",
  messagingSenderId: "909298886886",
  appId: "1:909298886886:web:86c0712dc539785d06f327",
};

export const app = getApps().length ? getApp() : initializeApp(firebaseConfig);
export const storage = getStorage(app);

const { getReactNativePersistence } = require("firebase/auth");

let _auth;
try {
  _auth = initializeAuth(app, {
    persistence: getReactNativePersistence(AsyncStorage),
  });
} catch (e) {
  _auth = getAuth(app);
}

export const auth = _auth;