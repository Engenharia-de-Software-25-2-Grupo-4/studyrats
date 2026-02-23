import { Animated, StyleSheet, View, useWindowDimensions } from "react-native";

import { colors } from "@/styles/colors";
import { CardContent } from "@/utils/pages";

type PaginatorProps = {
    data: CardContent[]
    scrollX: Animated.Value
}

export function Paginator({ data, scrollX } : PaginatorProps){
    const { width } = useWindowDimensions()
    return (
        <View>
            <View style={{ flexDirection: 'row', height: 64}}>
                {data.map((_,i) => {
                    const inputRange = [(i -1) * width, i * width, (i + 1) * width]

                    const dotWidth = scrollX.interpolate({
                        inputRange,
                        outputRange: [10, 20, 10],
                        extrapolate: 'clamp'
                    })

                    const opacity = scrollX.interpolate({
                        inputRange,
                        outputRange: [0.3, 1, 0.3],
                        extrapolate: 'clamp'
                    })
                    return (
                        <Animated.View style={[
                            styles.dot, 
                            {
                                width: dotWidth,
                                opacity
                            }]}
                            key={i.toString()}
                        />
                    )
                })}
            </View>
        </View>
    )
}

const styles = StyleSheet.create({
    dot: {
        height: 10,
        borderRadius: 5,
        backgroundColor: colors.cinza[200],
        marginHorizontal: 8,
        marginTop: 20
    }
})