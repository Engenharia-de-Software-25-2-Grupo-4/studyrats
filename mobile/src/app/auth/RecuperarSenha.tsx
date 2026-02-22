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
  
  import type { StackScreenProps } from "@react-navigation/stack";
  import type { StackParams } from "@/utils/routesStack";

  type Props = StackScreenProps<StackParams, "RecuperarSenha">;
    
  type Step = "EMAIL" | "SUCESSO";  

export default function RecuperarSenha({ navigation }: Props) {
    const [step, setStep] = useState<Step>("EMAIL");
  
    const [email, setEmail] = useState("");

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

    const limparMensagens = () => {
      setErro("");
      setInfo("");
    }; 
  
    const handleEnviarLink = async () => {
      limparMensagens();

      const e = email.trim();
      if (!e) {
        setErro("Informe seu email.");
        return;
      }

      try {
        await firebaseSendPasswordResetEmail(e);
        setInfo("Enviamos um link de recuperação para o seu e-mail.");
        setStep("SUCESSO");
      } catch (err: any) {
        setErro(err?.message ?? "Não foi possível enviar o e-mail de recuperação.");
      }
    };

  const tituloPorEtapa = () => {
    if (step === "EMAIL") return "Recuperar senha";
    return "Concluído";
  };
  
    return (
      <SafeAreaView style={styles.container}>
        {/* TOPO */}
        <View style={styles.header}>
          <Image source={require("../../assets/logo.png")} style={styles.logo} />
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
  
              <TouchableOpacity style={styles.buttonPrimary} onPress={handleEnviarLink}>
                <Text style={styles.buttonPrimaryText}>ENVIAR E-MAIL DE VERIFICAÇÃO</Text>
              </TouchableOpacity>
  
              <TouchableOpacity activeOpacity={0.6} onPress={() => navigation.navigate("Login")}>
                <Text style={styles.linkText}>Voltar para o login</Text>
              </TouchableOpacity>
            </>
          )}
  
          {/* SUCESSO */}
          {step === "SUCESSO" && (
            <>
              <Text style={styles.info}>
                Verifique também a caixa de spam.
              </Text>

              <TouchableOpacity activeOpacity={0.6} onPress={() => navigation.navigate("Login")}>
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