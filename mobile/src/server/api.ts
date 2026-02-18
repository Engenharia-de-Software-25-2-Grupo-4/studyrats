import axios from "axios"

export const api = axios.create({
    baseURL: "http://192.168.0.9:8080" // trocar localhost pelo endereço ip da máquina
})