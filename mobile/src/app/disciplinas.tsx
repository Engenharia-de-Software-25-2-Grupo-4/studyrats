import { StyleSheet, Text, View, FlatList, TouchableOpacity, ActivityIndicator } from "react-native"
import { Ionicons } from "@expo/vector-icons"
import { colors } from "@/styles/colors"
import { useNavigation } from "@react-navigation/native"
import { StackNavigationProp } from "@react-navigation/stack"
import { StackParams } from "@/utils/routesStack"
import { useEffect, useState } from "react"
import { listMinhasDisciplinas } from "@/services/disciplinas"

type NavigationProp = StackNavigationProp<StackParams, "Disciplinas">

interface Discipline {
  id: string
  name: string
}

export default function Disciplinas() {
  const navigation = useNavigation<NavigationProp>()

  const [disciplines, setDisciplines] = useState<Discipline[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let alive = true

    async function load() {
      try {
        setLoading(true)
        const data = await listMinhasDisciplinas()

        const mapped: Discipline[] = (data ?? []).map((d, index) => ({
          id: String(index + 1),
          name: d.nome,
        }))

        if (alive) setDisciplines(mapped)
      } catch (e) {
        if (alive) setDisciplines([])
      } finally {
        if (alive) setLoading(false)
      }
    }

    load()

    return () => {
      alive = false
    }
  }, [])

  return (
    <View style={styles.container}>
      {/* HEADER */}
      <View style={styles.header}>
        <TouchableOpacity style={styles.backButton} onPress={() => navigation.goBack()}>
          <Ionicons name="arrow-back" size={24} color={colors.azul[300]} />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Disciplinas</Text>
        <View style={styles.placeholder} />
      </View>

      {/* LISTA DE DISCIPLINAS */}
      {loading ? (
        <ActivityIndicator size="small" color={colors.azul[300]} style={{ marginTop: 16 }} />
      ) : disciplines.length === 0 ? (
        <Text style={{ color: colors.preto, marginTop: 12, paddingHorizontal: 20 }}>
          Você ainda não tem disciplinas cadastradas
        </Text>
      ) : (
        <FlatList
          data={disciplines}
          renderItem={({ item }) => <DisciplineCard discipline={item} />}
          keyExtractor={(item) => item.id}
          contentContainerStyle={styles.listContainer}
          showsVerticalScrollIndicator={false}
        />
      )}
    </View>
  )
}

function DisciplineCard({ discipline }: { discipline: Discipline }) {
  return (
    <TouchableOpacity style={styles.disciplineCard} activeOpacity={0.7}>
      <Ionicons name="book-outline" size={24} color="#FFFFFF" />
      <Text style={styles.disciplineName}>{discipline.name}</Text>
    </TouchableOpacity>
  )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: colors.cinza[400]
    },

    header: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        paddingHorizontal: 20,
        paddingTop: 60,
        paddingBottom: 20,
        backgroundColor: colors.cinza[400]
    },

    backButton: {
        padding: 4
    },

    headerTitle: {
        fontSize: 20,
        fontWeight: "700",
        color: colors.azul[300]
    },

    placeholder: {
        width: 32
    },

    listContainer: {
        padding: 20,
        paddingTop: 8
    },

    disciplineCard: {
        flexDirection: "row",
        alignItems: "center",
        backgroundColor: colors.azul[300],
        paddingVertical: 20,
        paddingHorizontal: 20,
        borderRadius: 12,
        marginBottom: 16
    },

    disciplineName: {
        fontSize: 16,
        fontWeight: "600",
        color: "#FFFFFF",
        marginLeft: 16,
        flex: 1
    }
})