# CipherLab — Classical Cryptography Toolkit with AI-Assisted Cryptanalysis

CipherLab is a Java console application that implements several classical ciphers and
gives you tools to both encrypt/decrypt with them and attempt to *break* them —
including an optional integration with the Gemini API that automatically picks out
the most "readable" candidate from a set of decryption attempts.

The project was built as a cryptography coursework exercise and covers the Hill
Cipher, the Vigenère Cipher (with Kasiski examination), and the Shift (Caesar)
Cipher, all driven from a single interactive menu.

## Features

- **Hill Cipher**
  - 3x3 matrix implementation (`HillCipherCryptoSystem`) used by the main menu, with
    key-validity checking (matrix must be invertible mod 26) and full
    encrypt/decrypt support.
  - A standalone 2x2 implementation (`HillCipher2x2`) with its own determinant,
    modular inverse, and matrix inversion logic.
- **Vigenère Cipher + Kasiski Examination**
  - Classic polyalphabetic encrypt/decrypt (`VigenereCryptoSystem`).
  - Kasiski analysis (`KasiskiAnalysis`) that finds repeated trigrams in a
    ciphertext, computes the GCD of their distances to estimate key length, and
    then recovers the key using frequency analysis.
  - Optional AI-assisted attack that tries several candidate key lengths and asks
    Gemini to identify which decrypted output is genuine English.
- **Shift (Caesar) Cipher**
  - Basic encrypt/decrypt (`ShiftCryptoSystem`).
  - Brute-force attack that prints all 26 possible decryptions.
  - AI-assisted brute-force attack that sends all 26 candidates to Gemini and asks
    it to return the correct key and plaintext as JSON.
- **Gemini API helper** (`GeminiAPI`) — a small utility for checking whether a
  string of text looks like readable English, used to support cryptanalysis.
- **Interactive console menu** (`CryptoDriver`) tying all of the above together.

## Project Structure

```
.
├── CryptoDriver.java              # Main entry point / interactive menu
├── HillCipherCryptoSystem.java    # 3x3 Hill Cipher (used by the menu)
├── HillCipher2x2.java             # Standalone 2x2 Hill Cipher implementation
├── VigenereCryptoSystem.java      # Vigenère encrypt/decrypt
├── KasiskiAnalysis.java           # Kasiski examination + AI-assisted attack
├── ShiftCryptoSystem.java         # Caesar/shift cipher, brute force, AI attack
├── GeminiAPI.java                 # Helper for checking text readability via Gemini
└── .idea/                         # IntelliJ IDEA project files
```

## Prerequisites

- **Java 17+** (the code uses text blocks and the `java.net.http` client).
- An internet connection if you want to use the Gemini-powered attack features.
- A valid **Gemini API key** if you want AI-assisted cryptanalysis to work (see
  the Security section below — the key must not be committed to the repo).

## Building and Running

From the project root:

```bash
javac *.java
java CryptoDriver
```

You'll be greeted with a menu:

```
=== MAIN MENU ===
1- Hill Cipher
2- Vigenere + Kasiski
3- Shift Cipher
0- Exit
```

Each submenu then lets you encrypt, decrypt, brute-force, or run an AI-assisted
attack, depending on the cipher.

## ⚠️ Security Note — Rotate Your API Key

The Gemini API key is currently **hardcoded directly in the source code**
(`GeminiAPI.java`, `KasiskiAnalysis.java`, and `ShiftCryptoSystem.java`). This means:

- Anyone with access to this repository (public or private) can see and use your key.
- If this repo is ever made public, or already has been, **the key should be
  considered compromised.**

**Before sharing or publishing this project:**
1. Revoke/regenerate the exposed key in Google AI Studio.
2. Remove the hardcoded key from all `.java` files.
3. Load it from an environment variable instead, e.g.:
   ```java
   String apiKey = System.getenv("GEMINI_API_KEY");
   ```
   (Note: `KasiskiAnalysis.java` currently calls
   `System.getenv("AIzaSy...")`, i.e. it passes the *key itself* as the
   *environment variable name* — this is a bug and will always return `null`.
   It should instead be `System.getenv("GEMINI_API_KEY")`, with the real key set
   in your shell/environment.)
4. Add a `.env`/config file to `.gitignore` rather than hardcoding secrets.

## Known Issues / Rough Edges

- `KasiskiAnalysis.attackWithGemini` reads the API key via
  `System.getenv("AIzaSyC...")`, which will not work as intended (see Security
  Note above).
- JSON parsing of the Gemini responses is done with manual string/index parsing
  rather than a JSON library, so unexpected response formats may cause silently
  incorrect output.
- The AI-assisted attacks make real network calls to the Gemini API and will
  fail gracefully (printing an error) if there's no network access or the key is
  invalid.
