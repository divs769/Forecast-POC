package naming

class NameFormatter {

    String getPackageName(gitRepoName) {
        gitRepoName.replace('-','')
    }

    String getDisplayableServiceName(gitRepoName) {

        String[] arr=gitRepoName.split("-");
        uppercaseFirstLetter(arr)
        def newDisplayableServiceName = putWordsBackTogether(arr.toList())
    }

    private String putWordsBackTogether(ArrayList<String> arrayString) {

        String newDisplayableServiceName = "";

        for (String s : arrayString) {

            int lastElement = arrayString.size() - 1;
            //arrayString.size() - 1
            if (!(s.equals(arrayString.get(lastElement)))) {
                newDisplayableServiceName += s + " ";
            } else {
                newDisplayableServiceName += s;
            }
        }
        newDisplayableServiceName
    }

    private void uppercaseFirstLetter(String[] arr) {

        for (int i = 0; i < arr.length; i++) {
            String word = arr[i];
            char[] letters = word.toCharArray();
            letters[0] = Character.toUpperCase(letters[0]);
            arr[i] = new String(letters);

        }

    }

}
