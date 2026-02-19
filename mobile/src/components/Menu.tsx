import { colors } from "@/styles/colors";
import { StackParams } from "@/utils/routesStack";
import { MaterialIcons } from "@expo/vector-icons";
import type { NavigationProp } from "@react-navigation/native";
import { useNavigation } from "@react-navigation/native";
import { Alert, StyleSheet, Text, TouchableOpacity, View } from "react-native";

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

  const handleLogout = () => {
    Alert.alert(
      "Sair",
      "Deseja realmente sair?",
      [
        { text: "Cancelar", style: "cancel" },
        { 
          text: "Sair", 
          onPress: () => {
            console.log("Fazendo logout...");
            navigation.reset({
              index: 0,
              routes: [{ name: "StudyGroupScreen" }],
            });
          }
        }
      ]
    );
  };

  const handleTabPress = (tab: TabItem) => {
    switch(tab.id) {
      case "1": // Home
        navigation.navigate("Home");
        break;
      case "2": // Grupos de estudo
        navigation.navigate("StudyGroupScreen");
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