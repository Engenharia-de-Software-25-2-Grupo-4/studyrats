import { View, Text, StyleSheet } from "react-native";
import { Menu } from "@/components/Menu";
import { categories } from "@/utils/categories";
import { colors } from "@/styles/colors";

export default function Home() {
  return (
    <View style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.title}>Início</Text>
        <Text style={styles.subtitle}>Tela Inicial</Text>
      </View>
      
      <Menu
        tabs={categories}
        activeTabId="1"
      />
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