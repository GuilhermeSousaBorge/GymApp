import env from "@/env"
import axios from "axios"
const baseURL = env.NEXT_PUBLIC_API_URL 

export const api = axios.create({
    baseURL: baseURL,
    withCredentials: true
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