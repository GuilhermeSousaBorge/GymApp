import { CardDescription, CardHeader, CardTitle } from "../ui/card";

export const AuthHeader = ({
  title,
  description,
}: {
  title: string;
  description: string;
}) => {
  return (
    <CardHeader className="space-y-1 mb-5">
      <CardTitle className="text-2xl">{title}</CardTitle>
      <CardDescription>{description}</CardDescription>
    </CardHeader>
  );
};
