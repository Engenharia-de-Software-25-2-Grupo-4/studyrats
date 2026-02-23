# 🐀📚 StudyRats

O **StudyRats** é um aplicativo voltado para organização e acompanhamento de estudos, permitindo a criação de sessões de estudo, interação entre estudantes e gerenciamento de atividades acadêmicas.

O projeto foi desenvolvido utilizando:

- ⚛️ **React Native**
- 🚀 **Expo**
- 🔥 **Firebase**

Por utilizar **React Native com Expo**, existe um passo a passo específico para rodar o aplicativo corretamente no celular utilizando o **Expo Go**.

---

# Guia Completo para Rodar o Projeto

> ⚠️ **IMPORTANTE:**
> O computador e o celular precisam estar conectados **na mesma rede Wi-Fi** para que o aplicativo funcione corretamente.

---

## 1. Clone o repositório

```bash
git clone https://github.com/Engenharia-de-Software-25-2-Grupo-4/studyrats.git
cd studyrats
```

---

## 2. Acesse a pasta mobile

A partir da raiz do projeto:

```bash

cd mobile
```

---

## 3. Instale as dependências

Dentro da pasta `mobile`, execute:

```bash
npm install
npm install firebase
```

---

## 4. Instale o Expo Go no celular

Baixe o aplicativo **Expo Go**:

- 📱 **Android** → [Play Store](https://play.google.com/store/apps/details?id=host.exp.exponent)
- 📱 **iOS** → [App Store](https://apps.apple.com/app/expo-go/id982107779)

---

## 5. Inicie o aplicativo

Ainda dentro da pasta `mobile`, execute:

```bash
npx expo start
```

Após rodar esse comando, será exibido um **QR Code** no terminal ou no navegador.

---

## 6. Abra o aplicativo no celular

1. Abra o aplicativo **Expo Go**
2. Escaneie o **QR Code** com a câmera do celular
3. O aplicativo será carregado automaticamente 🎉

---

## ✅ Observações Importantes

- O IP configurado deve ser o IP correto da sua máquina.
- O celular e o computador devem estar na **mesma rede Wi-Fi**.
- Caso o QR Code não funcione, tente reiniciar com:

```bash
npx expo start
```

---

👩‍💻 **Equipe StudyRats** — Projeto desenvolvido com React Native, Expo e Firebase para facilitar a experiência de estudos de forma colaborativa.
