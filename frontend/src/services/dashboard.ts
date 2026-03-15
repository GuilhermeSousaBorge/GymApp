import { api } from "@/lib/api"
import { AdminDashboard, StudentDashboard } from "@/types/dashboard"


export const dashboardService = {

    async studentDashboard(): Promise<StudentDashboard> {
        const response = await api.get("/dashboard/student")
        return response.data
    },
    
    async adminDashboard(): Promise<AdminDashboard>{
        const response = await api.get("/dashboard/admin")
        return response.data
    }
}