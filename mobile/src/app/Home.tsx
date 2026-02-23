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
import { colors } from "@/styles/colors"
import { useNavigation, useFocusEffect } from "@react-navigation/native"
import { StackNavigationProp } from "@react-navigation/stack"
import { Menu } from "@/components/Menu"
import { StackParams } from "@/utils/routesStack"
import { categories } from "@/utils/categories"

import { getEstudanteAtual } from "@/services/estudante"
import { GrupoDetails, listGrupos, getQuantidadeMembrosCached, promisePool } from "@/services/grupo"
import { DisciplinaDTO, listMinhasDisciplinas } from "@/services/disciplinas"
import { authFetch } from "@/services/backendApi"

import { ImageSourcePropType } from "react-native"
import { fetchProfilePhoto } from "../server/estudanteInfo/fetchProfilePhoto"

type HomeNavProp = StackNavigationProp<StackParams, "Home">

type GrupoComQtd = GrupoDetails & { quantidadeMembros: number }

export default function Home() {
  const navigation = useNavigation<HomeNavProp>()

  const [greeting, setGreeting] = useState("")
  const [userName, setUserName] = useState("Carregando...")

  const [grupos, setGrupos] = useState<GrupoComQtd[]>([])
  const [loadingGrupos, setLoadingGrupos] = useState(true)

  const [imagensGrupos, setImagensGrupos] = useState<Record<string, string>>({})
  const imagensGruposRef = useRef<Record<string, string>>({})
  const pendentesRef = useRef<Set<string>>(new Set())

  const [disciplinas, setDisciplinas] = useState<DisciplinaDTO[]>([])
  const [loadingDisciplinas, setLoadingDisciplinas] = useState(true)

  const disciplinaColors = [
    colors.verde,
    colors.laranja,
    colors.vermelho,
    colors.roxo,
    colors.rosa,
    colors.turquesa,
  ]

  const [estudante, setEstudante] = useState<any>(null)
  const [fotoPerfil, setFotoPerfil] = useState<ImageSourcePropType>(
    require("@/assets/default_profile.jpg")
  )
  const [loadingFoto, setLoadingFoto] = useState(true)

  useEffect(() => {
    imagensGruposRef.current = imagensGrupos
  }, [imagensGrupos])

  const getGreeting = () => {
    const now = new Date()

    const hour = Number(
      new Intl.DateTimeFormat("pt-BR", {
        hour: "numeric",
        hour12: false,
        timeZone: "America/Sao_Paulo",
      }).format(now)
    )

    if (hour >= 5 && hour < 12) return "Bom dia,"
    if (hour >= 12 && hour < 18) return "Boa tarde,"
    return "Boa noite,"
  }

  const goToLogin = () => {
    navigation.reset({
      index: 0,
      routes: [{ name: "Login" as keyof StackParams }],
    })
  }

  const loadUserName = async () => {
    try {
      const estudanteAtual = await getEstudanteAtual()

      if (!estudanteAtual) {
        goToLogin()
        return
      }

      setEstudante(estudanteAtual)

      const nome = estudanteAtual.nome?.trim()
      setUserName(nome && nome.length > 0 ? nome : "Usuário")
    } catch (error: any) {
      if (String(error?.message).includes("USUARIO_NAO_LOGADO")) {
        goToLogin()
        return
      }

      console.log("Erro ao buscar estudante:", error)
      setUserName("Usuário")
    }
  }

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

  const loadDisciplinas = useCallback(async () => {
    try {
      setLoadingDisciplinas(true)
      const data = await listMinhasDisciplinas()
      setDisciplinas(data.slice(0, 6))
    } catch (error: any) {
      console.log("Erro ao buscar disciplinas:", error)

      if (String(error?.message).includes("USUARIO_NAO_LOGADO")) {
        goToLogin()
        return
      }

      setDisciplinas([])
    } finally {
      setLoadingDisciplinas(false)
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
            // Se ainda ficar desatualizado, o problema é cache.
            // Aí você pode trocar por uma função "sem cache" ou adicionar "force refresh" no service.
            const qtd = await getQuantidadeMembrosCached(g.id_grupo, true)
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
      setGrupos([])
    } finally {
      setLoadingGrupos(false)
    }
  }, [buscarImagemDoGrupo])

  const resolveGroupImageSource = (grupo: GrupoDetails) => {
    const base64 = imagensGrupos[grupo.id_grupo]
    if (base64) return { uri: base64 }

    if (grupo.foto_perfil && grupo.foto_perfil.startsWith("http")) {
      return { uri: grupo.foto_perfil }
    }

    return require("@/assets/image.png")
  }

  const formatPeriodo = (inicio?: string, fim?: string) => {
    try {
      if (!inicio || !fim) return ""
      const di = new Date(inicio)
      const df = new Date(fim)

      const ini = di.toLocaleDateString("pt-BR", { day: "2-digit", month: "short" })
      const end = df.toLocaleDateString("pt-BR", { day: "2-digit", month: "short" })
      return `${ini} - ${end}`
    } catch {
      return ""
    }
  }

  const grupoDestaque =
    grupos.find((g) => {
      try {
        return new Date(g.data_fim) >= new Date()
      } catch {
        return false
      }
    }) ?? grupos[0]

  useEffect(() => {
    setGreeting(getGreeting())

    const interval = setInterval(() => {
      setGreeting(getGreeting())
    }, 60000)

    return () => clearInterval(interval)
  }, [])

  // ✅ MODIFICAÇÃO: recarrega quando a tela volta a ficar em foco
  useFocusEffect(
    useCallback(() => {
      loadUserName()
      loadGroups()
      loadDisciplinas()
    }, [loadGroups, loadDisciplinas])
  )

  useEffect(() => {
    let alive = true

    async function loadPhoto() {
      try {
        setLoadingFoto(true)

        const uid = estudante?.firebaseUid ?? estudante?.uid ?? estudante?.id_firebase ?? ""
        const img = await fetchProfilePhoto(uid)

        if (alive) setFotoPerfil(img)
      } finally {
        if (alive) setLoadingFoto(false)
      }
    }

    if (estudante) loadPhoto()

    return () => {
      alive = false
    }
  }, [estudante])

  const formatParticipantes = (qtd: number) => `${qtd} ${qtd === 1 ? "participante" : "participantes"}`

  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        {/* HEADER */}
        <View style={styles.header}>
          <View style={styles.userInfo}>
            <View style={styles.avatarWrapper}>
              <Image source={fotoPerfil} style={styles.avatar} />
              {loadingFoto && (
                <View style={styles.avatarLoadingOverlay}>
                  <ActivityIndicator size="small" color="#FFF" />
                </View>
              )}
            </View>
            <View>
              <Text style={styles.greeting}>{greeting}</Text>
              <Text style={styles.name}>{userName}</Text>
            </View>
          </View>
        </View>

        {/*  GROUP CARD GRANDE  */}
        {loadingGrupos ? (
          <View style={[styles.groupCard, { alignItems: "center", justifyContent: "center", padding: 24 }]}>
            <ActivityIndicator size="small" color={colors.azul[300]} />
          </View>
        ) : !grupoDestaque ? (
          <View style={styles.groupCard}>
            <View style={styles.groupContent}>
              <Text style={styles.groupTitle}>Nenhum grupo ainda</Text>
              <Text style={styles.groupSub}>Entre ou crie um grupo para acompanhar o status aqui.</Text>

              <TouchableOpacity
                style={styles.statusButton}
                onPress={() => navigation.navigate("Profile")}
                activeOpacity={0.85}
              >
                <Text style={styles.statusText}>VER GRUPOS</Text>
              </TouchableOpacity>
            </View>

            <Image source={require("@/assets/fazer-check-in.png")} style={styles.groupImage} resizeMode="cover" />
          </View>
        ) : (
          <View style={styles.groupCard}>
            <View style={styles.groupContent}>
              <Text style={styles.groupTitle}>{grupoDestaque.nome}</Text>

              <Text style={styles.groupSub}>
                {formatPeriodo(grupoDestaque.data_inicio, grupoDestaque.data_fim) || "Período não informado"}
                {"\n"}
                {formatParticipantes(grupoDestaque.quantidadeMembros ?? 0)}
              </Text>

              <TouchableOpacity
                style={styles.statusButton}
                onPress={() =>
                  navigation.navigate("StudyGroupScreen", { grupoId: grupoDestaque.id_grupo } as any)
                }
                activeOpacity={0.85}
              >
                <Text style={styles.statusText}>VER STATUS</Text>
              </TouchableOpacity>
            </View>

            <Image source={resolveGroupImageSource(grupoDestaque)} style={styles.groupImage} resizeMode="cover" />
          </View>
        )}

        {/* MEUS DESAFIOS */}
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Meus desafios</Text>
          <TouchableOpacity onPress={() => navigation.navigate("Profile")}>
            <Text style={styles.link}>Ver mais</Text>
          </TouchableOpacity>
        </View>

        {loadingGrupos ? (
          <ActivityIndicator size="small" color={colors.azul[300]} style={{ marginBottom: 16 }} />
        ) : grupos.length === 0 ? (
          <Text style={{ color: colors.preto, marginBottom: 16 }}>
            Você ainda não participa de nenhum grupo.
          </Text>
        ) : (
          <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.challengesScroll}>
            {grupos.map((grupo) => (
              <TouchableOpacity
                key={grupo.id_grupo}
                style={styles.challengeCard}
                activeOpacity={0.85}
                onPress={() => navigation.navigate("StudyGroupScreen", { grupoId: grupo.id_grupo })}
              >
                <Image source={resolveGroupImageSource(grupo)} style={styles.challengeImage} resizeMode="cover" />
                <Text style={styles.challengeTitle} numberOfLines={1}>
                  {grupo.nome}
                </Text>
              </TouchableOpacity>
            ))}
          </ScrollView>
        )}

        {/* DISCIPLINAS */}
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>Minhas disciplinas</Text>
          <TouchableOpacity onPress={() => navigation.navigate("Disciplinas")}>
            <Text style={styles.link}>Ver mais</Text>
          </TouchableOpacity>
        </View>

        {loadingDisciplinas ? (
          <ActivityIndicator size="small" color={colors.azul[300]} style={{ marginBottom: 16 }} />
        ) : disciplinas.length === 0 ? (
          <Text style={{ color: colors.preto, marginBottom: 16 }}>
            Você ainda não tem disciplinas cadastradas
          </Text>
        ) : (
          <View style={styles.chipsRow}>
            {disciplinas.map((d, index) => (
              <Chip
                key={d.id_disciplina}
                label={d.nome}
                color={disciplinaColors[index % disciplinaColors.length]}
              />
            ))}
          </View>
        )}

        <TouchableOpacity style={styles.bigButton} onPress={() => navigation.navigate("Profile")}>
          <Text style={styles.bigButtonText}>VER TODOS OS DESAFIOS</Text>
        </TouchableOpacity>
      </ScrollView>

      <Menu tabs={categories} activeTabId="1" />
    </View>
  )
}

