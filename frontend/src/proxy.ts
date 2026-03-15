import { NextResponse, type NextRequest } from "next/server";

export function proxy(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const token = request.cookies.get("token")?.value;
  const isPublic =
    pathname === "/" ||
    pathname.startsWith("/auth");

  // não logado tentando rota privada
  if (!token && !isPublic) {
    return NextResponse.redirect(
      new URL("/auth", request.url)
    );
  }

  // logado tentando rota pública
  if (token && isPublic) {
    return NextResponse.redirect(
      new URL("/dashboard", request.url)
    );
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!_next|favicon.ico).*)"],
};
