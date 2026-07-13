import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiAPI {

    private static final String API_KEY = "AIzaSyCnF6eXAcaD_rVSUVeNJ15fI1alR-YvXhA";

    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + "gemini-2.0-flash:generateContent?key=" + API_KEY;

    public static boolean isReadable(String text) {
        try {
            String prompt = "Is the following text readable and meaningful English? "
                    + "Answer only Yes or No.\\n\\n" + text;

            String safePrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"");

            String body = "{"
                    + "\"contents\": [{"
                    + "  \"parts\": [{\"text\": \"" + safePrompt + "\"}]"
                    + "}]}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body().toLowerCase();

            System.out.println("[Gemini Raw Response]: " + response.body());

            return responseBody.contains("\"yes\"") || responseBody.contains(" yes");

        } catch (Exception e) {
            System.out.println("[Gemini Error]: " + e.getMessage());
            return false;
        }
    }
}