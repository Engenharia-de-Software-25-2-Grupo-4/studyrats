import { useState } from "react";
import { Modal, StyleSheet, Text, View, ScrollView, TouchableOpacity, TextInput, Image, Alert} from "react-native"
import { Ionicons } from '@expo/vector-icons';
import { colors } from "@/styles/colors";

export default function EditProfile(){

    const [name, setName] = useState("Kemilli Nicole");
    const [email, setEmail] = useState("kemilli.nicole@email.com");
    const [phone, setPhone] = useState("(83) 99999-9999");
    const [bio, setBio] = useState("Estudante de Ciência da Computação");
    const [university, setUniversity] = useState("UFCG");

    const [photoModalVisible, setPhotoModalVisible] = useState(false);
    const [codeModalVisible, setCodeModalVisible] = useState(false);
    const [changeModalVisible, setChangeModalVisible] = useState(false);

    const [verificationCode, setVerificationCode] = useState("");
    const [newValue, setNewValue] = useState("");
    const [securityAction, setSecurityAction] = useState(null);

    const [isLoading, setIsLoading] = useState(false);
    const [resendTimer, setResendTimer] = useState(30);
    const [canResend, setCanResend] = useState(false);

    const [profileImage, setProfileImage] = useState(require("@/assets/profile.jpg"));

    const cancelCodeModal = () => {
      setCodeModalVisible(false);
      setVerificationCode("");
      setIsLoading(false);
      setSecurityAction(null);
    };
  
    const cancelChangeModal = () => {
      setChangeModalVisible(false);
      setNewValue("");
      setVerificationCode("");
      setIsLoading(false);
      setSecurityAction(null);
    };

    const handleSave = () => {
        Alert.alert("Sucesso", "Perfil atualizado com sucesso!");
    };

    const handleChangePhoto = () => {
        setPhotoModalVisible(true);
    };

    const requestVerificationCode = (type) => {
      setSecurityAction(type);
      setCodeModalVisible(true);
      setResendTimer(30);
      setCanResend(false);
  
    const interval = setInterval(() => {
        setResendTimer(prev => {
            if (prev <= 1) {
                clearInterval(interval);
                setCanResend(true);
                return 0;
            }
            return prev - 1;
        });
    }, 1000);
    };  

    const validateVerificationCode = () => {
      if (verificationCode.length !== 6) return;
  
      setIsLoading(true);
  
      setTimeout(() => {
          setIsLoading(false);
  
          if (verificationCode === "123456") {
              setCodeModalVisible(false);
              setChangeModalVisible(true);
          } else {
              Alert.alert("Código inválido");
          }
      }, 800);
    };

    const confirmSecurityChange = () => {
      if (!newValue.trim()) return;
  
      setIsLoading(true);
  
      setTimeout(() => {
          if (securityAction === "email") {
              setEmail(newValue);
          }
  
          setIsLoading(false);
          setChangeModalVisible(false);
          setVerificationCode("");
          setNewValue("");
      }, 800);
    };

    return (
        <View style={styles.container}>
            <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
                {/* HEADER */}
                <View style={styles.header}>
                    <TouchableOpacity style={styles.backButton}>
                        <Ionicons name="arrow-back" size={24} color={colors.azul[300]} />
                    </TouchableOpacity>
                    <Text style={styles.headerTitle}>Configurações</Text>
                    <View style={styles.backButton} />
                </View>

                {/* FOTO */}
                <View style={styles.photoSection}>
                    <View style={styles.avatarContainer}>
                    <Image 
                      source={profileImage}
                      style={styles.avatar}
                      />

                        <TouchableOpacity 
                            style={styles.editPhotoButton}
                            onPress={handleChangePhoto}
                        >
                            <Ionicons name="camera" size={20} color="#FFF" />
                        </TouchableOpacity>
                    </View>
                    <Text style={styles.photoLabel}>Toque no ícone para alterar sua foto de perfil</Text>
                </View>

                {/* FORMULÁRIO (INTEIRO PRESERVADO) */}
                <View style={styles.form}>
                    
                    <View style={styles.inputGroup}>
                        <Text style={styles.label}>Nome</Text>
                        <View style={styles.inputContainer}>
                            <Ionicons name="person-outline" size={20} color={colors.azul[300]} style={styles.inputIcon} />
                            <TextInput
                                value={name}
                                onChangeText={setName}
                                style={styles.input}
                                placeholder="Digite seu nome"
                                placeholderTextColor={colors.cinza[500]}
                            />
                        </View>
                    </View>

                    <View style={styles.inputGroup}>
                        <Text style={styles.label}>Universidade</Text>
                        <View style={styles.inputContainer}>
                            <Ionicons name="school-outline" size={20} color={colors.azul[300]} style={styles.inputIcon} />
                            <TextInput
                                value={university}
                                onChangeText={setUniversity}
                                style={styles.input}
                                placeholder="Digite sua universidade"
                                placeholderTextColor={colors.cinza[500]}
                            />
                        </View>
                    </View>

                    <View style={styles.inputGroup}>
                        <Text style={styles.label}>Sobre mim</Text>
                        <View style={[styles.inputContainer, styles.textAreaContainer]}>
                            <Ionicons name="document-text-outline" size={20} color={colors.azul[300]} style={[styles.inputIcon, styles.textAreaIcon]} />
                            <TextInput
                                value={bio}
                                onChangeText={setBio}
                                style={[styles.input, styles.textArea]}
                                placeholder="Conte um pouco sobre você"
                                placeholderTextColor={colors.cinza[500]}
                                multiline
                                numberOfLines={4}
                                textAlignVertical="top"
                            />
                        </View>
                    </View>

                </View>

                {/* SEÇÃO DE SEGURANÇA */}
                <View style={styles.securitySection}>
                    <Text style={styles.sectionTitle}>Segurança</Text>
                    
                    {/* Alterar senha */}
                    <TouchableOpacity 
                        style={styles.securityButton}
                        onPress={() => requestVerificationCode("password")}
                    >
                        <View style={styles.securityButtonLeft}>
                            <Ionicons name="lock-closed-outline" size={22} color={colors.azul[300]} />
                            <Text style={styles.securityButtonText}>Alterar senha</Text>
                        </View>
                        <Ionicons name="chevron-forward" size={20} color={colors.cinza[500]} />
                    </TouchableOpacity>

                    {/* Alterar e-mail */}
                    <TouchableOpacity 
                        style={styles.securityButton}
                        onPress={() => requestVerificationCode("email")}
                    >
                        <View style={styles.securityButtonLeft}>
                            <Ionicons name="mail-outline" size={22} color={colors.azul[300]} />
                            <Text style={styles.securityButtonText}>Alterar e-mail</Text>
                        </View>
                        <Ionicons name="chevron-forward" size={20} color={colors.cinza[500]} />
                    </TouchableOpacity>

                </View>


                {/* BOTÕES */}
                <TouchableOpacity style={styles.saveButton} onPress={handleSave}>
                    <Text style={styles.saveButtonText}>SALVAR ALTERAÇÕES</Text>
                </TouchableOpacity>

                <TouchableOpacity style={styles.cancelButton}>
                    <Text style={styles.cancelButtonText}>Cancelar</Text>
                </TouchableOpacity>

            </ScrollView>

            {/* ================= MODAL CÓDIGO ================= */}
            <Modal visible={codeModalVisible} transparent animationType="fade">
                <View style={styles.modalOverlay}>
                    <View style={styles.modalBox}>
                        <Text style={styles.stepText}>Etapa 1 de 2</Text>
                        <Text style={styles.modalTitle}>Verificação de segurança</Text>

                        <Text style={styles.modalSubtitle}>
                            Enviamos um código para:
                        </Text>
                        <Text style={styles.maskedEmail}>
                            {email.replace(/(.{2}).+(@.+)/, "$1****$2")}
                        </Text>

                        <TextInput
                            value={verificationCode}
                            onChangeText={setVerificationCode}
                            keyboardType="numeric"
                            maxLength={6}
                            style={styles.codeInput}
                        />

                        <TouchableOpacity
                            style={[
                                styles.modalButton,
                                verificationCode.length !== 6 && { opacity: 0.5 }
                            ]}
                            disabled={verificationCode.length !== 6 || isLoading}
                            onPress={validateVerificationCode}
                        >
                            <Text style={styles.modalButtonText}>
                                {isLoading ? "Verificando..." : "Confirmar Código"}
                            </Text>
                        </TouchableOpacity>

                        {/* BOTÃO CANCELAR */}
                        <TouchableOpacity
                            style={styles.modalCancelButton}
                            onPress={cancelCodeModal}
                        >
                            <Text style={styles.modalCancelText}>Cancelar</Text>
                        </TouchableOpacity>

                    </View>
                </View>
            </Modal>

            {/* ================= MODAL ALTERAÇÃO ================= */}
            <Modal visible={changeModalVisible} transparent animationType="fade">
                <View style={styles.modalOverlay}>
                    <View style={styles.modalBox}>
                        <Text style={styles.stepText}>Etapa 2 de 2</Text>

                        <Text style={styles.modalTitle}>
                            {securityAction === "password"
                                ? "Defina sua nova senha"
                                : "Defina seu novo e-mail"}
                        </Text>

                        <TextInput
                            value={newValue}
                            onChangeText={setNewValue}
                            secureTextEntry={securityAction === "password"}
                            autoCapitalize="none"
                            style={styles.modalInput}
                        />

                        <TouchableOpacity
                            style={[
                                styles.modalButton,
                                !newValue.trim() && { opacity: 0.5 }
                            ]}
                            disabled={!newValue.trim() || isLoading}
                            onPress={confirmSecurityChange}
                        >
                            <Text style={styles.modalButtonText}>
                                {isLoading ? "Salvando..." : "Salvar"}
                            </Text>
                        </TouchableOpacity>

                        {/* BOTÃO CANCELAR */}
                        <TouchableOpacity
                            style={styles.modalCancelButton}
                            onPress={cancelChangeModal}
                        >
                            <Text style={styles.modalCancelText}>Cancelar</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </Modal>

            {/* ================= MODAL UPLOAD FOTO ================= */}
            <Modal visible={photoModalVisible} transparent animationType="fade">
                <View style={styles.uploadOverlay}>
                    <View style={styles.uploadContainer}>
                        <Text style={styles.uploadTitle}>Selecione uma imagem</Text>

                        <TouchableOpacity
                            style={styles.uploadBox}
                            onPress={async () => {

                                // ===============================
                                // 🔗 INTEGRAÇÃO IMAGE PICKER
                                // import * as ImagePicker from 'expo-image-picker';
                                //
                                // const result = await ImagePicker.launchImageLibraryAsync({
                                //   mediaTypes: ImagePicker.MediaTypeOptions.Images,
                                //   allowsEditing: true,
                                //   aspect: [1,1],
                                //   quality: 0.8,
                                // });
                                //
                                // if (!result.canceled) {
                                //   setProfileImage({ uri: result.assets[0].uri });
                                // }
                                // ===============================

                                Alert.alert("Integração necessária", "Implementar ImagePicker aqui.");
                            }}
                        >
                            <Ionicons name="image-outline" size={40} color="#AAA" />
                            <Text style={styles.uploadText}>Enviar imagem</Text>
                        </TouchableOpacity>

                        <TouchableOpacity
                            style={styles.uploadCancel}
                            onPress={() => setPhotoModalVisible(false)}
                        >
                            <Text style={styles.uploadCancelText}>Cancelar</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </Modal>
        </View>
    )
}

