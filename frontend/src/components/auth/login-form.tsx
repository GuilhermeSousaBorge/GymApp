"use client";

import { loginSchema } from "@/lib/validations/auth";
import { authService } from "@/services/auth";
import { useAuth } from "@/stores/auth";
import { useUi } from "@/stores/ui";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import z from "zod";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { Label } from "../ui/label";

type LoginFormData = z.infer<typeof loginSchema>;

export const LoginForm = () => {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormData>({ resolver: zodResolver(loginSchema) });

  const {login} = useAuth()
  const router = useRouter()
  const {setLoading} = useUi()

  const onSubmit = async (data: LoginFormData) =>  {
    try{
      setLoading(true)

      const response = await authService.login(data)
      login(response.user, response.token)

      router.push("/dashboard")
    }catch(err){
      const message = "Email ou senha invalidos";
      toast.error(message, {position: "top-right"})
    }finally{
      setLoading(false)
    }
  }
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
    <div className="space-y-2">
      <Label>Email</Label>
      <Input
        type="email"
        placeholder="seu@email.com"
        {...register("email")}
      />
      {errors.email && (
        <p className="text-sm text-red-500">
          {errors.email.message}
        </p>
      )}
    </div>

    <div className="space-y-2">
      <Label>Senha</Label>
      <Input
        type="password"
        {...register("password")}
      />
      {errors.password && (
        <p className="text-sm text-red-500">
          {errors.password.message}
        </p>
      )}
    </div>

    <div className="flex justify-end">
      <Button
        type="button"
        variant="link"
        className="h-auto p-0 text-sm text-muted-foreground hover:text-yellow-500"
      >
        Esqueceu a senha?
      </Button>
    </div>

    <Button
      disabled={isSubmitting}
      className="w-full h-11 bg-yellow-400 text-zinc-900 hover:bg-yellow-500"
    >
      Entrar
    </Button>
  </form>
  );
};
