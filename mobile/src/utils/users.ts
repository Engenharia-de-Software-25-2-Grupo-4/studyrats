type User = {
    id: string
    name: string
    daysActive: number
    groups: number
    avatar?: string | null
}

const users: User[] = [
    {
        id: "1",
        name: "Kemilli Lima",
        daysActive: 14,
        groups: 5,
        avatar: "https://avatars.githubusercontent.com/u/140713454?v=4",
    },
    {
        id: "2",
        name: "Gabriella Letícia",
        daysActive: 13,
        groups: 3,
        avatar: "https://avatars.githubusercontent.com/u/140713498?v=4",
    },
    {
        id: "3",
        name: "Júlia Leal",
        daysActive: 10,
        groups: 2,
        avatar: "https://avatars.githubusercontent.com/u/180411150?v=4",
    },
    {
        id: "4",
        name: "João Victor",
        daysActive: 8,
        groups: 10,
        avatar: "https://avatars.githubusercontent.com/u/140714611?v=4",
    },
    {
        id: "5",
        name: "Oscar Henrique",
        daysActive: 6,
        groups: 7,
        avatar: null,
    },
    {
        id: "6",
        name: "Eyshila Buriti",
        daysActive: 5,
        groups: 5,
        avatar: "https://avatars.githubusercontent.com/u/105816646?v=4",
    },
]

export { User, users }