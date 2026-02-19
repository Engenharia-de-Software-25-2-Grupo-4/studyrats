import { ImageSourcePropType } from 'react-native'

type Post = {
    id: string
    title: string
    user: string
    subject: string
    date: string
    image: ImageSourcePropType
}

const posts: Post[] = [
    {
        id: "1",
        user: "Gabriella Letícia",
        title: "Telinhas",
        subject: "Engenharia de Software",
        date: "01-01-2026",
        image: require("@/assets/fazer-check-in.png")
    },
    {
        id: "2",
        user: "Kemilli Lima",
        title: "Padrão Strategy",
        subject: "Projeto de Software",
        date: "01-01-2026",
        image: require("@/assets/fazer-check-in.png")
    },
    {
        id: "3",
        title: "Gerência de tempo",
        user: "Júlia Leal",
        subject: "Engenharia de Software",
        date: "01-01-2026",
        image: require("@/assets/fazer-check-in.png")
    },
    {
        id: "4",
        title: "Lista de exercícios",
        user: "Eyshila Buriti",
        subject: "Estatística",
        date: "01-01-2026",
        image: require("@/assets/fazer-check-in.png")
    }
]

export { Post, posts }
