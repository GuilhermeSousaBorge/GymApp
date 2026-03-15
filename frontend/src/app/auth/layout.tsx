import React from "react";

export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex min-h-screen">
      <section className="w-2/3 bg-zinc-900 text-white flex flex-col justify-center px-20">
        <h1 className="text-4xl font-semibold mb-4">Seja bem-vindo</h1>

        <p className="text-lg text-zinc-300 max-w-md">
          Gerencie seus serviços e planos de forma simples, rápida e segura.
        </p>
      </section>
      <section className="w-1/3 bg-zinc-100 flex items-center justify-center">
        {children}
      </section>
    </div>
  );
}
