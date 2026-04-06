import { api } from "@/lib/api";
import { Auth, Gender, User } from "@/types/user";

type LoginPayload = {
  email: string;
  password: string;
};

type RegisterPayload = {
    name: string
    email: string
    gender: Gender
    password: string
    confirmPassword: string
}

type RefreshResponse = {
    token: string;
}

export const authService = {
  async login(payload: LoginPayload): Promise<Auth>{
    const response = await api.post("/auth/login", payload);
    return response.data;
  },

  async register(payload: RegisterPayload): Promise<Auth>{
    const {data} = await api.post("/auth/register", payload)
    return data
  },

  async refresh(): Promise<RefreshResponse> {
    const { data } = await api.post("/auth/refresh")
    return data
  },

  async logout(){
    await api.post("/auth/logout")
  },

  async me(): Promise<User>{
    const {data} = await api.get("/auth/me")
    return data
  },

  async forgotPassword(email: string): Promise<{ message: string }> {
    const { data } = await api.post("/auth/password/forgot", { email })
    return data
  },

  async resetPassword(token: string, newPassword: string): Promise<{ message: string }> {
    const { data } = await api.post("/auth/password/reset", { token, newPassword })
    return data
  },

  async sendEmailVerification(email: string): Promise<{ message: string }> {
    const { data } = await api.post("/auth/email/verification/send", { email })
    return data
  },

  async confirmEmailVerification(token: string): Promise<{ message: string }> {
    const { data } = await api.post("/auth/email/verification/confirm", { token })
    return data
  },
};
