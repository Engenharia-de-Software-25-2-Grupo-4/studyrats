import { MaterialIcons } from "@expo/vector-icons";
import { ImageSourcePropType } from 'react-native'

type Category = {
    id: string;
    name: string;
    icon: keyof typeof MaterialIcons.glyphMap;
    route?: string;
    image?: ImageSourcePropType
};

export const categories: Category[] = [
    { id: "1", name: "Início", icon: "home", route: "StudyGroupScreen"},
    { id: "2", name: "Desafios", icon: "check-circle", route: "Challenges"},
    { id: "3", name: "Perfil", icon: "person", route: "Profile" },
    { id: "4", name: "Sair", icon: "logout" },
];