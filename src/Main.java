import java.util.Map;

public class Main {
    public static void main(String[] args) {

        HuffmanCoding adaptiveHuffman = new HuffmanCoding();

        boolean enCode = adaptiveHuffman.encode("text.txt", 9, false, "encoded.txt");

        boolean deCode = adaptiveHuffman.decode("encoded.txt", "decoded.txt");

        Map<Character, String> codeBook = adaptiveHuffman.codebook();

        System.out.println("Encode Status: " + enCode + "\n Decode Status: " + deCode);

        System.out.println("Codebook: " + codeBook);

    }
}