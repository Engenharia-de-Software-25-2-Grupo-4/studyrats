import { colors } from "@/styles/colors";
import React from "react";
import { Image, StyleSheet, Text, View } from "react-native";

type Props = {
    title: string
    user: string
    subject: string
}

export function Post({ title, user, subject }: Props) {
    return (
        <View style={styles.container}>

            <Image source={require("@/assets/fazer-check-in.png")} style={styles.image}/> 
            
            <View style={styles.contentLeft}>
                <Text style={styles.title}>
                    {title}
                </Text>

                <View style={styles.descricoes}> 
                    <Text style={styles.description}>
                        Por: <Text style={styles.destaque}>{user}</Text>
                    </Text> 
                    <Text style={styles.description}>
                        Disciplina: <Text style={styles.destaque}>{subject}</Text>
                    </Text>
                </View>
            </View>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flexDirection: "row",
        borderRadius: 16,
        width: '100%',
        columnGap: 5,
        backgroundColor: colors.azul[100],
        borderWidth: 1.5,
        borderColor: colors.azul[200],
        alignItems: "stretch",
        overflow: "hidden",
    },
    contentLeft: {
        flex: 1,
        justifyContent: "space-between",
        paddingVertical: 4,
        marginLeft: 10,
        marginBottom: 10
    },
    title: {
        fontWeight: '600',
        fontSize: 18,
        color: colors.azul[400],
        marginBottom: 20,
        // marginLeft: 10,
        marginTop: 10
    },
    description: {
        fontWeight: '300',
        color: colors.azul[400],
        fontSize: 12,
    },
    image: {
        width: 110,
        height: "100%",
        resizeMode: "cover",
        borderTopLeftRadius: 12,
        borderBottomLeftRadius: 12
        
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
    },
    destaque: {
        fontWeight: '400',
    },
    descricoes: {
        //marginBottom: 10
    },
})

