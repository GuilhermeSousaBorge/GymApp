"use client";
import { registerSchema } from "@/lib/validations/auth";
import { authService } from "@/services/auth";
import { useAuth } from "@/stores/auth";
import { useUi } from "@/stores/ui";
import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { Controller, useForm } from "react-hook-form";
import { toast } from "sonner";
import z from "zod";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "../ui/select";

type RegisterFormData = z.infer<typeof registerSchema>;

export const RegisterForm = () => {
  const {
    register,
    handleSubmit,
    control,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormData>({ resolver: zodResolver(registerSchema) });

  const { login } = useAuth();
  const { setLoading } = useUi()
  const router = useRouter()

  const onSubmit = async(data: RegisterFormData) => {
    try{
      setLoading(true)
      const response = await authService.register(data);

      toast.success("Conta criada com sucesso!", { position: "top-right" });
      login(response.user, response.token)
      router.push("/dashboard")
    }catch(err){
      const message = err instanceof Error ? err.message : "Erro ao criar conta";
      toast.error(message, { position: "top-right" });
    }finally{
      setLoading(false)
    }
  }
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="space-y-1">
        <Label>Nome</Label>
        <Input {...register("name")} />
        {errors.name && (
          <p className="text-sm text-red-500">{errors.name.message}</p>
        )}
      </div>

      <div className="space-y-1">
        <Label>Email</Label>
        <Input type="email" {...register("email")} />
        {errors.email && (
          <p className="text-sm text-red-500">{errors.email.message}</p>
        )}
      </div>

      <div className="space-y-1">
        <Label>Genero</Label>
        <Controller
          control={control}
          name="gender"
          render={({ field }) => (
            <Select onValueChange={field.onChange} value={field.value}>
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Genero" />
              </SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  <SelectLabel>Genero</SelectLabel>
                  <SelectItem value="MALE">Masculino</SelectItem>
                  <SelectItem value="FEMALE">Feminino</SelectItem>
                  <SelectItem value="OTHER">Outro</SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
          )}
        />

        {errors.gender && (
          <p className="text-sm text-red-500">{errors.gender.message}</p>
        )}
      </div>

      <div className="space-y-1">
        <Label>Senha</Label>
        <Input type="password" {...register("password")} />
        {errors.password && (
          <p className="text-sm text-red-500">{errors.password.message}</p>
        )}
      </div>

      <div className="space-y-1">
        <Label>Confirmar senha</Label>
        <Input type="password" {...register("confirmPassword")} />
        {errors.confirmPassword && (
          <p className="text-sm text-red-500">
            {errors.confirmPassword.message}
          </p>
        )}
      </div>

      <Button
        disabled={isSubmitting}
        className="w-full h-11 bg-yellow-400 text-zinc-900 hover:bg-yellow-500"
      >
        Criar conta
      </Button>
    </form>
  );
};
