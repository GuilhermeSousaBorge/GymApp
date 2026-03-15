import { api } from "@/lib/api";
import { UserFormData } from "@/lib/validations/user";
import { User } from "@/types/user";

const BASE_URL = "/users";

export const userService = {

    async listUsers(): Promise<User[]> {
        const response = await api.get(BASE_URL)
        return response.data;
    },

    async details(id: number | undefined): Promise<User>{
        const response = await api.get(`${BASE_URL}/${id}`);
        return response.data;
    },

    async createUser(user: UserFormData): Promise<User> {
        const response = await api.post(BASE_URL, user);
        return response.data;
    },

    async updateUser(id: number, user: UserFormData): Promise<User> {
        const response = await api.put(`${BASE_URL}/${id}`, user);
        return response.data;
    }
}