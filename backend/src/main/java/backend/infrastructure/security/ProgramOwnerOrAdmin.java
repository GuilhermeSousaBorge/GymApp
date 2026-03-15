package backend.infrastructure.security;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('Administrador') or hasRole('PersonalTrainer') or @securityService.isProgramOwner(#programId, authentication.principal)")
public @interface ProgramOwnerOrAdmin {
}
