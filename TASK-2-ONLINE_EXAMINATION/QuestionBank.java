import java.util.*;

public class QuestionBank {

    public static Map<String, List<Question>> getAllSubjects() {
        Map<String, List<Question>> bank = new LinkedHashMap<>();
        bank.put("Java Programming", javaQuestions());
        bank.put("Data Structures",  dsQuestions());
        bank.put("General Knowledge", gkQuestions());
        return bank;
    }

    private static List<Question> javaQuestions() {
        return Arrays.asList(
            new Question("Which keyword is used to inherit a class in Java?",
                new String[]{"implements", "extends", "inherits", "super"}, 1),
            new Question("Which of these is NOT a Java primitive type?",
                new String[]{"int", "float", "String", "boolean"}, 2),
            new Question("What is the size of an int in Java?",
                new String[]{"2 bytes", "4 bytes", "8 bytes", "Depends on OS"}, 1),
            new Question("Which method is the entry point of a Java program?",
                new String[]{"start()", "init()", "main()", "run()"}, 2),
            new Question("What does JVM stand for?",
                new String[]{"Java Variable Machine", "Java Virtual Machine", "Java Verified Module", "Java Value Manager"}, 1),
            new Question("Which interface must be implemented for multithreading?",
                new String[]{"Serializable", "Runnable", "Cloneable", "Comparable"}, 1),
            new Question("What is the default value of a boolean in Java?",
                new String[]{"true", "false", "null", "0"}, 1),
            new Question("Which collection maintains insertion order and allows duplicates?",
                new String[]{"HashSet", "TreeSet", "ArrayList", "HashMap"}, 2),
            new Question("What does 'final' keyword prevent on a method?",
                new String[]{"Overloading", "Overriding", "Calling", "Inheritance"}, 1),
            new Question("Which exception is thrown when array index is out of bounds?",
                new String[]{"NullPointerException", "ClassCastException", "ArrayIndexOutOfBoundsException", "StackOverflowError"}, 2)
        );
    }

    private static List<Question> dsQuestions() {
        return Arrays.asList(
            new Question("Which data structure uses LIFO order?",
                new String[]{"Queue", "Stack", "LinkedList", "Tree"}, 1),
            new Question("Time complexity of binary search on sorted array?",
                new String[]{"O(n)", "O(n²)", "O(log n)", "O(1)"}, 2),
            new Question("Which traversal visits root first?",
                new String[]{"Inorder", "Postorder", "Preorder", "Level order"}, 2),
            new Question("What is the best-case time complexity of quicksort?",
                new String[]{"O(n)", "O(n log n)", "O(n²)", "O(log n)"}, 1),
            new Question("A complete binary tree with n leaves has how many internal nodes?",
                new String[]{"n", "n-1", "n+1", "2n"}, 1),
            new Question("Which data structure is used for BFS traversal?",
                new String[]{"Stack", "Queue", "Priority Queue", "Deque"}, 1),
            new Question("Hash table average case search complexity?",
                new String[]{"O(n)", "O(log n)", "O(1)", "O(n log n)"}, 2),
            new Question("A graph with no cycles is called a:",
                new String[]{"Complete graph", "Tree", "Bipartite graph", "Directed graph"}, 1),
            new Question("Which sorting algorithm is stable?",
                new String[]{"Quicksort", "Heapsort", "Merge sort", "Selection sort"}, 2),
            new Question("Minimum spanning tree of a graph with V vertices has how many edges?",
                new String[]{"V", "V-1", "V+1", "2V"}, 1)
        );
    }

    private static List<Question> gkQuestions() {
        return Arrays.asList(
            new Question("What is the capital of India?",
                new String[]{"Mumbai", "Kolkata", "New Delhi", "Chennai"}, 2),
            new Question("Who wrote 'Discovery of India'?",
                new String[]{"Mahatma Gandhi", "Jawaharlal Nehru", "B.R. Ambedkar", "Subhas Chandra Bose"}, 1),
            new Question("Which planet is closest to the Sun?",
                new String[]{"Venus", "Earth", "Mercury", "Mars"}, 2),
            new Question("What is the chemical symbol for Gold?",
                new String[]{"Go", "Gd", "Au", "Ag"}, 2),
            new Question("The 2024 Olympics were held in which city?",
                new String[]{"Tokyo", "Paris", "London", "Los Angeles"}, 1),
            new Question("Who invented the telephone?",
                new String[]{"Thomas Edison", "Nikola Tesla", "Alexander Graham Bell", "James Watt"}, 2),
            new Question("What is the largest ocean on Earth?",
                new String[]{"Atlantic", "Indian", "Arctic", "Pacific"}, 3),
            new Question("Which country has the largest population in 2024?",
                new String[]{"China", "India", "USA", "Indonesia"}, 1),
            new Question("DNA stands for?",
                new String[]{"Deoxyribonucleic Acid", "Diribonucleic Acid", "Dynamic Nucleic Acid", "None"}, 0),
            new Question("How many bones are in the adult human body?",
                new String[]{"196", "206", "216", "226"}, 1)
        );
    }
}
