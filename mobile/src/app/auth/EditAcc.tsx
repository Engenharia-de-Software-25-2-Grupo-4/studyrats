import React, { useCallback, useMemo, useState } from "react";
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
  BackHandler,
  ActivityIndicator,
  Platform,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import { colors } from "@/styles/colors";
import { fetchProfilePhoto } from "@/server/estudanteInfo/fetchProfilePhoto";
import * as ImagePicker from "expo-image-picker";
import { uploadProfilePhoto } from "@/server/estudanteInfo/uploadProfilePhoto";
import { changePassword } from "@/services/changePassword";
import { auth } from "@/firebaseConfig";
import { fetchUserInfo } from "@/server/estudanteInfo/fetchUserInfo";
import { changeUsername } from "@/server/estudanteInfo/changeUsername";
import {
  EmailAuthProvider,
  reauthenticateWithCredential,
  deleteUser,
} from "firebase/auth";
import { deleteStudentAccount } from "@/server/estudanteInfo/deleteStudentAccount";
import { privacyPolicyText } from "@/assets/privacy_policy";

function withCacheBuster(uri: string) {
  if (/^https?:\/\//i.test(uri)) {
    return `${uri}${uri.includes("?") ? "&" : "?"}t=${Date.now()}`;
  }
  return uri;
}