const styles = StyleSheet.create({
  containerRoot: {
    flex: 1,
    backgroundColor: "#FFFFFF",
  },

  contentWrap: {
    flex: 1,
    backgroundColor: "#FFFFFF",
  },

  screen: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    paddingHorizontal: 16,
    paddingTop: 16,
  },

  // Header do perfil
  profileTop: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: 20,
    paddingVertical: 10,
  },  

  profileLeft: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
  },

  avatar: {
    width: 56,
    height: 56,
    borderRadius: 28,
    backgroundColor: "#EAF9FF",
  },  

  profileInfo: {
    gap: 4,
  },

  profileName: {
    fontSize: 22,
    fontWeight: "900",
    color: "#01415B",
  },  

  statsRow: {
    flexDirection: "row",
    gap: 16,
  },

  stat: {
    flexDirection: "row",
    alignItems: "baseline",
    gap: 6,
  },

  statNumber: {
    fontSize: 18,
    fontWeight: "900",
    color: "#01415B",
  },  

  statLabel: {
    fontSize: 13,
    color: "#01415B",
    opacity: 0.7,
  },  

  // Botões de ação
  actionsRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    marginBottom: 12,
  },

  actionButton: {
    flex: 1,
    borderWidth: 1,
    borderColor: "#01415B",
    paddingVertical: 10,
    borderRadius: 8,
    alignItems: "center",
    backgroundColor: "#FFFFFF",
  },

  actionButtonText: {
    color: "#01415B",
    fontWeight: "800",
    fontSize: 12,
  },

  iconButton: {
    width: 42,
    height: 42,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#01415B",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#FFFFFF",
  },

  // Tabs do perfil
  perfilTabsRow: {
    flexDirection: "row",
    gap: 12,
    marginBottom: 10,
    alignItems: "center",
  },

  perfilTab: {
    paddingVertical: 8,
  },

  perfilTabText: {
    color: "#01415B",
    fontWeight: "800",
    fontSize: 13,
    opacity: 0.6,
  },

  perfilTabTextActive: {
    opacity: 1,
    textDecorationLine: "underline",
  },

  // Grid desafios
  gridContent: {
    paddingBottom: 16,
  },

  gridRow: {
    justifyContent: "space-between",
    marginBottom: 12,
  },

  cardItem: {
    width: "48%",
    borderRadius: 12,
    overflow: "hidden",
    backgroundColor: "#EAF9FF",
  },

  cardImageWrap: {
    height: 120,
    backgroundColor: "#EAF9FF",
  },

  cardLogoFallback: {
    flex: 1,
    backgroundColor: "#01415B",
    alignItems: "center",
    justifyContent: "center",
  },

  fallbackLogo: {
    width: 72,
    height: 72,
    resizeMode: "contain",
    opacity: 0.95,
  },

  cardFooter: {
    padding: 10,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    gap: 8,
  },

  cardTitle: {
    flex: 1,
    color: "#01415B",
    fontWeight: "800",
    fontSize: 12,
  },

  // Conquistas
  conquistasWrap: {
    paddingTop: 8,
    gap: 10,
  },

  conquistaItem: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    backgroundColor: "#EAF9FF",
    borderRadius: 10,
    padding: 12,
  },

  conquistaText: {
    color: "#01415B",
    fontWeight: "700",
  },

  // Modal edição
  modalOverlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.35)",
    justifyContent: "center",
    paddingHorizontal: 18,
  },

  modalBox: {
    backgroundColor: "#FFFFFF",
    borderRadius: 14,
    padding: 16,
  },

  modalTitle: {
    fontSize: 16,
    fontWeight: "900",
    color: "#01415B",
    marginBottom: 12,
  },

  modalLabel: {
    color: "#01415B",
    fontWeight: "700",
    marginBottom: 6,
  },

  modalInput: {
    backgroundColor: "#EAF9FF",
    borderRadius: 10,
    padding: 12,
    color: "#01415B",
    borderWidth: 1,
    borderColor: "#01415B",
  },

  errorMsg: {
    marginTop: 10,
    color: "#C62828",
    fontWeight: "700",
  },

  modalActions: {
    flexDirection: "row",
    gap: 10,
    marginTop: 14,
  },

  modalBtnSecondary: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#01415B",
    alignItems: "center",
  },

  modalBtnSecondaryText: {
    color: "#01415B",
    fontWeight: "900",
    fontSize: 12,
  },

  modalBtnPrimary: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 10,
    backgroundColor: "#01415B",
    alignItems: "center",
  },

  modalBtnPrimaryText: {
    color: "#FFFFFF",
    fontWeight: "900",
    fontSize: 12,
  },

  // Bottom nav
  bottomNav: {
    height: 64,
    backgroundColor: "#01415B",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-around",
    paddingBottom: 6,
    paddingTop: 6,
  },

  navItem: {
    alignItems: "center",
    justifyContent: "center",
    gap: 2,
  },

  navText: {
    color: "#FFFFFF",
    fontSize: 11,
    opacity: 0.8,
    fontWeight: "700",
  },

  navTextActive: {
    opacity: 1,
    textDecorationLine: "underline",
  },

  // Destaque visual durante edição
  profileTopEditing: {
    backgroundColor: "#EAF9FF",
    borderRadius: 14,
    paddingHorizontal: 12,
  },

  profileNameEditing: {
    textDecorationLine: "underline",
  },

  avatarWrap: {
    position: "relative",
  },

  editBadge: {
    position: "absolute",
    right: -6,
    bottom: -6,
    width: 26,
    height: 26,
    borderRadius: 13,
    backgroundColor: "#FFFFFF",
    borderWidth: 1,
    borderColor: "#01415B",
    alignItems: "center",
    justifyContent: "center",
  },

  // Preview do perfil dentro do modal
  editPreview: {
    flexDirection: "row",
    alignItems: "center",
    gap: 12,
    backgroundColor: "#EAF9FF",
    borderRadius: 12,
    padding: 12,
    marginBottom: 14,
  },

  editPreviewAvatar: {
    width: 46,
    height: 46,
    borderRadius: 23,
    backgroundColor: "#FFFFFF",
  },

  editPreviewName: {
    color: "#01415B",
    fontWeight: "900",
    fontSize: 14,
  },

  // Botões de escolha no modal (foto/nome)
  choiceButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    borderWidth: 1,
    borderColor: "#01415B",
    backgroundColor: "#FFFFFF",
    borderRadius: 10,
    paddingVertical: 12,
    paddingHorizontal: 12,
    marginTop: 10,
  },

  choiceButtonText: {
    color: "#01415B",
    fontWeight: "900",
    fontSize: 12,
  },

  photoHint: {
    marginTop: 10,
    color: "#01415B",
    opacity: 0.7,
    fontSize: 12,
  },

}   );
