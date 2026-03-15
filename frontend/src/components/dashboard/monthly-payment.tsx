import { TrendingUp } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "../ui/card";

type Props = {
  pendingAmount: number;
  paidAmount: number
};

export const MonthlyPayment = ({ pendingAmount, paidAmount }: Props) => {
  return (
    <Card className="border-zinc-200 bg-white hover:shadow-md transition-shadow">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium text-zinc-700">
          Receita Mensal
        </CardTitle>
        <div className="h-8 w-8 rounded-full bg-green-100 flex items-center justify-center">
          <TrendingUp className="h-4 w-4 text-green-600" />
        </div>
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold text-zinc-900">
          R$ {paidAmount.toFixed(2)}
        </div>
        <p className="text-xs text-green-600 mt-1">
          R$ {pendingAmount.toFixed(2)} pendente
        </p>
      </CardContent>
    </Card>
  );
};