export default function EditProfile() {
  const navigation = useNavigation();

  const [name, setName] = useState("");
  const [initialName, setInitialName] = useState("");
  const [email, setEmail] = useState("");

  const [profileImage, setProfileImage] = useState<ImageSourcePropType>(
    require("@/assets/default_profile.jpg")
  );
  const [originalProfileImage, setOriginalProfileImage] =
    useState<ImageSourcePropType>(require("@/assets/default_profile.jpg"));

  const [photoModalVisible, setPhotoModalVisible] = useState(false);

  // Password toggle + fields
  const [isPasswordOpen, setIsPasswordOpen] = useState(false);
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);

  const clearPasswordFields = useCallback(() => {
    setCurrentPassword("");
    setNewPassword("");
    setShowCurrentPassword(false);
    setShowNewPassword(false);
  }, []);

  const [pendingPhotoUri, setPendingPhotoUri] = useState<string | null>(null);
  const [isUploadingPhoto, setIsUploadingPhoto] = useState(false);
  const [photoVersion, setPhotoVersion] = useState(0);

  const [isLoadingProfile, setIsLoadingProfile] = useState(true);

  const [privacyModalVisible, setPrivacyModalVisible] = useState(false);

  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [deletePassword, setDeletePassword] = useState("");
  const [showDeletePassword, setShowDeletePassword] = useState(false);
  const [isDeletingAccount, setIsDeletingAccount] = useState(false);

  useFocusEffect(
    useCallback(() => {
      let alive = true;

      (async () => {
        try {
          setIsLoadingProfile(true);

          const user = auth.currentUser;
          if (!user?.uid) return;

          const [userInfo, source] = await Promise.all([
            fetchUserInfo(user.uid),
            fetchProfilePhoto(user.uid),
          ]);

          if (!alive) return;

          if (userInfo?.nome) {
            setName(userInfo.nome);
            setInitialName(userInfo.nome);
          } else {
            setName("");
            setInitialName("");
          }

          setEmail(userInfo?.email ?? user.email ?? "");

          let finalSource: any = source;
          if (typeof (source as any)?.uri === "string") {
            finalSource = { uri: withCacheBuster((source as any).uri) };
          }

          setProfileImage(finalSource);
          setOriginalProfileImage(finalSource);
          setPendingPhotoUri(null);
          setPhotoVersion((v) => v + 1);

          // Se quiser: sempre fechar senha ao reabrir a tela
          setIsPasswordOpen(false);
          clearPasswordFields();
        } catch (e) {
          console.warn("Erro ao buscar dados do perfil:", e);
        } finally {
          if (alive) setIsLoadingProfile(false);
        }
      })();

      return () => {
        alive = false;
      };
    }, [clearPasswordFields])
  );

  const hasUnsavedChanges = useMemo(() => {
    const nameChanged = name.trim() !== initialName.trim();

    const passwordTouched =
      isPasswordOpen &&
      (!!currentPassword.trim() ||
        !!newPassword.trim() );

    const photoTouched = !!pendingPhotoUri;

    return nameChanged || passwordTouched || photoTouched;
  }, [
    name,
    initialName,
    currentPassword,
    newPassword,
    pendingPhotoUri,
    isPasswordOpen,
  ]);

  const resetUnsavedChanges = useCallback(() => {
    setProfileImage(originalProfileImage);
    setPhotoVersion((v) => v + 1);
    setPendingPhotoUri(null);

    setName(initialName);

    setIsPasswordOpen(false);
    clearPasswordFields();
  }, [originalProfileImage, initialName, clearPasswordFields]);

  const confirmBackIfDirty = useCallback(() => {
    if (!hasUnsavedChanges) {
      navigation.goBack();
      return;
    }

    Alert.alert(
      "Alterações não salvas",
      "Você fez alterações que ainda não foram salvas. Deseja descartá-las e sair?",
      [
        { text: "Continuar editando", style: "cancel" },
        {
          text: "Descartar e sair",
          style: "destructive",
          onPress: () => {
            resetUnsavedChanges();
            navigation.goBack();
          },
        },
      ]
    );
  }, [hasUnsavedChanges, navigation, resetUnsavedChanges]);

  useFocusEffect(
    useCallback(() => {
      const onBackPress = () => {
        confirmBackIfDirty();
        return true;
      };

      const sub = BackHandler.addEventListener(
        "hardwareBackPress",
        onBackPress
      );
      return () => sub.remove();
    }, [confirmBackIfDirty])
  );

  const handleSave = async () => {
    const tryingToChangePassword =
      isPasswordOpen &&
      (!!currentPassword.trim() ||
        !!newPassword.trim());

    const tryingToChangeName = name.trim() !== initialName.trim();
    const tryingToChangePhoto = !!pendingPhotoUri;

    if (!tryingToChangePassword && !tryingToChangePhoto && !tryingToChangeName) {
      Alert.alert("Atenção", "Nenhuma alteração para salvar.");
      return;
    }

    if (tryingToChangeName) {
      const userEmail = email || auth.currentUser?.email || "";
      await changeUsername(name, userEmail);
      setInitialName(name.trim());
    }

    if (tryingToChangePassword) {
      if (
        !currentPassword.trim() ||
        !newPassword.trim()
      ) {
        Alert.alert("Atenção", "Preencha todos os campos de senha para alterar.");
        return;
      }
      if (newPassword.length < 6) {
        Alert.alert("Atenção", "A nova senha deve ter pelo menos 6 caracteres.");
        return;
      }

    }

    try {
      const user = auth.currentUser;
      if (!user) {
        Alert.alert("Sessão expirada", "Faça login novamente.");
        return;
      }

      setIsUploadingPhoto(true);

      if (tryingToChangePhoto && pendingPhotoUri) {
        await uploadProfilePhoto(pendingPhotoUri);

        const source = await fetchProfilePhoto(user.uid);
        if (typeof (source as any)?.uri === "string") {
          const final = { uri: withCacheBuster((source as any).uri) };
          setProfileImage(final);
          setOriginalProfileImage(final);
        } else {
          setProfileImage(source);
          setOriginalProfileImage(source);
        }

        setPhotoVersion((v) => v + 1);
        setPendingPhotoUri(null);
      }

      if (tryingToChangePassword) {
        await changePassword(currentPassword, newPassword);

        // ✅ só fecha/limpa se deu certo
        clearPasswordFields();
        setIsPasswordOpen(false);
      }

      Alert.alert("Sucesso", "Perfil atualizado com sucesso!");
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
        Alert.alert(
          "Atenção",
          "A nova senha é fraca. Use pelo menos 6 caracteres."
        );
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

      Alert.alert("Erro", e?.message ?? "Não foi possível salvar as alterações.");
    } finally {
      setIsUploadingPhoto(false);
    }
  };

  const handleChangePhoto = () => setPhotoModalVisible(true);

  async function pickPhotoOnly() {
    try {
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
      setProfileImage({ uri: picked.uri });
      setPhotoVersion((v) => v + 1);
      setPendingPhotoUri(picked.uri);
      setPhotoModalVisible(false);
    } catch (e: any) {
      console.warn("Erro ao escolher foto:", e);
      Alert.alert("Erro", e?.message ?? "Falha ao escolher foto.");
    }
  }

  function openPrivacyModal() {
    setPrivacyModalVisible(true);
  }

  function requestDeleteAccount() {
    setDeletePassword("");
    setShowDeletePassword(false);
    setDeleteModalVisible(true);
  }

  async function confirmDeleteAccount() {
    try {
      if (!deletePassword.trim()) {
        Alert.alert("Atenção", "Digite sua senha para confirmar a exclusão.");
        return;
      }

      const user = auth.currentUser;
      if (!user) {
        Alert.alert("Sessão expirada", "Faça login novamente.");
        return;
      }

      if (!user.email) {
        Alert.alert(
          "Atenção",
          "Sua conta não possui email/senha para validação. Faça login novamente usando email/senha para excluir por aqui."
        );
        return;
      }

      setIsDeletingAccount(true);

      const cred = EmailAuthProvider.credential(user.email, deletePassword);
      await reauthenticateWithCredential(user, cred);

      const backendRes = await deleteStudentAccount();
      if (!backendRes.ok) {
        if (backendRes.status === 404) {
          Alert.alert("Conta não encontrada", "Não encontramos seus dados no sistema.");
          return;
        }
        if (backendRes.status === 401) {
          Alert.alert("Sessão expirada", "Faça login novamente e tente de novo.");
          return;
        }

        Alert.alert("Erro", backendRes.message ?? "Não foi possível excluir sua conta.");
        return;
      }

      await deleteUser(user);

      setDeleteModalVisible(false);

      Alert.alert("Conta excluída", "Sua conta e dados foram removidos com sucesso.");

      try {
        (navigation as any).reset?.({
          index: 0,
          routes: [{ name: "Login" }],
        });
      } catch {
        try {
          (navigation as any).navigate?.("Login");
        } catch {
          (navigation as any).goBack?.();
        }
      }
    } catch (e: any) {
      const code = e?.code as string | undefined;

      if (code === "auth/wrong-password") {
        Alert.alert("Atenção", "Senha incorreta.");
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

      Alert.alert("Erro", e?.message ?? "Não foi possível excluir a conta.");
    } finally {
      setIsDeletingAccount(false);
    }
  }

  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        {/* HEADER */}
        <View style={styles.header}>
          <TouchableOpacity
            style={styles.backButton}
            onPress={() => navigation.goBack()}
            disabled={isUploadingPhoto || isLoadingProfile}
          >
            <Ionicons name="arrow-back" size={24} color={colors.azul[300]} />
          </TouchableOpacity>

          <Text style={styles.headerTitle}>Editar perfil</Text>

          <View style={styles.backButton} />
        </View>

        {/* FOTO */}
        <View style={styles.photoSection}>
          <View style={styles.avatarContainer}>
            <Image key={photoVersion} source={profileImage} style={styles.avatar} />

            <TouchableOpacity
              style={styles.editPhotoButton}
              onPress={handleChangePhoto}
              disabled={isUploadingPhoto || isLoadingProfile}
            >
              <Ionicons name="camera" size={20} color="#FFF" />
            </TouchableOpacity>
          </View>

          <Text style={styles.photoLabel}>Toque no ícone para alterar sua foto de perfil</Text>

          {!!pendingPhotoUri && (
            <Text style={styles.pendingHint}>Clique em SALVAR para aplicar alterações pendentes.</Text>
          )}
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

          {/* Senha (Toggle mais bonito no estilo “card” da imagem) */}
          <TouchableOpacity
            style={[
              styles.passwordCard,
              isPasswordOpen && styles.passwordCardOpen,
              (isUploadingPhoto || isLoadingProfile) && { opacity: 0.7 },
            ]}
            onPress={() => {
              setIsPasswordOpen((open) => {
                const next = !open;
                if (!next) clearPasswordFields();
                return next;
              });
            }}
            activeOpacity={0.85}
            disabled={isUploadingPhoto || isLoadingProfile}
          >
            <View style={styles.passwordCardLeft}>
              <View style={styles.passwordIconPill}>
                <Ionicons
                  name={isPasswordOpen ? "lock-open-outline" : "lock-closed-outline"}
                  size={18}
                  color="#01415B"
                />
              </View>

              <View style={styles.passwordTextBlock}>
                <Text style={styles.passwordTitle}>Senha</Text>
                <Text style={styles.passwordSubtitle}>
                  {isPasswordOpen ? "Preencha os campos e aperte salvar para alterar sua senha" : "Toque para alterar sua senha"}
                </Text>
              </View>
            </View>

            <View style={styles.passwordCardRight}>
              <View style={styles.passwordActionPill}>
                <Text style={styles.passwordActionText}>
                  {isPasswordOpen ? "Ocultar" : "Alterar"}
                </Text>
                <Ionicons
                  name={isPasswordOpen ? "chevron-up" : "chevron-down"}
                  size={18}
                  color="#01415B"
                />
              </View>
            </View>
          </TouchableOpacity>

          {isPasswordOpen && (
            <View style={styles.passwordBlock}>
              {/* Senha atual */}
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
                    secureTextEntry={!showCurrentPassword}
                    autoCapitalize="none"
                    editable={!isUploadingPhoto}
                  />
                  <TouchableOpacity
                    onPress={() => setShowCurrentPassword((v) => !v)}
                    style={styles.eyeButton}
                    hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
                    disabled={isUploadingPhoto}
                  >
                    <Ionicons
                      name={showCurrentPassword ? "eye-off-outline" : "eye-outline"}
                      size={20}
                      color={colors.cinza?.[500] ?? "#64748B"}
                    />
                  </TouchableOpacity>
                </View>
              </View>

              {/* Nova senha */}
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
                    secureTextEntry={!showNewPassword}
                    autoCapitalize="none"
                    editable={!isUploadingPhoto}
                  />
                  <TouchableOpacity
                    onPress={() => setShowNewPassword((v) => !v)}
                    style={styles.eyeButton}
                    hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
                    disabled={isUploadingPhoto}
                  >
                    <Ionicons
                      name={showNewPassword ? "eye-off-outline" : "eye-outline"}
                      size={20}
                      color={colors.cinza?.[500] ?? "#64748B"}
                    />
                  </TouchableOpacity>
                </View>
              </View>
            </View>
          )}
        </View>

        {/* CONTA */}
        <Text style={styles.sectionTitle}>Conta</Text>

        <TouchableOpacity
          style={styles.secondaryAction}
          onPress={openPrivacyModal}
          disabled={isUploadingPhoto || isLoadingProfile || isDeletingAccount}
        >
          <Ionicons
            name="document-text-outline"
            size={20}
            color={colors.azul?.[300] ?? "#0EA5E9"}
          />
          <Text style={styles.secondaryActionText}>Política de Privacidade e Termos de Uso</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.dangerAction}
          onPress={requestDeleteAccount}
          disabled={isUploadingPhoto || isLoadingProfile || isDeletingAccount}
        >
          <Ionicons name="trash-outline" size={20} color="#a00505" />
          <Text style={styles.dangerActionText}>Excluir conta</Text>
        </TouchableOpacity>

        {/* BOTÃO SALVAR */}
        <TouchableOpacity
          style={[styles.saveButton, isUploadingPhoto && { opacity: 0.7 }]}
          onPress={handleSave}
          disabled={isUploadingPhoto}
        >
          <Text style={styles.saveButtonText}>
            {isUploadingPhoto ? "SALVANDO..." : "SALVAR"}
          </Text>
        </TouchableOpacity>
      </ScrollView>

      {/* MODAL UPLOAD FOTO */}
      <Modal visible={photoModalVisible} transparent animationType="fade">
        <View style={styles.uploadOverlay}>
          <View style={styles.uploadContainer}>
            <Text style={styles.uploadTitle}>Selecione uma imagem</Text>

            <TouchableOpacity style={styles.uploadBox} onPress={pickPhotoOnly}>
              <Ionicons name="image-outline" size={40} color="#AAA" />
              <Text style={styles.uploadText}>Escolher imagem</Text>
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

      {/* MODAL PRIVACIDADE/TERMOS */}
      <Modal visible={privacyModalVisible} transparent animationType="fade">
        <View style={styles.modalOverlay}>
          <View style={styles.modalCard}>
            <Text style={styles.modalTitle}>Política de Privacidade e Termos de Uso</Text>

            <ScrollView style={styles.modalScroll} showsVerticalScrollIndicator>
              <Text style={styles.modalBodyText}>{privacyPolicyText}</Text>
            </ScrollView>

            <TouchableOpacity
              style={styles.modalCloseButton}
              onPress={() => setPrivacyModalVisible(false)}
            >
              <Text style={styles.modalCloseButtonText}>Fechar</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      {/* MODAL EXCLUIR CONTA */}
      <Modal visible={deleteModalVisible} transparent animationType="fade">
        <View style={styles.modalOverlay}>
          <View style={styles.modalCard}>
            <Text style={styles.modalTitle}>Excluir conta</Text>

            <Text style={styles.deleteWarningText}>
              Esta ação é permanente. Para confirmar, digite sua senha.
            </Text>

            <View style={styles.inputGroup}>
              <Text style={styles.label}>Senha</Text>
              <View style={styles.inputContainer}>
                <Ionicons
                  name="lock-closed-outline"
                  size={20}
                  color={colors.azul?.[300] ?? "#0EA5E9"}
                  style={styles.inputIcon}
                />
                <TextInput
                  value={deletePassword}
                  onChangeText={setDeletePassword}
                  style={styles.input}
                  placeholder="Digite sua senha"
                  placeholderTextColor={colors.cinza?.[500] ?? "#64748B"}
                  secureTextEntry={!showDeletePassword}
                  autoCapitalize="none"
                  editable={!isDeletingAccount}
                />
                <TouchableOpacity
                  onPress={() => setShowDeletePassword((v) => !v)}
                  style={styles.eyeButton}
                  hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
                  disabled={isDeletingAccount}
                >
                  <Ionicons
                    name={showDeletePassword ? "eye-off-outline" : "eye-outline"}
                    size={20}
                    color={colors.cinza?.[500] ?? "#64748B"}
                  />
                </TouchableOpacity>
              </View>
            </View>

            <View style={styles.modalRow}>
              <TouchableOpacity
                style={[styles.modalSecondaryButton, isDeletingAccount && { opacity: 0.7 }]}
                onPress={() => setDeleteModalVisible(false)}
                disabled={isDeletingAccount}
              >
                <Text style={styles.modalSecondaryButtonText}>Cancelar</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={[styles.modalDangerButton, isDeletingAccount && { opacity: 0.7 }]}
                onPress={confirmDeleteAccount}
                disabled={isDeletingAccount}
              >
                {isDeletingAccount ? (
                  <ActivityIndicator />
                ) : (
                  <Text style={styles.modalDangerButtonText}>Excluir</Text>
                )}
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {isLoadingProfile && (
        <View style={styles.loadingOverlay}>
          <View style={styles.loadingCard}>
            <ActivityIndicator size="large" color="#01415B" />
            <Text style={styles.loadingText}>Carregando informações...</Text>
          </View>
        </View>
      )}
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
    paddingBottom: 48,
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
    gap: 10,
  },
  inputGroup: {
    gap: 5,
  },
  label: {
    color: "#01415B",
    fontWeight: "800",
  },

  inputContainer: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#FFFFFF",
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#01415B",
    paddingHorizontal: 12,
    height: 44,
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
    marginTop: 10,
    fontSize: 16,
    fontWeight: "900",
    color: "#01415B",
  },

  saveButton: {
    marginTop: 14,
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
    backgroundColor: "#FFFFFF",
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

  eyeButton: {
    paddingLeft: 10,
    paddingRight: 4,
    alignItems: "center",
    justifyContent: "center",
  },

  pendingHint: {
    marginTop: 6,
    color: colors.cinza?.[500] ?? "#64748B",
  },

  loadingOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: "rgba(255,255,255,0.75)",
    alignItems: "center",
    justifyContent: "center",
  },

  loadingCard: {
    backgroundColor: "rgba(250,255,255,0.75)",
    borderRadius: 14,
    paddingVertical: 16,
    paddingHorizontal: 18,
    alignItems: "center",
    gap: 10,
    borderWidth: 1,
    borderColor: "#01415B",
  },

  loadingText: {
    color: "#01415B",
    fontWeight: "900",
  },

  secondaryAction: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    paddingVertical: 14,
    paddingHorizontal: 14,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    backgroundColor: "#FFFFFF",
    marginTop: 10,
  },
  secondaryActionText: {
    fontSize: 14,
    color: "#0F172A",
    flex: 1,
  },

  dangerAction: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    paddingVertical: 14,
    paddingHorizontal: 14,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#FCA5A5",
    backgroundColor: "#FEF2F2",
    marginTop: 12,
  },
  dangerActionText: {
    fontSize: 14,
    color: "#B91C1C",
    fontWeight: "600",
    flex: 1,
  },

  modalOverlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.45)",
    justifyContent: "center",
    padding: 16,
  },
  modalCard: {
    backgroundColor: "#FFF",
    borderRadius: 16,
    padding: 16,
    maxHeight: "85%",
  },
  modalTitle: {
    fontSize: 16,
    fontWeight: "700",
    color: "#0F172A",
    marginBottom: 12,
  },
  modalScroll: {
    borderWidth: 1,
    borderColor: "#E2E8F0",
    borderRadius: 12,
    padding: 12,
    marginBottom: 12,
    backgroundColor: "#FFFFFF",
  },
  modalBodyText: {
    fontSize: 13,
    color: "#0F172A",
    lineHeight: 18,
  },
  modalCloseButton: {
    paddingVertical: 12,
    borderRadius: 12,
    backgroundColor: "#01415B",
    alignItems: "center",
  },
  modalCloseButtonText: {
    color: "#FFF",
    fontWeight: "700",
  },

  deleteWarningText: {
    color: "#7F1D1D",
    backgroundColor: "#FEF2F2",
    borderWidth: 1,
    borderColor: "#FCA5A5",
    padding: 12,
    borderRadius: 12,
    marginBottom: 12,
    fontSize: 13,
    lineHeight: 18,
  },

  modalRow: {
    flexDirection: "row",
    gap: 10,
    marginTop: 6,
  },
  modalSecondaryButton: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: "#E2E8F0",
    backgroundColor: "#FFFFFF",
    alignItems: "center",
  },
  modalSecondaryButtonText: {
    color: "#0F172A",
    fontWeight: "700",
  },
  modalDangerButton: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 12,
    backgroundColor: "#DC2626",
    alignItems: "center",
    justifyContent: "center",
  },
  modalDangerButtonText: {
    color: "#FFF",
    fontWeight: "800",
  },

  // ✅ Toggle de senha com fundo branco
  passwordCard: {
    marginTop: 6,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingVertical: 12,
    paddingHorizontal: 12,
    borderRadius: 14,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#01415B",
  },
  passwordCardOpen: {
    borderColor: "#01415B",
  },
  passwordCardLeft: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    flex: 1,
  },
  passwordIconPill: {
    width: 34,
    height: 34,
    borderRadius: 12,
    backgroundColor: "rgba(1,65,91,0.10)",
    alignItems: "center",
    justifyContent: "center",
    borderWidth: 1,
    borderColor: "rgba(1,65,91,0.18)",
  },
  passwordTextBlock: {
    flex: 1,
    gap: 2,
  },
  passwordTitle: {
    color: "#01415B",
    fontWeight: "900",
    fontSize: 15,
  },
  passwordSubtitle: {
    color: "#0F172A",
    opacity: 0.65,
    fontSize: 12,
    fontWeight: "700",
  },
  passwordCardRight: {
    marginLeft: 10,
  },
  passwordActionPill: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    paddingVertical: 8,
    paddingHorizontal: 10,
    borderRadius: 999,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#D7EEF8",
  },
  passwordActionText: {
    color: "#01415B",
    fontWeight: "900",
    fontSize: 12,
  },

  passwordBlock: {
    gap: 12,
    marginTop: 10,
  },
});