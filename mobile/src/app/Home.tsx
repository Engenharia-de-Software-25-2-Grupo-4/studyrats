import { useEffect, useState } from "react";
import { StyleSheet, Text, View, ScrollView, TouchableOpacity, TextInput, Image } from "react-native"
import { Ionicons } from '@expo/vector-icons';
import { colors } from "@/styles/colors";
import { useNavigation } from "@react-navigation/native";
import { StackNavigationProp } from "@react-navigation/stack";
import { Menu } from "@/components/Menu";
import { StackParams } from "@/utils/routesStack";
import { categories } from "@/utils/categories";

type HomeNavProp = StackNavigationProp<StackParams, "Home">;

export default function Home(){

    const navigation = useNavigation<HomeNavProp>();
    const [greeting, setGreeting] = useState("");

    const getGreeting = () => {
        const now = new Date();

        const hour = Number(
            new Intl.DateTimeFormat("pt-BR", {
                hour: "numeric",
                hour12: false,
                timeZone: "America/Sao_Paulo",
            }).format(now)
        );

        if (hour >= 5 && hour < 12) return "Bom dia,";
        if (hour >= 12 && hour < 18) return "Boa tarde,";
        return "Boa noite,";
    };

    useEffect(() => {
        setGreeting(getGreeting());

        const interval = setInterval(() => {
            setGreeting(getGreeting());
        }, 60000);

        return () => clearInterval(interval);
    }, []);

    return (
        <View style={styles.container}>
            <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>

                {/* HEADER */}
                <View style={styles.header}>
                    <View style={styles.userInfo}>
                        <Image 
                            source={require("@/assets/profile.jpg")} 
                            style={styles.avatar}
                        />
                        <View>
                            <Text style={styles.greeting}>{greeting}</Text>
                            <Text style={styles.name}>Kemilli Nicole</Text>
                        </View>
                    </View>
                </View>

                {/* GROUP CARD */}
                <View style={styles.groupCard}>
                    <View style={styles.groupContent}>
                        <Text style={styles.groupTitle}>Grupo de estudos</Text>
                        <Text style={styles.groupSub}>
                            02/dez - 26/jan{"\n"}8 participantes
                        </Text>
                        <TouchableOpacity style={styles.statusButton} onPress={() => navigation.navigate("StudyGroupScreen")}>
                            <Text style={styles.statusText}>VER STATUS</Text>
                        </TouchableOpacity>
                    </View>
                    <Image
                        source={require("@/assets/fazer-check-in.png")} 
                        style={styles.groupImage}
                        resizeMode="cover"
                    />
                </View>

                {/* SEARCH */}
                <View style={styles.searchRow}>
                    <View style={styles.searchContainer}>
                        <Ionicons name="search" size={20} color={colors.azul[300]} style={styles.searchIcon} />
                        <TextInput
                            placeholder="Busque por algum desafio"
                            placeholderTextColor={colors.azul[300]}
                            style={styles.searchInput}
                        />
                    </View>
                </View>

                {/* DESAFIOS */}
                <View style={styles.sectionHeader}>
                    <Text style={styles.sectionTitle}>Meus desafios</Text>
                    {}
                    <TouchableOpacity onPress={() => navigation.navigate("Profile")}>
                        <Text style={styles.link}>Ver mais</Text>
                    </TouchableOpacity>
                </View>

                <ScrollView 
                    horizontal 
                    showsHorizontalScrollIndicator={false}
                    style={styles.challengesScroll}
                >
                    <ChallengeCard title="Desafio 1"/>
                    <ChallengeCard title="Desafio 2"/>
                    <ChallengeCard title="Desafio 3"/>
                </ScrollView>

                {/* DISCIPLINAS */}
                <View style={styles.sectionHeader}>
                    <Text style={styles.sectionTitle}>Minhas disciplinas</Text>
                    {}
                    <TouchableOpacity onPress={() => navigation.navigate("Disciplinas")}>
                        <Text style={styles.link}>Ver mais</Text>
                    </TouchableOpacity>
                </View>

                <View style={styles.chipsRow}>
                    <Chip label="Engenharia de Software" color={colors.verde}/>
                    <Chip label="BD" color={colors.laranja}/>
                    <Chip label="PLP" color={colors.azul[300]}/>
                    <Chip label="SO" color={colors.roxo}/>
                </View>

                {}
                <TouchableOpacity
                    style={styles.bigButton}
                    onPress={() => navigation.navigate("Profile")}
                >
                    <Text style={styles.bigButtonText}>
                        VER TODOS OS DESAFIOS
                    </Text>
                </TouchableOpacity>

            </ScrollView>
            
            <Menu
                tabs={categories}
                activeTabId="1" // "1" = Home
            />    
        </View>
    )
}

