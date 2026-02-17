import { createStackNavigator } from '@react-navigation/stack';
import { NavigationContainer } from '@react-navigation/native';
import Index from '@/app/index'
import Login from '@/app/login';   
import Home from '@/app/home';
import Profile from '@/app/profile';
import Disciplinas from '@/app/disciplinas';

export type StackParams = {
  Index: undefined
  Login: undefined
  Home: undefined
  Profile: undefined
  Disciplinas: undefined
}

const Stack = createStackNavigator<StackParams>();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false}}>
        <Stack.Screen name="Index" component={Index} />
        <Stack.Screen name="Login" component={Login} />
        <Stack.Screen name="Home" component={Home} />
        <Stack.Screen name="Profile" component={Profile} />
        <Stack.Screen name = "Disciplinas" component={Disciplinas}/>
      </Stack.Navigator>
    </NavigationContainer>
  );
}
