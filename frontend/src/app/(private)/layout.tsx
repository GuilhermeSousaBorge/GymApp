"use client";

import { AppSidebar } from "@/components/layout/app-sidebar";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import { Separator } from "@/components/ui/separator";
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar";
import { modules } from "@/constants/modules";
import { findBreadcrumbsByPath } from "@/lib/utils";
import { usePathname } from "next/navigation";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const pathname = usePathname();

  const breadcrumbs = findBreadcrumbsByPath(modules, pathname) ?? [];

  return (
    <SidebarProvider defaultOpen={true}>
      <AppSidebar />

      <SidebarInset>
        <header className="flex h-16 shrink-0 items-center gap-2 border-b px-4">
          <SidebarTrigger className="-ml-1" />
          <Separator orientation="vertical" className="mr-2 h-4" />

          <Breadcrumb>
            <BreadcrumbList>
              {breadcrumbs.length > 0 && (
                <Breadcrumb>
                  <BreadcrumbList>
                    {breadcrumbs.map((crumb, index) => (
                      <div key={crumb.href} className="flex items-center gap-2">
                        {index > 0 && (
                          <BreadcrumbSeparator className="" />
                        )}

                        <BreadcrumbItem>
                          {index === breadcrumbs.length - 1 ? (
                            <BreadcrumbPage className=" font-medium">
                              {crumb.name}
                            </BreadcrumbPage>
                          ) : (
                            <BreadcrumbLink href={crumb.href}>
                              {crumb.name}
                            </BreadcrumbLink>
                          )}
                        </BreadcrumbItem>
                      </div>
                    ))}
                  </BreadcrumbList>
                </Breadcrumb>
              )}
            </BreadcrumbList>
          </Breadcrumb>
        </header>

        <main className="flex-1 p-6">{children}</main>
      </SidebarInset>
    </SidebarProvider>
  );
}
