import { createStackNavigator } from '@react-navigation/stack';
import { NavigationContainer } from '@react-navigation/native';
import Index from '@/app/index';
import Login from '@/app/login';
import Profile from '@/app/Profile';
import StudyGroupScreen from '@/app/groupStudy/StudyGroupScreen';
import FeedScreen from '@/app/groupStudy/FeedScreen';
import Home from '@/app/Home';
import GrupoCriado from '@/app/grupo/grupo_criado';
import CriarGrupo from '@/app/grupo/criar_grupo';
import { StackParams } from '@/utils/routesStack';

const Stack = createStackNavigator<StackParams>();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator 
        initialRouteName="StudyGroupScreen"
        screenOptions={{ headerShown: false }}
      >
        <Stack.Screen name="Index" component={Index} />
        <Stack.Screen name="Login" component={Login} />
        <Stack.Screen name="Home" component={Home} />
        <Stack.Screen name="StudyGroupScreen" component={StudyGroupScreen} />
        <Stack.Screen name="Feed" component={FeedScreen} />
        <Stack.Screen name="Profile" component={Profile} />
        <Stack.Screen name="CriarGrupo" component={CriarGrupo} /> 
        <Stack.Screen name="GrupoCriado" component={GrupoCriado} />
        
      </Stack.Navigator>
    </NavigationContainer>
  )
}