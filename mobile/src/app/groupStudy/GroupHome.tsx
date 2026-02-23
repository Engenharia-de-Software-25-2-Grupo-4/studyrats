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
import { useCallback, useRef, useState } from "react"
import { Feather, Ionicons } from "@expo/vector-icons"
import { useFocusEffect, useNavigation } from "@react-navigation/native"
import { StackNavigationProp } from "@react-navigation/stack"
import { colors } from "@/styles/colors"
import { Menu } from "@/components/Menu"
import { StackParams } from "@/utils/routesStack"
import { categories } from "@/utils/categories"
import { GrupoDetails, listGrupos, getQuantidadeMembrosCached, promisePool } from "@/services/grupo"
import { authFetch } from "@/services/backendApi"

type GruposNavProp = StackNavigationProp<StackParams, "GroupHome">

type GrupoComQtd = GrupoDetails & { quantidadeMembros: number }

export default function GruposScreen() {
  const navigation = useNavigation<GruposNavProp>()

  const [grupos, setGrupos] = useState<GrupoComQtd[]>([])
  const [loadingGrupos, setLoadingGrupos] = useState(true)

  const [imagensGrupos, setImagensGrupos] = useState<Record<string, string>>({})
  const imagensGruposRef = useRef<Record<string, string>>({})
  const pendentesRef = useRef<Set<string>>(new Set())

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

      data.forEach((g) => buscarImagemDoGrupo(g.id_grupo))

      const gruposComQtd = await promisePool(
        data,
        async (g) => {
          try {
            const qtd = await getQuantidadeMembrosCached(g.id_grupo)
            return { ...g, quantidadeMembros: qtd }
          } catch {
            return { ...g, quantidadeMembros: 0 }
          }
        },
        5
      )

      setGrupos(gruposComQtd)
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

  const handleNavigateToHome = () => {
    navigation.goBack()
  }

  const handleNavigateToGroup = (grupoId: string) => {
    navigation.navigate("StudyGroupScreen", { grupoId })
  }

  const handleNavigateToCriarGrupo = () => {
    navigation.navigate("CriarGrupo")
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
      <View style={styles.header}>
        <TouchableOpacity onPress={handleNavigateToHome}>
          <Ionicons name="arrow-back" size={24} />
        </TouchableOpacity>

        <Text style={styles.headerTitle}>Grupos</Text>
        
        <View style={styles.headerIcon} />
      </View>

      <ScrollView
        contentContainerStyle={styles.content}
        showsVerticalScrollIndicator={false}
      >
        {loadingGrupos ? (
          <ActivityIndicator
            size="large"
            color={colors.azul[300]}
            style={{ marginTop: 40 }}
          />
        ) : (
          <View style={styles.cardsGrid}>
            {grupos.map((grupo) => {
              const isFinished = new Date(grupo.data_fim) < new Date()

              return (
                <GroupCard
                  key={grupo.id_grupo}
                  title={grupo.nome}
                  participants={grupo.quantidadeMembros ?? 0}
                  color={colors.azul[200]}
                  badge={isFinished ? "check" : "time"}
                  imageSource={resolveGroupImageSource(grupo)}
                  onPress={() => handleNavigateToGroup(grupo.id_grupo)}
                />
              )
            })}

            <NewGroupCard onPress={handleNavigateToCriarGrupo} />
          </View>
        )}
      </ScrollView>

      <Menu tabs={categories} activeTabId="2" />
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
    <TouchableOpacity
      style={[styles.groupCard, styles.newGroupCard]}
      onPress={onPress}
      activeOpacity={0.85}
    >
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

  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    padding: 16,
    marginBottom: 12,
  },

  headerTitle: {
    marginTop: 45,
    fontSize: 21,
    fontWeight: "900",
    textAlign: "center",
  },

  headerIcon: {
    padding: 5,
    width: 24,
  },

  content: {
    padding: 20,
    paddingBottom: 100,
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
})