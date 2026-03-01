import { auth } from "@/firebaseConfig";
import type { StackParams } from "@/utils/routesStack";
import AsyncStorage from "@react-native-async-storage/async-storage";
import type { NavigationProp } from "@react-navigation/native";
import { useNavigation } from "@react-navigation/native";
import { useEffect } from "react";
import { ActivityIndicator, StyleSheet, View } from "react-native";

import { colors } from "@/styles/colors";
import { StackActions } from "@react-navigation/native";

export default function Index() {
    const navigation = useNavigation<NavigationProp<StackParams>>();

    useEffect(() => {
        checkInitialRoute();
    }, []);

    const checkInitialRoute = async () => {
        try {
            const viewedOnboarding = await AsyncStorage.getItem("@viewedOnboarding");
            
            if (!viewedOnboarding) {
                navigation.dispatch(
                    StackActions.replace("Onboarding")
                )
                return;
            }

            const user = auth.currentUser;
            if (user) {
                navigation.dispatch(
                    StackActions.replace("Home")
                );
            } else {
                navigation.dispatch(
                    StackActions.replace("Login")
                );
            }
        } catch (error) {
            console.log("Error @checkInitialRoute: ", error);
            navigation.dispatch(
                StackActions.replace("Login")
            );
        }
    };
    return (
        <View style={styles.container}>
            <ActivityIndicator size="large" color={colors.azul[300]} />
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: colors.cinza[400],
    },
});