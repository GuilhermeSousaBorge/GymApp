"use client";

import { AdminDashboard } from "@/components/dashboard/admin-dashboard";
import { StudentDashboard } from "@/components/dashboard/student-dashboard";
import { useUser } from "@/stores/auth";

export default function DashboardPage() {
  const user = useUser();
  const isAdmin = user?.role?.name === "Administrador" || user?.role?.name === "PersonalTrainer"

  if(isAdmin) return <AdminDashboard />
  return <StudentDashboard />
}