import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

public interface FileCompressor {
    boolean encode(String input_filename, int level, boolean reset, String output_filename);

    boolean decode(String input_filename, String output_filename);

    Map<Character, String> codebook();
}

class HuffmanCoding implements FileCompressor {

    /**
     * To get path value of particular node, from searchNode1 method
     */
    static String keyPath1 = "";

    public String getKeyPath1() {
        return keyPath1;
    }

    public static void setKeyPath1(String keyPath1) {
        HuffmanCoding.keyPath1 = keyPath1;
    }

    /**
     * to store encoded value of the character
     */
    static String enCoding = "";

    public String getenCoding() {
        return enCoding;
    }

    public static void setenCoding(String enCoding) {
        HuffmanCoding.enCoding = enCoding;
    }

    /**
     * store character and its frequency
     */
    static HashMap<String, Integer> frequencyCount = new HashMap<String, Integer>();

    public HashMap<String, Integer> getFrequencyCount() {
        return frequencyCount;
    }

    public static void setFrequencyCount(HashMap<String, Integer> frequencyCount) {
        HuffmanCoding.frequencyCount = frequencyCount;
    }

    /**
     * store first adaption tree according to its stages
     */
    static HashMap<Integer, AHnode> fTreeMap = new HashMap<Integer, AHnode>();

    public HashMap<Integer, AHnode> getfTreeMap() {
        return fTreeMap;
    }

    public static void setfTreeMap(HashMap<Integer, AHnode> fTreeMap) {
        HuffmanCoding.fTreeMap = fTreeMap;
    }

    /**
     * Store rebuilt tree according to stages
     */
    static HashMap<Integer, AHnode> reTreeMap = new HashMap<Integer, AHnode>();

    public HashMap<Integer, AHnode> getreTreeMap() {
        return reTreeMap;
    }

    public static void setreTreeMap(HashMap<Integer, AHnode> reTreeMap) {
        HuffmanCoding.reTreeMap = reTreeMap;
    }

    /**
     * To increase end of File value
     */
    public boolean flagForeF = false;

    /**
     * Store codeBook of the final rebuilt tree
     */
    static HashMap<Character, String> codeBookforRB = new HashMap<Character, String>();

    public HashMap<Character, String> getcodeBookforRB() {
        return codeBookforRB;
    }

    public static void setcodeBookforRB(HashMap<Character, String> codeBookforRB) {
        HuffmanCoding.codeBookforRB = codeBookforRB;
    }

