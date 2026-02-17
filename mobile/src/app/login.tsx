import AsyncStorage from "@react-native-async-storage/async-storage";
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from "@react-navigation/stack";
import { useState } from "react";
import { Alert, StyleSheet, Text, TouchableOpacity, View } from "react-native";

import { Button } from "@/components/Button";
import { Input } from "@/components/Input";

import { colors } from "@/styles/colors";
import { studentServer } from "@/server/student-server";
import { StackParams } from "App";

export default function Login(){
    // DATA
    const [name, setName] = useState("")
    const [email, setEmail] = useState("")

    const navigation =  useNavigation<StackNavigationProp<StackParams>>()

    const clearOnboarding = async () => {
        try {
            console.log("Rever Onboarding")
            await AsyncStorage.removeItem('@viewedOnboarding')
        } catch (error) {
            console.log('Error @clearOnboarding: ', error)
        }
    }

    function handleLogin(){
        if(name.trim().length === 0 || email.trim().length === 0){
            return Alert.alert("Criar conta", "Preencha todos os dados")
        }

        Alert.alert("Novo estudante", "Confirmar", [
            {
                text: "Não",
                style: "cancel"
            },
            {
                text: "Sim",
                onPress: createStudent,
            }
        ])
    }

    function redirectHome(){
        navigation.replace("Home")
    }

    async function createStudent() {
        try {
            console.log("Criando estudante...")
            const newStudent = await studentServer.create({
                name,
                email
            })

            Alert.alert("Novo estudante", "Estudante adicionado com sucesso!", [
                {
                    text: "Ok",
                    onPress: redirectHome
                }
            ])
            
        } catch (error) {
            console.log(error)
        }
    }

    return (
        <View style={styles.container}>
            <View style={styles.header}>
                <Text style={styles.title}>Login</Text>
            </View>
            <View style={styles.form}>
                <Text style={styles.label}>Nome</Text>
                <Input
                    placeholder="Nome"
                    autoCorrect={false}
                    onChangeText={setName}
                    value={name}
                />
                <Text style={styles.label}>E-mail</Text>
                <Input
                    placeholder="URL"
                    autoCorrect={false}
                    autoCapitalize="none"
                    onChangeText={setEmail}
                    value={email}
                />
                <Button onPress={handleLogin} title="Adicionar"/>
                <TouchableOpacity onPress={clearOnboarding}>
                    <Text>Clear Onboarding</Text>
                </TouchableOpacity>
            </View>
        </View>
    )
}

export const styles = StyleSheet.create({
    container: {
        justifyContent: "center",
        marginTop: 90,
        backgroundColor: colors.cinza[200]
    },
    header: {
        flexDirection: "row",
        justifyContent: "space-between",
        paddingHorizontal: 24,
        marginBottom: 24
    },
    title: {
        color: colors.azul[300],
        fontSize: 24,
        fontWeight: "600"
    },
    label: {
        color: colors.azul[300],
        fontSize: 14,
        paddingHorizontal: 24
    },
    form: {
        padding: 24,
        gap: 16
    }
})