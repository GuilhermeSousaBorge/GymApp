import { Users } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "../ui/card"

type Props = {
    totalStudents: number
    totalActiveStudents: number
}

export const TotalStudents = ({totalStudents, totalActiveStudents}: Props) => {

    return(
        <Card className="border-zinc-200 bg-white hover:shadow-md transition-shadow">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-zinc-700">Total de Alunos</CardTitle>
            <div className="h-8 w-8 rounded-full bg-yellow-100 flex items-center justify-center">
              <Users className="h-4 w-4 text-yellow-600" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-zinc-900">{totalStudents}</div>
            <p className="text-xs text-zinc-600 mt-1">
              {totalActiveStudents} ativos
            </p>
          </CardContent>
        </Card>
    )
}