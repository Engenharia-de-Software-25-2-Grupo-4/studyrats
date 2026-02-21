import { createStackNavigator } from '@react-navigation/stack';
import { NavigationContainer } from '@react-navigation/native';

import Index from '@/app/index';
import Login from '@/app/auth/Login';
import Registro from '@/app/auth/Registro';
import Profile from '@/app/Profile';
import StudyGroupScreen from '@/app/groupStudy/StudyGroupScreen';
import FeedScreen from '@/app/groupStudy/FeedScreen';
import Home from '@/app/Home';
import GrupoCriado from '@/app/grupo/grupo_criado';
import CriarGrupo from '@/app/grupo/criar_grupo';
import CriarSessao from '@/app/sessaoEstudo/sessao_estudo';
import Publicacao from '@/app/sessaoEstudo/publicacao';
import Disciplinas from '@/app/disciplinas';

import { StackParams } from '@/utils/routesStack';

const Stack = createStackNavigator<StackParams>();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator
        initialRouteName="Registro"   // abre direto na tela de registro
        screenOptions={{ headerShown: false }}
      >
        <Stack.Screen name="Index" component={Index} />
        <Stack.Screen name="Login" component={Login} />
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
      </Stack.Navigator>
    </NavigationContainer>
  );
}