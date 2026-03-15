import { Module } from "@/types/module";
import { Weekday, WEEKDAYS } from "@/types/training";
import { User } from "@/types/user";
import { clsx, type ClassValue } from "clsx";
import { format } from "date-fns";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function isAdmin(user: User | null){
  return user?.role?.name == "Administrador"
}

export function isStudent(user: User | null){
  return user?.role?.name == "Aluno"
}

export function canAccessModule(
  module: Module,
  user: User | null
): boolean {
  if (!user) return false;

  if (!module.permission || module.permission.length === 0) {
    return true;
  }

  return module.permission.some((p) =>
    user.role?.name.includes(p)
  );
}

export function filterModulesByPermission(
  modules: Module[],
  user: User | null
): Module[] {
  return modules
    .filter((module) => canAccessModule(module, user))
    .map((module) => ({
      ...module,
      children: module.children
        ? filterModulesByPermission(module.children, user)
        : undefined,
    }))
    .filter(
      (module) =>
        module.url || (module.children && module.children.length > 0)
    );
}

type BreadcrumbItem = {
  name: string;
  href: string;
};

export function findBreadcrumbsByPath(
  modules: Module[],
  pathname: string,
  parentPath: BreadcrumbItem[] = []
): BreadcrumbItem[] | null {
  for (const item of modules) {
    const currentPath = item.url
      ? [...parentPath, { name: item.name, href: item.url }]
      : parentPath;

    if (item.url === pathname) {
      return currentPath;
    }

    if (item.children) {
      const found = findBreadcrumbsByPath(
        item.children,
        pathname,
        currentPath
      );
      if (found) return found;
    }
  }

  return null;
}

export function formatDate(date?: Date | null) {
  if (!date) return "-"
  const parsed = new Date(date)
  if (isNaN(parsed.getTime())) return "-"
  return format(parsed, "dd/MM/yyyy")
}

export function isValidCPF(cpf: string) {
  // remove tudo que não é número
  cpf = cpf.replace(/\D/g, "")

  if (cpf.length !== 11) return false
  if (/^(\d)\1+$/.test(cpf)) return false

  // cálculo dígitos verificadores...
  let sum = 0
  for (let i = 0; i < 9; i++) {
    sum += parseInt(cpf[i]) * (10 - i)
  }
  let firstDigit = (sum * 10) % 11
  if (firstDigit === 10) firstDigit = 0
  if (firstDigit !== parseInt(cpf[9])) return false

  sum = 0
  for (let i = 0; i < 10; i++) {
    sum += parseInt(cpf[i]) * (11 - i)
  }
  let secondDigit = (sum * 10) % 11
  if (secondDigit === 10) secondDigit = 0
  if (secondDigit !== parseInt(cpf[10])) return false

  return true
}

export function translateWeekDays(dias: string[] = []) {
  const ordem: Weekday[] = [
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
    "SUNDAY"
  ]

  return dias
    .filter((d): d is Weekday => d in WEEKDAYS)
    .sort((a, b) => ordem.indexOf(a) - ordem.indexOf(b))
    .map(d => WEEKDAYS[d]).join(" / ")
}