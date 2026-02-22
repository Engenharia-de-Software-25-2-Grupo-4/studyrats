import { View, Text, StyleSheet, TouchableOpacity, TextInput, ScrollView, Image, Alert, KeyboardAvoidingView, Platform } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import * as ImagePicker from "expo-image-picker";
import { useState, useEffect } from "react";
import DateTimePicker from "@react-native-community/datetimepicker";
import { useNavigation, useRoute, RouteProp } from '@react-navigation/native';
import type { NavigationProp } from '@react-navigation/native';
import { StackParams } from '@/utils/routesStack';
import { Menu } from "@/components/Menu";
import { categories } from "@/utils/categories";
import { colors } from "@/styles/colors";
import { createGrupo } from "@/services/grupo";
import { updateGrupo } from "@/services/grupo";
import { uploadImagem } from "@/services/grupo";

export default function CriarGrupo() {
    
    const navigation = useNavigation<NavigationProp<StackParams>>();
    const route = useRoute();
    
    const grupo = (route.params as any)?.grupo;
    
    const [imagem, setImage] = useState<string | null>(null);
    const [nomeDesafio, setnomeDesafio] = useState("");
    const [descricao, setDescricao] = useState("");
    const [regras, setRegras] = useState("");
    const [dataInicio, setDataInicio] = useState(new Date());
    const [dataFinal, setDataFinal] = useState(new Date());
    const [mostrarPicker, setMostrarPicker] = useState(false);

    useEffect(() => {
        if (grupo) {
            setnomeDesafio(grupo.nomeDesafio);
            setDescricao(grupo.descricao);
            setRegras(grupo.regras);
            setDataInicio(new Date(grupo.dataInicio));
            setDataFinal(new Date(grupo.dataFinal));
            setImage(grupo.imagem);
        }
    }, [grupo]);

    const pickImage = async () => {
        const result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ["images"],
            allowsEditing: true,
            quality: 1,
        });

        if (!result.canceled) {
            setImage(result.assets[0].uri);
        }
    };

    const handleSubmit = async () => {
        if (!nomeDesafio.trim()) {
            Alert.alert("Erro", "O nome do desafio é obrigatório.");
            return;
        }

        if (!descricao.trim()) {
            Alert.alert("Erro", "A descrição é obrigatória.");
            return;
        }

        if (!regras.trim()) {
            Alert.alert("Erro", "As regras são obrigatórias.");
            return;
        }
        if (!imagem) {
            Alert.alert("Erro", "A imagem é obrigatória.");
            return;
        }

        try {
            console.log("1. iniciando submit");
            console.log("2. grupo:", grupo);
            if (grupo) {
                console.log("3. editando grupo");
                await updateGrupo(grupo.id, {
                    nome: nomeDesafio,
                    descricao,
                    foto_perfil: "",
                    regras,
                    data_inicio: dataInicio.toISOString(),
                    data_fim: dataFinal.toISOString(),
                },);
                if (imagem) await uploadImagem(grupo.id_grupo, imagem); 

                Alert.alert("Sucesso", "Desafio atualizado!", [
                    { text: "OK", onPress: () => navigation.goBack() }
                ]);
            } else {
                console.log("3. criando sessao");
                const novoGrupo = await createGrupo({
                    nome: nomeDesafio,
                    descricao,
                    foto_perfil: "",
                    regras,
                    data_inicio: dataInicio.toISOString(),
                    data_fim: dataFinal.toISOString(),
                });
                console.log("retorno createSessao:", novoGrupo); 
                if (imagem) await uploadImagem(novoGrupo.id_grupo, imagem); 
                navigation.navigate("GrupoCriado", { desafio: novoGrupo });
            }

        } catch (error: any) {
            Alert.alert("Erro", error.message);
        }
    };

    const handleGoBack = () => {
        navigation.goBack();
    };


    return (
        <View style={styles.container}>

        {/* HEADER */}
        <View style={styles.header}>
            <TouchableOpacity onPress={handleGoBack}>
                <Ionicons name="arrow-back" size={24} />
            </TouchableOpacity>

           <Text style={styles.headerTitle}>{grupo ? "Editar desafio" : "Novo desafio"}</Text>
           <TouchableOpacity onPress={handleSubmit}>
                <Text style={styles.publish}> {grupo ? "Salvar" : "Criar"}</Text>
            </TouchableOpacity>
            
        </View>

       <KeyboardAvoidingView
            style={{ flex: 1 }}
            behavior={Platform.OS === "ios" ? "padding" : undefined}
            keyboardVerticalOffset={Platform.OS === "ios" ? 0 : 0}
        >
            <ScrollView 
                contentContainerStyle={styles.content}
                keyboardShouldPersistTaps="handled"
                showsVerticalScrollIndicator={false}
            >
            
                {/* FOTO */}
                <TouchableOpacity style={styles.photoBox} onPress={pickImage}>
                {imagem ? (
                <Image source={{ uri: imagem }} style={styles.photo} />
                ) : (
                <>
                    <Ionicons name="add" size={32} color="#1E6F7C" />
                    <Text style={styles.photoText}>Adicione aqui a foto do banner</Text>
                </>
                )}
                </TouchableOpacity>
                
                {/* FORMULÁRIO */}
                <Text style={styles.sectionTitle}>Informações</Text>

                <TextInput
                    placeholder="Nome do desafio"
                    placeholderTextColor="#2b2c2c"
                    style={styles.input}
                    value={nomeDesafio}
                    onChangeText={setnomeDesafio}
                />

                <TextInput
                    placeholder="Descrição"
                    placeholderTextColor="#2b2c2c"
                    style={styles.input}
                    value={descricao}
                    onChangeText={setDescricao}
                />

                <TextInput
                    placeholder="Regras"
                    placeholderTextColor="#2b2c2c"
                    style={styles.input}
                    value={regras}
                    onChangeText={setRegras}
                />

                <View style={styles.field}>
                    <Text style={styles.label}>Data de início</Text>

                    <TouchableOpacity
                        style={styles.input}
                        onPress={() => setMostrarPicker(true)}
                    >
                        <Text style={styles.inputText}>
                        {dataInicio.toLocaleDateString()}{" "}
                        {dataInicio.toLocaleTimeString([], {
                            hour: "2-digit",
                            minute: "2-digit",
                        })}
                        </Text>
                    </TouchableOpacity>
                    </View>

                    {mostrarPicker && (
                    <DateTimePicker
                        value={dataInicio}
                        mode="datetime"
                        display="default"
                        onChange={(event, selectedDate) => {
                        setMostrarPicker(false);
                        if (selectedDate) setDataInicio(selectedDate);
                        }}
                    />
                )}

                <View style={styles.field}>
                    <Text style={styles.label}>Data final</Text>

                    <TouchableOpacity
                        style={styles.input}
                        onPress={() => setMostrarPicker(true)}
                    >
                        <Text style={styles.inputText}>
                        {dataFinal.toLocaleDateString()}{" "}
                        {dataFinal.toLocaleTimeString([], {
                            hour: "2-digit",
                            minute: "2-digit",
                        })}
                        </Text>
                    </TouchableOpacity>
                    </View>

                    {mostrarPicker && (
                    <DateTimePicker
                        value={dataFinal}
                        mode="datetime"
                        display="default"
                        onChange={(event, selectedDate) => {
                        setMostrarPicker(false);
                        if (selectedDate) setDataFinal(selectedDate);
                        }}
                    />
                )}

                </ScrollView>
            </KeyboardAvoidingView>
        </View>
    );
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.cinza[400],
  },

  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    padding: 16,
    backgroundColor: colors.cinza[400],
  },


  headerTitle: {
    marginTop: 60,
    fontSize: 21,
    fontWeight: "900",
    textAlign: "left"
  },

  publish: {
    color: colors.azul[300],
    fontWeight: "700",
  },

  content: {
    padding: 16,
  },

  photoBox: {
    height: 300,
    borderWidth: 1,
    backgroundColor: "#e4f4fc",
    borderColor: "#cbeefc",
    borderRadius: 12,
    justifyContent: "center",
    alignItems: "center",
    marginBottom: 24,
  },

  photoText: {
    marginTop: 8,
    color: colors.azul[300],
  },

  sectionTitle: {
    fontSize: 16,
    fontWeight: "900",
    marginBottom: 12,
  },

  input: {
    backgroundColor: "#e4f4fc",
    borderRadius: 8,
    padding: 14,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: "#cbeefc",
    },

  photo: {
    width: "100%",
        height: "100%",
        borderRadius: 12,
    },

    field: {
        marginBottom: 16,
    },
    label: {
        fontSize: 14,
        fontWeight: "600",
        marginBottom: 6,
        color: "#333",
    },
    inputText: {
        fontSize: 16,
        color: "#000",
    }

});