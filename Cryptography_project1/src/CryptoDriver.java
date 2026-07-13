import java.util.Scanner;

public class CryptoDriver {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1- Hill Cipher");
            System.out.println("2- Vigenere + Kasiski");
            System.out.println("3- Shift Cipher");
            System.out.println("0- Exit");

            System.out.print("Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 0) break;

            switch (choice) {
                case 1:
                    hillMenu(scanner);
                    break;
                case 2:
                    vigenereMenu(scanner);
                    break;
                case 3:
                    shiftMenu(scanner);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }

        System.out.println("Program Ended");
    }

    public static void hillMenu(Scanner scanner) {
        System.out.println("\n1- Encrypt");
        System.out.println("2- Decrypt");

        int choice = scanner.nextInt();
        scanner.nextLine();

        char[][] key = new char[3][3];

        System.out.println("Enter 3x3 key matrix (letters only):");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                key[i][j] = scanner.next().toUpperCase().charAt(0);
            }
        }
        scanner.nextLine();

        if (!HillCipherCryptoSystem.isValidKey(key)) {
            System.out.println("Invalid Key Matrix");
            return;
        }

        System.out.print("Enter text: ");
        String text = scanner.nextLine();

        if (choice == 1) {
            String cipher = HillCipherCryptoSystem.encrypt(text, key);
            System.out.println("Cipher: " + cipher);
        } else {
            String plain = HillCipherCryptoSystem.decrypt(text, key);
            System.out.println("Plain: " + plain);
        }
    }

    public static void vigenereMenu(Scanner scanner) {
        VigenereCryptoSystem vigenere = new VigenereCryptoSystem();

        System.out.println("\n1- Encrypt");
        System.out.println("2- Decrypt");
        System.out.println("3- Kasiski Attack");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("Enter key: ");
            String key = scanner.nextLine();

            System.out.print("Enter plaintext: ");
            String text = scanner.nextLine();

            String cipher = vigenere.encrypt(key, text);
            System.out.println("Cipher: " + cipher);

        } else if (choice == 2) {
            System.out.print("Enter key: ");
            String key = scanner.nextLine();

            System.out.print("Enter ciphertext: ");
            String text = scanner.nextLine();

            String plain = vigenere.decrypt(key, text);
            System.out.println("Plain: " + plain);

        } else if (choice == 3) {
            System.out.print("Enter ciphertext: ");
            String cipher = scanner.nextLine();

            int gcd = KasiskiAnalysis.findGCDForAllRepetitions(cipher);

            if (gcd == -1) {
                System.out.println("Kasiski failed");
                return;
            }

            KasiskiAnalysis.attackWithGemini(cipher, gcd);
        }
    }

    public static void shiftMenu(Scanner scanner) {
        ShiftCryptoSystem shift = new ShiftCryptoSystem();

        System.out.println("\n1- Encrypt");
        System.out.println("2- Decrypt");
        System.out.println("3- Brute Force");
        System.out.println("4- Break with Gemini");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("Enter key: ");
            int key = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter plaintext: ");
            String text = scanner.nextLine();

            System.out.println("Cipher: " + shift.encryption(text, key));

        } else if (choice == 2) {
            System.out.print("Enter key: ");
            int key = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter ciphertext: ");
            String text = scanner.nextLine();

            System.out.println("Plain: " + shift.decryption(text, key));

        } else if (choice == 3) {
            System.out.print("Enter ciphertext: ");
            String text = scanner.nextLine();

            shift.breakShiftCipher(text);

        } else if (choice == 4) {
            System.out.print("Enter ciphertext: ");
            String text = scanner.nextLine();

            shift.breakShiftCipherWithGemini(text);
        }
    }
}