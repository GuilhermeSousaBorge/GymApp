import { dashboardService } from "@/services/dashboard";
import { useQuery } from "@tanstack/react-query";


export const useAdminDashboard = () => useQuery({
    queryKey: ["admin-dashboard"],
    queryFn: () => dashboardService.adminDashboard()
})

export const useStudentDashboard = () => useQuery({
    queryKey: ["student-dashboard"],
    queryFn: () => dashboardService.studentDashboard()
})