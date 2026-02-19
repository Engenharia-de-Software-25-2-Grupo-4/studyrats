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
  
  type Step = "EMAIL" | "CODIGO" | "NOVA_SENHA" | "SUCESSO";
  
  export default function RecuperarSenha() {
    const [step, setStep] = useState<Step>("EMAIL");
  
    const [email, setEmail] = useState("");
    const [codigo, setCodigo] = useState("");
    const [senha, setSenha] = useState("");
    const [confirmarSenha, setConfirmarSenha] = useState("");
  
    const [mostrarSenha, setMostrarSenha] = useState(false);
    const [mostrarConfirmacao, setMostrarConfirmacao] = useState(false);
  
    const [erro, setErro] = useState("");
    const [info, setInfo] = useState("");
  
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
  
    // SIMUL
    const emailCadastradoFake = "teste@studyrats.com";
    const codigoCorretoFake = "123456";
    // =====================================
  
    const limparMensagens = () => {
      setErro("");
      setInfo("");
    };
  
    const handleEnviarCodigo = () => {
      limparMensagens();
  
      if (!email.trim()) {
        setErro("Informe seu email.");
        return;
      }
  
      // Fluxo secundário 1: email não existe
      if (email.trim().toLowerCase() !== emailCadastradoFake) {
        setErro("Não há conta vinculada a esse email. Faça seu cadastro.");
        return;
      }
  
      // Fluxo principal 3: enviar email
      setInfo("Enviamos um código para seu email.");
      setStep("CODIGO");
    };
  
    const handleVerificarCodigo = () => {
      limparMensagens();
  
      if (!codigo.trim()) {
        setErro("Informe o código recebido no email.");
        return;
      }
  
      // Fluxo secundário 2: código errado
      if (codigo.trim() !== codigoCorretoFake) {
        setErro("Código inválido. Tente novamente.");
        return;
      }
  
      setInfo("Código verificado. Crie uma nova senha.");
      setStep("NOVA_SENHA");
    };
  
    const handleSalvarNovaSenha = () => {
      limparMensagens();
  
      if (!senha || !confirmarSenha) {
        setErro("Preencha a nova senha e a confirmação.");
        return;
      }
  
      if (senha !== confirmarSenha) {
        setErro("As senhas não coincidem. Digite novamente.");
        return;
      }
  
      // Aqui entra o salvamento no banco via backend.
      setStep("SUCESSO");
    };
  
    const tituloPorEtapa = () => {
      if (step === "EMAIL") return "Recuperar senha";
      if (step === "CODIGO") return "Verificar código";
      if (step === "NOVA_SENHA") return "Nova senha";
      return "Concluído";
    };
  
    return (
      <SafeAreaView style={styles.container}>
        {/* TOPO */}
        <View style={styles.header}>
          <Image source={require("../../../assets/logo.png")} style={styles.logo} />
          <Text style={styles.logoText}>StudyRats</Text>
        </View>
  
        {/* CARD */}
        <Animated.View style={[styles.card, { transform: [{ translateY }] }]}>
          <Text style={styles.title}>{tituloPorEtapa()}</Text>
  
          {/* Mensagens */}
          {erro ? <Text style={styles.error}>{erro}</Text> : null}
          {info ? <Text style={styles.info}>{info}</Text> : null}
  
          {/* ETAPA 1: Email */}
          {step === "EMAIL" && (
            <>
              <TextInput
                placeholder="Email"
                placeholderTextColor="#01415B"
                style={styles.input}
                value={email}
                onChangeText={setEmail}
                keyboardType="email-address"
                autoCapitalize="none"
              />
  
              <TouchableOpacity style={styles.buttonPrimary} onPress={handleEnviarCodigo}>
                <Text style={styles.buttonPrimaryText}>ENVIAR CÓDIGO</Text>
              </TouchableOpacity>
  
              <TouchableOpacity activeOpacity={0.6}>
                <Text style={styles.linkText}>Voltar para o login</Text>
              </TouchableOpacity>
            </>
          )}
  
          {/* ETAPA 2: Código */}
          {step === "CODIGO" && (
            <>
              <TextInput
                placeholder="Código (6 dígitos)"
                placeholderTextColor="#01415B"
                style={styles.input}
                value={codigo}
                onChangeText={setCodigo}
                keyboardType="number-pad"
                maxLength={6}
              />
  
              <TouchableOpacity style={styles.buttonPrimary} onPress={handleVerificarCodigo}>
                <Text style={styles.buttonPrimaryText}>VERIFICAR</Text>
              </TouchableOpacity>
  
              <TouchableOpacity
                activeOpacity={0.6}
                onPress={() => {
                  limparMensagens();
                  setCodigo("");
                  setStep("EMAIL");
                }}
              >
                <Text style={styles.linkText}>Trocar email</Text>
              </TouchableOpacity>
            </>
          )}
  
          {/* ETAPA 3: Nova senha */}
          {step === "NOVA_SENHA" && (
            <>
              {/* Senha */}
              <View style={styles.passwordContainer}>
                <TextInput
                  placeholder="Nova senha"
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
  
              {/* Confirmar */}
              <View style={styles.passwordContainer}>
                <TextInput
                  placeholder="Confirmar nova senha"
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
  
              <TouchableOpacity style={styles.buttonPrimary} onPress={handleSalvarNovaSenha}>
                <Text style={styles.buttonPrimaryText}>SALVAR</Text>
              </TouchableOpacity>
            </>
          )}
  
          {/* SUCESSO */}
          {step === "SUCESSO" && (
            <>
              <Text style={styles.success}>
                Senha alterada com sucesso!
              </Text>
  
              <TouchableOpacity activeOpacity={0.6}>
                <Text style={styles.linkText}>Voltar para o login</Text>
              </TouchableOpacity>
            </>
          )}
        </Animated.View>
      </SafeAreaView>
    );
  }

  const styles = StyleSheet.create({
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
    marginBottom: 12,
    color: "#01415B",
  },

  error: {
    color: "#C62828",
    fontWeight: "600",
    marginBottom: 8,
  },

  info: {
    color: "#01415B",
    fontWeight: "500",
    marginBottom: 8,
  },

  success: {
    color: "#2E7D32",
    fontWeight: "700",
    marginBottom: 12,
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

  buttonPrimary: {
    backgroundColor: "#01415B",
    padding: 14,
    borderRadius: 8,
    alignItems: "center",
    marginTop: 8,
  },

  buttonPrimaryText: {
    color: "#FFFFFF",
    fontWeight: "700",
    fontSize: 14,
  },

  linkText: {
    marginTop: 16,
    textAlign: "center",
    color: "#01415B",
    fontWeight: "700",
    textDecorationLine: "underline",
  },
});