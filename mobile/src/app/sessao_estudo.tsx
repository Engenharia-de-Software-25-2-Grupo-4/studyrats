import { View, Text, StyleSheet, TouchableOpacity, TextInput, ScrollView, Image, Alert } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import * as ImagePicker from "expo-image-picker";
import { useState, useEffect } from "react";
import DateTimePicker from "@react-native-community/datetimepicker";
import { Modal } from "react-native";

type Props = {
  onCriar: (dados: any) => void;
  sessao?: any;
};

export default function NovoCheckIn({onCriar, sessao}: Props) {
    
    const [image, setImage] = useState<string | null>(null);
    const [titulo, setTitulo] = useState("");
    const [descricao, setDescricao] = useState("");
    const [disciplina, setDisciplina] = useState<{ id: string; nome: string } | null>(null);
    const [mostrarDisciplinas, setMostrarDisciplinas] = useState(false);
    const [criandoNova, setCriandoNova] = useState(false);
    const [novaDisciplina, setNovaDisciplina] = useState("");
    const [dataHora, setDataHora] = useState(new Date());
    const [duracao, setDuracao] = useState<string>("");
    const [topico, setTopico] = useState<string>("");
    const [mostrarPicker, setMostrarPicker] = useState(false)

    useEffect(() => {
      if (sessao) {
        setTitulo(sessao.titulo);
        setDescricao(sessao.descricao);
        setDisciplina(sessao.disciplina);
        setDataHora(new Date(sessao.dataHora));
        setImage(sessao.image);
        setDuracao(sessao.duracao);
        setTopico(sessao.topico);
      }
    }, [sessao]);

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
    const disciplinas = [
    { id: "1", nome: "Matemática" },
    { id: "2", nome: "Programação" },
    { id: "3", nome: "Banco de Dados" },
    ];

    const handleSubmit = () => {
       if (!titulo.trim()) {
            Alert.alert("Erro", "O título é obrigatório.");
            return;
        }

        if (!disciplina) {
            Alert.alert("Erro", "A disciplina é obrigatória.");
            return;
        }

        const dados = {
            titulo,
            descricao,
            disciplina,
            dataHora,
            image,
            duracao,
            topico
      };
      onCriar(dados);
    };

    return (
        <View style={styles.container}>

        {/* HEADER */}
        <View style={styles.header}>
            <TouchableOpacity>
            <Ionicons name="arrow-back" size={24} />
            </TouchableOpacity>

           <Text style={styles.headerTitle}>{sessao ? "Editar check-in":"Novo check-in"}</Text>
           <TouchableOpacity onPress={handleSubmit}>
                <Text style={styles.publish}>{sessao ? "Salvar":"Criar"}</Text>
            </TouchableOpacity>
            
        </View>

        <ScrollView contentContainerStyle={styles.content}>
            
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
              ? disciplina.nome
              : "Selecionar disciplina"}
          </Text>
        </TouchableOpacity>

        <Modal
          visible={mostrarDisciplinas}
          transparent
          animationType="fade"
        >
          <View style={styles.modalOverlay}>

            <TouchableOpacity
              style={StyleSheet.absoluteFill}
              onPress={() => {
                setMostrarDisciplinas(false);
                setCriandoNova(false);
              }}
            />

            <View style={styles.modalBox}>
              <ScrollView>

                {/* Lista do banco */}
                {disciplinas.map((d) => (
                  <TouchableOpacity
                    key={d.id}
                    style={styles.option}
                    onPress={() => {
                      setDisciplina(d);
                      setMostrarDisciplinas(false);
                      setCriandoNova(false);
                    }}
                  >
                    <Text style={styles.optionText}>{d.nome}</Text>
                  </TouchableOpacity>
                ))}

                {/* Botão criar nova */}
                <TouchableOpacity
                  style={styles.option}
                  onPress={() => setCriandoNova(true)}
                >
                  <Text style={[styles.optionText, { color: "#1E6F7C" }]}>
                    + Nova disciplina
                  </Text>
                </TouchableOpacity>

                {/* Campo para nova disciplina */}
                {criandoNova && (
                  <>
                    <TextInput
                      placeholder="Digite o nome"
                      style={styles.input}
                      value={novaDisciplina}
                      onChangeText={setNovaDisciplina}
                    />

                    <TouchableOpacity
                      style={styles.option}
                      onPress={() => {
                        if (!novaDisciplina.trim()) return;

                        const nova = {
                          id: "n", 
                          nome: novaDisciplina
                        };

                        setDisciplina(nova);
                        setNovaDisciplina("");
                        setCriandoNova(false);
                        setMostrarDisciplinas(false);
                      }}
                    >
                      <Text style={[styles.optionText, { color: "green" }]}>
                        Salvar disciplina
                      </Text>
                    </TouchableOpacity>
                  </>
                )}

              </ScrollView>
            </View>

          </View>
        </Modal>


        <TextInput
            placeholder="Tópico"
            placeholderTextColor="#2b2c2c"
            style={styles.input}
            value={topico}
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
        />


        </ScrollView>
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

});