import { api } from "./api";

export type StudentDetails = {
    id: string
    name: string
    email: string
}

export type StudentCreate = {
    name: string
    email: string
}

async function create({ name, email }: StudentCreate){
    try {
        const { data } = await api.post<StudentDetails>("/students", {
            name,
            email
        })
        
        return data
    } catch (error) {
        throw error
    }
}

export const studentServer = { create }