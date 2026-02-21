import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  Image,
  Keyboard,
  Animated,
  StyleSheet
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useEffect, useRef, useState } from "react";
import { Ionicons } from "@expo/vector-icons";

import { firebaseSignUp, firebaseDeleteAccount } from "../../services/firebaseAuth";
import { createEstudante } from "../../services/backendApi";
import { saveSession } from "../../services/authStorage";

import type { StackScreenProps } from "@react-navigation/stack";
import type { StackParams } from "@/utils/routesStack"; 

type Props = StackScreenProps<StackParams, "Registro">;

function mapFirebaseError(msg: string) {
  if (msg.includes("EMAIL_EXISTS")) return "Esse email já está cadastrado.";
  if (msg.includes("WEAK_PASSWORD")) return "Senha fraca (mín. 6 caracteres).";
  if (msg.includes("INVALID_EMAIL")) return "Email inválido.";
  if (msg.includes("INVALID_LOGIN_CREDENTIALS")) return "Email ou senha inválidos.";
  return msg;
}

export default function Registro({ navigation }: any) {
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");

  const [mostrarSenha, setMostrarSenha] = useState(false);
  const [mostrarConfirmacao, setMostrarConfirmacao] = useState(false);

  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [loading, setLoading] = useState(false);

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

  const handleCadastro = async () => {
    setErro("");
    setSucesso("");

    const emailNorm = email.trim().toLowerCase();

    if (!nome || !emailNorm || !senha || !confirmarSenha) {
      setErro("Preencha todos os campos.");
      return;
    }
    if (senha !== confirmarSenha) {
      setErro("As senhas não coincidem.");
      return;
    }

    setLoading(true);

    try {
      const auth = await firebaseSignUp(emailNorm, senha);
      const expiresAt = Date.now() + Number(auth.expiresIn) * 1000;

      try {
        await createEstudante({ nome, email: auth.email }, auth.idToken);

        await saveSession({
          idToken: auth.idToken,
          refreshToken: auth.refreshToken,
          expiresAt,
          localId: auth.localId,
          email: auth.email,
        });

        setSucesso("Cadastro realizado com sucesso!");
      } catch (err) {
        await firebaseDeleteAccount(auth.idToken).catch(() => {});
        throw err;
      }
    } catch (e: any) {
      setErro(mapFirebaseError(String(e?.message ?? "Erro inesperado")));
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      {/* TOPO */}
      <View style={styles.header}>
        <Image source={require("../../assets/logo.png")} style={styles.logo} />
        <Text style={styles.logoText}>StudyRats</Text>
      </View>

      {/* CARD */}
      <Animated.View
        style={[
          styles.card,
          {
            transform: [{ translateY }],
          },
        ]}
      >
        <Text style={styles.title}>Criar sua conta</Text>

        <TextInput
          placeholder="Nome completo"
          placeholderTextColor="#01415B"
          style={styles.input}
          value={nome}
          onChangeText={setNome}
        />

        <TextInput
          placeholder="Email"
          placeholderTextColor="#01415B"
          style={styles.input}
          value={email}
          onChangeText={setEmail}
          autoCapitalize="none"
          keyboardType="email-address"
        />

        {/* SENHA */}
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

        {/* CONFIRMAR SENHA */}
        <View style={styles.passwordContainer}>
          <TextInput
            placeholder="Confirmar senha"
            placeholderTextColor="#01415B"
            style={styles.passwordInput}
            secureTextEntry={!mostrarConfirmacao}
            value={confirmarSenha}
            onChangeText={setConfirmarSenha}
          />
          <TouchableOpacity
            onPress={() => setMostrarConfirmacao(!mostrarConfirmacao)}
            activeOpacity={0.6}
          >
            <Ionicons
              name={mostrarConfirmacao ? "eye-off" : "eye"}
              size={22}
              color="#01415B"
            />
          </TouchableOpacity>
        </View>

        {/* MENSAGENS */}
        {erro ? <Text style={styles.error}>{erro}</Text> : null}
        {sucesso ? <Text style={styles.success}>{sucesso}</Text> : null}

        <TouchableOpacity
          style={[styles.button, loading && { opacity: 0.7 }]}
          onPress={handleCadastro}
          disabled={loading}
        >
          <Text style={styles.buttonText}>
            {loading ? "CRIANDO..." : "CRIAR CONTA"}
          </Text>
        </TouchableOpacity>

      <TouchableOpacity activeOpacity={0.6} onPress={() => navigation.navigate("Login")}>
        <Text style={styles.linkLogin}>ENTRAR COM EMAIL</Text>
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
    minHeight: 420,
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
    color: "#01415B",
  },

  button: {
    backgroundColor: "#01415B",
    padding: 14,
    borderRadius: 8,
    alignItems: "center",
    marginTop: 8,
  },

  buttonText: {
    color: "#EAF9FF",
    fontWeight: "700",
    fontSize: 14,
  },

  error: {
    color: "#C62828",
    fontWeight: "600",
    marginBottom: 8,
  },

  success: {
    color: "#2E7D32",
    fontWeight: "600",
    marginBottom: 8,
  },

  linkLogin: {
    marginTop: 16,
    textAlign: "center",
    color: "#01415B",
    fontWeight: "700",
  },
});

