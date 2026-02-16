import { GroupOverviewCard } from "@/components/GroupOverviewCard";
import { Menu } from "@/components/Menu";
import { Post } from "@/components/Post";
import { UserItem } from "@/components/UserItem";
import { colors } from "@/styles/colors";
import { categories } from "@/utils/categories";
import { posts } from "@/utils/posts";
import { RootStackParamList } from "@/utils/routesStack";
import { users } from "@/utils/users";
import { Ionicons, MaterialIcons } from "@expo/vector-icons";
import type { NavigationProp } from "@react-navigation/native";
import { useNavigation } from "@react-navigation/native";
import { useState } from "react";
import { FlatList, StyleSheet, Text, TouchableOpacity, View } from "react-native";

enum Tab {
    ESTATISTICAS = 1,
    PARTICIPANTES = 2
}

export default function StudyGroupScreen() {
    const navigation = useNavigation<NavigationProp<RootStackParamList>>();
    const [tab, setTab] = useState(Tab.ESTATISTICAS);
    const post = posts[0];

    const sortedUsers = [...users].sort(
        (a, b) => b.daysActive - a.daysActive
    );

    const topThree = sortedUsers.slice(0, 3);

    const ITEMS_PER_LOAD = 5;
    const [visibleCount, setVisibleCount] = useState(ITEMS_PER_LOAD);

    const loadMore = () => {
        if (visibleCount >= users.length) return;
        setVisibleCount(prev => prev + ITEMS_PER_LOAD);
    };

    const handleNavigateToHome = () => {
        navigation.navigate("Home")
    }

    const handleNavigateToFeed = () => {
        navigation.navigate("Feed")
    };

    return (
        <View style={styles.container}>
            <View style={styles.header}>
                <TouchableOpacity onPress={handleNavigateToHome}>
                    <Ionicons name="arrow-back" size={24} />
                </TouchableOpacity>

                <Text style={styles.headerTitle}>
                    Grupo de Estudos
                </Text>

                <TouchableOpacity style={styles.headerIcon}>
                    <MaterialIcons
                        name="notifications-none"
                        color={colors.azul[300]}
                        size={23} 
                    />
                </TouchableOpacity>
            </View>
            
            <View style={styles.checkinCard}>
                <GroupOverviewCard/>
            </View>

            {tab === Tab.ESTATISTICAS && (
                <>
                    <View style={styles.card}>
                        <Text style={styles.cardTitle}>
                            Acompanhe as publicações recentes
                        </Text>
                        <TouchableOpacity onPress={handleNavigateToFeed}>
                            <Post
                                title={post.title}
                                user={post.user}
                                subject={post.subject}
                            />
                        </TouchableOpacity>
                    </View>
                </>
            )}

            <View style={styles.tabs}>
                <TouchableOpacity onPress={() => setTab(Tab.ESTATISTICAS)}>
                    <Text style={[styles.tab, tab === Tab.ESTATISTICAS && styles.activeTab]}>
                        ESTATÍSTICAS
                    </Text>
                </TouchableOpacity>

                <TouchableOpacity onPress={() => setTab(Tab.PARTICIPANTES)}>
                    <Text style={[styles.tab, tab === Tab.PARTICIPANTES && styles.activeTab]}>
                        PARTICIPANTES
                    </Text>
                </TouchableOpacity>
            </View>

            <View style={styles.content}>
                {tab === Tab.ESTATISTICAS ? (
                    <>
                        <FlatList
                            data={topThree}
                            keyExtractor={(item) => item.id}
                            renderItem={({ item, index }) => (
                                <UserItem
                                    user={item}
                                    showMedal
                                    medal={
                                        index === 0 ? "gold" :
                                        index === 1 ? "silver" : "bronze"
                                    }
                                />
                            )}
                            contentContainerStyle={{ padding: 20, paddingTop: 8 }}
                        />
                    </>
                ) : (
                    <FlatList
                        data={users.slice(0, visibleCount)}
                        keyExtractor={(item) => item.id}
                        renderItem={({ item }) => <UserItem user={item} />}
                        contentContainerStyle={{ padding: 20, paddingTop: 8 }}
                        onEndReached={loadMore}
                        onEndReachedThreshold={0.5}
                    />
                )}
            </View>
            
        <Menu
            tabs={categories}
            activeTabId="2" // "2" = Desafios
        />
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
        marginBottom: 12,
    },
    headerTitle: {
        marginTop: 45,
        fontSize: 21,
        fontWeight: "900",
        textAlign: "left"
    },
    headerIcon: {
        borderColor: colors.cinza[600],
        borderRadius: 22,
        padding: 5,
        borderWidth: 0.5,
    },
    checkinCard: {
        paddingHorizontal: 16,
        marginLeft: 10,
        marginRight: 10
    },
    card: {
        marginLeft: 20,
        marginRight: 20,
        marginBottom: 5,
        marginTop: 20
    },
    cardTitle: {
        marginBottom: 12,
        fontSize: 17,
        fontWeight: "900",
    },
    content: {
        flex: 1,
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
    activeTab: {
        color: colors.azul[400],
        borderBottomWidth: 1.5,
        borderColor: colors.azul[400],
    },
});