import { ImageSourcePropType } from 'react-native'

type CardContent = {
    id: string
    title: string
    description: string
    image: ImageSourcePropType
}

const cardContents: CardContent[] = [
    {
        id: "1",
        title: "Comece estudando em conjunto",
        description: "Registre suas sessões de estudo e veja outras pessoas fazendo o mesmo. Desde o primeiro dia, seu esforço faz parte de um ambiente coletivo e compartilhado.",
        image: require("@/assets/image-1.png")
    },
    {
        id: "2",
        title: "Mantenha a constância em grupo",
        description: "Acompanhe seu progresso, construa sua sequência de dias estudados e siga evoluindo com o apoio e a presença de outros estudantes.",
        image: require("@/assets/image-2.png")
    },
    {
        id: "3",
        title: "Evolua com desafios coletivos",
        description: "Participe de grupos, interaja no feed, acompanhe rankings e transforme metas individuais em resultados compartilhados.",
        image: require("@/assets/image-3.png")
    },
]

export { CardContent, cardContents }
