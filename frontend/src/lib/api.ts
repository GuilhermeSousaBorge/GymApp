import env from "@/env"
import { useAuth } from "@/stores/auth"
import axios from "axios"
const baseURL = env.NEXT_PUBLIC_API_URL 

export const api = axios.create({
    baseURL: baseURL,
    withCredentials: true
})

api.interceptors.request.use((config) => {
    const token = useAuth.getState().token
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
})

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            // Handle unauthorized access, e.g., redirect to login page
            window.location.href = "/unauthorized"
        }
        return Promise.reject(error)
    }
)