import { createNativeStackNavigator } from "@react-navigation/native-stack";
import {
  NavigationContainer,
  createNavigationContainerRef,
} from "@react-navigation/native";
import { useEffect } from "react";
import * as Linking from "expo-linking";

import Index from "@/app/index";
import Login from "@/app/auth/Login";
import Registro from "@/app/auth/Registro";
import Profile from "@/app/Profile";
import StudyGroupScreen from "@/app/groupStudy/StudyGroupScreen";
import FeedScreen from "@/app/groupStudy/FeedScreen";
import Home from "@/app/Home";
import GrupoCriado from "@/app/grupo/grupo_criado";
import CriarGrupo from "@/app/grupo/criar_grupo";
import CriarSessao from "@/app/sessaoEstudo/sessao_estudo";
import Publicacao from "@/app/sessaoEstudo/publicacao";
import Disciplinas from "@/app/disciplinas";
import EntrarNoGrupo from "@/app/grupo/EntrarGrupo";
import RecuperarSenha from "@/app/auth/RecuperarSenha";

import { StackParams } from "@/utils/routesStack";

const Stack = createNativeStackNavigator<StackParams>();

// ✅ ref global pra navegar de fora das telas
const navigationRef = createNavigationContainerRef<StackParams>();

function extractToken(url: string): string | null {
  // pega qualquer coisa depois de /convites/
  const match = url.match(/\/convites\/([^/?#]+)/);
  return match?.[1] ?? null;
}

function DeepLinkHandler() {
  useEffect(() => {
    const handleUrl = (url: string) => {
      const token = extractToken(url);
      console.log("DEEP LINK URL:", url);
      console.log("DEEP LINK TOKEN:", token);

      if (!token) return;

      // garante que o nav está pronto
      if (navigationRef.isReady()) {
        navigationRef.navigate("EntrarNoGrupo", { token });
      } else {
        // fallback: tenta um pouco depois (caso abra muito rápido)
        setTimeout(() => {
          if (navigationRef.isReady()) {
            navigationRef.navigate("EntrarNoGrupo", { token });
          }
        }, 300);
      }
    };

    // cold start (app aberto pelo link)
    Linking.getInitialURL().then((url) => {
      if (url) handleUrl(url);
    });

    // app já aberto / background
    const sub = Linking.addEventListener("url", ({ url }) => handleUrl(url));

    return () => sub.remove();
  }, []);

  return null;
}

export default function App() {
  return (
    <NavigationContainer ref={navigationRef}>
      <DeepLinkHandler />

      <Stack.Navigator
        initialRouteName="Registro"
        screenOptions={{
          headerShown: false,
          animation: "slide_from_right",
          animationDuration: 250,
          gestureEnabled: true,
        }}
      >
        <Stack.Screen name="Index" component={Index} />
        <Stack.Screen name="Login" component={Login} />
        <Stack.Screen name="RecuperarSenha" component={RecuperarSenha} />
        <Stack.Screen name="Registro" component={Registro} />
        <Stack.Screen name="Home" component={Home} />
        <Stack.Screen name="StudyGroupScreen" component={StudyGroupScreen} />
        <Stack.Screen name="Feed" component={FeedScreen} />
        <Stack.Screen name="Profile" component={Profile} />
        <Stack.Screen name="Disciplinas" component={Disciplinas} />
        <Stack.Screen name="CriarGrupo" component={CriarGrupo} />
        <Stack.Screen name="GrupoCriado" component={GrupoCriado} />
        <Stack.Screen name="CriarSessao" component={CriarSessao} />
        <Stack.Screen name="Publicacao" component={Publicacao} />
        <Stack.Screen name="EntrarNoGrupo" component={EntrarNoGrupo} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}