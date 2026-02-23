import { useEffect, useRef, useState } from "react";
import { View, Text, ActivityIndicator, TouchableOpacity, StyleSheet } from "react-native";
import { useNavigation, useRoute } from "@react-navigation/native";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { StackParams } from "@/utils/routesStack";
import { entrarNoGrupo, validarConvite, ConviteValidacao } from "@/services/grupo";
import { getAuthenticatedUid } from "@/services/authStorage";

type Status =
  | "loading"
  | "need_token"
  | "need_auth"
  | "ready_join"
  | "already_member"
  | "success"
  | "error";

type Nav = NativeStackNavigationProp<StackParams>;

export default function EntrarNoGrupo() {
  const navigation = useNavigation<Nav>();
  const route = useRoute();

  const token = (route.params as any)?.token as string | undefined;

  const [status, setStatus] = useState<Status>("loading");
  const [message, setMessage] = useState<string>("");
  const [convite, setConvite] = useState<ConviteValidacao | null>(null);

  const ranRef = useRef(false);

  useEffect(() => {
    if (ranRef.current) return;
    ranRef.current = true;

    async function run() {
      console.log("===== DEBUG CONVITE =====");
      console.log("Token recebido:", token);

      if (!token || token.trim().length < 5) {
        setStatus("need_token");
        setMessage("Convite inválido ou ausente.");
        return;
      }

      const uid = await getAuthenticatedUid();
      console.log("UID autenticado:", uid);

      if (!uid) {
        setStatus("need_auth");
        setMessage("Você precisa entrar na conta para aceitar o convite.");
        return;
      }

      try {
        setStatus("loading");
        setMessage("Validando convite...");

        const info = await validarConvite(token);
        setConvite(info);

        if (info.jaMembro) {
          setStatus("already_member");
          setMessage("Você já participa desse grupo.");
        } else {
          setStatus("ready_join");
          setMessage(`Convite para: ${info.nomeGrupo}`);
        }
      } catch (e: any) {
        console.log("Erro ao validar:", e);
        setStatus("error");
        setMessage(e?.message ?? "Erro ao validar convite.");
      }
    }

    run();
  }, [token]);

  const goProfile = () => {
    navigation.replace("Profile");
  };

  const goLogin = () => {
    navigation.navigate("Login");
  };

  const goBackSafe = () => {
    navigation.goBack();
  };

  const aceitar = async () => {
    if (!token) return;
    try {
      setStatus("loading");
      setMessage("Entrando no grupo...");

      await entrarNoGrupo(token);

      setStatus("success");
      setMessage("Você entrou no grupo com sucesso!");
    } catch (e: any) {
      // aqui vai cair, por ex, se expirar ou qualquer regra
      setStatus("error");
      // seu backend já retorna {"mensagem": "..."} no 409; seu entrarNoGrupo já loga o body.
      setMessage(e?.message ?? "Erro ao entrar no grupo.");
    }
  };

  return (
    <View style={styles.container}>
      {status === "loading" && (
        <>
          <ActivityIndicator size="large" />
          <Text style={styles.title}>Processando convite</Text>
          <Text style={styles.subtitle}>{message || "Aguarde..."}</Text>
        </>
      )}

      {status === "need_token" && (
        <>
          <Text style={styles.title}>Convite inválido</Text>
          <Text style={styles.subtitle}>{message}</Text>
          <TouchableOpacity style={styles.secondaryBtn} onPress={goBackSafe}>
            <Text style={styles.secondaryBtnText}>Voltar</Text>
          </TouchableOpacity>
        </>
      )}

      {status === "need_auth" && (
        <>
          <Text style={styles.title}>Login necessário</Text>
          <Text style={styles.subtitle}>{message}</Text>
          <TouchableOpacity style={styles.primaryBtn} onPress={goLogin}>
            <Text style={styles.primaryBtnText}>Ir para Login</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.secondaryBtn} onPress={goBackSafe}>
            <Text style={styles.secondaryBtnText}>Cancelar</Text>
          </TouchableOpacity>
        </>
      )}

      {status === "already_member" && (
        <>
          <Text style={styles.title}>Você já é membro ✅</Text>
          <Text style={styles.subtitle}>
            {convite?.nomeGrupo ? `Grupo: ${convite.nomeGrupo}\n\n` : ""}
            {message}
          </Text>
          <TouchableOpacity style={styles.primaryBtn} onPress={goProfile}>
            <Text style={styles.primaryBtnText}>Ir para o Perfil</Text>
          </TouchableOpacity>
        </>
      )}

      {status === "ready_join" && (
        <>
          <Text style={styles.title}>Convite encontrado</Text>
          <Text style={styles.subtitle}>
            {convite?.nomeGrupo ? `Grupo: ${convite.nomeGrupo}\n` : ""}
            {convite?.descricaoGrupo ? `${convite.descricaoGrupo}\n\n` : "\n"}
            {message}
          </Text>

          <TouchableOpacity style={styles.primaryBtn} onPress={aceitar}>
            <Text style={styles.primaryBtnText}>Entrar no grupo</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.secondaryBtn} onPress={goBackSafe}>
            <Text style={styles.secondaryBtnText}>Cancelar</Text>
          </TouchableOpacity>
        </>
      )}

      {status === "success" && (
        <>
          <Text style={styles.title}>Tudo certo ✅</Text>
          <Text style={styles.subtitle}>{message}</Text>
          <TouchableOpacity style={styles.primaryBtn} onPress={goProfile}>
            <Text style={styles.primaryBtnText}>Ir para o Perfil</Text>
          </TouchableOpacity>
        </>
      )}

      {status === "error" && (
        <>
          <Text style={styles.title}>Não deu 😕</Text>
          <Text style={styles.subtitle}>{message}</Text>
          <TouchableOpacity style={styles.primaryBtn} onPress={goProfile}>
            <Text style={styles.primaryBtnText}>Ir para o Perfil</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.secondaryBtn} onPress={goBackSafe}>
            <Text style={styles.secondaryBtnText}>Voltar</Text>
          </TouchableOpacity>
        </>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
    justifyContent: "center",
    alignItems: "center",
    gap: 12,
    backgroundColor: "#fff",
  },
  title: { fontSize: 20, fontWeight: "700", color: "#01415B", textAlign: "center" },
  subtitle: { fontSize: 14, color: "#333", textAlign: "center", lineHeight: 20 },
  primaryBtn: {
    marginTop: 8,
    backgroundColor: "#01415B",
    paddingVertical: 12,
    paddingHorizontal: 18,
    borderRadius: 12,
    minWidth: 220,
    alignItems: "center",
  },
  primaryBtnText: { color: "#fff", fontWeight: "700" },
  secondaryBtn: {
    backgroundColor: "transparent",
    paddingVertical: 10,
    paddingHorizontal: 18,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#01415B",
    minWidth: 220,
    alignItems: "center",
  },
  secondaryBtnText: { color: "#01415B", fontWeight: "700" },
});