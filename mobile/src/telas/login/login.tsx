import {
    View,
    Text,
    TextInput,
    TouchableOpacity,
    Image,
    Keyboard,
    Animated,
  } from "react-native";
  import { SafeAreaView } from "react-native-safe-area-context";
  import { useEffect, useRef, useState } from "react";
  import { Ionicons } from "@expo/vector-icons";
  import { styles } from "./login.styles";
  
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
