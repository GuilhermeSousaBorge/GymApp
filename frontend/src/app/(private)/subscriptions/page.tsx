"use client"

import { AdminSubscriptionView } from "@/components/subscription/admin-subscription-view"
import { StudentSubscriptionView } from "@/components/subscription/student-subscription-view"
import { isStudent } from "@/lib/utils"
import { useUser } from "@/stores/auth"

const SubscriptionsPage = () => {
    const user = useUser()

    if (isStudent(user)) {
        return <StudentSubscriptionView />
    }

    return <AdminSubscriptionView />
}

export default SubscriptionsPage
