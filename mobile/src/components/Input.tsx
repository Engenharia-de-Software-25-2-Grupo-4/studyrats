import { TextInput, TextInputProps, StyleSheet } from "react-native"

import { colors } from "@/styles/colors"

export function Input({ ...rest }: TextInputProps){
    return (
        <TextInput
            style={styles.container}
            placeholderTextColor={colors.cinza[100]}
            { ...rest }/>
    )
}

export const styles = StyleSheet.create({
    container: {
        height: 52,
        width: "100%",
        backgroundColor: colors.cinza[100],
        borderRadius: 8,
        borderWidth: 1,
        borderColor: colors.cinza[100],
        padding: 10,
        color: colors.azul[300],
        fontSize: 16
    }
})