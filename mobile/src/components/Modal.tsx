import { colors } from "@/styles/colors";
import { Ionicons } from "@expo/vector-icons";
import { Modal, StyleSheet, Text, TouchableOpacity, View, ScrollView } from "react-native";

type FilterModalProps = {
  visible: boolean;
  onClose: () => void;
  selectedSubject: string | null;
  selectedUser: string | null;
  onSelectSubject: (subject: string | null) => void;
  onSelectUser: (user: string | null) => void;
  subjects: string[];
  users: string[];
};

export function FilterModal({
  visible,
  onClose,
  selectedSubject,
  selectedUser,
  onSelectSubject,
  onSelectUser,
  subjects,
  users,
}: FilterModalProps) {
    const handleClearFilters = () => {
        onSelectSubject(null);
        onSelectUser(null);
    };

    const hasActiveFilters = selectedSubject || selectedUser;

    return (
        <Modal
        visible={visible}
        transparent
        animationType="slide"
        onRequestClose={onClose}
        >
        <TouchableOpacity 
            style={styles.overlay} 
            activeOpacity={1} 
            onPress={onClose}
        >
            <View style={styles.modal} onStartShouldSetResponder={() => true}>
            <View style={styles.header}>
                <Text style={styles.title}>Filtrar publicações</Text>
                <TouchableOpacity onPress={onClose}>
                <Ionicons name="close" size={24} color={colors.azul[400]} />
                </TouchableOpacity>
            </View>

            <ScrollView style={styles.content}>
                {/* Disciplinas */}
                <Text style={styles.sectionTitle}>Disciplina</Text>
                <View style={styles.chipContainer}>
                {subjects.map((subject) => (
                    <TouchableOpacity
                    key={subject}
                    style={[
                        styles.chip,
                        selectedSubject === subject && styles.chipActive,
                    ]}
                    onPress={() => 
                        onSelectSubject(selectedSubject === subject ? null : subject)
                    }
                    >
                    <Text
                        style={[
                        styles.chipText,
                        selectedSubject === subject && styles.chipTextActive,
                        ]}
                    >
                        {subject}
                    </Text>
                    </TouchableOpacity>
                ))}
                </View>

                <Text style={styles.sectionTitle}>Usuário</Text>
                <View style={styles.chipContainer}>
                {users.map((user) => (
                    <TouchableOpacity
                    key={user}
                    style={[
                        styles.chip,
                        selectedUser === user && styles.chipActive,
                    ]}
                    onPress={() => 
                        onSelectUser(selectedUser === user ? null : user)
                    }
                    >
                    <Text
                        style={[
                        styles.chipText,
                        selectedUser === user && styles.chipTextActive,
                        ]}
                    >
                        {user}
                    </Text>
                    </TouchableOpacity>
                ))}
                </View>
            </ScrollView>

            <View style={styles.footer}>
                {hasActiveFilters && (
                <TouchableOpacity 
                    style={styles.clearButton} 
                    onPress={handleClearFilters}
                >
                    <Text style={styles.clearButtonText}>Limpar filtros</Text>
                </TouchableOpacity>
                )}
                <TouchableOpacity style={styles.applyButton} onPress={onClose}>
                <Text style={styles.applyButtonText}>Aplicar</Text>
                </TouchableOpacity>
            </View>
            </View>
        </TouchableOpacity>
        </Modal>
    );
}

const styles = StyleSheet.create({
    overlay: {
        flex: 1,
        backgroundColor: "rgba(0, 0, 0, 0.5)",
        justifyContent: "flex-end",
    },
    modal: {
        backgroundColor: colors.cinza[400],
        borderTopLeftRadius: 20,
        borderTopRightRadius: 20,
        maxHeight: "70%",
    },
    header: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        padding: 20,
        borderBottomWidth: 1,
        borderBottomColor: colors.cinza[500],
    },
    title: {
        fontSize: 18,
        fontWeight: "700",
        color: colors.azul[400],
    },
    content: {
        padding: 20,
    },
    sectionTitle: {
        fontSize: 16,
        fontWeight: "600",
        color: colors.azul[400],
        marginBottom: 12,
        marginTop: 8,
    },
    chipContainer: {
        flexDirection: "row",
        flexWrap: "wrap",
        gap: 8,
        marginBottom: 20,
    },
    chip: {
        paddingHorizontal: 16,
        paddingVertical: 8,
        borderRadius: 20,
        backgroundColor: colors.cinza[200],
        borderWidth: 1,
        borderColor: colors.cinza[500],
    },
    chipActive: {
        backgroundColor: colors.azul[300],
        borderColor: colors.azul[300],
    },
    chipText: {
        fontSize: 14,
        color: colors.azul[400],
    },
    chipTextActive: {
        color: "#FFF",
        fontWeight: "600",
    },
    footer: {
        flexDirection: "row",
        padding: 20,
        gap: 12,
        borderTopWidth: 1,
        borderTopColor: colors.cinza[500],
    },
    clearButton: {
        flex: 1,
        paddingVertical: 14,
        borderRadius: 8,
        borderWidth: 1,
        borderColor: colors.azul[300],
        alignItems: "center",
    },
    clearButtonText: {
        color: colors.azul[300],
        fontWeight: "600",
        fontSize: 16,
    },
    applyButton: {
        flex: 1,
        paddingVertical: 14,
        borderRadius: 8,
        backgroundColor: colors.azul[300],
        alignItems: "center",
    },
    applyButtonText: {
        color: "#FFF",
        fontWeight: "600",
        fontSize: 16,
    },
});