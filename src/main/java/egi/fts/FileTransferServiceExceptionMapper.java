package egi.fts;

import jakarta.annotation.Priority;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.resteasy.reactive.common.jaxrs.ResponseImpl;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Custom exception mapper for FileTransferService API calls, allows access to response body in case of error
 */
@Priority(Priorities.USER)
public final class FileTransferServiceExceptionMapper implements ResponseExceptionMapper<FileTransferServiceException> {

    @Override
    public FileTransferServiceException toThrowable(Response response) {
        try {
            response.bufferEntity();
        } catch(Exception ignored) {}

        String msg = getBody(response);
        return new FileTransferServiceException(response, msg);
    }

    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        return status >= StatusCode.BAD_REQUEST;
    }

    private String getBody(Response response) {
        if (!response.hasEntity()) return "";

        try (InputStream is = response.readEntity(InputStream.class);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            is.transferTo(baos);
            byte[] bytes = baos.toByteArray();
            return new String(bytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            return "";
        }
    }
}
