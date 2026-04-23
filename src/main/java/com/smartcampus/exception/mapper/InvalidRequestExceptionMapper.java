package com.smartcampus.exception.mapper;

import com.smartcampus.exception.InvalidRequestException;
import com.smartcampus.model.ErrorResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidRequestExceptionMapper implements ExceptionMapper<InvalidRequestException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(InvalidRequestException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(System.currentTimeMillis(), 400, "Bad Request", exception.getMessage(), path()))
                .build();
    }

    private String path() {
        return uriInfo == null ? null : uriInfo.getPath();
    }
}
