import { Image, StyleSheet, Text, useWindowDimensions, View } from "react-native";

import { colors } from "@/styles/colors";
import { CardContent } from "@/utils/pages";

type OnboardingItemProps = {
    item: CardContent;
}

export function OnboadingItem({ item }: OnboardingItemProps) {
    const { width } = useWindowDimensions()

    return (
        <View style={[ { width }]}>
            <Image source={item.image} style={[styles.image, { width, resizeMode: 'contain'}]}/>
            
            <View style={styles.card}>
                <Text style={styles.title}>{item.title}</Text>
                <Text style={styles.description}>{item.description}</Text>
            </View>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    image: {
        flex: 0.7,
        alignItems: 'center',
    },
    card: {
        flex: 0.4,
        padding: 30
    },
    title: {
        fontWeight: '800',
        fontSize: 28,
        marginBottom: 10,
        color: colors.cinza[200],

    },
    description: {
        fontWeight: '300',
        color: colors.cinza[200],
        fontSize: 14
    },
})