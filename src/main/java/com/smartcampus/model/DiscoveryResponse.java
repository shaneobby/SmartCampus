package com.smartcampus.model;

import java.util.Map;

public class DiscoveryResponse {
    private String version;
    private String contact;
    private String basePath;
    private Map<String, String> resources;
    private Map<String, String> links;

    public DiscoveryResponse() {
    }

    public DiscoveryResponse(String version, String contact, String basePath, Map<String, String> resources, Map<String, String> links) {
        this.version = version;
        this.contact = contact;
        this.basePath = basePath;
        this.resources = resources;
        this.links = links;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Map<String, String> getResources() {
        return resources;
    }

    public void setResources(Map<String, String> resources) {
        this.resources = resources;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
