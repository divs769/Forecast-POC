package ui

class UserInputHandler {

    String promptForInput(String prompt) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
        println prompt
        br.readLine()
    }

}