import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/*
 * A program that implements word dictionary with location positions stored from a given text file using trie structure.
 *
 * It implements something similar to the first half of the following link:
 *
 * http://www.ardendertat.com/2011/12/20/programming-interview-questions-23-find-word-positions-in-text/
 *
 * Thanks for the following code snippet for reference at the following site:
 * http://alexcode.tumblr.com/question_9
 *
 * author: Frank Giordano 11/26/2015
 */
class Node {

    private char letter;
    private boolean isTerminal = false;
    private Map<Character, Node> children = new TreeMap<>();
    private HashMap<Integer, List<Integer>> wordLinesPositionsInfo;

    public Node() {
        this.wordLinesPositionsInfo = new HashMap<Integer, List<Integer>>();
    }

    public Node(char letter) {
        this.wordLinesPositionsInfo = new HashMap<Integer, List<Integer>>();
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public void setTerminal(boolean isTerminal) {
        this.isTerminal = isTerminal;
    }

    public Map<Character, Node> getChildren() {
        return children;
    }

    public void setChildren(Map<Character, Node> children) {
        this.children = children;
    }

    public HashMap<Integer, List<Integer>> getWordLinesPositionsInfo() {
        return wordLinesPositionsInfo;
    }

    public void setWordLinesPositionsInfo(HashMap<Integer, List<Integer>> wordLinesPositionsInfo) {
        this.wordLinesPositionsInfo = wordLinesPositionsInfo;
    }

}

class Trie {

    private Node root = new Node();

    public boolean contains(String word) {
        Node current = root;
        char[] chars = word.toCharArray();

        for (Character c : chars) {
            Node next = current.getChildren().get(c);
            if (next == null)
                return false;
            else
                current = next;
        }

        return current.isTerminal();
    }

    public Node getTerminalNode(String word) {
        Node current = root;
        char[] chars = word.toCharArray();

        for (Character c : chars) {
            Node next = current.getChildren().get(c);
            if (next == null)
                return null;
            else
                current = next;
        }

        return current;
    }

    public Node getItem(String word) {
        Node current = root;
        char[] chars = word.toCharArray();

        for (Character c : chars) {
            Node next = current.getChildren().get(c);
            if (next == null) {
                next = new Node(c);
                current.getChildren().put(c, next);
            }
            current = next;
        }

        current.setTerminal(true);
        return current;
    }

    public void printALL() {
        List<Node> l = new ArrayList<>();
        l.add(root);
        output(l, "");
    }

    public void printALL(String searchWord, Trie dictionary) {
        if (dictionary.getTerminalNode(searchWord) == null) {
            System.out.println(" The following word " + searchWord + " was not found.");
        } else {
            for (Entry<Integer, List<Integer>> entry : dictionary.getTerminalNode(searchWord).getWordLinesPositionsInfo().entrySet()) {
                foundMsg(searchWord, entry);
            }
        }
    }

    private void foundMsg(String searchWord, Entry<Integer, List<Integer>> entry) {
        StringBuilder message = new StringBuilder();
        message.append("The following word ");
        message.append("\"");
        message.append(searchWord);
        message.append("\"");
        message.append(" was found at line number ");
        message.append(entry.getKey());
        message.append(" at position(s): ");
        message.append((entry.getValue().toString()));
        System.out.println(message);
    }

    // Depth First Search - output all words and their lines and positions locations within the given text file
    public void output(List<Node> currentPath, String indent) {
        Node current = currentPath.get(currentPath.size() - 1);

        if (current.isTerminal()) {
            String word = "";
            for (Node n : currentPath)
                word += n.getLetter();
            System.out.print(indent + word + " ");

            if (!current.getWordLinesPositionsInfo().isEmpty()) {
                System.out.println(current.getWordLinesPositionsInfo().toString());
            } else {
                System.out.println();
            }

        }

        for (Entry<Character, Node> e : current.getChildren().entrySet()) {
            Node node = e.getValue();
            List<Node> newlist = new ArrayList<>();
            newlist.addAll(currentPath);
            newlist.add(node);
            output(newlist, indent);
        }
    }

}

public class FindWord {

    private static Trie dictionary = null;
    private static String fileName = "t3.txt";

