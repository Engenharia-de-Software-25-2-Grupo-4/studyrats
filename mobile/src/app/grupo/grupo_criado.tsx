import { View, Text, StyleSheet, TouchableOpacity, TextInput, ScrollView, Image } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation, useRoute, RouteProp, NavigationProp } from '@react-navigation/native';
import { StackParams } from '@/utils/routesStack'; 
import { Menu } from "@/components/Menu";
import { categories } from "@/utils/categories";

export default function GrupoCriado() {
  const navigation = useNavigation<NavigationProp<StackParams>>();
  const route = useRoute();
  
  const dados = (route.params as any)?.desafio;

  const handleVoltar = () => {
    navigation.goBack();
  };

  const handleEditar = () => {
    navigation.navigate('CriarGrupo', { grupo: dados } as any);
  };

  return (
    <View style={styles.container}>

      {/* HEADER */}
      <View style={styles.header}>
        <TouchableOpacity onPress={handleVoltar}>
          <Ionicons name="arrow-back" size={24} color="#01415B"/>
        </TouchableOpacity>

        <Text style={styles.headerTitle}>Desafio</Text>
        <TouchableOpacity onPress={handleEditar}>
          <Text style={styles.editButton}>Editar</Text>
        </TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={styles.content}>

        {/* TÍTULO */}
        <Text style={styles.titulo}>
          {dados.nomeDesafio}
        </Text>

        {/* IMAGEM */}
        <Image
          source={{ uri: dados.imagem }}
          style={styles.image}
        />

        {/* CARD INFORMAÇÕES */}
        <View style={styles.card}>
          <Text style={styles.section}>Data Início</Text>
          <Text style={styles.data}>
            {new Date(dados.dataInicio).toLocaleDateString()}{" "}
            {new Date(dados.dataInicio).toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit"
            })}
          </Text>
          <View style={styles.divider} />
          <Text style={styles.section}>Data Final</Text>
          <Text style={styles.data}>
            {new Date(dados.dataFinal).toLocaleDateString()}{" "}
            {new Date(dados.dataFinal).toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit"
            })}
          </Text>
          <View style={styles.divider} />
          <Text style={styles.section}>Descrição</Text>
          <Text style={styles.texto}>{dados.descricao}</Text>

          <View style={styles.divider} />

          <Text style={styles.section}>Regras</Text>
          <Text style={styles.texto}>{dados.regras}</Text>

        </View>

      </ScrollView>

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
  backgroundColor: "#F3FBFF",
},

header: {
  flexDirection: "row",
  alignItems: "center",
  justifyContent: "space-between",
  paddingHorizontal: 18,
  paddingTop: 80,
  paddingBottom: 30,
  backgroundColor: "#F3FBFF",
},

headerTitle: {
    fontSize: 21,
    fontWeight: "900",
    textAlign: "left"
},

content: {
  paddingHorizontal: 16,
  paddingBottom: 40,
},

titulo: {
  fontSize: 19,
  fontWeight: "600",
  marginBottom: 15,
},

image: {
  width: "100%",
  height: 300,
  borderRadius: 16,
  //marginBottom: 1
},

card: {
  backgroundColor: "#F3FBFF",
  borderRadius: 16,
  padding: 18,
  marginTop: 20,
},

data: {
  color: "#9CA3AF",
  fontSize: 13,
  marginBottom: 18,
},

section: {
  fontSize: 16,
  fontWeight: "bold",
  marginBottom: 6,
  marginTop: 12,
},

texto: {
  fontSize: 14,
  color: "#4B5563",
  lineHeight: 20,
},
divider: {
  height: 1,
  backgroundColor: "#E5E7EB",
  marginVertical: 12,
},
editButton: {
  color: "#01415B",
  fontWeight: "700",
}
});