function ChallengeCard({title}:{title:string}){
    return(
        <View style={styles.challengeCard}>
            <Image 
                source={require("@/assets/image.png")} 
                style={styles.challengeImage}
                resizeMode="cover"
            />
            <Text style={styles.challengeTitle}>{title}</Text>
        </View>
    )
}

function Chip({label,color}:{label:string,color:string}){
    return(
        <View style={[styles.chip,{backgroundColor: color+"22"}]}>
            <Text style={[styles.chipText,{color}]}>{label}</Text>
        </View>
    )
}

const styles = StyleSheet.create({
    container:{
        flex:1,
        backgroundColor: colors.cinza[400]
    },
    content:{
        padding:20,
        paddingTop: 60,
        paddingBottom:80
    },
    header:{
        flexDirection:"row",
        justifyContent:"space-between",
        alignItems:"center",
        marginBottom:24
    },
    userInfo:{
        flexDirection:"row",
        alignItems:"center",
        gap:12,
        flex:1
    },
    avatar:{
        width:48,
        height:48,
        borderRadius:24,
    },
    greeting:{
        color: colors.cinza[500],
        fontSize:14,
        marginBottom:2
    },
    name:{
        color: colors.preto,
        fontWeight:"700",
        fontSize:18
    },
    groupTitle:{
        fontWeight:"700",
        fontSize:18,
        color: colors.azul[300],
        marginBottom:8
    },
    groupSub:{
        color: colors.preto,
        fontSize:14,
        lineHeight:20,
        marginBottom:12
    },
    statusButton:{
        backgroundColor: colors.azul[300],
        alignSelf:"flex-start",
        paddingHorizontal:16,
        paddingVertical:10,
        borderRadius:8
    },
    statusText:{
        color:"#FFF",
        fontWeight:"700",
        fontSize:12,
        letterSpacing:0.5
    },
    groupCard:{
        flexDirection:"row",
        backgroundColor: colors.azul[100],
        borderRadius:16,
        borderWidth: 1,
        borderColor: colors.azul[200],
        marginBottom:24,
        overflow:"hidden",
    },
    groupContent:{
        flex:1,
        padding:20,
    },
    groupImage:{
        width:140,
        height:"100%",
    },
    searchRow:{
        marginBottom:28
    },
    searchContainer: {
        flex: 1,
        flexDirection: "row",
        alignItems: "center",
        backgroundColor: colors.azul[100],
        borderRadius: 12,
        paddingHorizontal: 16,
        borderWidth: 1,
        borderColor: colors.azul[200],
    },
    searchIcon:{
        marginRight:8
    },
    searchInput:{
        flex:1,
        paddingVertical:14,
        fontSize:15
    },
    filterButton:{
        width:50,
        backgroundColor: colors.azul[300],
        borderRadius:12
    },
    sectionHeader:{
        flexDirection:"row",
        justifyContent:"space-between",
        alignItems:"center",
        marginBottom:16
    },
    sectionTitle:{
        fontWeight:"700",
        fontSize:18,
        color: colors.azul[300]
    },
    link:{
        color: colors.azul[300],
        fontSize:14,
        fontWeight:"600"
    },
    challengesScroll:{
        marginBottom:32
    },
    challengeCard:{
        backgroundColor: colors.azul[100],
        borderWidth: 1,
        borderColor: colors.azul[200],
        borderRadius:16,
        marginRight:16,
        width:160,
        overflow:"hidden"
    },
    challengeImage:{
        height:100,
        width: "100%",
    },
    challengeTitle:{
        padding:14,
        fontWeight:"600",
        fontSize:15,
        color: colors.azul[300]
    },
    chipsRow:{
        flexDirection:"row",
        flexWrap:"wrap",
        gap:10,
        marginBottom:28
    },
    chip:{
        paddingHorizontal:16,
        paddingVertical:10,
        borderRadius:20
    },
    chipText:{
        fontWeight:"700",
        fontSize:14
    },
    bigButton:{
        backgroundColor: colors.azul[300],
        padding:18,
        borderRadius:12,
        alignItems:"center"
    },
    bigButtonText:{
        color:"#FFF",
        fontWeight:"700",
        fontSize:15,
        letterSpacing:0.5
    }, 
})