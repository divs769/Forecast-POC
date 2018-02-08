package task

class TaskRunner {

    private final List<Task> tasks

    TaskRunner(List<Task> tasks) {
        this.tasks = tasks
    }

    boolean run() {

        int numberOfTasks = tasks.size()
        int successfulTasks = 0

        for (Task task : tasks){

            boolean success = task.run()

            if(success) {
                successfulTasks ++
            }

        }
        return numberOfTasks == successfulTasks
    }

}