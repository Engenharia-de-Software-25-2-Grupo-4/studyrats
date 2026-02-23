import { Menu } from "@/components/Menu";
import { Post } from "@/components/Post";
import { colors } from "@/styles/colors";
import { categories } from "@/utils/categories";
import { posts } from "@/utils/posts";
import { Ionicons, MaterialIcons } from "@expo/vector-icons";
import { useNavigation, useRoute  } from "@react-navigation/native";
import type { NavigationProp } from '@react-navigation/native';
import { ActivityIndicator, Alert, FlatList, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { StackParams } from "@/utils/routesStack";
import { useEffect, useMemo, useState } from "react";
import { FilterModal } from "@/components/Modal";
import { SessaoDetails } from "@/services/sessao";
import { grupoServer } from "@/services/grupo";

export default function FeedScreen() {
    const navigation = useNavigation<NavigationProp<StackParams>>();
    const route = useRoute();

    const [filterVisible, setFilterVisible] = useState(false);
    const [selectedSubject, setSelectedSubject] = useState<string | null>(null);
    const [selectedUser, setSelectedUser] = useState<string | null>(null);

    const grupoId = (route.params as any)?.grupoId

    const [sessoes, setSessoes] = useState<SessaoDetails[]>([]);
    const [loading, setLoading] = useState(true);

    const subjects = useMemo(() => 
        [...new Set(sessoes.map(s => s.disciplina))], 
        [sessoes]
    );
    
    const users = useMemo(() => 
        [...new Set(sessoes.map(s => s.nome_criador))], 
        [sessoes]
    );

    useEffect(() => {
        async function loadSessoes() {
        if (!grupoId) return;

        try {
            setLoading(true);
            const data = await grupoServer.getSessoes(grupoId);
            setSessoes(data);
        } catch (e: any) {
            Alert.alert("Erro", "Não foi possível carregar as publicações");
        } finally {
            setLoading(false);
        }
        }

        loadSessoes();
    }, [grupoId])

    const handleNavigateToCheckIn = () => {
        if (!grupoId) return;
        navigation.navigate("CriarSessao", { grupoId });
    }

    const handleNavigateToPublicacao = (sessao: SessaoDetails) => {
        navigation.navigate("Publicacao", { sessao });
    };

    // Filtra as sessões reais
    const filteredSessoes = useMemo(() => {
        return sessoes.filter(sessao => {
            const matchesSubject = !selectedSubject || sessao.disciplina === selectedSubject;
            const matchesUser = !selectedUser || sessao.nome_criador === selectedUser;
            return matchesSubject && matchesUser;
        });
    }, [sessoes, selectedSubject, selectedUser]);

    const hasActiveFilters = selectedSubject || selectedUser
    
    return (
        <View style={styles.container}>
            <View style={styles.header}>
                <TouchableOpacity onPress={() => navigation.goBack()}>
                    <Ionicons name="arrow-back" size={24} />
                </TouchableOpacity>
                {/* <Text style={styles.headerTitle}>Grupo de Estudos</Text> */}
                <TouchableOpacity style={styles.addIcon} onPress={handleNavigateToCheckIn}>
                    <MaterialIcons
                        name="add"
                        color={colors.cinza[100]}
                        size={23} />
                </TouchableOpacity> 
            </View>

            <View style={styles.headerSecondaryTitle}>
                <Text style={styles.title}>
                    Publicações
                </Text>

                <TouchableOpacity onPress={() => setFilterVisible(true)}>
                    <View style={styles.filterButton}>
                        <Text style={styles.headerSecondarySubitle}>
                            Filtrar
                        </Text>
                        {hasActiveFilters && <View style={styles.filterBadge} />}
                    </View>
                </TouchableOpacity>
            </View>

            {loading ? (
                <ActivityIndicator size="large" color={colors.azul[300]} style={{ marginTop: 40 }} />
            ) : (
                <FlatList
                    data={filteredSessoes}
                    keyExtractor={item => item.id_sessao}
                    renderItem={({ item }) => (
                        <TouchableOpacity onPress={() => handleNavigateToPublicacao(item)}>
                            <Post
                                title={item.titulo}
                                user={item.nome_criador}
                                subject={item.disciplina}
                                idSessao={item.id_sessao}
                            />
                        </TouchableOpacity>
                    )}
                    showsVerticalScrollIndicator={false}
                    contentContainerStyle={{ padding: 20, paddingTop: 8, gap: 10 }}
                    ListEmptyComponent={
                        <Text style={styles.emptyText}>
                            {hasActiveFilters 
                                ? "Nenhuma publicação encontrada com os filtros selecionados"
                                : "Nenhuma publicação neste grupo ainda"
                            }
                        </Text>
                    }
                />
            )}

            <FilterModal
                visible={filterVisible}
                onClose={() => setFilterVisible(false)}
                selectedSubject={selectedSubject}
                selectedUser={selectedUser}
                onSelectSubject={setSelectedSubject}
                onSelectUser={setSelectedUser}
                subjects={subjects}
                users={users}
            />
        </View>)
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: colors.cinza[400]
    },
    header: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        padding: 16,
        marginBottom: 12,
    },
    headerTitle: {
        marginTop: 45,
        fontSize: 21,
        fontWeight: "900",
        textAlign: "left"
    },
    title: {
        marginLeft: 25,
        marginBottom: 5,
        fontSize: 20,
        fontWeight: "900",
    },
    headerSecondaryTitle: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        marginBottom: 12,
    },
    headerSecondarySubitle: {
        marginRight: 35,
        color: colors.azul[300]
    },
    headerIcon: {
        borderColor: colors.cinza[600],
        borderRadius: 22,
        padding: 5,
        borderWidth: 0.5,
    },
    addIcon: {
        alignSelf: "center",
        marginTop: 40,
        width: 50,
        height: 50,
        borderRadius: 28,
        backgroundColor: colors.azul[300],
        justifyContent: "center",
        alignItems: "center",
    },
    card: {
        marginLeft: 20,
        marginRight: 20,
        marginBottom: 5,
        marginTop: 20
    },
    tabs: {
        flexDirection: "row",
        justifyContent: "space-around",
        borderBottomWidth: 1,
        borderColor: colors.cinza[500],
        marginTop: 20
        },
    tab: {
        paddingBottom: 8,
        fontWeight: "600",
        color: colors.cinza[500],
    },
    filterButton: {
        flexDirection: "row",
        alignItems: "center",
        gap: 4,
    },
    filterBadge: {
        width: 8,
        height: 8,
        borderRadius: 4,
        backgroundColor: colors.azul[300],
        marginRight: 30,
    },
    emptyText: {
        textAlign: "center",
        color: colors.cinza[500],
        marginTop: 40,
        fontSize: 16,
    },
})