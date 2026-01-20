import { useRef, useState } from "react";
import { Animated, FlatList, StyleSheet, useWindowDimensions, View } from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";

import { OnboadingItem } from "@/components/OnboardingItem";
import { CardContent, cardContents } from "@/utils/pages";
import { Paginator } from "./Paginator";
import { NextButton } from "./NextButton";
import { colors } from "@/styles/colors";

export default function Onboarding() {
    const { width } = useWindowDimensions()

    const [currentIndex, setCurrentIndex] = useState(0)
    const scrollX = useRef(new Animated.Value(0)).current
    const slidesRef = useRef<FlatList<CardContent>>(null);

    async function scrollTo(){
        if(currentIndex < cardContents.length - 1){
            const nextIndex = currentIndex + 1;

            slidesRef.current?.scrollToIndex({ index: nextIndex})

            setCurrentIndex(nextIndex);
        } else {
            console.log("Last page.")
            try {
                await AsyncStorage.setItem('@viewedOnboarding', 'true')
            } catch (error) {
                console.log('Error @setItem: ', error)
            }
        }
    }

    return (
        <View style={[styles.container, { width }]}>

            <View style={styles.staticCard} />

            <View style={{ flex: 3 }}> 
                <FlatList
                    scrollEnabled={false}
                    data={cardContents}
                    renderItem={({ item }) => <OnboadingItem item={item} />}
                    horizontal
                    showsHorizontalScrollIndicator={false}
                    pagingEnabled
                    keyExtractor={(item) => item.id}
                    onScroll={Animated.event([{nativeEvent: {contentOffset: {x: scrollX}}}], {
                        useNativeDriver: false,
                    })}
                    ref={slidesRef}
                />
            </View>
            
            <View style={styles.footer}>
                < Paginator data={cardContents} scrollX={scrollX}/>
                < NextButton isLastPage={currentIndex === cardContents.length - 1} scrollTo={scrollTo}/>
            </View>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: colors.cinza[200],
    },
    staticCard: {
        position: 'absolute',
        bottom: 0,
        width: '100%',
        height: '45%',
        backgroundColor: colors.azul[300],
        borderTopLeftRadius: 40,
        borderTopRightRadius: 40,
        justifyContent: 'flex-end'
    },
    footer: {
        position: 'absolute',
        bottom: 40,
        width: '100%',
        flexDirection: 'row',
        justifyContent: 'space-between',
        paddingHorizontal: 35,
        alignItems: 'center',
    }
});