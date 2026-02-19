import Onboarding from "@/components/Onboarding";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useEffect, useState } from "react";
import { ActivityIndicator, StyleSheet, useWindowDimensions, View } from "react-native";
import FeedScreen from "./groupStudy/FeedScreen";
import StudyGroupScreen from "./groupStudy/StudyGroupScreen";
import CriarGrupo from "./grupo/criar_grupo";
import GrupoCriado from "./grupo/grupo_criado";

const Loading = () => {
    return (
        <View>
            <ActivityIndicator size="large"/>
        </View>
    )
}

import { useFocusEffect } from "@react-navigation/native";
import { useCallback } from "react";

export default function Index() {
    const { width } = useWindowDimensions();
    const [loading, setLoading] = useState(true);
    const [viewedOnboarding, setViewedOnboarding] = useState(false);

    const checkOnboarding = async () => {

        try {
            const value = await AsyncStorage.getItem("@viewedOnboarding");
            setViewedOnboarding(value !== null);
        } catch (error) {
            console.log("Error @checkOnboarding: ", error);
        } finally {
            setLoading(false);
        }
    }

    useFocusEffect(
        useCallback(() => {
        checkOnboarding();
        }, [])
    )

    return (
        <View style={[styles.container, { width }]}>
        {loading ? <Loading /> : viewedOnboarding ? <StudyGroupScreen /> : <Onboarding />}
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    }
})