    public static void search(String searchWord) {
        if (dictionary != null) {  // use current cache
            dictionary.printALL(searchWord, dictionary);
            return;
        }

        processFile();
        dictionary.printALL(searchWord, dictionary);
    }

    private static void processFile() {

        int position = 0;
        int lineCount = 0;
        String line = "";

        dictionary = new Trie();
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(fileName));

            // while we loop through the file, read each line until there is nothing left to read.
            // this assumes we have carriage return ending at each text line.
            while ((line = bReader.readLine()) != null) {

                lineCount++;

                line = toLowerCase(line);
                String[] words = line.replaceAll("[^a-z0-9]", " ").split(" ");
                for (String word : words) {
                    if ("".equals(word))
                        continue;

                    int startIndex = 0;
                    while ((position = line.indexOf(word, startIndex)) != -1) {
                        startIndex = position + word.length();
                        // ignore finding the word text within another larger word
                        if (isWordEmbedded(position, word, line)) {
                            continue; // skip this position
                        }

                        // first time this word is seen
                        if (!dictionary.contains(word)) {
                            Node node = dictionary.getItem(word);
                            addWordPosition(position, lineCount, node);
                            break;
                        }

                        Node node = dictionary.getTerminalNode(word);
                        // first time adding a position for this word for this line
                        if (dictionary.contains(word) && node != null && node.getWordLinesPositionsInfo().get(lineCount) == null) {
                            addWordPosition(position, lineCount, node);
                            break;
                        }

                        // at this point, it is obvious add position to existing word/line storage
                        List<Integer> positions = node.getWordLinesPositionsInfo().get(lineCount);
                        // increase last known position to the end of the word so it starts searching there
                        int lastKnownPos = positions.get(positions.size() - 1) + word.length();
                        // search for the next word occurrence from current read in line file
                        position = line.indexOf(word, lastKnownPos);
                        if (position == -1)
                            break;
                        if (!positions.contains(Integer.valueOf(position))) {
                            positions.add(position);
                            startIndex = position + word.length();
                        }
                    }
                }
            }

            bReader.close();
        } catch (IOException e) {
            System.out.print("Error reading file. Error message = " + e.getMessage());
            System.exit(-1);
        }
    }

    private static void addWordPosition(int position, int lineCount, Node node) {
        List<Integer> positions = new ArrayList<Integer>();
        positions.add(position);
        node.getWordLinesPositionsInfo().put(lineCount, positions);
    }

    private static boolean isWordEmbedded(int position, String word, String line) {
        if (position != 0 && position != line.length() - 1 && position + (word.length() - 1) != (line.length() - 1)) {
            boolean isAlphabeticRightSide = Character.isAlphabetic(line.charAt(position + (word.length())));
            boolean isAlphabeticLeftSide = Character.isAlphabetic(line.charAt(position - 1));
            if (isAlphabeticRightSide || isAlphabeticLeftSide) {
                return true;
            }
        }

        if (position == 0 && (position + (word.length()) < line.length())) {
            boolean isAlphabeticRightSide = Character.isAlphabetic(line.charAt(position + (word.length())));
            if (isAlphabeticRightSide)
                return true;
        }

        return false;
    }

    private static String toLowerCase(String input) {
        StringBuilder stringBuffer = new StringBuilder(input);
        String result = "";

        for (int i = 0; i < stringBuffer.length(); i++) {
            Character charAt = stringBuffer.charAt(i);
            if (Character.isAlphabetic(charAt)) {
                result = result + Character.toLowerCase(charAt);
            } else {
                result = result + charAt;
            }
        }

        return result;
    }

    public static void main(String args[]) {

        String searchWord = null;
        byte[] input;

        do {
            input = new byte[80];
            System.out.printf("Enter a word to search the following file %s for location info\n", fileName);
            System.out.print("> ");
            try {
                System.in.read(input);
            } catch (IOException e) {
                System.out.print("Error reading given input. Error message = " + e.getMessage());
                System.exit(-1);
            }
            searchWord = (new String(input, 0, input.length)).trim();
            if (searchWord.length() > 0) {
                FindWord.search(searchWord);
            } else {
                System.exit(0);
            }
        } while (searchWord.length() > 0);
    }

}