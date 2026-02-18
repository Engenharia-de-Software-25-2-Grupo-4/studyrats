import { Image, StyleSheet, Text, TouchableOpacity, View } from "react-native";

import { colors } from "@/styles/colors";
import { StackParams } from "@/utils/routesStack";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { NavigationProp, useNavigation } from "@react-navigation/native";

export function GroupOverviewCard() {
    const navigation = useNavigation<NavigationProp<StackParams>>();


    const clearOnboarding = async () => {
        try {
            await AsyncStorage.removeItem("@viewedOnboarding");
            navigation.navigate("Index");
        } catch (error) {
            console.log("Error @clearOnboarding: ", error);
        }
    };
    const handleNavigateToCheckIn = () =>{
        navigation.navigate("CriarSessao")
    }
    return (
        <View style={styles.container}>
            <View style={styles.contentLeft}>
                <Text style={styles.title}>
                    Faça seu check-in diário e garanta o 1º lugar
                </Text>

                <View>
                    <Text style={styles.description}>Início: 02 de dezembro</Text>
                    <Text style={styles.description}>Fim: 26 de janeiro</Text>
                </View>

                <TouchableOpacity style={styles.buttonStyle} onPress={handleNavigateToCheckIn}>
                    <Text style={styles.buttonText}>FAZER CHECK-IN</Text>
                </TouchableOpacity>
            </View>

            <Image source={require("@/assets/fazer-check-in.png")} style={styles.image}/> 
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flexDirection: "row",
        borderRadius: 16,
        width: '100%',
        columnGap: 5
    },
    contentLeft: {
        flex: 1,
        justifyContent: "space-between",
        paddingVertical: 4,
    },
    title: {
        fontWeight: '600',
        fontSize: 14,
        color: colors.azul[400],
        marginBottom: 20
    },
    description: {
        fontWeight: '300',
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
        alignSelf: 'flex-start',
        marginTop: 20
    },
    buttonText: {
        color: '#FFF',
        fontSize: 12,
        fontWeight: 'bold', 
    }
})