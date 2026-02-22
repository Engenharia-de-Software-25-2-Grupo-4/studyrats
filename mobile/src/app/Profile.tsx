import {
  StyleSheet,
  Text,
  View,
  ScrollView,
  TouchableOpacity,
  Image,
  Alert,
  ActivityIndicator,
} from "react-native"
import { useCallback, useEffect, useRef, useState } from "react"
import { Feather, Ionicons } from "@expo/vector-icons"
import { useFocusEffect, useNavigation } from "@react-navigation/native"
import { StackNavigationProp } from "@react-navigation/stack"
import { colors } from "@/styles/colors"
import { Menu } from "@/components/Menu"
import { StackParams } from "@/utils/routesStack"
import { categories } from "@/utils/categories"
import { GrupoDetails, listGrupos } from "@/services/grupo"
import { getEstudanteAtual, Estudante } from "@/services/estudante"
import { ImageSourcePropType } from "react-native"
import { fetchProfilePhoto } from "../server/estudanteInfo/fetchProfilePhoto" 


import { authFetch } from "@/services/backendApi" 

type HomeNavProp = StackNavigationProp<StackParams, "Profile">

export default function Profile() {
  const navigation = useNavigation<HomeNavProp>()

  const [estudante, setEstudante] = useState<Estudante | null>(null)
  const [grupos, setGrupos] = useState<GrupoDetails[]>([])
  const [loadingEstudante, setLoadingEstudante] = useState(true)
  const [loadingGrupos, setLoadingGrupos] = useState(true)


  const [imagensGrupos, setImagensGrupos] = useState<Record<string, string>>({})
  const imagensGruposRef = useRef<Record<string, string>>({})
  const pendentesRef = useRef<Set<string>>(new Set())

  const [fotoPerfil, setFotoPerfil] = useState<ImageSourcePropType>(require("@/assets/default_profile.jpg"))
  const [loadingFoto, setLoadingFoto] = useState(true)

  useEffect(() => {
    imagensGruposRef.current = imagensGrupos
  }, [imagensGrupos])

  useEffect(() => {
    async function loadPhoto() {
        try {
        setLoadingFoto(true)
        const uid = estudante?.firebaseUid // <-- ajuste o nome do campo aqui
        const img = await fetchProfilePhoto(uid || "")
        setFotoPerfil(img)
        } finally {
        setLoadingFoto(false)
        }
    }

    if (estudante) loadPhoto()
    }, [estudante])

  useEffect(() => {
    async function fetchEstudante() {
      try {
        setLoadingEstudante(true)
        const data = await getEstudanteAtual()
        setEstudante(data)
      } catch (error) {
        console.log("Erro ao buscar estudante:", error)
      } finally {
        setLoadingEstudante(false)
      }
    }

    fetchEstudante()
  }, [])

  const blobToBase64 = (blob: Blob): Promise<string> => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.onload = () => resolve(reader.result as string)
      reader.onerror = reject
      reader.readAsDataURL(blob)
    })
  }

  const buscarImagemDoGrupo = useCallback(async (grupoId: string) => {
    if (imagensGruposRef.current[grupoId]) return
    if (pendentesRef.current.has(grupoId)) return

    pendentesRef.current.add(grupoId)

    try {
      const res = await authFetch(`/imagens/grupo/${grupoId}`, { method: "GET" })
      if (!res.ok) {
        console.log("Falha ao buscar imagem do grupo:", grupoId, res.status)
        return
      }

      const blob = await res.blob()
      const base64 = await blobToBase64(blob)

      setImagensGrupos((prev) => ({ ...prev, [grupoId]: base64 }))
    } catch (error) {
      console.log("Erro ao buscar imagem do grupo:", grupoId, error)
    } finally {
      pendentesRef.current.delete(grupoId)
    }
  }, [])

  const loadGroups = useCallback(async () => {
    try {
      setLoadingGrupos(true)
      const data = await listGrupos()
      setGrupos(data)

      data.forEach((g) => {
        buscarImagemDoGrupo(g.id_grupo)
      })
    } catch (error) {
      Alert.alert("Erro", "Não foi possível carregar os grupos")
      console.error(error)
    } finally {
      setLoadingGrupos(false)
    }
  }, [buscarImagemDoGrupo])

  useFocusEffect(
    useCallback(() => {
      loadGroups()
    }, [loadGroups])
  )

  const handleNavigateToGroup = (grupoId: string) => {
    console.log("Abrir grupo:", grupoId)
    navigation.navigate("StudyGroupScreen", { grupoId: grupoId })
  }

  const resolveGroupImageSource = (grupo: GrupoDetails) => {
    const base64 = imagensGrupos[grupo.id_grupo]
    if (base64) return { uri: base64 }

    if (grupo.foto_perfil && grupo.foto_perfil.startsWith("http")) {
      return { uri: grupo.foto_perfil }
    }

    return require("@/assets/image.png")
  }

  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        {/* HEADER */}
        <View style={styles.header}>
          <View style={styles.profileHeader}>
            <View style={styles.avatar}>
              <Image source={fotoPerfil} style={styles.avatarImage} />
                {loadingFoto && (
                <View style={styles.avatarLoadingOverlay}>
                    <ActivityIndicator size="small" color="#FFF" />
                </View>
                )}
            </View>

            <View style={styles.profileInfo}>
              <Text style={styles.name}>
                {loadingEstudante ? "Carregando..." : estudante?.nome || "Usuário"}
              </Text>

              <View style={styles.stats}>
                <View style={styles.statItem}>
                  <Text style={styles.statNumber}>{grupos.length}</Text>
                  <Text style={styles.statLabel}>Grupos</Text>
                </View>
              </View>
            </View>
          </View>
        </View>

        {/* ACTION BUTTON */}
        <View style={styles.actionButtons}>
            <TouchableOpacity
            style={styles.editButton}
            activeOpacity={0.85}
            onPress={() => navigation.navigate("EditAcc" as any)}
            >
            <Text style={styles.editButtonText}>CONFIGURE SEU PERFIL</Text>
            </TouchableOpacity>
        </View>

        {/* TABS */}
        <View style={styles.tabs}>
          <TouchableOpacity style={[styles.tab, styles.tabActive]} activeOpacity={0.85}>
            <Ionicons name="grid" size={24} color={colors.azul[300]} />
          </TouchableOpacity>

          <TouchableOpacity style={styles.tab} activeOpacity={0.85}>
            <Ionicons name="trophy" size={24} color={colors.cinza[300]} />
          </TouchableOpacity>
        </View>

        {/* CARDS GRID */}
        {loadingGrupos ? (
          <ActivityIndicator size="large" color={colors.azul[300]} style={{ marginTop: 40 }} />
        ) : (
          <View style={styles.cardsGrid}>
            {grupos.map((grupo) => {
              const isFinished = new Date(grupo.data_fim) < new Date()

              return (
                <GroupCard
                  key={grupo.id_grupo}
                  title={grupo.nome}
                  participants={0} // ajustar quando a API retornar membros
                  color={colors.azul[200]}
                  badge={isFinished ? "check" : "time"}
                  imageSource={resolveGroupImageSource(grupo)}
                  onPress={() => handleNavigateToGroup(grupo.id_grupo)}
                />
              )
            })}

            <NewGroupCard onPress={() => navigation.navigate("CriarGrupo")} />
          </View>
        )}
      </ScrollView>

      {/* MENU FIXO */}
      <View style={styles.menuWrapper}>
        <Menu tabs={categories} activeTabId="3" />
      </View>
    </View>
  )
}

