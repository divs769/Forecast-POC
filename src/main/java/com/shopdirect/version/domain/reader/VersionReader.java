package com.shopdirect.version.domain.reader;

import com.shopdirect.version.domain.exceptions.VersionException;
import com.shopdirect.version.infrastructure.properties.VersionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class VersionReader {

    private VersionProperties versionProperties;

    @Autowired
    public VersionReader(VersionProperties versionProperties) {
        this.versionProperties = versionProperties;
    }

    public Map getVersion() throws VersionException {
        Map result = new HashMap<String, String>();
        ClassLoader classLoader = getClass().getClassLoader();
        try(InputStream inputStream = classLoader.getResourceAsStream(versionProperties.getVersionPath())){
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitline = line.split("=");
                result.put(splitline[0], splitline[1]);
            }
            scanner.close();
        } catch (Exception e) {
            throw new VersionException(e);
        }
        return result;
    }
}