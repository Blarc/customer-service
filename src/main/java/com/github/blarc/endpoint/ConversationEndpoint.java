package com.github.blarc.endpoint;

import com.github.blarc.entity.ConversationTypeEnum;
import com.github.blarc.entity.UserRole;
import com.github.blarc.exception.ExpectedCustomerServiceException;
import com.github.blarc.model.ConversationDto;
import com.github.blarc.model.CreateConversationDto;
import com.github.blarc.model.MessageDto;
import com.github.blarc.model.PagedResultDto;
import com.github.blarc.service.ConversationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@ApplicationScoped
@Path("/conversations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConversationEndpoint {

    @Inject
    ConversationService conversationService;

    @GET
    @RolesAllowed({UserRole.USER, UserRole.OPERATOR})
    public List<ConversationDto> getConversations(@Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        boolean isOperator = securityContext.isUserInRole(UserRole.OPERATOR);
        return conversationService.getConversations(username, isOperator);
    }

    @POST
    @RolesAllowed({UserRole.USER})
    public ConversationDto createConversation(@NotNull @Valid CreateConversationDto createConversationDto, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        return conversationService.createConversation(createConversationDto.type(), username);
    }

    @POST
    @Path("/{id}/take")
    @RolesAllowed({UserRole.OPERATOR})
    public ConversationDto takeConversation(@PathParam("id") @NotNull Long id, @Context SecurityContext securityContext) throws ExpectedCustomerServiceException {
        String username = securityContext.getUserPrincipal().getName();
        return conversationService.takeConversation(id, username);
    }

    @GET
    @Path("/{id}/messages")
    @RolesAllowed({UserRole.USER, UserRole.OPERATOR})
    public PagedResultDto<MessageDto> getMessages(
            @PathParam("id") @NotNull Long conversationId,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize,
            @QueryParam("pageIndex") @DefaultValue("0") int pageIndex,
            @Context SecurityContext securityContext
    ) throws ExpectedCustomerServiceException {
        String username = securityContext.getUserPrincipal().getName();
        boolean isOperator = securityContext.isUserInRole(UserRole.OPERATOR);
        return conversationService.getMessagesForConversation(conversationId, username, isOperator, pageSize, pageIndex);
    }

    @POST
    @Path("/{id}/messages")
    @RolesAllowed({UserRole.USER, UserRole.OPERATOR})
    @Consumes(MediaType.TEXT_PLAIN)
    public void addMessage(@NotBlank String message, @PathParam("id") @NotNull Long conversationId, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        boolean isOperator = securityContext.isUserInRole(UserRole.OPERATOR);
        conversationService.addMessageToConversation(message, conversationId, username, isOperator);
    }

}
