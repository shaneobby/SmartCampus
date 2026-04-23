package com.smartcampus.resource;

import com.smartcampus.model.DiscoveryResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {
    @GET
    public DiscoveryResponse getDiscovery(@Context UriInfo uriInfo) {
        String base = uriInfo.getBaseUri().toString().replaceAll("/$", "");
        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms", base + "/rooms");
        resources.put("sensors", base + "/sensors");
        Map<String, String> links = new LinkedHashMap<>();
        links.put("self", base);
        links.put("rooms", base + "/rooms");
        links.put("sensors", base + "/sensors");
        return new DiscoveryResponse("v1", "admin@smartcampus.local", base, resources, links);
    }
}
