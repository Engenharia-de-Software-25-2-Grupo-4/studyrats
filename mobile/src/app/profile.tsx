import { StyleSheet, Text, View, ScrollView, TouchableOpacity, Image } from "react-native"
import { Feather, Ionicons, MaterialCommunityIcons } from '@expo/vector-icons';
import { colors } from "@/styles/colors";

export default function Profile(){
    return (
        <View style={styles.container}>
            <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>

                {/* HEADER */}
                <View style={styles.header}>
                    <View style={styles.profileHeader}>
                        <View style={styles.avatar}>
                            <Image 
                                source={require("@/assets/profile.jpg")}
                                style={styles.avatarImage}
                            />
                        </View>
                        
                        <View style={styles.profileInfo}>
                            <Text style={styles.name}>Kemilli Nicole</Text>
                            
                            <View style={styles.stats}>
                                <View style={styles.statItem}>
                                    <Text style={styles.statNumber}>5</Text>
                                    <Text style={styles.statLabel}>Desafios</Text>
                                </View>
                                
                                <View style={styles.statItem}>
                                    <Text style={styles.statNumber}>2</Text>
                                    <Text style={styles.statLabel}>Vitórias</Text>
                                </View>
                            </View>
                        </View>
                    </View>
                </View>

                {/* ACTION BUTTONS */}
                <View style={styles.actionButtons}>
                    <TouchableOpacity style={styles.editButton}>
                        <Text style={styles.editButtonText}>CONFIGURE SEU PERFIL</Text>
                    </TouchableOpacity>
                </View>

                {/* TABS */}
                <View style={styles.tabs}>
                    <TouchableOpacity style={[styles.tab, styles.tabActive]}>
                        <Ionicons name="grid" size={24} color={colors.azul[300]} />
                    </TouchableOpacity>

                    <TouchableOpacity style={styles.tab}>
                        <Ionicons name="trophy" size={24} color={colors.cinza[300]} />
                    </TouchableOpacity>
                </View>

                {/* CARDS GRID */}
                <View style={styles.cardsGrid}>
                    <GroupCard 
                        title="Grupo de Estudos"
                        participants={8}
                        color={colors.azul[200]}
                        badge="trophy"
                        imageSource={require("@/assets/image.png")}
                    />

                    <GroupCard 
                        title="Desafio 1"
                        participants={10}
                        color={colors.azul[200]}
                        badge="time"
                        imageSource={require("@/assets/image.png")}
                    />

                    <GroupCard 
                        title="Desafio 2"
                        participants={8}
                        color={colors.azul[200]}
                        badge="check"
                        imageSource={require("@/assets/image.png")}
                    />

                    <NewGroupCard />
                </View>

            </ScrollView>
        </View>
    )
}


function GroupCard({title, participants, color, badge, imageSource}:{
    title:string, 
    participants:number, 
    color:string,
    badge: 'trophy' | 'time' | 'check',
    imageSource: any
}){
    return(
        <View style={[styles.groupCard, {backgroundColor: color}]}>
            {}
            <View style={styles.topBadge}>
                <View style={[styles.badgeIconContainer, 
                    badge === 'trophy' && {backgroundColor: colors.laranja},
                    badge === 'time' && {backgroundColor: colors.vermelho},
                    badge === 'check' && {backgroundColor: colors.verde}
                ]}>
                    {badge === 'trophy' && <Ionicons name="trophy" size={16} color="#FFF" />}
                    {badge === 'time' && <Ionicons name="time" size={16} color="#FFF" />}
                    {badge === 'check' && <Feather name="check" size={16} color="#FFF" />}
                </View>
            </View>

            {}
            <View style={styles.participantsBadge}>
                <Ionicons name="people" size={14} color={colors.azul[300]} />
                <Text style={styles.participantsText}>{participants}</Text>
            </View>

            {}
            <View style={styles.cardImageContainer}>
                <Image 
                    source={imageSource}
                    style={styles.cardImage}
                    resizeMode="cover"
                />
            </View>

            {/* Título */}
            <View style={styles.cardFooter}>
                <Text style={styles.cardTitle}>{title}</Text>
            </View>
        </View>
    )
}