function Chip({ label, color }: { label: string; color: string }) {
  return (
    <View style={[styles.chip, { backgroundColor: color + "22" }]}>
      <Text style={[styles.chipText, { color }]} numberOfLines={1}>
        {label}
      </Text>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.cinza[400],
  },
  content: {
    padding: 20,
    paddingTop: 60,
    paddingBottom: 80,
  },
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 24,
  },
  userInfo: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    flex: 1,
  },
  avatar: {
    width: 48,
    height: 48,
    borderRadius: 24,
  },
  greeting: {
    color: colors.cinza[500],
    fontSize: 14,
    marginBottom: 2,
  },
  name: {
    color: colors.preto,
    fontWeight: "700",
    fontSize: 18,
  },
  groupTitle: {
    fontWeight: "700",
    fontSize: 18,
    color: colors.azul[300],
    marginBottom: 8,
  },
  groupSub: {
    color: colors.preto,
    fontSize: 14,
    lineHeight: 20,
    marginBottom: 12,
  },
  statusButton: {
    backgroundColor: colors.azul[300],
    alignSelf: "flex-start",
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 8,
  },
  statusText: {
    color: "#FFF",
    fontWeight: "700",
    fontSize: 12,
    letterSpacing: 0.5,
  },
  groupCard: {
    flexDirection: "row",
    backgroundColor: colors.azul[100],
    borderRadius: 16,
    borderWidth: 1,
    borderColor: colors.azul[200],
    marginBottom: 24,
    overflow: "hidden",
    minHeight: 160,
  },
  groupContent: {
    flex: 1,
    padding: 20,
  },
  groupImage: {
    width: 140,
    height: "100%",
  },
  sectionHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 16,
  },
  sectionTitle: {
    fontWeight: "700",
    fontSize: 18,
    color: colors.azul[300],
  },
  link: {
    color: colors.azul[300],
    fontSize: 14,
    fontWeight: "600",
  },
  challengesScroll: {
    marginBottom: 32,
  },
  challengeCard: {
    backgroundColor: colors.azul[100],
    borderWidth: 1,
    borderColor: colors.azul[200],
    borderRadius: 16,
    marginRight: 16,
    width: 160,
    overflow: "hidden",
  },
  challengeImage: {
    height: 100,
    width: "100%",
  },
  challengeTitle: {
    padding: 14,
    fontWeight: "600",
    fontSize: 15,
    color: colors.azul[300],
  },
  chipsRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 10,
    marginBottom: 28,
  },
  chip: {
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 20,
  },
  chipText: {
    fontWeight: "700",
    fontSize: 14,
  },
  bigButton: {
    backgroundColor: colors.azul[300],
    padding: 18,
    borderRadius: 12,
    alignItems: "center",
  },
  bigButtonText: {
    color: "#FFF",
    fontWeight: "700",
    fontSize: 15,
    letterSpacing: 0.5,
  },
  avatarWrapper: {
    width: 48,
    height: 48,
    borderRadius: 24,
    overflow: "hidden",
  },
  avatarLoadingOverlay: {
    ...StyleSheet.absoluteFillObject,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "rgba(0,0,0,0.15)",
  },
})