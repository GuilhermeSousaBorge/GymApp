"use client";

import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar";

import {
  Bolt,
  ClipboardList,
  CreditCard,
  DollarSign,
  Dumbbell,
  LayoutDashboard,
  LogOut,
  LucideIcon,
  UserCog,
  Users
} from "lucide-react";

import { modules } from "@/constants/modules";
import { filterModulesByPermission } from "@/lib/utils";
import { authService } from "@/services/auth";
import { useAuth } from "@/stores/auth";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";

const iconMap: Record<string, LucideIcon> = {
  dashboard: LayoutDashboard,
  trainings: Dumbbell,
  exercises: ClipboardList,
  financial: DollarSign,
  payments: CreditCard,
  users: Users,
  config: Bolt
};

export function AppSidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const { user, logout } = useAuth();

  const visibleModules = filterModulesByPermission(modules, user);

  return (
    <Sidebar className="border-r">
      <SidebarContent>

        {/* Logo */}
        <div className="px-4 py-4 border-b">
          <span className="font-black text-lg tracking-tight text-primary">
            GYM SYSTEM
          </span>
        </div>

        {visibleModules.map((module) => {
          const Icon = iconMap[module.key];
          const hasChildren = !!module.children?.length;

          // Módulo sem filhos — item direto
          if (!hasChildren && module.url) {
            const isActive = pathname.startsWith(module.url)
            return (
              <SidebarGroup key={module.key}>
                <SidebarGroupContent>
                  <SidebarMenu>
                    <SidebarMenuItem>
                      <SidebarMenuButton asChild isActive={isActive} tooltip={module.name}>
                        <Link href={module.url}>
                          {Icon && <Icon className="h-4 w-4" />}
                          <span>{module.name}</span>
                        </Link>
                      </SidebarMenuButton>
                    </SidebarMenuItem>
                  </SidebarMenu>
                </SidebarGroupContent>
              </SidebarGroup>
            )
          }

          // Módulo com filhos — seção com label
          return (
            <SidebarGroup key={module.key}>
              <SidebarGroupLabel className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                {Icon && <Icon className="h-3.5 w-3.5" />}
                {module.name}
              </SidebarGroupLabel>
              <SidebarGroupContent>
                <SidebarMenu>
                  {module.children!.map((child) => {
                    const resolvedUrl = child.url?.replace("__PROFILE__", String(user?.id ?? "")) ?? "#"
                    const isActive = (child.url && pathname.startsWith(resolvedUrl)) || false
                    return (
                      <SidebarMenuItem key={child.key}>
                        <SidebarMenuButton
                          asChild
                          isActive={isActive}
                          className="text-sm"
                          tooltip={child.name}
                        >
                          <Link href={resolvedUrl}>
                            {child.name}
                          </Link>
                        </SidebarMenuButton>
                      </SidebarMenuItem>
                    )
                  })}
                </SidebarMenu>
              </SidebarGroupContent>
            </SidebarGroup>
          )
        })}

      </SidebarContent>

      <SidebarFooter className="border-t p-2">
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton asChild>
              <Link href={`/users/${user?.id}/edit`}>
                <UserCog className="h-4 w-4" />
                <span>Meu perfil</span>
              </Link>
            </SidebarMenuButton>
            <SidebarMenuButton
              onClick={() => {
                logout();
                authService.logout();
                router.push("/auth");
              }}
              className="text-destructive hover:bg-destructive/10"
            >
              <LogOut className="h-4 w-4" />
              <span>Sair</span>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarFooter>
    </Sidebar>
  );
}