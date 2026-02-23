import { StackParams } from '@/utils/routesStack';
import { Feather } from "@expo/vector-icons";
import { useNavigation } from '@react-navigation/native';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';

import { colors } from '@/styles/colors';

type NextButtonProps = {
    scrollTo: () => void
    isLastPage: boolean
}

import { StackNavigationProp } from '@react-navigation/stack/';


export function NextButton({ scrollTo, isLastPage }: NextButtonProps) {
    const navigation =  useNavigation<StackNavigationProp<StackParams>>()

    function handlePress() {
        scrollTo();
        if (isLastPage) {
            isLastPage = false;
            navigation.replace("StudyGroupScreen");
        }     
    }

    return (
        <View>
            <TouchableOpacity onPress={handlePress} style={styles.button}> 
                {isLastPage ? (
                    <Text style={styles.text}>Comece já</Text>
                ) : (
                    <Feather name="arrow-right" size={24} color={colors.azul[300]} /> 
                )}
            </TouchableOpacity> 
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    button: {
        backgroundColor: colors.cinza[200],
        borderRadius: 100,
        padding: 15,
    },
    text: {
        color: colors.azul[300],
        fontSize: 18,
        fontWeight: 'bold',
    }
});
