import env from "@/env"
import { useAuth } from "@/stores/auth"
import axios, { AxiosError, InternalAxiosRequestConfig } from "axios"

const baseURL = env.NEXT_PUBLIC_API_URL

export const api = axios.create({
    baseURL: baseURL,
    withCredentials: true,
})

api.interceptors.request.use((config) => {
    const token = useAuth.getState().token
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
})

let isRefreshing = false
let failedQueue: Array<{
    resolve: (token: string) => void
    reject: (error: unknown) => void
}> = []

const processQueue = (error: unknown, token: string | null) => {
    failedQueue.forEach(({ resolve, reject }) => {
        if (token) resolve(token)
        else reject(error)
    })
    failedQueue = []
}

api.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
        const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean }

        // Nao tenta refresh para rotas de auth (evita loop infinito)
        const isAuthRoute = originalRequest?.url?.startsWith("/auth/")

        if (error.response?.status === 401 && !originalRequest._retry && !isAuthRoute) {
            if (isRefreshing) {
                // Se ja esta refreshing, enfileira a request
                return new Promise((resolve, reject) => {
                    failedQueue.push({
                        resolve: (token: string) => {
                            originalRequest.headers.Authorization = `Bearer ${token}`
                            resolve(api(originalRequest))
                        },
                        reject,
                    })
                })
            }

            originalRequest._retry = true
            isRefreshing = true

            try {
                const { data } = await axios.post(
                    `${baseURL}/auth/refresh`,
                    null,
                    { withCredentials: true }
                )

                const newToken = data.token
                useAuth.getState().setToken(newToken)

                originalRequest.headers.Authorization = `Bearer ${newToken}`
                processQueue(null, newToken)

                return api(originalRequest)
            } catch (refreshError) {
                processQueue(refreshError, null)
                useAuth.getState().logout()
                window.location.href = "/auth"
                return Promise.reject(refreshError)
            } finally {
                isRefreshing = false
            }
        }

        return Promise.reject(error)
    }
)
