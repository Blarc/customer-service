package com.github.blarc.exception;

import com.github.blarc.model.ErrorCodeEnum;
import com.github.blarc.model.ErrorDto;
import io.quarkus.logging.Log;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.time.OffsetDateTime;
import java.util.Optional;

@Provider
public class ExpectedCustomerServiceExceptionMapper
        implements ExceptionMapper<ExpectedCustomerServiceException>
{
    @Override
    @APIResponse(responseCode = "default", content = @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = MediaType.APPLICATION_JSON))
    public Response toResponse(ExpectedCustomerServiceException e)
    {
        Log.warn(".toResponse Expected exception: " + e.getMessage(), e);
        return createErrorResponse(e.getHttpStatusCode(), e.getErrorCodeEnum(), e.getMessage());
    }

    public static Response createErrorResponse(int httpStatusCode, ErrorCodeEnum errorCodeEnum, String message)
    {
        ErrorDto errorResponse = new ErrorDto(
                message,
                Optional.ofNullable(errorCodeEnum).map(ErrorCodeEnum::getValue).orElse(null),
                OffsetDateTime.now()
        );

        return Response
                .status(httpStatusCode)
                .entity(errorResponse)
                .build();
    }
}