    /**
     * This method is used to encode the data of the text file. It takes characters
     * based on the level. To build the first adaption tree and to rebuilt it again,
     * it counts the frequencies of the characters and sort it based on its
     * frequency on every stages, to do these two processes, this method calls
     * ‘countChar()’ and ‘setFrequenciesStart’ methods. After that, it builds the
     * first adaption tree and alongside, it generates encoded string and store it
     * to ‘enCoding’ string. Based on the reset value, program updates frequency
     * count value and call ‘rebuiltTree’ method to update the tree. The rebuilt
     * tree will be stored according to its stages in a ‘reTreeMap’ (key: integer
     * and value: binary tree) HashMap. Using the value of the HashMap, first
     * adaptive tree is generated for the next adaptive break string. At last, it
     * writes encoded string to the file.
     * 
     * @param input_filename  to get data from file
     * @param level           to decide break in adaption
     * @param reset           to decide previous character's frequency will use or
     *                        not
     * @param output_filename to display encoded string
     * 
     * @return true if compress successfully else false
     */
    public boolean encode(String input_filename, int level, boolean reset, String output_filename) {

        AHnode rootNode = new AHnode();
        rootNode.value = "--";
        rootNode.freq = 0;

        AHnode leftNode = new AHnode();
        leftNode.value = "nC";
        leftNode.freq = 0;

        AHnode rightNode = new AHnode();
        rightNode.value = "eF";
        rightNode.freq = 0;

        rootNode.left = leftNode;
        rootNode.right = rightNode;

        reTreeMap.put(0, rootNode);
        setreTreeMap(reTreeMap);

        String str = "";
        try {
            str = readText(input_filename, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File myObj = new File(output_filename);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        int stages = 1;
        int startTo = 1;
        int endTo = 1;
        int forLevel = 1;

        while (startTo <= str.length()) {

            if (forLevel > level) {
                forLevel = level;
            }

            int base = 2;
            int power = forLevel;
            int result = 1;
            while (power != 0) {
                result *= base;
                --power;
            }
            endTo = startTo + result - 1;

            String forEncoding = breakString(str, startTo, endTo);

            if (forEncoding.length() > 3 && str.length() > 3) {
                String lastOfFile = str.substring(str.length() - 3);
                String lastThreeChar = forEncoding.substring(forEncoding.length() - 3);
                if (lastOfFile.equals(lastThreeChar)) {
                    flagForeF = true;
                }
            }

            System.out.println("\n New String: " + forEncoding);

            if (startTo == 1) {

                frequencyCount = countChar(forEncoding);

                frequencyCount.put("nC", 0);
                frequencyCount.put("eF", 0);

                int set1 = 4;

                if (str.length() < 2) {
                    set1 = 3;
                }

                String[] inputStr = new String[set1];
                inputStr[0] = "nC";
                inputStr[1] = "eF";
                inputStr[2] = String.valueOf(forEncoding.charAt(0));
                if (str.length() > 1) {
                    inputStr[3] = String.valueOf(forEncoding.charAt(1));
                }
                int ascii = String.valueOf(level).charAt(0);
                // System.out.println(ascii);
                int castAscii;
                if (reset) {
                    castAscii = String.valueOf(1).charAt(0);
                } else {
                    castAscii = String.valueOf(0).charAt(0);
                }

                enCoding = ascii + " " + castAscii;
                HuffmanCoding.setenCoding(enCoding);

                AHnode fTree = AdaptiveHuffmanCode.FirstAdaption(frequencyCount, inputStr, null);

                fTreeMap.put(stages, fTree);
                setfTreeMap(fTreeMap);

            } else {

                String[] frequenciesStart = forEncoding.split("");

                frequencyCount = getFrequencyCount();

                // System.out.println(frequencyCount);
                reTreeMap = getreTreeMap();
                AHnode node = reTreeMap.get(stages - 1);

                AHnode please = node.copy();

                AHnode fTree = AdaptiveHuffmanCode.FirstAdaption(frequencyCount, frequenciesStart, node);
                fTreeMap.put(stages, fTree);
                setfTreeMap(fTreeMap);
                reTreeMap.put(stages - 1, please);

            }

            frequencyCount = getFrequencyCount();
            HashMap<String, Integer> forResetTrue = new HashMap<String, Integer>();

            if (reset) {

                frequencyCount = countChar(forEncoding);
                frequencyCount.put("nC", frequencyCount.size());
                frequencyCount.put("eF", 0);
                TreeSet<String> keys = new TreeSet<>(forResetTrue.keySet());
                for (String key : keys) {
                    if (!frequencyCount.containsKey(key)) {
                        frequencyCount.put(key, 0);
                    }
                }
            }

            if (flagForeF) {
                frequencyCount.put("eF", 1);
            }

            String[] frequenciesStart = setFrequenciesStart(frequencyCount);

            HashMap<String, Integer> storeFrequencyCount = new HashMap<String, Integer>();
            storeFrequencyCount.putAll(frequencyCount);

            AHnode reTree = AdaptiveHuffmanCode.rebuiltTree(frequencyCount, frequenciesStart);
            reTreeMap.put(stages, reTree);
            setreTreeMap(reTreeMap);
            frequencyCount.clear();
            frequencyCount.putAll(storeFrequencyCount);
            startTo = endTo + 1;
            stages++;
            forLevel++;
        }

        String eCstring = getenCoding();
        AHnode lastNode = fTreeMap.get(stages - 1);

        String s1 = "";
        AdaptiveHuffmanCode.searchNode1(lastNode, "eF", s1);
        s1 = getKeyPath1();
        eCstring = eCstring + " " + s1;

        try {
            PrintWriter encodeWriter = new PrintWriter(output_filename, "UTF-8");
            encodeWriter.println(eCstring);
            encodeWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return true;

    }

    /**
     * This method reads the input_file and store its value in array string using
     * string’s split method. Before starting the decoding process, it calls
     * ‘reTreeMap’ HashMap and retrieve the tree information. From this tree, it
     * checks the path (encoded string of the character) is in the tree or not if
     * yes then get its value else add it to the right most side of the tree. The
     * decoded value will be stored in output_filename text file.
     * 
     * @param input_filename  to get encoded data
     * @param output_filename to store decoded data
     * 
     * @return true if decode successfully else false
     */
    @Override
    public boolean decode(String input_filename, String output_filename) {

        try {
            File myObj = new File(output_filename);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        String decodedString = "";

        String str = "";
        try {
            str = readText(input_filename, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] tokens1 = str.split(" ");

        String[] tokens = Arrays.copyOf(tokens1, tokens1.length - 1);

        int level1 = Character.getNumericValue(Integer.parseInt(tokens[0]));

        int count = 2;
        int level2 = 1;
        int stages = 1;

        HashMap<Integer, AHnode> forDecoding = getreTreeMap();
        int endCount = 0;
        int spacecount = 0;
        while (tokens.length > count) {

            if (level2 > level1) {
                level2 = level1;
            }

            int base = 2;
            int power = level2;
            int result = 1;
            while (power != 0) {
                result *= base;
                --power;
            }

            endCount = count + result + spacecount;
            if (endCount > tokens.length) {
                endCount = tokens.length;
            }
            AHnode nodeForDecoding = forDecoding.get(stages - 1);
            while (count < endCount) {
                String token = tokens[count];
                if (count + 1 < tokens.length) {
                    if (tokens[count + 1].equals("")) {
                        token = token + " ";
                        count++;
                        endCount++;
                    }
                }

                Boolean flag = true;

                boolean found = AdaptiveHuffmanCode.searchNode1(nodeForDecoding, "nC", "");
                String s = getKeyPath1();

                if (found) {
                    String check = token.substring(0, token.length() - 1);
                    if (s.equals(check)) {
                        flag = false;
                    }

                } else if (flag) {
                    for (int i = 0; i < token.length(); i++) {
                        flag = Character.isDigit(token.charAt(i));
                    }
                }

                String value = "";

                if (flag) {
                    value = AdaptiveHuffmanCode.searchTheValue(nodeForDecoding, token);
                } else {
                    value = Character.toString(token.charAt(token.length() - 1));
                    AdaptiveHuffmanCode.putAtrightMost(nodeForDecoding, value, 0, 0);
                }

                decodedString = decodedString + value;
                count++;
            }

            stages++;
            level2++;
        }

        try {
            PrintWriter encodeWriter = new PrintWriter(output_filename, "UTF-8");
            encodeWriter.println(decodedString);
            encodeWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return true;
    }

    @Override
    public Map<Character, String> codebook() {

        HashMap<Character, String> codebookMap = getcodeBookforRB();

        return codebookMap;
    }

    /**
     * This method read the data from file
     * 
     * @param forDecoding to use multiple lines
     */
    String readText(String input_filename, boolean forDecoding) throws IOException {

        String returnStr = "";
        // int cl = 1;
        int line = 1;
        try {
            File myObj = new File(input_filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {

                String data = myReader.nextLine();
                if (line == 1) {
                    returnStr = returnStr + data;
                }

                if (line > 1 && forDecoding) {
                    returnStr = returnStr + "\n" + data;
                }
                line++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return returnStr;
    }

    String breakString(String str, int startTo, int endTo) {
        String output = "";
        if (str.length() < endTo)
            endTo = str.length();

        for (int i = startTo; i <= endTo; i++) {
            output = output + str.charAt(i - 1);
        }
        return output;
    }

    HashMap<String, Integer> countChar(String str) {

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        char[] str1 = str.toCharArray();

        for (char c : str1) {
            if (map.containsKey(String.valueOf(c))) {
                map.put(String.valueOf(c), map.get(String.valueOf(c)) + 1);
            } else {
                map.put(String.valueOf(c), 1);
            }
        }

        return map;
    }

    /**
     * First,the frequency count list’s characters are added to the TreeSet with
     * their frequency and in-between these two values add some symbol to
     * differentiate the character and frequency value (for example, character: a,
     * frequency: 5 and symbol ^, then store 5^a in the TreeSet). So, it first
     * TreeSet sorts this value based on the first character, i.e. frequency and if
     * characters have same frequency then it sorts according to character.
     * 
     * 
     * @param fC2
     * @return sorted list of frequency counts
     */
    String[] setFrequenciesStart(HashMap<String, Integer> fC2) {

        TreeSet<String> keys = new TreeSet<>(fC2.keySet());
        TreeSet<String> ts1 = new TreeSet<>();

        for (String sr : keys) {
            if (fC2.get(sr) > 9) {
                ts1.add(900 + fC2.get(sr) + "0^^" + sr);
            } else {
                ts1.add(fC2.get(sr) + "0^^" + sr);
            }

        }

        String[] output = new String[ts1.size()];
        int count = 0;
        for (String sr1 : ts1) {
            String temp = "";
            int start = 0;
            for (int i = 0; i < sr1.length(); i++) {
                if (sr1.charAt(i) == (char) '0' && sr1.charAt(i + 1) == (char) '^' && sr1.charAt(i + 2) == (char) '^') {
                    start = i + 3;
                    break;
                }
            }

            for (int i = start; i < sr1.length(); i++) {
                temp = temp + sr1.charAt(i);
            }

            output[count] = temp;
            count++;
        }
        return output;
    }

}

class AHnode {
    int freq;
    String value;

    AHnode left;
    AHnode right;

    public AHnode() {
    }

    public AHnode(int freq, String value, AHnode left, AHnode right) {
        this.freq = freq;
        this.value = value;
        this.left = left;
        this.right = right;
    }

    AHnode copy() {
        AHnode left = null;
        AHnode right = null;
        if (this.left != null) {
            left = this.left.copy();
        }
        if (this.right != null) {
            right = this.right.copy();
        }
        return new AHnode(freq, value, left, right);
    }
}

class AdaptiveHuffmanCode {

    /**
     * 
     * @param root        tree
     * @param s           empty string
     * @param finalOutput to store the codebook
     * @return codebook
     */
    public static HashMap<Character, String> codeBook(AHnode root, String s, HashMap<Character, String> finalOutput) {

        if (root.left == null && root.right == null) {

            if (root.value.length() < 2) {
                finalOutput.put(root.value.charAt(0), s);
            }

            return finalOutput;
        }

        codeBook(root.left, s + "0", finalOutput);
        codeBook(root.right, s + "1", finalOutput);

        return finalOutput;
    }

    /**
     * This method first checks the new character is in the node (previously rebuilt
     * tree), if yes then simply update its value to fC else add the new character
     * to the right most side of the node. After that it returns the encoded value
     * of the character based on the previous condition.
     * 
     * 
     * @param fC      frequency count
     * @param fS      frequencies start
     * @param node123 rebuilt node
     * @return ending tree
     */
    public static AHnode FirstAdaption(HashMap<String, Integer> fC, String[] fS, AHnode node123) {

        HuffmanCoding obj = new HuffmanCoding();

        String eCstring = obj.getenCoding();
        HashMap<Integer, AHnode> treeStore = new HashMap<Integer, AHnode>();

        AHnode root = node123;

        int count = 0;
        int addedChar = 1;
        int index1 = 0;
        while (fS.length > count && fS.length > index1) {

            if (root != null) {

                String s = "";
                searchNode1(root, "nC", s);

                s = obj.getKeyPath1();

                String s1 = "";
                boolean f1 = searchNode1(root, fS[index1], s1);

                if (f1) {

                    fC.put(fS[index1], fC.get(fS[index1]) + 1);
                    s1 = obj.getKeyPath1();
                    eCstring = eCstring + " " + s1;

                    index1++;
                } else {
                    eCstring = eCstring + " " + s + fS[index1];

                    fC.put("nC", fC.get("nC") + 1);

                    fC.put(fS[index1], 1);

                    root = putAtrightMost(root, fS[index1], fC.get(fS[index1]), addedChar);

                    index1++;
                }

            } else {
                AHnode left11 = new AHnode();
                left11.value = fS[0];
                left11.freq = fC.get(fS[0]);

                AHnode right11 = new AHnode();
                right11.value = fS[1];
                right11.value = fS[1];

                AHnode bTree = new AHnode();

                bTree.freq = left11.freq + right11.freq;
                bTree.value = "*" + count;

                bTree.left = left11;
                bTree.right = right11;

                root = bTree;

                treeStore.put(0, bTree);
                index1 = 2;
            }
            count++;
        }

        System.out.println("Code boook for first adaption: ");

        HashMap<Character, String> forCodeBook = new HashMap<Character, String>();

        HashMap<Character, String> codeBookforFA = codeBook(root, "", forCodeBook);
        System.out.println(codeBookforFA);

        HuffmanCoding.setenCoding(eCstring);
        HuffmanCoding.setFrequencyCount(fC);
        return root;
    }

    /**
     * 
     * 
     * 
     * @param node tree from which to find the key
     * @param key  key value
     * @param s    to store path
     * @return true if it finds
     */
    public static boolean searchNode1(AHnode node, String key, String s) {

        if (node == null)
            return false;

        if (node.value.equals(key)) {
            HuffmanCoding.setKeyPath1(s);
            return true;
        }

        if (searchNode1(node.left, key, s = s + 0))
            return true;

        s = s.substring(0, s.length() - 1);

        if (searchNode1(node.right, key, s = s + 1))
            return true;

        s = s.substring(0, s.length() - 1);

        return false;
    }

    /**
     * 
     * @param node1     node in which value will be added at right most
     * @param value1    character
     * @param freq1     character frequnecy
     * @param addedChar to count
     * @return character added tree
     */
    public static AHnode putAtrightMost(AHnode node1, String value1, int freq1, int addedChar) {

        if (node1.right == null) {

            AHnode left1 = new AHnode();
            left1.value = node1.value;
            left1.freq = node1.freq;

            AHnode right1 = new AHnode();
            right1.value = value1;
            right1.freq = freq1;

            node1.value = "*" + addedChar;
            node1.freq = left1.freq + right1.freq;

            node1.left = left1;
            node1.right = right1;

            return node1;
        }

        AHnode node2 = putAtrightMost(node1.right, value1, freq1, addedChar);

        node1.right = node2;

        return node1;
    }

    /**
     * This method rebuilt the tree using frequencyCount (fC1) HashMap and
     * frequencies start (fS). It uses the first two values every time to create
     * tree and then again sort fC1. At last, ‘codeBook’ method is called to get the
     * path of each character in the string and store it to ‘codeBookforRB’.
     * 
     * 
     * @param fC1 frequency count
     * @param fS  frequencies start
     * @return node
     */
    public static AHnode rebuiltTree(HashMap<String, Integer> fC1, String[] fS) {

        HashMap<String, AHnode> treeStore = new HashMap<String, AHnode>();

        HuffmanCoding obj = new HuffmanCoding();

        AHnode root = null;

        int count = 0;

        while (fS.length > 1) {

            AHnode left1 = new AHnode();

            if (treeStore.containsKey(fS[0])) {
                left1 = treeStore.get(fS[0]);
                treeStore.remove(fS[0]);
            } else {
                left1.value = fS[0];
                left1.freq = fC1.get(fS[0]);
            }
            fC1.remove(fS[0]);

            AHnode right1 = new AHnode();
            if (treeStore.containsKey(fS[1])) {
                right1 = treeStore.get(fS[1]);
                treeStore.remove(fS[1]);
            } else {
                right1.value = fS[1];
                right1.freq = fC1.get(fS[1]);
            }
            fC1.remove(fS[1]);

            AHnode bTree = new AHnode();

            bTree.freq = left1.freq + right1.freq;
            bTree.value = "zzz" + count;

            bTree.left = left1;
            bTree.right = right1;

            root = bTree;

            fC1.put(bTree.value, bTree.freq);

            treeStore.put(bTree.value, bTree);

            fS = obj.setFrequenciesStart(fC1);
            count++;

        }

        System.out.println("\nCodeBook for rebuilt: ");

        HashMap<Character, String> forCodeBook = new HashMap<Character, String>();

        HuffmanCoding.codeBookforRB = codeBook(root, "", forCodeBook);

        System.out.println(HuffmanCoding.codeBookforRB);

        return root;
    }

    /**
     * 
     * @param nodeForDecoding tree to get value of the encoded character
     * @param token           encoded value of character
     * @return character value
     */
    public static String searchTheValue(AHnode nodeForDecoding, String token) {

        for (int i = 0; i < token.length(); i++) {
            char direction = token.charAt(i);

            if (direction == '0') {
                nodeForDecoding = nodeForDecoding.left;
            } else {
                nodeForDecoding = nodeForDecoding.right;
            }
        }

        return nodeForDecoding.value;
    }

}