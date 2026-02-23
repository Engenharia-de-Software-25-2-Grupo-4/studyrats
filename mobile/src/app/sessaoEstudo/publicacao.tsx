import { View, Text, StyleSheet, TouchableOpacity, TextInput, ScrollView, Image, Alert, ImageSourcePropType } from "react-native";
import { useNavigation, useRoute, RouteProp, NavigationProp } from '@react-navigation/native';
import { StackParams } from '@/utils/routesStack';
import { Menu } from "@/components/Menu";
import { Ionicons } from "@expo/vector-icons";
import { useState, useEffect } from "react";
import "@/styles/colors"
import { colors } from "@/styles/colors";
import { deleteSessao } from "@/services/sessao";
import { reactSessao } from "@/services/sessao";
import { comentarSessao } from "@/services/sessao";
import { getAuthenticatedUid } from "@/services/authStorage";
import { authFetch } from "@/services/backendApi";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { getGrupoById } from "@/services/grupo";
import { categories } from "@/utils/categories";
import { getEstudanteByFirebaseUid } from "@/services/backendApi";
import { fetchProfilePhoto } from "@/server/estudanteInfo/fetchProfilePhoto";

type PublicacaoNavProp = NativeStackNavigationProp<StackParams, "Publicacao">;
type PublicacaoRouteProp = RouteProp<StackParams, "Publicacao">;

type UsuarioUI = {
  id: string;
  nome: string;
  foto: ImageSourcePropType | null;
  email?: string;
};

type Comentario = {
  firebaseUid_autor: string;
  nome_autor: string;
  texto: string;
};

