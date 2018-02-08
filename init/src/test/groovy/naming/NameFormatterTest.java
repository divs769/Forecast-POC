package naming;

import org.junit.Test;


import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import java.util.ArrayList;


/**
 * Created by IgniteAdmin on 7/12/2017.
 */
public class NameFormatterTest {

    public static final String ORIGINAL_SERVICE_NAME = "test-service";
    public static final String DISPLAYABLE_SERVICE_NAME = "Test Service";

    @Test
    public void getDisplayableServiceName() throws Exception {

        //arrange
        NameFormatter nameFormatter = new NameFormatter();

        //act
        String displayableServiceName = nameFormatter.getDisplayableServiceName(ORIGINAL_SERVICE_NAME);

        //assert
        assertThat(displayableServiceName, is(DISPLAYABLE_SERVICE_NAME));
    }
}