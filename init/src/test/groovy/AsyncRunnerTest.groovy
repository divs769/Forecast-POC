import org.junit.Test
import utilities.AsyncRunner

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class AsyncRunnerTest {

    public static final int TIMES_TO_CALL = 2
    public static final String RETURN_PARAMETER = "OK"

    def tester = {
        true
    }

    def paramTester = { returnValue ->
        returnValue
    }

    @Test
    void asyncCallPositiveOutcome() throws Exception {

        AsyncRunner asyncRunner = new AsyncRunner()
        def outcome = asyncRunner.run(tester, null, TIMES_TO_CALL, 2, true)
        assertThat(outcome, is(true))
        assertThat(asyncRunner.timesCalled, is(1))
    }

    @Test
    void asyncCallNegativeOutcome() throws Exception {

        AsyncRunner asyncRunner = new AsyncRunner()
        def outcome = asyncRunner.run(tester, null, TIMES_TO_CALL, 2, false)
        assertThat(outcome, is(true))
        assertThat(asyncRunner.timesCalled, is(TIMES_TO_CALL))
    }

    @Test
    void asyncCallWithParameter() throws Exception {

        AsyncRunner asyncRunner = new AsyncRunner()
        def outcome = asyncRunner.run(paramTester, RETURN_PARAMETER, TIMES_TO_CALL, 2, RETURN_PARAMETER)
        assertThat(outcome, is(RETURN_PARAMETER))
    }
}
