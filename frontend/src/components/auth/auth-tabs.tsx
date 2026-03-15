import { Tabs, TabsList, TabsTrigger } from "../ui/tabs";

export const AuthTabs = ({ children }: { children: React.ReactNode }) => {
  return (
    <Tabs defaultValue="login" className="w-full min-h-100">
      <TabsList className="w-full grid grid-cols-2 mb-6 bg-zinc-200 rounded-lg">
        <TabsTrigger
          value="login"
          className="data-[state=active]:bg-yellow-400 data-[state=active]:text-zinc-950 text-zinc-700"
        >
          Entrar
        </TabsTrigger>

        <TabsTrigger
          value="register"
          className="data-[state=active]:bg-yellow-400 data-[state=active]:text-zinc-900 text-zinc-700"
        >
          Criar conta
        </TabsTrigger>
      </TabsList>
      {children}
    </Tabs>
  );
};