function NewGroupCard(){
    return(
        <TouchableOpacity style={[styles.groupCard, styles.newGroupCard]}>
            <View style={styles.newGroupIcon}>
                <Feather name="plus" size={30} color={colors.azul[300]} />
            </View>
            <Text style={styles.newGroupText}>Novo grupo</Text>
        </TouchableOpacity>
    )
}


const styles = StyleSheet.create({
    container:{
        flex:1,
        backgroundColor: colors.cinza[400]
    },

    content:{
        padding:20,
        paddingTop: 80,
        paddingBottom:80
    },

    header:{
        flexDirection:"row",
        justifyContent:"space-between",
        alignItems:"flex-start",
        marginBottom:20
    },

    profileHeader:{
        flexDirection:"row",
        gap:16,
        flex:1
    },

    avatar:{
        width:70,
        height:70,
        borderRadius:35,
        backgroundColor: colors.azul[300],
        overflow: 'hidden'
    },

    avatarImage:{
        width: '100%',
        height: '100%'
    },

    profileInfo:{
        flex:1,
        justifyContent:"center"
    },

    name:{
        fontSize:20,
        fontWeight:"700",
        color: colors.azul[300],
        marginBottom:12
    },

    stats:{
        flexDirection:"row",
        gap:24
    },

    statItem:{
        alignItems:"flex-start"
    },

    statNumber:{
        fontSize:18,
        fontWeight:"700",
        color: colors.azul[300]
    },

    statLabel:{
        fontSize:12,
        color: colors.azul[300],
        marginTop:2
    },

    actionButtons:{
        flexDirection:"row",
        marginBottom:24
    },

    editButton:{
        flex:1,
        backgroundColor: colors.azul[300],
        paddingVertical:10,
        borderRadius:8,
        alignItems:"center"
    },

    editButtonText:{
        color: "#FFF",
        fontWeight:"700",
        fontSize:12,
        letterSpacing:0.5
    },

    tabs:{
        flexDirection:"row",
        gap:40,
        marginBottom:24,
        borderBottomWidth:1,
        borderBottomColor: colors.cinza[300]
    },

    tab:{
        paddingVertical:12,
        opacity:0.4
    },

    tabActive:{
        opacity:1,
        borderBottomWidth:2,
        borderBottomColor: colors.azul[300]
    },

    cardsGrid:{
        flexDirection:"row",
        flexWrap:"wrap",
        gap:12
    },

    groupCard:{
        width:"48%",
        aspectRatio:0.75,
        borderRadius:16,
        padding:12,
        position:"relative",
        overflow:"hidden"
    },

    topBadge:{
        position:"absolute",
        top:12,
        left:12,
        zIndex:1
    },

    badgeIconContainer:{
        width:28,
        height:28,
        borderRadius:14,
        alignItems: 'center',
        justifyContent: 'center'
    },

    participantsBadge:{
        position:"absolute",
        top:12,
        right:12,
        backgroundColor:"#FFF",
        flexDirection:"row",
        alignItems:"center",
        paddingHorizontal:8,
        paddingVertical:4,
        borderRadius:12,
        gap:4,
        zIndex:1
    },

    participantsText:{
        fontSize:12,
        fontWeight:"700",
        color: colors.azul[300]
    },

    cardImageContainer:{
        flex:1,
        marginTop:40,
        borderRadius:8,
        overflow: 'hidden'
    },

    cardImage:{
        width: '100%',
        height: '100%'
    },

    cardFooter:{
        paddingTop:8
    },

    cardTitle:{
        fontSize:14,
        fontWeight:"600",
        color: colors.azul[300]
    },

    newGroupCard:{
        backgroundColor: colors.azul[300],
        alignItems:"center",
        justifyContent:"center",
        gap:12
    },

    newGroupIcon:{
        width:60,
        height:60,
        backgroundColor:"#FFF",
        borderRadius:30,
        opacity:0.9,
        alignItems: 'center',
        justifyContent: 'center'
    },

    newGroupText:{
        fontSize:15,
        fontWeight:"700",
        color:"#FFF"
    }
})