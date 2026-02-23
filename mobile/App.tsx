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
import RecuperarSenha from "@/app/auth/RecuperarSenha";

import Home from "@/app/Home";
import Profile from "@/app/Profile";
import Disciplinas from "@/app/disciplinas";

import StudyGroupScreen from "@/app/groupStudy/StudyGroupScreen";
import FeedScreen from "@/app/groupStudy/FeedScreen";
import GroupHome from "@/app/groupStudy/GroupHome";

import CriarGrupo from "@/app/grupo/criar_grupo";
import GrupoCriado from "@/app/grupo/grupo_criado";
import EntrarNoGrupo from "@/app/grupo/EntrarGrupo";

import CriarSessao from "@/app/sessaoEstudo/sessao_estudo";
import Publicacao from "@/app/sessaoEstudo/publicacao";

import EditAcc from "@/app/auth/EditAcc";

import { StackParams } from "@/utils/routesStack";

const Stack = createNativeStackNavigator<StackParams>();

// ✅ ref global pra navegar de fora das telas (deep link)
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

      const go = () => navigationRef.navigate("EntrarNoGrupo", { token });

      if (navigationRef.isReady()) {
        go();
      } else {
        setTimeout(() => {
          if (navigationRef.isReady()) go();
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
        initialRouteName="Login"
        screenOptions={{
          headerShown: false,
          animation: "slide_from_right",
          animationDuration: 250,
          gestureEnabled: true,
        }}
      >
        <Stack.Screen name="Index" component={Index} />

        {/* Auth */}
        <Stack.Screen name="Login" component={Login} />
        <Stack.Screen name="RecuperarSenha" component={RecuperarSenha} />
        <Stack.Screen name="Registro" component={Registro} />
        <Stack.Screen name="EditAcc" component={EditAcc} />

        {/* App */}
        <Stack.Screen name="Home" component={Home} />
        <Stack.Screen name="Profile" component={Profile} />
        <Stack.Screen name="Disciplinas" component={Disciplinas} />

        {/* Grupos / Feed */}
        <Stack.Screen name="StudyGroupScreen" component={StudyGroupScreen} />
        <Stack.Screen name="GroupHome" component={GroupHome} />
        <Stack.Screen name="Feed" component={FeedScreen} />
        <Stack.Screen name="EntrarNoGrupo" component={EntrarNoGrupo} />

        {/* Fluxos de grupo/sessão */}
        <Stack.Screen name="CriarGrupo" component={CriarGrupo} />
        <Stack.Screen name="GrupoCriado" component={GrupoCriado} />
        <Stack.Screen name="CriarSessao" component={CriarSessao} />
        <Stack.Screen name="Publicacao" component={Publicacao} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}