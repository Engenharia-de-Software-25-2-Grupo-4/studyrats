import { StyleSheet, Text, TouchableOpacity, View } from "react-native";

export default function Index(){

    return (
        <View style={styles.container}>
            <Text>Index</Text>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center'
    }
})