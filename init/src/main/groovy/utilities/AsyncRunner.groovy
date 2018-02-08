package utilities

class AsyncRunner {

    private outcome
    def timesCalled = 0

    def run(method, parameters, int timesToCall, int secondsToSleep, expectedOutcome) {

        while (timesCalled != timesToCall && outcome != expectedOutcome) {

            outcome = method parameters

            if (outcome != expectedOutcome)
                sleep(secondsToSleep * 1000)

            timesCalled ++
        }

        outcome

    }
}