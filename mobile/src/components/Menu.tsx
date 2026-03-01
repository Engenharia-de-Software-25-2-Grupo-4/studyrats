import { colors } from "@/styles/colors";
import { StackParams } from "@/utils/routesStack";
import { MaterialIcons } from "@expo/vector-icons";
import type { NavigationProp } from "@react-navigation/native";
import { useNavigation } from "@react-navigation/native";
import { Alert, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { clearSession } from "../services/authStorage";
import AsyncStorage from "@react-native-async-storage/async-storage";

type TabItem = {
  id: string;
  name: string;
  icon: keyof typeof MaterialIcons.glyphMap;
  route?: string;
};

type Props = {
  tabs: TabItem[];
  activeTabId: string;
};

export function Menu({ tabs, activeTabId }: Props) {
  const navigation = useNavigation<NavigationProp<StackParams>>();

  // Função para limpar o onboarding
  const clearOnboarding = async () => {
    try {
      console.log("Rever onboarding");
      await AsyncStorage.removeItem('@viewedOnboarding');
    } catch (error) {
      console.log('Error @clearOnboarding: ', error);
    }
  };

  const handleLogout = () => {
    Alert.alert("Sair", "Deseja realmente sair?", [
      { text: "Cancelar", style: "cancel" },
      {
        text: "Sair",
        style: "destructive",
        onPress: async () => {
          try {
            // await clearSession();
            // await clearOnboarding();
            await Promise.all([
              clearSession(),
              clearOnboarding()
            ]);
            navigation.reset({
              index: 0,
              routes: [{ name: "Index" }], 
            });
          } catch (e) {
            Alert.alert("Erro", "Não foi possível sair. Tente novamente.");
          }
        },
      },
    ]);
  };

  const handleTabPress = (tab: TabItem) => {
    switch(tab.id) {
      case "1": // Home
        navigation.navigate("Home");
        break;
      case "2": // Grupos de estudo
        // navigation.navigate("StudyGroupScreen");
        navigation.navigate("GroupHome");
        break;
      case "3": // Perfil
        navigation.navigate("Profile");
        break;
      case "4": // Sair
        handleLogout();
        break;
      default:
        if (tab.route) {
          navigation.navigate("Home")
        }
    }
  };


  return (
    <View style={styles.container}>
      {tabs.map((tab) => {
        const isActive = tab.id === activeTabId;
        
        return (
          <TouchableOpacity
            key={tab.id}
            style={styles.tab}
            onPress={() => handleTabPress(tab)}
            activeOpacity={0.7}
          >
            <MaterialIcons
              name={tab.icon}
              size={24}
              color={isActive ? colors.cinza[100] : colors.cinza[500]}
            />
            <Text
              style={[
                styles.label,
                isActive && styles.activeLabel,
              ]}
            >
              {tab.name}
            </Text>
          </TouchableOpacity>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    justifyContent: "space-around",
    alignItems: "center",
    height: 70,
    backgroundColor: colors.azul[300],
    paddingHorizontal: 20,
  },

  tab: {
    alignItems: "center",
    justifyContent: "center",
    flex: 1,
    paddingVertical: 8,
  },

  label: {
    fontSize: 12,
    color: colors.cinza[500],
    marginTop: 4,
    fontWeight: "500",
  },

  activeLabel: {
    color: colors.cinza[100],
    fontWeight: "700",
  },
});