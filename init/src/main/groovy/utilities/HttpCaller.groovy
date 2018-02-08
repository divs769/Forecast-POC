package utilities

class HttpCaller {

    int callEndpoint(String url) {

        URL obj = new URL(url)
        HttpURLConnection con = (HttpURLConnection) obj.openConnection()
        System.out.println("\nSending 'GET' request to URL : " + url)
        int responseCode = con.getResponseCode()
        System.out.println("Response Code : " + responseCode)
        responseCode
    }
}
