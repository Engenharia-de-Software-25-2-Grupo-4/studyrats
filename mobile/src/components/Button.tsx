import { StyleSheet, Text, TouchableOpacity, TouchableOpacityProps } from "react-native"
import { colors } from "@/styles/colors"

type Props = TouchableOpacityProps & {
    title: string
}

export function Button({ title, ...rest }: Props){
    return (
        <TouchableOpacity style={styles.container} activeOpacity={0.7} {...rest}>
            <Text style={styles.title}>{title}</Text>
        </TouchableOpacity>
    )
}

export const styles = StyleSheet.create({
    container: {
        height: 52,
        width: "100%",
        backgroundColor: colors.azul[300],
        borderRadius: 8,
        justifyContent: "center",
        alignItems: "center"
    },
    title: {
        color: colors.cinza[100],
        fontSize: 16,
        fontWeight: "600"
    }
})