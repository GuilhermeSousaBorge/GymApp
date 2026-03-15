import { AuthCard } from "@/components/auth/auth-card";
import { AuthHeader } from "@/components/auth/auth-header";
import { AuthTabs } from "@/components/auth/auth-tabs";
import { LoginForm } from "@/components/auth/login-form";
import { RegisterForm } from "@/components/auth/register-form";
import { TabsContent } from "@/components/ui/tabs";

export default function Login() {
  return (
    <AuthCard>
      <AuthTabs>
        <TabsContent value="login">
          <AuthHeader title="Entrar" description="Acesse sua conta para continuar"/>
          <LoginForm />
        </TabsContent>
        <TabsContent value="register">
        <AuthHeader title="Criar conta" description="Leva menos de um minuto"/>
          <RegisterForm />
        </TabsContent>
      </AuthTabs>
    </AuthCard>
  );
}
