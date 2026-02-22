// app/home.tsx ou screens/HomeScreen.tsx
import { View, Text, StyleSheet } from "react-native"
import { categories } from "@/utils/categories"
import { colors } from "@/styles/colors"
import { Menu } from "@/components/Menu"

export default function GroupHome() {
  return (
    <View style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.title}>GroupHome</Text>
        <Text style={styles.subtitle}>Clique em um grupo para visualizar</Text>
      </View>
      
    <Menu tabs={categories} activeTabId="2" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.cinza[400],
  },
  content: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  title: {
    fontSize: 48,
    fontWeight: "bold",
    color: colors.azul[300],
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 18,
    color: colors.cinza[600],
  },
});