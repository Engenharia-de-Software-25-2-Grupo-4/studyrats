import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
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
