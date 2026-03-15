import { CreditCard } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "../ui/card"

type Props = {
    pendingPayments: number
    overduePayments: number
}
export const PendingPayments = ({overduePayments, pendingPayments}: Props) => {

    return(
        <Card className="border-zinc-200 bg-white hover:shadow-md transition-shadow">
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium text-zinc-700">
            Pagamentos Pendentes
          </CardTitle>
          <div className="h-8 w-8 rounded-full bg-red-100 flex items-center justify-center">
            <CreditCard className="h-4 w-4 text-red-600" />
          </div>
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold text-zinc-900">{pendingPayments}</div>
          <p className="text-xs text-red-600 mt-1">
            {overduePayments} em atraso
          </p>
        </CardContent>
      </Card>
    )
}