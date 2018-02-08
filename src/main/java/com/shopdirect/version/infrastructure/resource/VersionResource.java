package com.shopdirect.version.infrastructure.resource;

import com.shopdirect.version.domain.exceptions.VersionException;
import com.shopdirect.version.infrastructure.model.Version;
import com.shopdirect.version.domain.reader.VersionReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/version", produces = "application/json")
public class VersionResource {

    private VersionReader versionReader;

    @Autowired
    public VersionResource(VersionReader versionReader) {
        this.versionReader = versionReader;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Version getVersion() throws VersionException {

        return new Version(versionReader.getVersion());
    }
}
