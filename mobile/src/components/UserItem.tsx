import { colors } from "@/styles/colors"
import { User } from "@/utils/users"
import { Image, StyleSheet, Text, View } from "react-native"

type Props = {
  user: User
  showMedal?: boolean
  medal?: "gold" | "silver" | "bronze"
  mode?: "stats" | "participants"
}

export function UserItem({ user, showMedal, medal, mode = "stats" }: Props) {
    const medalImages = {
      gold: require("@/assets/medalha-de-ouro.png"),
      silver: require("@/assets/medalha-de-prata.png"),
      bronze: require("@/assets/medalha-de-bronze.png"),
    } as const;

    const initial = user?.name ? user.name.charAt(0).toUpperCase() : "?"

    return (
      <View style={styles.container}>
        {user.avatar ? (
          <Image
            source={user.avatar}
            style={styles.avatar}
          />
        ) : (
          <View style={styles.initialAvatar}>
            <Text style={styles.initialText}>{initial}</Text>
          </View>
        )}


        <View style={styles.info}>
          <Text style={styles.name}>{user?.name || "Usuário"}</Text>
          {mode === "stats" && ( 
            <Text style={styles.subtitle}>
              {user?.daysActive || 0} {user?.daysActive === 1 ? 'check-in' : 'check-ins'}
            </Text>
          )}
        </View>

        {showMedal && medal && (
          <Image
            source={medalImages[medal]}
            style={styles.medal}
          />
        )}
      </View>
  )
}

const styles = StyleSheet.create({
    container: {
        flexDirection: "row",
        alignItems: "center",
        gap: 12,
        paddingVertical: 9,
    },
    avatar: {
        width: 44,
        height: 44,
        borderRadius: 22,
    },
    info: {
        flex: 1,
    },
    name: {
        fontSize: 16,
        fontWeight: "600",
        color: colors.azul[400],
    },
    subtitle: {
        fontSize: 13,
        color: colors.cinza[500],
    },
    medal: {
        width: 32,
        height: 32,

    },
    initialAvatar: {
        width: 44,
        height: 44,
        borderRadius: 22,
        backgroundColor: colors.cinza[200],
        alignItems: "center",
        justifyContent: "center",
    },
    initialText: {
        color: colors.azul[400],
        fontSize: 18,
        fontWeight: "700",
    },
})

