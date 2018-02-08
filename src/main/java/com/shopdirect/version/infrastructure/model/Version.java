package com.shopdirect.version.infrastructure.model;

import java.util.HashMap;
import java.util.Map;

public class Version {

    private Map version = new HashMap<String, String>();

    public Version(Map version) {
        this.version = version;
    }

    public Map getVersion() {
        return version;
    }

    public void setVersion(Map version) {
        this.version = version;
    }
}