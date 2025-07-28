package com.github.blarc.endpoint;

import com.github.blarc.entity.UserRole;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

@Path("/user")
@ApplicationScoped
public class UserEndpoint {

    @GET
    @RolesAllowed({UserRole.USER, UserRole.OPERATOR})
    public String me(@Context SecurityContext securityContext) {
        return securityContext.getUserPrincipal().getName();
    }
}
