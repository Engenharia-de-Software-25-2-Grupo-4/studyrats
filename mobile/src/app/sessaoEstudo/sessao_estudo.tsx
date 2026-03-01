import { View, Text, StyleSheet, TouchableOpacity, TextInput, ScrollView, Image, Alert, KeyboardAvoidingView, Platform } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import * as ImagePicker from "expo-image-picker";
import { useState, useEffect } from "react";
import DateTimePicker from "@react-native-community/datetimepicker";
import { useNavigation, useRoute, RouteProp } from '@react-navigation/native';
import type { NavigationProp } from '@react-navigation/native';
import { Modal } from "react-native";
import { Menu } from "@/components/Menu";
import { categories } from "@/utils/categories";
import { StackParams } from "@/utils/routesStack";
import { createSessao, updateSessao } from "@/services/sessao";
import { uploadImagem } from "@/services/sessao";
import { getAuthenticatedUid } from "@/services/authStorage";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { listarDisciplinasDoGrupo, DisciplinaDTO } from "@/services/disciplinas";

type CriarSessaoRouteProp = RouteProp<StackParams, "CriarSessao">;
type CriarSessaoNavProp = NativeStackNavigationProp<StackParams, "CriarSessao">;

export default function CriarSessao() {
    const navigation = useNavigation<CriarSessaoNavProp>();
    const route = useRoute<CriarSessaoRouteProp>();

    const { grupoId, sessao } = route.params;

    const [image, setImage] = useState("");
    const [titulo, setTitulo] = useState("");
    const [descricao, setDescricao] = useState("");
    const [disciplina, setDisciplina] = useState("");
    const [mostrarDisciplinas, setMostrarDisciplinas] = useState(false);
    const [criandoNova, setCriandoNova] = useState(false);
    const [novaDisciplina, setNovaDisciplina] = useState("");
    const [dataHora, setDataHora] = useState(new Date());
    const [duracao, setDuracao] = useState<string>("");
    const [topico, setTopico] = useState<string>("");
    const [mostrarPicker, setMostrarPicker] = useState(false)
    const [disciplinas, setDisciplinas] = useState<DisciplinaDTO[]>([]);
    const [carregandoDisciplinas, setCarregandoDisciplinas] = useState(false);

    useEffect(() => {
        if (!sessao) return;
        setTitulo(sessao.titulo);
        setDescricao(sessao.descricao);
        setDisciplina(sessao.disciplina);
        setDataHora(new Date(sessao.horario_inicio));
        setImage(sessao.url_foto);
        setDuracao(String(sessao.duracao_minutos));
        setTopico(sessao.topico);
    }, [sessao]);

    useEffect(() => {
        if (!grupoId) return;
        async function buscarDisciplinas() {
            try {
                setCarregandoDisciplinas(true);
                const lista = await listarDisciplinasDoGrupo(grupoId);
                console.log("disciplinas retornadas:", lista);
                const semDuplicatas = (lista ?? []).filter(
                    (d, index, self) => index === self.findIndex((x) => x.id_disciplina === d.id_disciplina)
                );
                setDisciplinas(semDuplicatas);
            } catch (e) {
                console.log("Erro ao buscar disciplinas:", e);
                setDisciplinas([]);
            } finally {
                setCarregandoDisciplinas(false);
            }
        }
        buscarDisciplinas();
    }, [grupoId]);

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

    const handleGoBack = () => {
        navigation.goBack();
    };

    const handleSubmit = async () => {
        const duracao_minutos = parseInt(duracao);

        if (isNaN(duracao_minutos)) {
            Alert.alert("Erro", "A duração deve ser um número.");
            return;
        }

        if (!titulo.trim()) {
            Alert.alert("Erro", "O título é obrigatório.");
            return;
        }

        if (!disciplina) {
            Alert.alert("Erro", "A disciplina é obrigatória.");
            return;
        }
        if (!topico) {
            Alert.alert("Erro", "O tópico é obrigatório.");
            return;
        }
        try {
            console.log("1. iniciando submit");
            console.log("2. sessao:", sessao);

            if (sessao) {
                console.log("3. editando sessao");
                await updateSessao(sessao.id_sessao, {
                    titulo,
                    descricao,
                    horario_inicio: dataHora.toISOString(),
                    duracao_minutos,
                    url_foto: "",
                    disciplina,
                    topico
                },);
                if (image) await uploadImagem(sessao.id_sessao, image);

                Alert.alert("Sucesso", "Check-in atualizado!", [
                { text: "OK", onPress: () => navigation.goBack() }
                ]);
            } else {
                console.log("3. criando sessao");
                const novaSessao = await createSessao(
                    {
                        titulo,
                        descricao,
                        horario_inicio: dataHora.toISOString(),
                        duracao_minutos,
                        url_foto: "",
                        disciplina,
                        topico,
                    },
                    grupoId
                );
                console.log("retorno createSessao:", novaSessao);
                if (image) await uploadImagem(novaSessao.id_sessao, image);
                navigation.navigate("Publicacao", { sessao: novaSessao, grupoId })
            }
        } catch (error: any) {
            Alert.alert("Erro", error.message);
        }
    }

    return (
        <View style={styles.container}>

            {/* HEADER */}
            <View style={styles.header}>
                <TouchableOpacity onPress={handleGoBack}>
                    <Ionicons name="arrow-back" size={24} />
                </TouchableOpacity>

                <Text style={styles.headerTitle}>{sessao ? "Editar check-in" : "Novo check-in"}</Text>
                <TouchableOpacity onPress={handleSubmit}>
                    <Text style={styles.publish}>{sessao ? "Salvar" : "Criar"}</Text>
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
                        {image ? (
                            <Image source={{ uri: image }} style={styles.photo} />
                        ) : (
                            <>
                                <Ionicons name="add" size={32} color="#1E6F7C" />
                                <Text style={styles.photoText}>Adicionar sua foto</Text>
                            </>
                        )}
                    </TouchableOpacity>

                    {/* FORMULÁRIO */}
                    <Text style={styles.sectionTitle}>Informações</Text>

                    <TextInput
                        placeholder="Título"
                        placeholderTextColor="#2b2c2c"
                        style={styles.input}
                        value={titulo}
                        onChangeText={setTitulo}
                    />

                    <TextInput
                        placeholder="Descrição (Opcional)"
                        placeholderTextColor="#2b2c2c"
                        style={styles.input}
                        value={descricao}
                        onChangeText={setDescricao}
                    />

                    <TouchableOpacity
                        style={styles.input}
                        onPress={() => setMostrarDisciplinas(true)}
                    >
                        <Text style={{ color: disciplina ? "#000" : "#2b2c2c" }}>
                            {disciplina
                                ? disciplina
                                : "Selecionar disciplina"}
                        </Text>
                    </TouchableOpacity>

                    <Modal visible={mostrarDisciplinas} transparent animationType="fade">
                        <View style={styles.modalOverlay}>
                            {/* clique fora fecha */}
                            <TouchableOpacity
                                style={StyleSheet.absoluteFill}
                                onPress={() => {
                                    setMostrarDisciplinas(false);
                                    setCriandoNova(false);
                                    setNovaDisciplina("");
                                }}
                            />

                            <View style={styles.modalBox}>
                                <ScrollView keyboardShouldPersistTaps="handled">
                                    <Text style={styles.modalTitle}>Selecione uma disciplina</Text>

                                    {/* loading */}
                                    {carregandoDisciplinas && (
                                        <Text style={styles.modalHint}>Carregando disciplinas...</Text>
                                    )}

                                    {/* vazio */}
                                    {!carregandoDisciplinas && disciplinas.length === 0 && (
                                        <Text style={styles.modalHint}>
                                            Nenhuma disciplina cadastrada ainda.
                                        </Text>
                                    )}

                                    {/* lista do backend */}
                                    {!carregandoDisciplinas &&
                                        disciplinas.map((d) => (
                                            <TouchableOpacity
                                                key={d.nome}
                                                style={styles.option}
                                                onPress={() => {
                                                    setDisciplina(d.nome);
                                                    setMostrarDisciplinas(false);
                                                    setCriandoNova(false);
                                                    setNovaDisciplina("");
                                                }}
                                            >
                                                <Text style={styles.optionText}>{d.nome}</Text>
                                            </TouchableOpacity>
                                        ))}

                                    {/* botão criar nova */}
                                    <TouchableOpacity
                                        style={styles.option}
                                        onPress={() => setCriandoNova((prev) => !prev)}
                                    >
                                        <Text style={[styles.optionText, { color: "#1E6F7C" }]}>
                                            + Nova disciplina
                                        </Text>
                                    </TouchableOpacity>

                                    {/* criar nova */}
                                    {criandoNova && (
                                        <View style={{ marginTop: 8 }}>
                                            <TextInput
                                                placeholder="Digite o nome"
                                                placeholderTextColor="#2b2c2c"
                                                style={styles.input}
                                                value={novaDisciplina}
                                                onChangeText={setNovaDisciplina}
                                                autoFocus
                                                returnKeyType="done"
                                                onSubmitEditing={() => {
                                                    const nome = novaDisciplina.trim();
                                                    if (!nome) return;

                                                    // seleciona
                                                    setDisciplina(nome);

                                                    // adiciona na lista local (UX) se não existir
                                                    setDisciplinas((prev) => {
                                                        const jaExiste = prev.some(
                                                            (x) => x.nome.trim().toLowerCase() === nome.toLowerCase()
                                                        );
                                                        if (jaExiste) return prev;

                                                        return [
                                                            { id_disciplina: `temp-${Date.now()}`, nome },
                                                            ...prev,
                                                        ];
                                                    });

                                                    // fecha e limpa
                                                    setNovaDisciplina("");
                                                    setCriandoNova(false);
                                                    setMostrarDisciplinas(false);
                                                }}
                                            />

                                            <View style={{ flexDirection: "row", gap: 10 }}>
                                                <TouchableOpacity
                                                    style={[styles.option, { flex: 1 }]}
                                                    onPress={() => {
                                                        const nome = novaDisciplina.trim();
                                                        if (!nome) return;

                                                        setDisciplina(nome);

                                                        setDisciplinas((prev) => {
                                                            const jaExiste = prev.some(
                                                                (x) => x.nome.trim().toLowerCase() === nome.toLowerCase()
                                                            );
                                                            if (jaExiste) return prev;

                                                            return [
                                                                { id_disciplina: `temp-${Date.now()}`, nome },
                                                                ...prev,
                                                            ];
                                                        });

                                                        setNovaDisciplina("");
                                                        setCriandoNova(false);
                                                        setMostrarDisciplinas(false);
                                                    }}
                                                >
                                                    <Text style={[styles.optionText, { color: "green" }]}>
                                                        Salvar disciplina
                                                    </Text>
                                                </TouchableOpacity>

                                                <TouchableOpacity
                                                    style={[styles.option, { flex: 1 }]}
                                                    onPress={() => {
                                                        setCriandoNova(false);
                                                        setNovaDisciplina("");
                                                    }}
                                                >
                                                    <Text style={[styles.optionText, { color: "#999" }]}>
                                                        Cancelar
                                                    </Text>
                                                </TouchableOpacity>
                                            </View>
                                        </View>
                                    )}
                                </ScrollView>
                            </View>
                        </View>
                    </Modal>


                    <TextInput
                        placeholder="Tópico"
                        placeholderTextColor="#2b2c2c"
                        style={styles.input} value={topico}
                        onChangeText={setTopico}
                    />
                    <View style={styles.input}>
                        <Text>Data e hora do check-in</Text>

                        <TouchableOpacity
                            style={styles.input}
                            onPress={() => setMostrarPicker(true)}
                        >
                            <Text>
                                {dataHora.toLocaleDateString()}{" "}
                                {dataHora.toLocaleTimeString([], {
                                    hour: "2-digit",
                                    minute: "2-digit",
                                })}
                            </Text>
                        </TouchableOpacity>
                    </View>

                    {mostrarPicker && (
                        <DateTimePicker
                            value={dataHora}
                            mode="datetime"
                            display="default"
                            onChange={(event, selectedDate) => {
                                setMostrarPicker(false);
                                if (selectedDate) setDataHora(selectedDate);
                            }}
                        />
                    )}

                    <TextInput
                        placeholder="Duração"
                        placeholderTextColor="#2b2c2c"
                        style={styles.input}
                        value={duracao}
                        onChangeText={setDuracao}
                        keyboardType="numeric"
                    />


                </ScrollView>
            </KeyboardAvoidingView>
            
        </View>
    );
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#F8FFFC",
    },

    header: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        padding: 16,
        backgroundColor: "#F3FBFF",
    },


    headerTitle: {
        marginTop: 60,
        fontSize: 21,
        fontWeight: "900",
        textAlign: "left"
    },

    publish: {
        color: "#01415B",
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
        color: "#01415B",
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

    option: {
        padding: 16,
        borderBottomWidth: 1,
        borderBottomColor: "#eee",
    },

    optionText: {
        fontSize: 16,
    },

    modalBox: {
        backgroundColor: "#fff",
        borderRadius: 12,
        marginHorizontal: 20,
        marginTop: "40%",
        maxHeight: 300,
    },

    modalOverlay: {
        flex: 1,
        backgroundColor: "rgba(0,0,0,0.3)",
        justifyContent: "center",
    },
    modalTitle: {
        fontSize: 16,
        fontWeight: "700",
        color: "#01415B",
        marginBottom: 8,
    },
    modalHint: {
        paddingVertical: 8,
        color: "#2b2c2c",
    },

});