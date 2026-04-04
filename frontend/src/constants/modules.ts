import { Module } from "@/types/module";

export const modules: Module[] = [
    {
        key: "dashboard",
        name: "Dashboard",
        url: "/dashboard",
    },
    {
        key: "users",
        name: "Usuários",
        children: [
            {
                key: "users-profile",
                name: "Meu Perfil",
                url: "/users/__PROFILE__/edit"
            },
            {
                key: "users-list-all",
                name: "Lista de usuários",
                url: "/users",
                permission: ["Administrador", "PersonalTrainer"]
            },
            {
                key: "users-list-trainers",
                name: "Instrutores",
                url: "/users",
                permission: ["Aluno"]
            },
            {
                key: "users-new",
                name: "Cadastrar usuário",
                url: "/users/new/edit",
                permission: ["Administrador"],
            },

        ],
    },
    {
        key: "trainings",
        name: "Treinos",
        children: [
            {
                key: "trainings-programs-create",
                name: "Criar programa",
                url: "/training-programs/new/edit",
            },
            {
                key: "trainings-programs-list",
                name: "Meus programas",
                url: "/training-programs",
                permission: ["Aluno"]
            },
            {
                key: "trainings-programs-list-all",
                name: "Todos os programas",
                url: "/training-programs",
                permission: ["Administrador", "PersonalTrainer"]
            }
        ],
    },
    {
        key: "exercises",
        name: "Exercícios",
        children: [
            {
                key: "exercise-list",
                name: "Biblioteca de exercícios",
                url: "/exercises",
            },
            {
                key: "exercise-create",
                name: "Criar exercício",
                url: "/exercises/new/edit",
                permission: ["Administrador", "PersonalTrainer"]
            },
            {
                key: "exercise-category-list",
                name: "Categorias",
                url: "/exercises/categories"
            }
        ],
    },
    {
        key: "financial",
        name: "Financeiro",
        children: [
            {
                key: "plans-list",
                name: "Planos",
                url: "/plans",
            },
            {
                key: "subscriptions",
                name: "Inscrições",
                url: "/subscriptions",
            },
            {
                key: "payments",
                name: "Pagamentos",
                url: "/payments",
            },
        ],
    },

];
