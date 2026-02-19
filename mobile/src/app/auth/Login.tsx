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
  
  export default function Login() {
    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [mostrarSenha, setMostrarSenha] = useState(false);
  
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
  
    return (
      <SafeAreaView style={styles.container}>
        {/* TOPO */}
        <View style={styles.header}>
          <Image source={require("../../../assets/logo.png")} style={styles.logo} />
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
          <Text style={styles.title}>Fazer login</Text>
  
          <TextInput
            placeholder="Email"
            placeholderTextColor="#01415B"
            style={styles.input}
            value={email}
            onChangeText={setEmail}
          />
  
          {/* CAMPO SENHA*/}
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
  
          <TouchableOpacity style={styles.buttonEntrar}>
            <Text style={styles.buttonEntrarText}>ENTRAR</Text>
          </TouchableOpacity>
  
          <TouchableOpacity style={styles.buttonCriarConta} activeOpacity={0.8}>
            <Text style={styles.buttonCriarContaText}>CRIAR CONTA</Text>
          </TouchableOpacity>
  
          {/* ESQUECI MINHA SENHA */}
          <TouchableOpacity activeOpacity={0.6}>
            <Text style={styles.forgotPassword}>
              Esqueci minha senha
            </Text>
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
