import { Clock } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../ui/card";

export const RecentActivities = () => {
  return (
    <Card className="border-zinc-200 bg-white">
      <CardHeader>
        <CardTitle className="text-zinc-900">Atividades Recentes</CardTitle>
        <CardDescription className="text-zinc-600">
          Últimas ações no sistema
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          <div className="flex items-start gap-4 p-3 rounded-lg hover:bg-zinc-50 transition-colors">
            <div className="h-2 w-2 rounded-full bg-yellow-400 mt-2" />
            <div className="flex-1">
              <p className="text-sm font-medium text-zinc-900">
                Novo aluno cadastrado: Ana Paula Costa
              </p>
              <div className="flex items-center gap-2 mt-1">
                <Clock className="h-3 w-3 text-zinc-400" />
                <p className="text-xs text-zinc-500">Há 2 horas</p>
              </div>
            </div>
          </div>

          <div className="flex items-start gap-4 p-3 rounded-lg hover:bg-zinc-50 transition-colors">
            <div className="h-2 w-2 rounded-full bg-green-500 mt-2" />
            <div className="flex-1">
              <p className="text-sm font-medium text-zinc-900">
                Pagamento recebido: Pedro Henrique
              </p>
              <div className="flex items-center gap-2 mt-1">
                <Clock className="h-3 w-3 text-zinc-400" />
                <p className="text-xs text-zinc-500">Há 3 horas</p>
              </div>
            </div>
          </div>

          <div className="flex items-start gap-4 p-3 rounded-lg hover:bg-zinc-50 transition-colors">
            <div className="h-2 w-2 rounded-full bg-blue-500 mt-2" />
            <div className="flex-1">
              <p className="text-sm font-medium text-zinc-900">
                Novo programa de treino criado
              </p>
              <div className="flex items-center gap-2 mt-1">
                <Clock className="h-3 w-3 text-zinc-400" />
                <p className="text-xs text-zinc-500">Há 5 horas</p>
              </div>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
