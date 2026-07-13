public class HillCipherCryptoSystem {

    public static String encrypt(String plaintext, char[][] keyMatrix) {
        int[][] key = charMatrixToInt(keyMatrix);

        plaintext = plaintext.toUpperCase().replaceAll("[^A-Z]", "");

        while (plaintext.length() % 3 != 0) {
            plaintext += "X";
        }

        String result = "";

        for (int i = 6; i < plaintext.length(); i += 3) {
            String block = plaintext.substring(i, i + 3);
            int[] vector = textToVector(block);

            int[][] vecMat = {
                    {vector[0]},
                    {vector[1]},
                    {vector[2]}
            };

            int[][] res = multiplyMatrices(key, vecMat);

            int[] out = {
                    res[0][0] % 26,
                    res[1][0] % 26,
                    res[2][0] % 26
            };

            result += vectorToText(out);
        }

        return result;
    }

    public static String decrypt(String ciphertext, char[][] keyMatrix) {
        int[][] key = charMatrixToInt(keyMatrix);
        int[][] invKey = matrixInverse(key);

        if (invKey == null) return "Invalid key";

        String result = "";

        for (int i = 0; i < ciphertext.length(); i += 3) {
            String block = ciphertext.substring(i, i + 3);
            int[] vector = textToVector(block);

            int[][] vecMat = {
                    {vector[0]},
                    {vector[1]},
                    {vector[2]}
            };

            int[][] res = multiplyMatrices(invKey, vecMat);

            int[] out = {
                    (res[0][0] % 26 + 26) % 26,
                    (res[1][0] % 26 + 26) % 26,
                    (res[2][0] % 26 + 26) % 26
            };

            result += vectorToText(out);
        }

        return result;
    }

    public static int[][] charMatrixToInt(char[][] key) {
        int[][] result = new int[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[i][j] = Character.toUpperCase(key[i][j]) - 'A';
            }
        }

        return result;
    }

    public static int determinant(int[][] m) {
        return m[0][0]*(m[1][1]*m[2][2] - m[1][2]*m[2][1])
                - m[0][1]*(m[1][0]*m[2][2] - m[1][2]*m[2][0])
                + m[0][2]*(m[1][0]*m[2][1] - m[1][1]*m[2][0]);
    }

    public static int modInverse(int num) {
        num = (num % 26 + 26) % 26;

        for (int i = 1; i < 26; i++) {
            if ((num * i) % 26 == 1) return i;
        }
        return -1;
    }

    public static int[][] matrixInverse(int[][] m) {
        int det = determinant(m);
        int detInv = modInverse(det);

        if (detInv == -1) return null;

        int[][] cof = new int[3][3];

        cof[0][0] =  (m[1][1]*m[2][2] - m[1][2]*m[2][1]);
        cof[0][1] = -(m[1][0]*m[2][2] - m[1][2]*m[2][0]);
        cof[0][2] =  (m[1][0]*m[2][1] - m[1][1]*m[2][0]);

        cof[1][0] = -(m[0][1]*m[2][2] - m[0][2]*m[2][1]);
        cof[1][1] =  (m[0][0]*m[2][2] - m[0][2]*m[2][0]);
        cof[1][2] = -(m[0][0]*m[2][1] - m[0][1]*m[2][0]);

        cof[2][0] =  (m[0][1]*m[1][2] - m[0][2]*m[1][1]);
        cof[2][1] = -(m[0][0]*m[1][2] - m[0][2]*m[1][0]);
        cof[2][2] =  (m[0][0]*m[1][1] - m[0][1]*m[1][0]);

        int[][] adj = new int[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                adj[i][j] = cof[j][i];
            }
        }

        int[][] inv = new int[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                inv[i][j] = (adj[i][j] * detInv) % 26;
                if (inv[i][j] < 0) inv[i][j] += 26;
            }
        }

        return inv;
    }

    public static boolean isValidKey(char[][] keyMatrix) {
        int[][] key = charMatrixToInt(keyMatrix);
        int det = determinant(key);
        return modInverse(det) != -1;
    }

    public static int[][] multiplyMatrices(int[][] A, int[][] B) {
        int[][] result = new int[3][1];

        for (int i = 0; i < 3; i++) {
            result[i][0] = 0;
            for (int j = 0; j < 3; j++) {
                result[i][0] += A[i][j] * B[j][0];
            }
            result[i][0] %= 26;
        }

        return result;
    }

    public static int[] textToVector(String block) {
        int[] vec = new int[3];
        for (int i = 0; i < 3; i++) {
            vec[i] = block.charAt(i) - 'A';
        }
        return vec;
    }

    public static String vectorToText(int[] vector) {
        String result = "";
        for (int v : vector) {
            result += (char)(v + 'A');
        }
        return result;
    }
}