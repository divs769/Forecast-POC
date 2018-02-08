package com.shopdirect.version.domain.reader;

import com.shopdirect.version.domain.exceptions.VersionException;
import com.shopdirect.version.infrastructure.properties.VersionProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VersionReaderTest {

    public static final int NUMBER_OF_KEYS = 2;
    public static final String TEST_KEY = "version ";
    public static final String TEST_VALUE = " project";

    @Mock
    private VersionProperties versionProperties;
    private VersionReader versionReader;

    @Before
    public void setUp() throws Exception {

        versionReader = new VersionReader(versionProperties);
    }

    @Test
    public void shouldReadFileFully() throws Exception {

        //arrange

        when(versionProperties.getVersionPath()).thenReturn("version-test");

        //act

        Map versionMap = versionReader.getVersion();

        //assert

        assertThat(versionMap.size(), is(NUMBER_OF_KEYS));
        assertThat(versionMap.containsKey(TEST_KEY), is(true));
        assertThat(versionMap.containsValue(TEST_VALUE), is(true));
    }

    @Test(expected = VersionException.class)
    public void cantFindFile() throws Exception {

        //arrange

        when(versionProperties.getVersionPath()).thenReturn("missing-test-names.txt");

        //act

        versionReader.getVersion();
    }
}