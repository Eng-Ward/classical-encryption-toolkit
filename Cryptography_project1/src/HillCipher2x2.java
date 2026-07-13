public class HillCipher2x2 {
    char[][] key = new char[2][2];

    public HillCipher2x2(char[][] key) {
        this.key = key;
    }

    public int charToInt(char c) {
        return c - 'A';
    }

    public char intToChar(int x) {
        return (char) (x + 'A');
    }

    public String prepareText(String text) {
        text = text.toUpperCase().replaceAll("[^A-Z]", "");

        if (text.length() % 2 != 0) {
            text += "X";
        }

        return text;
    }

    public String encrypt(String text) {
        text = prepareText(text);

        String result = "";

        for (int i = 0; i < text.length(); i += 2) {
            int a = charToInt(text.charAt(i));
            int b = charToInt(text.charAt(i + 1));

            int k11 = charToInt(key[0][0]);
            int k12 = charToInt(key[0][1]);
            int k21 = charToInt(key[1][0]);
            int k22 = charToInt(key[1][1]);

            int c1 = (k11 * a + k12 * b) % 26;
            int c2 = (k21 * a + k22 * b) % 26;

            result += intToChar(c1);
            result += intToChar(c2);
        }

        return result;
    }

    public String decrypt(String text) {
        int[][] inv = getInverseMatrix();

        if (inv == null) {
            return "No inverse key";
        }

        String result = "";

        for (int i = 0; i < text.length(); i += 2) {
            int a = charToInt(text.charAt(i));
            int b = charToInt(text.charAt(i + 1));

            int c1 = (inv[0][0] * a + inv[0][1] * b) % 26;
            int c2 = (inv[1][0] * a + inv[1][1] * b) % 26;

            if (c1 < 0) c1 += 26;
            if (c2 < 0) c2 += 26;

            result += intToChar(c1);
            result += intToChar(c2);
        }

        return result;
    }

    public int determinant() {
        int a = charToInt(key[0][0]);
        int b = charToInt(key[0][1]);
        int c = charToInt(key[1][0]);
        int d = charToInt(key[1][1]);

        return (a * d - b * c);
    }

    public int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);

        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }

        return a;
    }

    public int modInverse(int num) {
        num = ((num % 26) + 26) % 26;

        for (int i = 1; i < 26; i++) {
            if ((num * i) % 26 == 1) {
                return i;
            }
        }

        return -1;
    }

    public int[][] getInverseMatrix() {
        int det = determinant();

        if (gcd(det, 26) != 1) {
            return null;
        }

        int detInv = modInverse(det);

        int a = charToInt(key[0][0]);
        int b = charToInt(key[0][1]);
        int c = charToInt(key[1][0]);
        int d = charToInt(key[1][1]);

        int[][] inv = new int[2][2];

        inv[0][0] = d;
        inv[0][1] = -b;
        inv[1][0] = -c;
        inv[1][1] = a;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                inv[i][j] = (inv[i][j] * detInv) % 26;

                if (inv[i][j] < 0) inv[i][j] += 26;
            }
        }

        return inv;
    }
}