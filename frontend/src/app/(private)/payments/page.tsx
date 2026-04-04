"use client"

import { AdminPaymentsView } from "@/components/payment/admin-payments-view"
import { StudentPaymentsView } from "@/components/payment/student-payments-view"
import { isStudent } from "@/lib/utils"
import { useUser } from "@/stores/auth"

const PaymentsPage = () => {
    const user = useUser()

    if (isStudent(user)) {
        return <StudentPaymentsView />
    }

    return <AdminPaymentsView />
}

export default PaymentsPage
