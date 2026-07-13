import java.util.Random;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ShiftCryptoSystem {

    char[] characters = new char[26];

    public ShiftCryptoSystem() {
        char letters = 'A';
        for (int i = 0; i < characters.length; i++) {
            characters[i] = letters;
            letters++;
        }
    }

    public int generateRandomKey(int seed) {
        Random random = new Random(seed);
        return random.nextInt(26);
    }

    public String encryption(String plainText, int key) {
        String cipherTxt = "";
        int k = key % 26;

        for (int i = 0; i < plainText.length(); i++) {
            char ch = plainText.charAt(i);

            if (!Character.isLetter(ch)) continue;

            int index = Character.toUpperCase(ch) - 'A';
            int newIndex = (index + k) % 26;

            cipherTxt += characters[newIndex];
        }

        return cipherTxt;
    }

    public String decryption(String cipherTxt, int key) {
        String plainTxt = "";
        int k = key % 26;

        for (int i = 0; i < cipherTxt.length(); i++) {
            int index = cipherTxt.charAt(i) - 'A';
            int newIndex = (index - k + 26) % 26;

            plainTxt += characters[newIndex];
        }

        return plainTxt;
    }

    public void breakShiftCipher(String cipherTxt) {
        for (int key = 0; key < 26; key++) {
            System.out.println("Key = " + key + " -> " + decryption(cipherTxt, key));
        }
    }

    public void breakShiftCipherWithGemini(String cipherTxt) {
        try {
            String apiKey = "AIzaSyCnF6eXAcaD_rVSUVeNJ15fI1alR-YvXhA";

            HttpClient client = HttpClient.newHttpClient();

            StringBuilder bruteForceResults = new StringBuilder();
            bruteForceResults.append("[");

            for (int key = 0; key < 26; key++) {
                String decrypted = decryption(cipherTxt, key);
                System.out.println("Key = " + key + " -> " + decrypted);
                bruteForceResults.append("\"")
                        .append(decrypted.replace("\"", ""))
                        .append("\",");
            }

            if (bruteForceResults.charAt(bruteForceResults.length() - 1) == ',') {
                bruteForceResults.deleteCharAt(bruteForceResults.length() - 1);
            }

            bruteForceResults.append("]");

            String prompt =
                    "You are a cryptanalysis assistant.\n" +
                            "From the following decrypted outputs of a shift cipher,\n" +
                            "identify the ONLY readable English sentence.\n\n" +
                            "Return ONLY JSON in this format only response if its readable or not with:\n" +
                            "{ \"best_key\": number, \"best_text\": \"string\" }\n\n" +
                            "DATA:\n" + bruteForceResults;

            String safePrompt = prompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n");

            String requestBody = """
                    {
                      "contents": [
                        {
                          "parts": [
                            {
                              "text": "%s"
                            }
                          ]
                        }
                      ]
                    }
                    """.formatted(safePrompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                                    + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            String responseBody = response.body();

            int textIndex = responseBody.indexOf("\"text\":");
            if (textIndex != -1) {
                int q1 = responseBody.indexOf("\"", textIndex + 7);
                int q2 = q1 + 1;
                while (q2 < responseBody.length()) {
                    if (responseBody.charAt(q2) == '"' && responseBody.charAt(q2 - 1) != '\\') {
                        break;
                    }
                    q2++;
                }

                String innerText = responseBody.substring(q1 + 1, q2);

                innerText = innerText.replace("\\n", "").replace("\\\"", "\"").trim();

                int bestTextIndex = innerText.indexOf("\"best_text\"");
                if (bestTextIndex != -1) {
                    int colon = innerText.indexOf(":", bestTextIndex);
                    int bq1 = innerText.indexOf("\"", colon + 1);
                    int bq2 = innerText.indexOf("\"", bq1 + 1);
                    String bestText = innerText.substring(bq1 + 1, bq2);
                    System.out.println("\nGemini Result :");
//                    System.out.println("Ciphertext :" + cipherTxt);
                    System.out.println(" Best decrypted text: " + bestText);
                } else {
                    System.out.println(" Could not find best_text in: " + innerText);
                }
            } else {
                System.out.println("Could not parse response: " + responseBody);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
