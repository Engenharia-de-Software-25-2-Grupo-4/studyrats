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

import { registerWithEmail } from "@/services/auth";
import { auth } from "@/firebaseConfig";

import { createEstudante } from "../../services/backendApi";
import { saveSession } from "../../services/authStorage";

import type { StackScreenProps } from "@react-navigation/stack";
import type { StackParams } from "@/utils/routesStack"; 

import { Modal, ScrollView } from "react-native";
import { privacyPolicyText } from "@/assets/privacy_policy";

type Props = StackScreenProps<StackParams, "Registro">;

function mapFirebaseError(e: any) {
  const code = String(e?.code ?? "");

  if (code === "auth/email-already-in-use") return "Esse email já está cadastrado.";
  if (code === "auth/weak-password") return "Senha fraca (mín. 6 caracteres).";
  if (code === "auth/invalid-email") return "Email inválido.";
  return e?.message ?? "Erro inesperado";
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

  const [acceptedTerms, setAcceptedTerms] = useState(false);
  const [termsModalVisible, setTermsModalVisible] = useState(false);

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

  useEffect(() => {
    const unsubscribe = navigation.addListener("blur", () => {
      setAcceptedTerms(false);
      setTermsModalVisible(false);
    });
    return unsubscribe;
  }, [navigation]);

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

    if (!acceptedTerms) {
      setErro("Você precisa ler e aceitar a Política de Privacidade e os Termos de Uso.");
      return;
    }

    setLoading(true);

    try {
      const user = await registerWithEmail(emailNorm, senha);

      // token do Firebase SDK (sempre válido e renovável)
      const idToken = await user.getIdToken(true);

      await createEstudante({ nome, email: user.email! }, idToken);

      await saveSession({
        idToken,
        refreshToken: "", 
        expiresAt: Date.now() + 55 * 60 * 1000, 
        localId: user.uid,
        email: user.email!,
      });

      setSucesso("Cadastro realizado com sucesso!");

      navigation.reset({
        index: 0,
        routes: [{ name: "Home" }],
      });

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

        {/* TERMOS */}
        <View style={styles.termsRow}>
          <TouchableOpacity
            style={styles.checkbox}
            activeOpacity={0.7}
            onPress={() => setAcceptedTerms((v) => !v)}
          >
            {acceptedTerms ? (
              <Ionicons name="checkmark" size={16} color="#01415B" />
            ) : null}
          </TouchableOpacity>

          <Text style={styles.termsText}>
            Eu li e concordo com a{" "}
            <Text
              style={styles.termsLink}
              onPress={() => setTermsModalVisible(true)}
            >
              Política de Privacidade e os Termos de Uso
            </Text>
            .
          </Text>
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

      {/* MODAL TERMOS */}
      <Modal visible={termsModalVisible} transparent animationType="fade">
        <View style={styles.modalOverlay}>
          <View style={styles.modalCard}>
            <Text style={styles.modalTitle}>Política de Privacidade e Termos de Uso</Text>

            <ScrollView style={styles.modalScroll} showsVerticalScrollIndicator>
              <Text style={styles.modalBody}>{privacyPolicyText}</Text>
            </ScrollView>

            <View style={styles.modalActions}>
              <TouchableOpacity
                style={styles.modalClose}
                onPress={() => setTermsModalVisible(false)}
              >
                <Text style={styles.modalCloseText}>Fechar</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={styles.modalAccept}
                onPress={() => {
                  setAcceptedTerms(true);
                  setTermsModalVisible(false);
                }}
              >
                <Text style={styles.modalAcceptText}>Aceitar</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

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

  termsRow: {
    flexDirection: "row",
    alignItems: "flex-start",
    gap: 10,
    marginTop: 10,
  },

  checkbox: {
    width: 20,
    height: 20,
    borderRadius: 6,
    borderWidth: 2,
    borderColor: "#01415B",
    backgroundColor: "#FFFFFF",
    alignItems: "center",
    justifyContent: "center",
    marginTop: 2,
  },

  termsText: {
    flex: 1,
    color: "#01415B",
    fontSize: 13,
    lineHeight: 18,
  },

  termsLink: {
    textDecorationLine: "underline",
    fontWeight: "700",
  },

  modalOverlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.45)",
    justifyContent: "center",
    padding: 16,
  },

  modalCard: {
    backgroundColor: "#FFFFFF",
    borderRadius: 16,
    padding: 16,
    maxHeight: "85%",
  },

  modalTitle: {
    fontSize: 16,
    fontWeight: "800",
    color: "#01415B",
    marginBottom: 12,
  },

  modalScroll: {
    borderWidth: 1,
    borderColor: "#E2E8F0",
    borderRadius: 12,
    padding: 12,
    marginBottom: 12,
  },

  modalBody: {
    fontSize: 13,
    color: "#0F172A",
    lineHeight: 18,
  },

  modalActions: {
    flexDirection: "row",
    gap: 10,
  },

  modalClose: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    alignItems: "center",
  },

  modalCloseText: {
    color: "#0F172A",
    fontWeight: "700",
  },

  modalAccept: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 12,
    backgroundColor: "#01415B",
    alignItems: "center",
  },

  modalAcceptText: {
    color: "#EAF9FF",
    fontWeight: "800",
  },
});

