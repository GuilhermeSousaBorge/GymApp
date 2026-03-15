import { AlertCircle } from "lucide-react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../ui/card";
import { Badge } from "../ui/badge";

export const PendingTasks = () => {
  return (
    <Card className="border-zinc-200 bg-white">
      <CardHeader>
        <CardTitle className="text-zinc-900">Tarefas Pendentes</CardTitle>
        <CardDescription className="text-zinc-600">
          Itens que requerem atenção
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          <div className="flex items-start gap-3 p-3 rounded-lg hover:bg-zinc-50 transition-colors border border-zinc-200">
            <input
              type="checkbox"
              className="mt-1 h-4 w-4 rounded border-zinc-300 text-yellow-400 focus:ring-yellow-400"
            />
            <div className="flex-1">
              <p className="text-sm font-medium text-zinc-900">
                Renovar plano de Juliana Ferreira
              </p>
              <div className="flex items-center gap-2 mt-1">
                <Badge className="text-xs border-yellow-400 text-yellow-700 bg-yellow-50">
                  Vence em 3 dias
                </Badge>
              </div>
            </div>
          </div>

          <div className="flex items-start gap-3 p-3 rounded-lg hover:bg-zinc-50 transition-colors border border-zinc-200">
            <input
              type="checkbox"
              className="mt-1 h-4 w-4 rounded border-zinc-300 text-yellow-400 focus:ring-yellow-400"
            />
            <div className="flex-1">
              <p className="text-sm font-medium text-zinc-900">
                Cobrar pagamento atrasado de Lucas Mendes
              </p>
              <div className="flex items-center gap-2 mt-1">
                <Badge className="text-xs border-red-400 text-red-700 bg-red-50">
                  <AlertCircle className="h-3 w-3 mr-1" />
                  Atrasado há 5 dias
                </Badge>
              </div>
            </div>
          </div>

          <div className="flex items-start gap-3 p-3 rounded-lg hover:bg-zinc-50 transition-colors border border-zinc-200">
            <input
              type="checkbox"
              className="mt-1 h-4 w-4 rounded border-zinc-300 text-yellow-400 focus:ring-yellow-400"
            />
            <div className="flex-1">
              <p className="text-sm font-medium text-zinc-900">
                Atualizar ficha de treino de Ana Paula
              </p>
              <div className="flex items-center gap-2 mt-1">
                <Badge
                    variant="outline"
                  className="text-xs border-blue-400 text-blue-700 bg-blue-50"
                >
                  Agendado para amanhã
                </Badge>
              </div>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
