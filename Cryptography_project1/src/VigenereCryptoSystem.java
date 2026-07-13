public class VigenereCryptoSystem {

    public String encrypt(String key, String plaintext) {
        key = key.toUpperCase();
        plaintext = plaintext.toUpperCase().replaceAll("[^A-Z]", "");
        String cipherText = "";

        for (int i = 0; i < plaintext.length(); i++) {
            int p = plaintext.charAt(i) - 'A';
            int k = key.charAt(i % key.length()) - 'A';

            cipherText += (char) (((p + k) % 26) + 'A');
        }


        return cipherText;
    }

    public String decrypt(String key, String cipherText) {
        key = key.toUpperCase();
        cipherText = cipherText.toUpperCase().replaceAll("[^A-Z]", "");
        String plainText = "";

        for (int i = 0; i < cipherText.length(); i++) {
            int c = cipherText.charAt(i) - 'A';
            int k = key.charAt(i % key.length()) - 'A';

            int p = (c - k);

            if (p < 0) {
                p = modulo((c - k), 26);
            }
            plainText += (char) ((p % 26) + 'A');
        }

        return plainText;
    }

    private int modulo(int a, int b) {

        while (a < 0) {
            a += b;
        }

        return a;
    }
}
