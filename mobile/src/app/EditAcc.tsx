import React, { useCallback, useState } from "react";
import {
  Alert,
  Image,
  Modal,
  ScrollView,
  Text,
  TextInput,
  TouchableOpacity,
  View,
  ImageSourcePropType,
  StyleSheet,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import { colors } from "@/styles/colors";
import { fetchProfilePhoto } from "@/server/fetchProfilePhoto";
import * as ImagePicker from "expo-image-picker";
import { uploadProfilePhoto } from "@/server/uploadProfilePhoto";
import { changePassword } from "@/services/changePassword";
import { auth } from "@/firebaseConfig";

// ✅ helper: só aplica cache-bust em URL de rede (http/https)
// ❌ NÃO mexe em data: (base64) nem file: (local), porque isso quebra a URI
function withCacheBuster(uri: string) {
  if (/^https?:\/\//i.test(uri)) {
    return `${uri}${uri.includes("?") ? "&" : "?"}t=${Date.now()}`;
  }
  return uri;
}

export default function EditProfile() {
  const navigation = useNavigation();

  const [name, setName] = useState("Kemilli Nicole");

  const [profileImage, setProfileImage] = useState<ImageSourcePropType>(
    require("@/assets/default_profile.jpg")
  );

  const [photoModalVisible, setPhotoModalVisible] = useState(false);

  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");

  const [isUploadingPhoto, setIsUploadingPhoto] = useState(false);

  // ✅ força rerender do <Image /> quando a foto mudar (ajuda com cache interno do RN)
  const [photoVersion, setPhotoVersion] = useState(0);

  // carrega/recarrega a foto sempre que a tela abrir/voltar
  useFocusEffect(
    useCallback(() => {
      let alive = true;

      (async () => {
        try {
          const user = auth.currentUser;
          if (!user?.uid) return;

          const source = await fetchProfilePhoto(user.uid);

          if (!alive) return;

          if (typeof (source as any)?.uri === "string") {
            const u = (source as any).uri;
            setProfileImage({ uri: withCacheBuster(u) });
          } else {
            setProfileImage(source);
          }

          setPhotoVersion((v) => v + 1);
        } catch (e) {
          console.warn("Erro ao buscar foto do perfil:", e);
        }
      })();

      return () => {
        alive = false;
      };
    }, [])
  );

  const handleSave = async () => {
    const tryingToChangePassword =
      currentPassword.trim() || newPassword.trim() || confirmNewPassword.trim();

    if (tryingToChangePassword) {
      if (!currentPassword.trim() || !newPassword.trim() || !confirmNewPassword.trim()) {
        Alert.alert("Atenção", "Preencha todos os campos de senha para alterar.");
        return;
      }
      if (newPassword.length < 6) {
        Alert.alert("Atenção", "A nova senha deve ter pelo menos 6 caracteres.");
        return;
      }
      if (newPassword !== confirmNewPassword) {
        Alert.alert("Atenção", "A confirmação da senha não confere.");
        return;
      }
    }

    try {
      const user = auth.currentUser;
      if (!user) {
        Alert.alert("Sessão expirada", "Faça login novamente.");
        return;
      }

      if (tryingToChangePassword) {
        await changePassword(currentPassword, newPassword);
      }

      Alert.alert("Sucesso", "Perfil atualizado com sucesso!");
      setCurrentPassword("");
      setNewPassword("");
      setConfirmNewPassword("");
    } catch (e: any) {
      const code = e?.code as string | undefined;

      if (e?.message === "USUARIO_NAO_LOGADO") {
        Alert.alert("Sessão expirada", "Faça login novamente.");
        return;
      }

      if (e?.message === "PROVEDOR_SEM_SENHA") {
        Alert.alert(
          "Atenção",
          "Sua conta não usa senha (provavelmente Google/Apple). Para criar senha, você precisa vincular um provedor de email/senha."
        );
        return;
      }

      if (code === "auth/wrong-password") {
        Alert.alert("Atenção", "Senha atual incorreta.");
        return;
      }

      if (code === "auth/weak-password") {
        Alert.alert("Atenção", "A nova senha é fraca. Use pelo menos 6 caracteres.");
        return;
      }

      if (code === "auth/too-many-requests") {
        Alert.alert("Atenção", "Muitas tentativas. Tente novamente mais tarde.");
        return;
      }

      if (code === "auth/requires-recent-login") {
        Alert.alert("Atenção", "Por segurança, faça login novamente e tente de novo.");
        return;
      }

      Alert.alert("Erro", e?.message ?? "Não foi possível alterar a senha.");
    }
  };

  const handleChangePhoto = () => setPhotoModalVisible(true);

  async function pickAndUploadPhoto() {
    try {
      setIsUploadingPhoto(true);

      const user = auth.currentUser;
      if (!user?.uid) {
        Alert.alert("Sessão expirada", "Faça login novamente para enviar sua foto.");
        return;
      }

      const perm = await ImagePicker.requestMediaLibraryPermissionsAsync();
      if (perm.status !== "granted") {
        Alert.alert("Permissão necessária", "Permita acesso à galeria para escolher uma foto.");
        return;
      }

      const result = await ImagePicker.launchImageLibraryAsync({
        mediaTypes: ["images"],
        allowsEditing: true,
        aspect: [1, 1],
        quality: 0.8,
      });

      if (result.canceled) return;

      const picked = result.assets[0];

      // ✅ Preview imediato (local)
      setProfileImage({ uri: picked.uri });
      setPhotoVersion((v) => v + 1);

      // Envia para o backend
      await uploadProfilePhoto(picked.uri);

      setPhotoModalVisible(false);

      // ✅ Recarrega do backend (fetchProfilePhoto deve evitar cache do GET do lado dele)
      const source = await fetchProfilePhoto(user.uid);
      console.log("[EditProfile] photo source:", source);

      if (typeof (source as any)?.uri === "string") {
        setProfileImage({ uri: withCacheBuster((source as any).uri) });
      } else {
        setProfileImage(source);
      }

      setPhotoVersion((v) => v + 1);

      Alert.alert("Sucesso", "Foto atualizada!");
    } catch (e: any) {
      console.warn("Erro upload foto:", e);

      if (e?.message === "USUARIO_NAO_LOGADO") {
        Alert.alert("Sessão expirada", "Faça login novamente.");
        return;
      }

      Alert.alert("Erro", e?.message ?? "Falha ao enviar foto.");
    } finally {
      setIsUploadingPhoto(false);
    }
  }

  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        {/* HEADER */}
        <View style={styles.header}>
          <TouchableOpacity style={styles.backButton} onPress={() => navigation.goBack()}>
            <Ionicons name="arrow-back" size={24} color={colors.azul[300]} />
          </TouchableOpacity>

          <Text style={styles.headerTitle}>Editar perfil</Text>

          <View style={styles.backButton} />
        </View>

        {/* FOTO */}
        <View style={styles.photoSection}>
          <View style={styles.avatarContainer}>
            {/* ✅ key força rerender quando a foto muda */}
            <Image key={photoVersion} source={profileImage} style={styles.avatar} />

            <TouchableOpacity style={styles.editPhotoButton} onPress={handleChangePhoto}>
              <Ionicons name="camera" size={20} color="#FFF" />
            </TouchableOpacity>
          </View>

          <Text style={styles.photoLabel}>Toque no ícone para alterar sua foto de perfil</Text>
        </View>

        {/* FORM */}
        <View style={styles.form}>
          {/* Nome */}
          <View style={styles.inputGroup}>
            <Text style={styles.label}>Nome</Text>
            <View style={styles.inputContainer}>
              <Ionicons
                name="person-outline"
                size={20}
                color={colors.azul?.[300] ?? "#0EA5E9"}
                style={styles.inputIcon}
              />
              <TextInput
                value={name}
                onChangeText={setName}
                style={styles.input}
                placeholder="Digite seu nome"
                placeholderTextColor={colors.cinza?.[500] ?? "#64748B"}
              />
            </View>
          </View>

          {/* Senha */}
          <Text style={styles.sectionTitle}>Senha</Text>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Senha atual</Text>
            <View style={styles.inputContainer}>
              <Ionicons
                name="lock-closed-outline"
                size={20}
                color={colors.azul?.[300] ?? "#0EA5E9"}
                style={styles.inputIcon}
              />
              <TextInput
                value={currentPassword}
                onChangeText={setCurrentPassword}
                style={styles.input}
                placeholder="Digite sua senha atual"
                placeholderTextColor={colors.cinza?.[500] ?? "#64748B"}
                secureTextEntry
                autoCapitalize="none"
              />
            </View>
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Nova senha</Text>
            <View style={styles.inputContainer}>
              <Ionicons
                name="key-outline"
                size={20}
                color={colors.azul?.[300] ?? "#0EA5E9"}
                style={styles.inputIcon}
              />
              <TextInput
                value={newPassword}
                onChangeText={setNewPassword}
                style={styles.input}
                placeholder="Digite sua nova senha"
                placeholderTextColor={colors.cinza?.[500] ?? "#64748B"}
                secureTextEntry
                autoCapitalize="none"
              />
            </View>
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Confirmar nova senha</Text>
            <View style={styles.inputContainer}>
              <Ionicons
                name="checkmark-circle-outline"
                size={20}
                color={colors.azul?.[300] ?? "#0EA5E9"}
                style={styles.inputIcon}
              />
              <TextInput
                value={confirmNewPassword}
                onChangeText={setConfirmNewPassword}
                style={styles.input}
                placeholder="Confirme sua nova senha"
                placeholderTextColor={colors.cinza?.[500] ?? "#64748B"}
                secureTextEntry
                autoCapitalize="none"
              />
            </View>
          </View>
        </View>

        {/* BOTÃO SALVAR */}
        <TouchableOpacity style={styles.saveButton} onPress={handleSave}>
          <Text style={styles.saveButtonText}>SALVAR ALTERAÇÕES</Text>
        </TouchableOpacity>
      </ScrollView>

      {/* MODAL UPLOAD FOTO */}
      <Modal visible={photoModalVisible} transparent animationType="fade">
        <View style={styles.uploadOverlay}>
          <View style={styles.uploadContainer}>
            <Text style={styles.uploadTitle}>Selecione uma imagem</Text>

            <TouchableOpacity
              style={styles.uploadBox}
              onPress={pickAndUploadPhoto}
              disabled={isUploadingPhoto}
            >
              <Ionicons name="image-outline" size={40} color="#AAA" />
              <Text style={styles.uploadText}>
                {isUploadingPhoto ? "Enviando..." : "Enviar imagem"}
              </Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.uploadCancel}
              onPress={() => setPhotoModalVisible(false)}
            >
              <Text style={styles.uploadCancelText}>Fechar</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFFFFF",
  },
  content: {
    paddingHorizontal: 16,
    paddingTop: 16,
    paddingBottom: 24,
  },

  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingVertical: 10,
    marginBottom: 12,
  },
  backButton: {
    width: 40,
    height: 40,
    borderRadius: 10,
    alignItems: "center",
    justifyContent: "center",
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: "900",
    color: "#01415B",
  },

  photoSection: {
    alignItems: "center",
    marginBottom: 18,
    gap: 10,
  },
  avatarContainer: {
    position: "relative",
  },
  avatar: {
    width: 92,
    height: 92,
    borderRadius: 46,
    backgroundColor: "#EAF9FF",
  },
  editPhotoButton: {
    position: "absolute",
    right: -2,
    bottom: -2,
    width: 34,
    height: 34,
    borderRadius: 17,
    backgroundColor: "#01415B",
    alignItems: "center",
    justifyContent: "center",
    borderWidth: 2,
    borderColor: "#FFFFFF",
  },
  photoLabel: {
    color: "#01415B",
    opacity: 0.75,
    fontSize: 12,
    textAlign: "center",
  },

  form: {
    gap: 12,
  },
  inputGroup: {
    gap: 6,
  },
  label: {
    color: "#01415B",
    fontWeight: "800",
  },
  inputContainer: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#EAF9FF",
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#01415B",
    paddingHorizontal: 12,
    height: 48,
  },
  inputIcon: {
    marginRight: 10,
  },
  input: {
    flex: 1,
    color: "#01415B",
    fontWeight: "700",
  },

  sectionTitle: {
    marginTop: 6,
    fontSize: 16,
    fontWeight: "900",
    color: "#01415B",
  },

  saveButton: {
    marginTop: 18,
    backgroundColor: "#01415B",
    paddingVertical: 14,
    borderRadius: 12,
    alignItems: "center",
  },
  saveButtonText: {
    color: "#FFFFFF",
    fontWeight: "900",
  },

  uploadOverlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.35)",
    justifyContent: "center",
    paddingHorizontal: 18,
  },
  uploadContainer: {
    backgroundColor: "#FFFFFF",
    borderRadius: 14,
    padding: 16,
    gap: 12,
  },
  uploadTitle: {
    fontSize: 16,
    fontWeight: "900",
    color: "#01415B",
  },
  uploadBox: {
    borderWidth: 1,
    borderColor: "#D1D5DB",
    borderRadius: 12,
    paddingVertical: 18,
    alignItems: "center",
    justifyContent: "center",
    gap: 6,
    backgroundColor: "#F8FAFC",
  },
  uploadText: {
    color: "#01415B",
    fontWeight: "800",
  },
  uploadCancel: {
    alignItems: "center",
    paddingVertical: 10,
  },
  uploadCancelText: {
    color: "#01415B",
    fontWeight: "900",
  },
});