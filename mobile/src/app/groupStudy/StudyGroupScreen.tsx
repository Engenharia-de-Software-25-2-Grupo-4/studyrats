import { GroupOverviewCard } from "@/components/GroupOverviewCard"
import { Menu } from "@/components/Menu"
import { Post } from "@/components/Post"
import { UserItem } from "@/components/UserItem"
import { colors } from "@/styles/colors"
import { categories } from "@/utils/categories"
import { posts } from "@/utils/posts"
import { StackParams } from "@/utils/routesStack"
import { users } from "@/utils/users"
import { Ionicons, MaterialIcons } from "@expo/vector-icons"
import { RouteProp, useNavigation, useRoute } from "@react-navigation/native"
import type { NavigationProp } from "@react-navigation/native"
import { useEffect, useMemo, useRef, useState } from "react"
import { ActivityIndicator, FlatList, StyleSheet, Text, TouchableOpacity, View } from "react-native"
import { getGrupoById, GrupoDetails } from "@/services/grupo"
import { authFetch } from "@/services/backendApi"

enum Tab {
  ESTATISTICAS = 1,
  PARTICIPANTES = 2,
}

type StudyGroupRouteProp = RouteProp<StackParams, "StudyGroupScreen">

export default function StudyGroupScreen() {
  const navigation = useNavigation<NavigationProp<StackParams>>()
  const route = useRoute<StudyGroupRouteProp>()

  const grupoId = route.params?.grupoId

  const [tab, setTab] = useState(Tab.ESTATISTICAS)

  const [grupo, setGrupo] = useState<GrupoDetails | null>(null)
  const [loadingGrupo, setLoadingGrupo] = useState(true)

  const [grupoImagemBase64, setGrupoImagemBase64] = useState<string | null>(null)
  const pendenteImagemRef = useRef(false)

  const blobToBase64 = (blob: Blob): Promise<string> => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.onload = () => resolve(reader.result as string)
      reader.onerror = reject
      reader.readAsDataURL(blob)
    })
  }

  useEffect(() => {
    async function loadGrupo() {
      try {
        if (!grupoId) {
          console.log("StudyGroupScreen: grupoId ausente nos params")
          setGrupo(null)
          return
        }

        setLoadingGrupo(true)
        console.log("Buscando grupo por ID:", grupoId)

        const data = await getGrupoById(grupoId)

        console.log("Grupo carregado:", data)
        setGrupo(data)
      } catch (e: any) {
        console.log("Erro ao carregar grupo:", e?.message ?? e)
        setGrupo(null)
      } finally {
        setLoadingGrupo(false)
      }
    }

    loadGrupo()
  }, [grupoId])

  useEffect(() => {
    async function loadGrupoImagem() {
      try {
        if (!grupoId) {
          setGrupoImagemBase64(null)
          return
        }

        if (pendenteImagemRef.current) return
        pendenteImagemRef.current = true

        const res = await authFetch(`/imagens/grupo/${grupoId}`, { method: "GET" })
        console.log("Imagem grupo status:", res.status)

        if (!res.ok) {
          const text = await res.text().catch(() => "")
          console.log("Falha ao buscar imagem do grupo:", grupoId, "status:", res.status, "body:", text)
          setGrupoImagemBase64(null)
          return
        }

        const blob = await res.blob()
        const base64 = await blobToBase64(blob)
        setGrupoImagemBase64(base64)
      } catch (e: any) {
        console.log("Erro ao buscar imagem do grupo:", e?.message ?? e)
        setGrupoImagemBase64(null)
      } finally {
        pendenteImagemRef.current = false
      }
    }

    loadGrupoImagem()
  }, [grupoId])

  const post = posts[0]

  const sortedUsers = useMemo(() => [...users].sort((a, b) => b.daysActive - a.daysActive), [])
  const topThree = useMemo(() => sortedUsers.slice(0, 3), [sortedUsers])

  const ITEMS_PER_LOAD = 5
  const [visibleCount, setVisibleCount] = useState(ITEMS_PER_LOAD)

  const loadMore = () => {
    if (visibleCount >= users.length) return
    setVisibleCount((prev) => prev + ITEMS_PER_LOAD)
  }

  const handleNavigateToHome = () => {
    navigation.navigate("Home")
  }

  const handleNavigateToFeed = () => {
    navigation.navigate("Feed")
  }

  const handleNavigateCheckIn = () => {
    console.log("Criar sessão do grupo:", grupoId)
    navigation.navigate("CriarSessao", { grupoId: grupoId })
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={handleNavigateToHome}>
          <Ionicons name="arrow-back" size={24} />
        </TouchableOpacity>

        <Text style={styles.headerTitle}>{grupo?.nome ?? "Grupo de Estudos"}</Text>

        <TouchableOpacity style={styles.headerIcon}>
          <MaterialIcons name="notifications-none" color={colors.azul[300]} size={23} />
        </TouchableOpacity>
      </View>

      <View style={styles.checkinCard}>
        {loadingGrupo ? (
          <ActivityIndicator size="small" color={colors.azul[300]} />
        ) : (
            <GroupOverviewCard
              grupo={grupo}
              grupoImagemBase64={grupoImagemBase64}
              onCheckIn={handleNavigateCheckIn}
            />
        )}
      </View>

      {grupo?.descricao ? (
        <View style={styles.groupMeta}>
          <Text style={styles.groupDesc} numberOfLines={2}>
          </Text>
        </View>
      ) : null}

      {tab === Tab.ESTATISTICAS && (
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Acompanhe as publicações recentes</Text>
          <TouchableOpacity onPress={handleNavigateToFeed}>
            <Post title={post.title} user={post.user} subject={post.subject} image={post.image} />
          </TouchableOpacity>
        </View>
      )}

      <View style={styles.tabs}>
        <TouchableOpacity onPress={() => setTab(Tab.ESTATISTICAS)}>
          <Text style={[styles.tab, tab === Tab.ESTATISTICAS && styles.activeTab]}>ESTATÍSTICAS</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={() => setTab(Tab.PARTICIPANTES)}>
          <Text style={[styles.tab, tab === Tab.PARTICIPANTES && styles.activeTab]}>PARTICIPANTES</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.content}>
        {tab === Tab.ESTATISTICAS ? (
          <FlatList
            data={topThree}
            keyExtractor={(item) => item.id}
            renderItem={({ item, index }) => (
              <UserItem user={item} showMedal medal={index === 0 ? "gold" : index === 1 ? "silver" : "bronze"} mode="stats" />
            )}
            contentContainerStyle={{ padding: 20, paddingTop: 8 }}
          />
        ) : (
          <FlatList
            data={users.slice(0, visibleCount)}
            keyExtractor={(item) => item.id}
            renderItem={({ item }) => <UserItem user={item} mode="participants" />}
            contentContainerStyle={{ padding: 20, paddingTop: 8 }}
            onEndReached={loadMore}
            onEndReachedThreshold={0.5}
          />
        )}
      </View>

      <Menu tabs={categories} activeTabId="2" />
    </View>
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
    textAlign: "left",
  },
  headerIcon: {
    borderColor: colors.cinza[600],
    borderRadius: 22,
    padding: 5,
    borderWidth: 0.5,
  },
  checkinCard: {
    paddingHorizontal: 16,
    marginLeft: 10,
    marginRight: 10,
  },
  groupMeta: {
    marginTop: 10,
    marginHorizontal: 20,
  },
  groupDesc: {
    color: colors.cinza[600],
    fontSize: 12,
  },
  card: {
    marginLeft: 20,
    marginRight: 20,
    marginBottom: 5,
    marginTop: 20,
  },
  cardTitle: {
    marginBottom: 12,
    fontSize: 17,
    fontWeight: "900",
  },
  content: {
    flex: 1,
  },
  tabs: {
    flexDirection: "row",
    justifyContent: "space-around",
    borderBottomWidth: 1,
    borderColor: colors.cinza[500],
    marginTop: 20,
  },
  tab: {
    paddingBottom: 8,
    fontWeight: "600",
    color: colors.cinza[500],
  },
  activeTab: {
    color: colors.azul[400],
    borderBottomWidth: 1.5,
    borderColor: colors.azul[400],
  },
})