export default function Publicacao() {
  const navigation = useNavigation<PublicacaoNavProp>();
  const route = useRoute<PublicacaoRouteProp>();

  const { grupoId, sessao } = route.params as any; // mantém seu formato

  const [isCriador, setIsCriador] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [imagemSessao, setImagemSessao] = useState<string | null>(null);

  const [autorNome, setAutorNome] = useState<string>(
    sessao?.nome_criador ?? "Usuário"
  );

  const [fotoAutor, setFotoAutor] = useState<ImageSourcePropType>(
    require("@/assets/default_profile.jpg")
  );

  const [autorLoading, setAutorLoading] = useState(false);

  const [comentarios, setComentarios] = useState<Comentario[]>([]);
  const [comentario, setComentario] = useState("");
  const [curtido, setCurtido] = useState(false);
  const [curtidas, setCurtidas] = useState(sessao?.curtidas || 0);

  const podeExcluir = isCriador || isAdmin;

  useEffect(() => {
    async function carregarAutor() {
      if (!sessao?.id_criador) return;

      try {
        setAutorLoading(true);

        const estudante = await getEstudanteByFirebaseUid(
          sessao.id_criador
        );

        setAutorNome(estudante.nome);

        try {
          const fotoSource = await fetchProfilePhoto(
            sessao.id_criador
          );

          setFotoAutor(fotoSource);
        } catch (e) {
          console.log("Sem foto do autor ou erro ao buscar:", e);
        }

      } catch (e) {
        console.log("Erro ao carregar autor:", e);
      } finally {
        setAutorLoading(false);
      }
    }

    carregarAutor();
  }, [sessao?.id_criador]);

  useEffect(() => {
    async function verificarPermissoes() {
      const uid = await getAuthenticatedUid();

      setIsCriador(!!uid && sessao?.id_criador === uid);

      if (!uid || !grupoId) {
        setIsAdmin(false);
        return;
      }

      try {
        const grupo = await getGrupoById(grupoId);
        setIsAdmin(grupo?.admin?.firebaseUid === uid);
      } catch (e) {
        console.log("Erro ao buscar grupo (admin):", e);
        setIsAdmin(false);
      }
    }

    async function buscarImagemSessao() {
      if (!sessao?.id_sessao) return;

      try {
        const url = `/imagens/sessaoDeEstudo/${sessao.id_sessao}`;
        const res = await authFetch(url, { method: "GET" });

        if (res.status === 404 || res.status === 204) {
          setImagemSessao(null);
          return;
        }

        if (!res.ok) {
          const text = await res.text().catch(() => "");
          throw new Error(text || `Erro ao buscar imagem (${res.status})`);
        }

        const blob = await res.blob();
        const reader = new FileReader();
        reader.onload = () => setImagemSessao(reader.result as string);
        reader.readAsDataURL(blob);
      } catch (e) {
        console.log("erro imagem:", e);
        setImagemSessao(null);
      }
    }

    verificarPermissoes();
    buscarImagemSessao();
  }, [sessao?.id_criador, sessao?.id_sessao, grupoId]);

  const handleVoltar = () => navigation.goBack();

  const handleEditar = () => {
    navigation.navigate(
      "CriarSessao",
      {
        sessao,
        grupoId,
      } as any
    );
  };

  const handleExcluir = () => {
    Alert.alert("Excluir publicação", "Tem certeza que deseja excluir esta publicação?", [
      { text: "Cancelar", style: "cancel" },
      {
        text: "Excluir",
        style: "destructive",
        onPress: async () => {
          try {
            await deleteSessao(sessao.id_sessao);
            navigation.goBack();
          } catch (error: any) {
            Alert.alert("Erro", error.message);
          }
        },
      },
    ]);
  };

  async function handleAdicionarComentario() {
    if (!comentario.trim()) return;

    try {
      const novoComentario = await comentarSessao(sessao.id_sessao, { texto: comentario });
      setComentarios((prev) => [...prev, novoComentario]);
      setComentario("");
    } catch (error: any) {
      Alert.alert("Erro", error.message);
    }
  }

  async function toggleLike() {
    try {
      const resultado = await reactSessao(sessao.id_sessao);
      setCurtido(resultado.reagiu);
      setCurtidas(resultado.total_reacoes);
    } catch (error: any) {
      Alert.alert("Erro", error.message);
    }
  }

  return (
    <View style={styles.container}>
      {/* HEADER */}
      <View style={styles.header}>
        <TouchableOpacity onPress={handleVoltar}>
          <Ionicons name="arrow-back" size={24} color="#01415B" />
        </TouchableOpacity>

        <Text style={styles.headerTitle}>Publicação</Text>

        <TouchableOpacity onPress={handleEditar}>
          <Text style={styles.editButton}>Editar</Text>
        </TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        {/* TÍTULO */}
        <Text style={styles.titulo}>{sessao.titulo}</Text>

        {/* IMAGEM */}
        {imagemSessao ? (
          <Image source={{ uri: imagemSessao }} style={styles.image} />
        ) : (
          <View style={styles.image} />
        )}

        {/* AUTOR + LIKE */}
        <View style={styles.userRow}>
          <View style={styles.userInfo}>
            <Image
              source={fotoAutor}
              style={styles.avatar}
            />

            <View>
              <Text style={styles.userName}>
                {autorLoading ? "Carregando..." : autorNome}
              </Text>
            </View>
          </View>

          <View style={styles.likeContainer}>
            <Ionicons name="chatbubble-outline" size={20} color="#01415B" />
            <Text style={styles.count}>{comentarios.length}</Text>

            <TouchableOpacity onPress={toggleLike}>
              <Ionicons
                name={curtido ? "heart" : "heart-outline"}
                size={22}
                color={curtido ? "red" : "#01415B"}
              />
            </TouchableOpacity>

            <Text style={styles.count}>{curtidas}</Text>
          </View>
        </View>

        <View style={styles.card}>
          <Text style={styles.section}>Descrição</Text>
          <Text style={styles.texto}>{sessao.descricao}</Text>

          <View style={styles.divider} />

          <Text style={styles.section}>Informações</Text>

          <View style={styles.infoRow}>
            <Text style={styles.label}>Disciplina</Text>
            <Text style={styles.value}>{sessao.disciplina}</Text>
          </View>

          <View style={styles.infoRow}>
            <Text style={styles.label}>Tópico</Text>
            <Text style={styles.value}>{sessao.topico}</Text>
          </View>

          <View style={styles.infoRow}>
            <Text style={styles.label}>Data</Text>
            <Text style={styles.value}>
              {new Date(sessao.horario_inicio).toLocaleDateString()}{" "}
              {new Date(sessao.horario_inicio).toLocaleTimeString([], {
                hour: "2-digit",
                minute: "2-digit",
              })}
            </Text>
          </View>

          <View style={styles.infoRow}>
            <Text style={styles.label}>Duração</Text>
            <Text style={styles.value}>{sessao.duracao_minutos}</Text>
          </View>

          <View style={styles.divider} />

          <Text style={styles.section}>Comentários</Text>

          {comentarios.map((item, index) => (
            <View key={index} style={styles.commentBox}>
              <Text style={styles.commentAuthor}>{item.nome_autor}</Text>
              <Text style={styles.commentText}>{item.texto}</Text>
            </View>
          ))}

          <View style={styles.commentInputContainer}>
            <TextInput
              placeholder="Adicione um comentário..."
              value={comentario}
              onChangeText={setComentario}
              style={styles.commentInput}
            />
            <TouchableOpacity onPress={handleAdicionarComentario}>
              <Text style={styles.commentButton}>Enviar</Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* EXCLUIR */}
        {podeExcluir && (
          <TouchableOpacity style={styles.deleteButton} onPress={handleExcluir}>
            <Text style={styles.deleteButtonText}>Excluir</Text>
          </TouchableOpacity>
        )}
      </ScrollView>

      <Menu tabs={categories} activeTabId="2" />
    </View>
  );
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.cinza[400],
  },

  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 18,
    paddingTop: 80,
    paddingBottom: 30,
    backgroundColor: "#F3FBFF",
  },

  headerTitle: {
    fontSize: 21,
    fontWeight: "900",
    textAlign: "left"
  },

  content: {
    paddingHorizontal: 16,
    paddingBottom: 40,
  },

  titulo: {
    fontSize: 19,
    fontWeight: "600",
    marginBottom: 15,
  },

  image: {
    width: "100%",
    height: 300,
    borderRadius: 16,
    marginBottom: 15
  },

  card: {
    backgroundColor: colors.cinza[400],
    borderRadius: 10,
    padding: 8,
    //marginTop: 0,
  },

  data: {
    color: "#9CA3AF",
    fontSize: 13,
    marginBottom: 18,
  },

  section: {
    fontSize: 16,
    fontWeight: "bold",
    marginBottom: 6,
    marginTop: 2,
  },

  texto: {
    fontSize: 14,
    color: "#4B5563",
    lineHeight: 20,
  },
  divider: {
    height: 1,
    backgroundColor: "#E5E7EB",
    marginVertical: 12,
  },
  editButton: {
    color: colors.azul[300],
    fontWeight: "700",
  },
  commentBox: {
    backgroundColor: "#e4f3f8",
    padding: 15,
    borderRadius: 8,
    marginBottom: 8,
  },

  commentText: {
    fontSize: 14,
  },

  commentInputContainer: {
    flexDirection: "row",
    alignItems: "center",
    marginTop: 10,
  },

  commentInput: {
    flex: 1,
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 20,
    paddingHorizontal: 12,
    paddingVertical: 8,
    marginRight: 8,
  },

  commentButton: {
    color: colors.azul[300],
    fontWeight: "bold",
  },
  infoRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 10,
  },

  label: {
    fontSize: 14,
    color: "#555",
  },

  value: {
    fontSize: 14,
    fontWeight: "600",
    color: "#01415B",
    maxWidth: "60%",
    textAlign: "right",
  },
  avatarSmall: {
    width: 32,
    height: 32,
    borderRadius: 16,
    marginRight: 8,
    marginBottom: 8
  },

  commentAuthor: {
    fontWeight: "600",
    fontSize: 14,
    color: "#333"
  },
  userRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 15,
  },

  userInfo: {
    flexDirection: "row",
    alignItems: "center",
  },

  avatar: {
    width: 40,
    height: 40,
    borderRadius: 20,
    marginRight: 10
  },

  userName: {
    fontWeight: "600",
    fontSize: 14,
  },

  likeContainer: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
  },

  count: {
    fontSize: 13,
    marginHorizontal: 4,
  },
  deleteButton: {
    backgroundColor: colors.azul[300],
    padding: 15,
    marginHorizontal: 20,
    borderRadius: 12,
    alignItems: "center",
    marginBottom: 10,
    marginTop: 15
  },

  deleteButtonText: {
    color: "#FFF",
    fontSize: 16,
    fontWeight: "bold"
  }
});