import java.util.ArrayList;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KasiskiAnalysis {

    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static int findGCDForAllRepetitions(String cipher) {
        ArrayList<Integer> distances = new ArrayList<>();

        int len = cipher.length();
        boolean found = false;

        for (int i = 0; i <= len - 3; i++) {
            String trigram = cipher.substring(i, i + 3);

            for (int j = i + 1; j <= len - 3; j++) {
                if (cipher.substring(j, j + 3).equals(trigram)) {
                    int dist = j - i;
                    distances.add(dist);
                    found = true;
                }
            }
        }

        if (!found) return -1;

        int result = distances.get(0);
        for (int i = 1; i < distances.size(); i++) {
            result = gcd(result, distances.get(i));
        }

        return result;
    }

    public static char findKeyChar(String row) {
        int[] freq = new int[26];

        for (int i = 0; i < row.length(); i++) {
            char c = Character.toLowerCase(row.charAt(i));
            if (c >= 'a' && c <= 'z') {
                freq[c - 'a']++;
            }
        }

        int maxIdx = 0;
        for (int i = 1; i < 26; i++) {
            if (freq[i] > freq[maxIdx]) maxIdx = i;
        }

        return (char) ('a' + (maxIdx - 4 + 26) % 26);
    }

    public static String decrypt(String cipher, String key) {
        String plain = "";
        int keyLen = key.length();
        int keyIdx = 0;

        for (int i = 0; i < cipher.length(); i++) {
            char c = cipher.charAt(i);

            if (Character.isLetter(c)) {
                boolean upper = Character.isUpperCase(c);

                int shifted = (Character.toLowerCase(c) - 'a'
                        - (key.charAt(keyIdx % keyLen) - 'a') + 26) % 26;

                char p = (char) ('a' + shifted);
                plain += upper ? Character.toUpperCase(p) : p;

                keyIdx++;
            } else {
                plain += c;
            }
        }

        return plain;
    }

    public static void attackWithGemini(String cipher, int baseGcd) {
        try {
            String apiKey = System.getenv("AIzaSyCnF6eXAcaD_rVSUVeNJ15fI1alR-YvXhA");

            HttpClient client = HttpClient.newHttpClient();

            int maxAttempts = 5;
            int multiplier = 1;

            StringBuilder allResults = new StringBuilder();
            allResults.append("[");

            for (int attempt = 0; attempt < maxAttempts; attempt++) {

                int keyLen = baseGcd * multiplier;

                ArrayList<String> rows = new ArrayList<>();
                for (int i = 0; i < keyLen; i++) rows.add("");

                for (int i = 0; i < cipher.length(); i++) {
                    if (Character.isLetter(cipher.charAt(i))) {
                        rows.set(i % keyLen, rows.get(i % keyLen) + cipher.charAt(i));
                    }
                }

                String key = "";

                for (int i = 0; i < keyLen; i++) {
                    key += findKeyChar(rows.get(i));
                }

                String decrypted = decrypt(cipher, key);

                System.out.println("Key: " + key + " -> " + decrypted);

                allResults.append("\"")
                        .append(decrypted.replace("\"", ""))
                        .append("\",");

                multiplier++;
            }

            if (allResults.charAt(allResults.length() - 1) == ',') {
                allResults.deleteCharAt(allResults.length() - 1);
            }

            allResults.append("]");

            String prompt =
                    "You are a cryptanalysis assistant.\n" +
                            "From the following decrypted outputs of a Vigenere cipher,\n" +
                            "identify the ONLY readable English sentence.\n\n" +
                            "Return ONLY JSON:\n" +
                            "{ \"best_text\": \"string\" }\n\n" +
                            "DATA:\n" + allResults;

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

                    System.out.println("\nGemini Result:");
                    System.out.println("Best decrypted text: " + bestText);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}