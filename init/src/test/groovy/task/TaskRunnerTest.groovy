package task

import com.amazonaws.services.cloudformation.model.Parameter
import file.ConfigFileFormatter
import file.ConfigFileWriter
import spock.lang.Specification
import file.ServiceStackConfigFileWriter

class TaskRunnerTest extends Specification {

    def "Both tasks are executed"() {

        given: "I have two tasks in the runner"

        Task task1 = Mock(Task)
        Task task2 = Mock(Task)

        TaskRunner taskRunner = new TaskRunner(Arrays.asList(task1,task2))

        when: "I run the task runner"

        taskRunner.run()

        then: "Both tasks are executed"

        1 * task1.run()
        1 * task2.run()
    }

    def "Both tasks are successful"() {

        given: "I have two tasks in the runner"

        Task task1 = Stub(Task)
        Task task2 = Stub(Task)

        TaskRunner taskRunner = new TaskRunner(Arrays.asList(task1,task2))

        and: "They are successful"

        task1.run() >> true
        task2.run() >> true

        when: "I run the task runner"

        boolean message = taskRunner.run()

        then: "A success message is displayed"

        message == true
    }

    def "One task is not successful"() {

        given: "I have two tasks in the runner"

        Task task1 = Stub(Task)
        Task task2 = Stub(Task)

        TaskRunner taskRunner = new TaskRunner(Arrays.asList(task1,task2))

        and: "They only one is successful"

        task1.run() >> task1Outcome
        task2.run() >> task2Outcome

        when: "I run the task runner"

        boolean runnerOutcome = taskRunner.run()

        then: "A success failure runnerOutcome is displayed"

        runnerOutcome == overallOutcome

        where: "One task is false"
        task1Outcome    |  task2Outcome |   overallOutcome
        true            |  false        |   false
        false           |  true         |   false
        false           |  false        |   false
        true            |  true         |   true

    }

    static String getProjectRoot() {
        File currentFile = new File('')
        currentFile.getAbsoluteFile().getParent()
    }

}
