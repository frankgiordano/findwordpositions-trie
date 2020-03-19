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
 * This program implements word dictionary with positions stored. It implements something similar to the
 * first half of the following link:
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
            for (Entry<Integer, List<Integer>> entry : dictionary.getTerminalNode(searchWord)
                    .getWordLinesPositionsInfo().entrySet()) {
                System.out.println("The following word " + searchWord + " was found at line number " + entry.getKey()
                        + " at position(s) " + entry.getValue().toString());
            }
        }
    }

    // Depth First Search
    // output all words and their lines and positions locations within the given text file
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

public class Find {

    private static Trie dictionary;
    private static String fileName = "t3.txt";

    private static void processFileWordSearch(String searchWord) {

        try {
            int posFound = 0;
            int lineCount = 0;
            String line = "";
            dictionary = new Trie();

            // create a reader which reads our file.
            BufferedReader bReader = new BufferedReader(new FileReader(fileName));

            // while we loop through the file, read each line until there is nothing left to read.
            // this assumes we have carriage return ending at each text line.
            while ((line = bReader.readLine()) != null) {

                lineCount++;
                line = toLowerCase(line);
                String[] words = line.split(" ");

                for (String word : words) {
                    // if the string is empty or not a word skip it
                    if (word.isEmpty() || !isWord(word)) {
                        continue;
                    }

                    posFound = line.indexOf(word);
                    if (dictionary.contains(word)) {
                        Node node = dictionary.getTerminalNode(word);
                        if (node.getWordLinesPositionsInfo().get(lineCount) != null) {
                            node.getWordLinesPositionsInfo().get(lineCount).add(posFound);
                        } else {
                            List<Integer> positions = new ArrayList<Integer>();
                            positions.add(posFound);
                            node.getWordLinesPositionsInfo().put(lineCount, positions);
                        }
                    } else {
                        Node node = dictionary.getItem(word);
                        List<Integer> positions = new ArrayList<Integer>();
                        positions.add(posFound);
                        node.getWordLinesPositionsInfo().put(lineCount, positions);
                    }
                }

            }
            // close the reader.
            bReader.close();
            dictionary.printALL(searchWord, dictionary);
            // dictionary.printALL(); // print all the words and its locations within the text file.
        } catch (IOException e) {
            // we encountered an error with the file, print it to the user.
            System.out.println("Error: " + e.toString());
        }
    }

    private static boolean isWord(String word) {
        int limit = word.length();
        int letter, index;
        boolean value = false;

        word = word.toLowerCase();
        for (int i = 0; i < limit; i++) {
            letter = word.charAt(i);
            index = letter - 'a';
            if (index >= 0 && index <= 26) {
                value = true;
            } else {
                value = false;
                break;
            }
        }

        return value;
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
                processFileWordSearch(searchWord);
            }
        } while (searchWord.length() > 0);

    }

}