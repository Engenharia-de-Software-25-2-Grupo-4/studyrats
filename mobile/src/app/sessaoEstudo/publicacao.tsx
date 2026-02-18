import { View, Text, StyleSheet, TouchableOpacity, TextInput, ScrollView, Image, Alert } from "react-native";
import { useNavigation, useRoute, RouteProp, NavigationProp } from '@react-navigation/native';
import { StackParams } from '@/utils/routesStack'; 
import { Menu } from "@/components/Menu";
import { Ionicons } from "@expo/vector-icons";
import { useState } from "react";
import "../styles/colors"
import "../assets/image-4.png"
import { colors } from "@/styles/colors";



export default function Publicacao() {
    const navigation = useNavigation<NavigationProp<StackParams>>();
    const route = useRoute();
    const dados = (route.params as any)?.desafio;

    type Usuario = {
        id: string;
        nome: string;
        foto: string | null;
    };

    type Comentario = {
        id: string;
        texto: string;
        usuario: Usuario;
    };

    const [comentarios, setComentarios] = useState<Comentario[]>([]);
    const [comentario, setComentario] = useState("");
    const [curtido, setCurtido] = useState(false);
    const [curtidas, setCurtidas] = useState(dados.curtidas || 0);


    const usuarioLogado: Usuario = {
        id: "1",
        nome: "Gabriella Letícia",
        foto: null
    };

    const [publicacao, setPublicacao] = useState({
        ...dados,
        usuario: usuarioLogado // mock 
    });

    const handleVoltar = () => {
        navigation.goBack();
    };

    const handleEditar = () => {
        navigation.navigate('CriarSessao', { sessao: dados } as any);
    };

    const handleExcluir = () => {
        Alert.alert(
        "Excluir desafio",
        "Tem certeza que deseja excluir este desafio?",
        [
            {
            text: "Cancelar",
            style: "cancel"
            },
            {
            text: "Excluir",
            style: "destructive",
            onPress: () => {
                // Aqui você coloca a lógica de exclusão
                console.log("Desafio excluído:", dados.id);

                navigation.goBack(); // volta após excluir
            }
            }
        ]
        );
    };

    function handleAdicionarComentario() {
        if (!comentario.trim()) return;

        const novoComentario: Comentario = {
        id: Date.now().toString(),
        texto: comentario,
        usuario: usuarioLogado
        };

        setComentarios([...comentarios, novoComentario]);
        setComentario("");
    }
    function toggleLike() {
        if (curtido) {
        setCurtidas(curtidas - 1);
        } else {
        setCurtidas(curtidas + 1);
        }
        setCurtido(!curtido);
    }

    return (
        <View style={styles.container}>

        {/* HEADER */}
        <View style={styles.header}>
            <TouchableOpacity onPress={handleVoltar}>
            <Ionicons name="arrow-back" size={24} color="#01415B"/>
            </TouchableOpacity>

            <Text style={styles.headerTitle}>Publicação</Text>
            <TouchableOpacity onPress={handleEditar}>
            <Text style={styles.editButton}>Editar</Text>
            </TouchableOpacity>
        </View>

        <ScrollView contentContainerStyle={styles.content}>

            {/* TÍTULO */}
            <Text style={styles.titulo}>
            {dados.titulo}
            </Text>

            {/* IMAGEM */}
            <Image
            source={{ uri: dados.image }}
            style={styles.image}
            />
            <View style={styles.userRow}>
            <View style={styles.userInfo}>
                <Image
                source={
                    publicacao.usuario.foto
                    ? { uri: publicacao.usuario.foto }
                    : require("../assets/image-4.png")
    }
                style={styles.avatar}
                />

                <View>
                <Text style={styles.userName}>
                    {publicacao.usuario.nome}
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
                <Text style={styles.texto}>{dados.descricao}</Text>

                <View style={styles.divider} />

                <Text style={styles.section}>Informações</Text>
                <View style={styles.infoRow}>
                <Text style={styles.label}>Disciplina</Text>
                <Text style={styles.value}>{dados.disciplina?.nome}</Text>
                </View>

                <View style={styles.infoRow}>
                <Text style={styles.label}>Tópico</Text>
                <Text style={styles.value}>{dados.topico}</Text>
                </View>

                <View style={styles.infoRow}>
                <Text style={styles.label}>Data</Text>
                <Text style={styles.value}>
                    {new Date(dados.dataHora).toLocaleDateString()}{" "}
                    {new Date(dados.dataHora).toLocaleTimeString([], {
                    hour: "2-digit",
                    minute: "2-digit"
                    })}
                </Text>
                </View>

                <View style={styles.infoRow}>
                <Text style={styles.label}>Duração</Text>
                <Text style={styles.value}>{dados.duracao}</Text>
                </View>

                <View style={styles.divider} />

                <Text style={styles.section}>Comentários</Text>

                {/* Lista só aparece se tiver comentários */}
                {comentarios.map((item) => (
                <View key={item.id} style={styles.commentBox}>
                    
                    <View style={{ flexDirection: "row", alignItems: "center" }}>
                    <Image
                        source={
                        item.usuario.foto
                            ? { uri: item.usuario.foto }
                            : require("../assets/image-4.png")
                        }
                        style={styles.avatarSmall}
                    />

                    <Text style={styles.commentAuthor}>
                        {item.usuario.nome}
                    </Text>
                    </View>

                    <Text style={styles.commentText}>{item.texto}</Text>
                </View>
                ))}

                    {/* Input sempre aparece */}
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

        </ScrollView>
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

});