type GroupCardProps = {
  title: string
  participants: number
  color: string
  badge: "trophy" | "time" | "check"
  imageSource: any
  onPress?: () => void
}

function GroupCard({ title, participants, color, badge, imageSource, onPress }: GroupCardProps) {
  return (
    <TouchableOpacity
      style={[styles.groupCard, { backgroundColor: color }]}
      onPress={onPress}
      activeOpacity={0.85}
    >
      <View style={styles.topBadge}>
        <View
          style={[
            styles.badgeIconContainer,
            badge === "trophy" && { backgroundColor: colors.laranja },
            badge === "time" && { backgroundColor: colors.vermelho },
            badge === "check" && { backgroundColor: colors.verde },
          ]}
        >
          {badge === "trophy" && <Ionicons name="trophy" size={16} color="#FFF" />}
          {badge === "time" && <Ionicons name="time" size={16} color="#FFF" />}
          {badge === "check" && <Feather name="check" size={16} color="#FFF" />}
        </View>
      </View>

      <View style={styles.participantsBadge}>
        <Ionicons name="people" size={14} color={colors.azul[300]} />
        <Text style={styles.participantsText}>{participants}</Text>
      </View>

      <View style={styles.cardImageContainer}>
        <Image source={imageSource} style={styles.cardImage} resizeMode="cover" />
      </View>

      <View style={styles.cardFooter}>
        <Text style={styles.cardTitle}>{title}</Text>
      </View>
    </TouchableOpacity>
  )
}

