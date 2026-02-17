// app/profile.tsx ou screens/ProfileScreen.tsx
import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { Menu } from "@/components/Menu";
import { categories } from "@/utils/categories";
import { colors } from "@/styles/colors";
import { MaterialIcons } from "@expo/vector-icons";
import { NavigationProp, useNavigation } from "@react-navigation/native";
import { StackParams } from "@/utils/routesStack";

export default function Profile() {
  const navigation = useNavigation<NavigationProp<StackParams>>();

  const handleNavigateToCriarGrupo = () => {
    navigation.navigate("CriarGrupo")
  }

  return (
    <View style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.title}>Perfil</Text>
        <Text style={styles.subtitle}>Tela de Perfil</Text>
      </View>

      <TouchableOpacity style={styles.addIcon} onPress={handleNavigateToCriarGrupo}>
          <MaterialIcons
              name="add"
              color={colors.cinza[100]}
              size={23} />
      </TouchableOpacity> 
      
      <Menu
        tabs={categories}
        activeTabId="3" // "3" = Perfil
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
  addIcon: {
      alignSelf: "center",
      width: 50,
      height: 50,
      borderRadius: 28,
      backgroundColor: colors.azul[300],
      justifyContent: "center",
      alignItems: "center",
    },
});