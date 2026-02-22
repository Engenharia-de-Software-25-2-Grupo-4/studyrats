import { Image, StyleSheet, Text, TouchableOpacity, View } from "react-native"
import { colors } from "@/styles/colors"
import { StackParams } from "@/utils/routesStack"
import { NavigationProp, useNavigation } from "@react-navigation/native"
import type { GrupoDetails } from "@/services/grupo"

type Props = {
  grupo: GrupoDetails | null
  grupoImagemBase64?: string | null
}

function formatDatePtBR(iso?: string) {
  if (!iso) return "-"
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return iso 
  return d.toLocaleDateString("pt-BR", { day: "2-digit", month: "long" })
}

export function GroupOverviewCard({ grupo, grupoImagemBase64 }: Props) {
  const navigation = useNavigation<NavigationProp<StackParams>>()

  const handleNavigateToCheckIn = () => {
    navigation.navigate("CriarSessao")
  }

  const inicio = formatDatePtBR(grupo?.data_inicio)
  const fim = formatDatePtBR(grupo?.data_fim)

  return (
    <View style={styles.container}>
      <View style={styles.contentLeft}>
        <Text style={styles.title}>
          {grupo?.nome ? `Faça seu check-in diário no grupo ${grupo.nome}` : "Faça seu check-in diário e garanta o 1º lugar"}
        </Text>

        <View>
          <Text style={styles.description}>Início: {inicio}</Text>
          <Text style={styles.description}>Fim: {fim}</Text>
        </View>

        <TouchableOpacity style={styles.buttonStyle} onPress={handleNavigateToCheckIn} disabled={!grupo}>
          <Text style={styles.buttonText}>FAZER CHECK-IN</Text>
        </TouchableOpacity>
      </View>

      {grupoImagemBase64 ? (
        <Image source={{ uri: grupoImagemBase64 }} style={styles.image} />
      ) : (
        <Image source={require("@/assets/fazer-check-in.png")} style={styles.image} />
      )}
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    borderRadius: 16,
    width: "100%",
    columnGap: 5,
  },
  contentLeft: {
    flex: 1,
    justifyContent: "space-between",
    paddingVertical: 4,
  },
  title: {
    fontWeight: "600",
    fontSize: 14,
    color: colors.azul[400],
    marginBottom: 20,
  },
  description: {
    fontWeight: "300",
    color: colors.azul[400],
    fontSize: 12,
  },
  image: {
    width: 150,
    height: 150,
    borderRadius: 12,
  },
  buttonStyle: {
    backgroundColor: "#01415B",
    paddingVertical: 10,
    paddingHorizontal: 14,
    borderRadius: 8,
    alignSelf: "flex-start",
    marginTop: 20,
  },
  buttonText: {
    color: "#FFF",
    fontSize: 12,
    fontWeight: "bold",
  },
})