function NewGroupCard({ onPress }: { onPress: () => void }) {
  return (
    <TouchableOpacity style={[styles.groupCard, styles.newGroupCard]} onPress={onPress} activeOpacity={0.85}>
      <View style={styles.newGroupIcon}>
        <Feather name="plus" size={30} color={colors.azul[300]} />
      </View>
      <Text style={styles.newGroupText}>Novo grupo</Text>
    </TouchableOpacity>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.cinza[400],
  },

  content: {
    padding: 20,
    paddingTop: 80,
    paddingBottom: 140,
  },

  menuWrapper: {
    position: "absolute",
    left: 0,
    right: 0,
    bottom: 0,
  },

  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "flex-start",
    marginBottom: 20,
  },

  profileHeader: {
    flexDirection: "row",
    gap: 16,
    flex: 1,
  },

  avatar: {
    width: 70,
    height: 70,
    borderRadius: 35,
    backgroundColor: colors.azul[300],
    overflow: "hidden",
  },

  avatarImage: {
    width: "100%",
    height: "100%",
  },

  profileInfo: {
    flex: 1,
    justifyContent: "center",
  },

  name: {
    fontSize: 20,
    fontWeight: "700",
    color: colors.azul[300],
    marginBottom: 12,
  },

  stats: {
    flexDirection: "row",
    gap: 24,
  },

  statItem: {
    alignItems: "flex-start",
  },

  statNumber: {
    fontSize: 18,
    fontWeight: "700",
    color: colors.azul[300],
  },

  statLabel: {
    fontSize: 12,
    color: colors.azul[300],
    marginTop: 2,
  },

  actionButtons: {
    flexDirection: "row",
    marginBottom: 24,
  },

  editButton: {
    flex: 1,
    backgroundColor: colors.azul[300],
    paddingVertical: 10,
    borderRadius: 8,
    alignItems: "center",
  },

  editButtonText: {
    color: "#FFF",
    fontWeight: "700",
    fontSize: 12,
    letterSpacing: 0.5,
  },

  tabs: {
    flexDirection: "row",
    gap: 40,
    marginBottom: 24,
    borderBottomWidth: 1,
    borderBottomColor: colors.cinza[300],
  },

  tab: {
    paddingVertical: 12,
    opacity: 0.4,
  },

  tabActive: {
    opacity: 1,
    borderBottomWidth: 2,
    borderBottomColor: colors.azul[300],
  },

  cardsGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 12,
    justifyContent: "center",
  },

  groupCard: {
    width: "48%",
    aspectRatio: 0.75,
    borderRadius: 16,
    padding: 12,
    position: "relative",
    overflow: "hidden",
  },

  topBadge: {
    position: "absolute",
    top: 12,
    left: 12,
    zIndex: 1,
  },

  badgeIconContainer: {
    width: 28,
    height: 28,
    borderRadius: 14,
    alignItems: "center",
    justifyContent: "center",
  },

  participantsBadge: {
    position: "absolute",
    top: 12,
    right: 12,
    backgroundColor: "#FFF",
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
    gap: 4,
    zIndex: 1,
  },

  participantsText: {
    fontSize: 12,
    fontWeight: "700",
    color: colors.azul[300],
  },

  cardImageContainer: {
    flex: 1,
    marginTop: 40,
    borderRadius: 8,
    overflow: "hidden",
  },

  cardImage: {
    width: "100%",
    height: "100%",
  },

  cardFooter: {
    paddingTop: 8,
  },

  cardTitle: {
    fontSize: 14,
    fontWeight: "600",
    color: colors.azul[300],
  },

  newGroupCard: {
    backgroundColor: colors.azul[300],
    alignItems: "center",
    justifyContent: "center",
    gap: 12,
    alignSelf: "center",
  },

  newGroupIcon: {
    width: 60,
    height: 60,
    backgroundColor: "#FFF",
    borderRadius: 30,
    opacity: 0.9,
    alignItems: "center",
    justifyContent: "center",
  },

  newGroupText: {
    fontSize: 15,
    fontWeight: "700",
    color: "#FFF",
    textAlign: "center",
  },

  avatarLoadingOverlay: {
    ...StyleSheet.absoluteFillObject,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "rgba(0,0,0,0.15)",
    },
})