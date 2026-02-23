import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  Image,
  Keyboard,
  Animated,
  StyleSheet,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useEffect, useRef, useState } from "react";
import { Ionicons } from "@expo/vector-icons";

import type { StackScreenProps } from "@react-navigation/stack";
import type { StackParams } from "@/utils/routesStack";

import { signInWithEmailAndPassword } from "firebase/auth";
import { auth } from "@/firebaseConfig";

import { getEstudanteByFirebaseUid } from "../../services/backendApi";
import { saveSession } from "../../services/authStorage"; // opcional

type Props = StackScreenProps<StackParams, "Login">;

function mapAuthError(e: any) {
  const code = String(e?.code ?? "");

  if (code === "auth/invalid-email") return "Email inválido.";
  if (code === "auth/user-not-found") return "Email ou senha incorretos.";
  if (code === "auth/wrong-password") return "Email ou senha incorretos.";
  if (code === "auth/invalid-credential") return "Email ou senha incorretos.";
  if (code === "auth/too-many-requests")
    return "Muitas tentativas. Tente novamente mais tarde.";

  return e?.message ?? "ERRO_AO_LOGAR";
}

export default function Login({ navigation }: Props) {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [mostrarSenha, setMostrarSenha] = useState(false);

  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const translateY = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    const showSub = Keyboard.addListener("keyboardDidShow", (event) => {
      Animated.timing(translateY, {
        toValue: -event.endCoordinates.height + 80,
        duration: 250,
        useNativeDriver: true,
      }).start();
    });

    const hideSub = Keyboard.addListener("keyboardDidHide", () => {
      Animated.timing(translateY, {
        toValue: 0,
        duration: 250,
        useNativeDriver: true,
      }).start();
    });

    return () => {
      showSub.remove();
      hideSub.remove();
    };
  }, []);

  async function handleEntrar() {
    const emailTrim = email.trim().toLowerCase();

    if (!emailTrim || !senha) {
      setErro("Preencha email e senha.");
      return;
    }

    setLoading(true);
    setErro(null);

    try {
      // ✅ LOGIN PELO SDK (isso cria auth.currentUser e persiste sessão)
      const cred = await signInWithEmailAndPassword(auth, emailTrim, senha);
      const user = cred.user;

      // ✅ token atual (e renovável) do Firebase SDK
      const idToken = await user.getIdToken();
      
      // (opcional) se você ainda usa authStorage em outras partes, pode salvar um cache:
      await saveSession({
        idToken,
        refreshToken: "", // você não precisa controlar refreshToken manualmente com o SDK
        expiresAt: Date.now() + 55 * 60 * 1000, // opcional (não use como verdade)
        localId: user.uid,
        email: user.email ?? emailTrim,
      });

      // ✅ Agora busque seu estudante usando o UID real do Firebase
      const estudante = await getEstudanteByFirebaseUid(user.uid);

      if (!estudante) {
        throw new Error("ESTUDANTE_NAO_CADASTRADO");
      }

      Keyboard.dismiss();
      navigation.replace("Home");
    } catch (e: any) {
      if (String(e?.message) === "ESTUDANTE_NAO_CADASTRADO") {
        setErro("Sua conta foi autenticada, mas não encontramos seu cadastro no sistema.");
      } else {
        setErro(mapAuthError(e));
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Image source={require("../../assets/logo.png")} style={styles.logo} />
        <Text style={styles.logoText}>StudyRats</Text>
      </View>

      <Animated.View style={[styles.card, { transform: [{ translateY }] }]}>
        <Text style={styles.title}>Fazer login</Text>

        <TextInput
          placeholder="Email"
          placeholderTextColor="#01415B"
          style={styles.input}
          value={email}
          onChangeText={setEmail}
          autoCapitalize="none"
          keyboardType="email-address"
        />

        <View style={styles.passwordContainer}>
          <TextInput
            placeholder="Senha"
            placeholderTextColor="#01415B"
            style={styles.passwordInput}
            secureTextEntry={!mostrarSenha}
            value={senha}
            onChangeText={setSenha}
          />

          <TouchableOpacity
            onPress={() => setMostrarSenha(!mostrarSenha)}
            activeOpacity={0.6}
          >
            <Ionicons
              name={mostrarSenha ? "eye-off" : "eye"}
              size={22}
              color="#01415B"
            />
          </TouchableOpacity>
        </View>

        {erro ? <Text style={{ color: "crimson", marginBottom: 8 }}>{erro}</Text> : null}

        <TouchableOpacity
          style={[styles.buttonEntrar, loading ? { opacity: 0.7 } : null]}
          onPress={handleEntrar}
          activeOpacity={0.8}
          disabled={loading}
        >
          <Text style={styles.buttonEntrarText}>
            {loading ? "ENTRANDO..." : "ENTRAR"}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.buttonCriarConta}
          activeOpacity={0.8}
          onPress={() => navigation.navigate("Registro")}
        >
          <Text style={styles.buttonCriarContaText}>CRIAR CONTA</Text>
        </TouchableOpacity>

        <TouchableOpacity
          activeOpacity={0.6}
          onPress={() => navigation.navigate("RecuperarSenha")}
        >
          <Text style={styles.forgotPassword}>Esqueci minha senha</Text>
        </TouchableOpacity>
      </Animated.View>
    </SafeAreaView>
  );
}

export const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#01415B",
    justifyContent: "space-between",
  },
  header: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  logo: {
    width: 140,
    height: 140,
    resizeMode: "contain",
  },
  logoText: {
    marginTop: 8,
    fontSize: 30,
    fontWeight: "700",
    color: "#FFFFFF",
  },
  card: {
    backgroundColor: "#EAF9FF",
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    padding: 24,
    minHeight: 360,
  },
  title: {
    fontSize: 18,
    fontWeight: "700",
    marginBottom: 16,
    color: "#01415B",
  },
  input: {
    backgroundColor: "#FFFFFF",
    borderRadius: 8,
    padding: 14,
    marginBottom: 12,
    color: "#01415B",
  },
  passwordContainer: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#FFFFFF",
    borderRadius: 8,
    paddingHorizontal: 14,
    marginBottom: 12,
  },
  passwordInput: {
    flex: 1,
    paddingVertical: 14,
    paddingLeft: 0,
    color: "#01415B",
  },
  buttonEntrar: {
    backgroundColor: "#01415B",
    padding: 14,
    borderRadius: 8,
    alignItems: "center",
    marginTop: 8,
  },
  buttonEntrarText: {
    color: "#FFFFFF",
    fontWeight: "700",
    fontSize: 14,
  },
  buttonCriarConta: {
    backgroundColor: "#FFFFFF",
    padding: 14,
    borderRadius: 8,
    alignItems: "center",
    marginTop: 8,
  },
  buttonCriarContaText: {
    color: "#01415B",
    fontWeight: "700",
    fontSize: 14,
  },
  forgotPassword: {
    marginTop: 16,
    textAlign: "center",
    color: "#01415B",
    fontWeight: "600",
    textDecorationLine: "underline",
  },
});