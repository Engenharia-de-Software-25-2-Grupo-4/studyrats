import Onboarding from "@/components/Onboarding";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useEffect, useState } from "react";
import { ActivityIndicator, StyleSheet, useWindowDimensions, View } from "react-native";
import Login from "./login";
import StudyGroupScreen from "./groupStudy/StudyGroupScreen";

const Loading = () => {
    return (
        <View>
            <ActivityIndicator size="large"/>
        </View>
    )
}

export default function Index(){
    const { width } = useWindowDimensions()

    const [loading, setLoading] = useState(true);
    const [viewedOnboarding, setViewedOnboarding] = useState(false)

    const checkOnboarding = async () => {
        try {
            const value = await AsyncStorage.getItem('@viewedOnboarding')

            if (value !== null){
                setViewedOnboarding(true)
            }
        } catch (error) {
            console.log('Error @checkOnboarding: ', error)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        checkOnboarding()
    }, [])

    return (
        <View style={[styles.container, { width }]}>
            {loading ? <Loading/> : viewedOnboarding ? <StudyGroupScreen/> : <Onboarding/>} 
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    }
})