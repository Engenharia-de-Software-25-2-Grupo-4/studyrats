import { StyleSheet, Text, View, FlatList, TouchableOpacity } from "react-native"
import { Ionicons } from '@expo/vector-icons';
import { colors } from "@/styles/colors";
import { useNavigation } from "@react-navigation/native";
import { StackNavigationProp } from "@react-navigation/stack";
import { StackParams } from "@/utils/routesStack";

type NavigationProp = StackNavigationProp<StackParams, "Disciplinas">;

interface Discipline {
  id: string;
  name: string;
}

const disciplines: Discipline[] = [
  { id: '1', name: 'Engenharia de Software' },
  { id: '2', name: 'Projeto de Software' },
  { id: '3', name: 'Programação Concorrente' },
  { id: '4', name: 'Banco de Dados' },
  { id: '5', name: 'Sistemas Operacionais' },
];

export default function Disciplinas() {
    const navigation = useNavigation<NavigationProp>();

    return (
        <View style={styles.container}>
            {/* HEADER */}
            <View style={styles.header}>
                <TouchableOpacity style={styles.backButton} onPress={() => navigation.goBack()}>
                    <Ionicons name="arrow-back" size={24} color={colors.azul[300]} />
                </TouchableOpacity>
                <Text style={styles.headerTitle}>Disciplinas</Text>
                <View style={styles.placeholder} />
            </View>

            {/* LISTA DE DISCIPLINAS */}
            <FlatList
                data={disciplines}
                renderItem={({ item }) => <DisciplineCard discipline={item} />}
                keyExtractor={(item) => item.id}
                contentContainerStyle={styles.listContainer}
                showsVerticalScrollIndicator={false}
            />
        </View>
    )
}
function DisciplineCard({ discipline }: { discipline: Discipline }) {
    return (
        <TouchableOpacity style={styles.disciplineCard} activeOpacity={0.7}>
            <Ionicons name="book-outline" size={24} color="#FFFFFF" />
            <Text style={styles.disciplineName}>{discipline.name}</Text>
        </TouchableOpacity>
    )
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
        paddingHorizontal: 20,
        paddingTop: 60,
        paddingBottom: 20,
        backgroundColor: colors.cinza[400]
    },

    backButton: {
        padding: 4
    },

    headerTitle: {
        fontSize: 20,
        fontWeight: "700",
        color: colors.azul[300]
    },

    placeholder: {
        width: 32
    },

    listContainer: {
        padding: 20,
        paddingTop: 8
    },

    disciplineCard: {
        flexDirection: "row",
        alignItems: "center",
        backgroundColor: colors.azul[300],
        paddingVertical: 20,
        paddingHorizontal: 20,
        borderRadius: 12,
        marginBottom: 16
    },

    disciplineName: {
        fontSize: 16,
        fontWeight: "600",
        color: "#FFFFFF",
        marginLeft: 16,
        flex: 1
    }
})