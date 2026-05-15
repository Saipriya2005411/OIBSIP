public class Question {
    private String   questionText;
    private String[] options;      // 4 options
    private int      correctIndex; // 0-based

    public Question(String questionText, String[] options, int correctIndex) {
        this.questionText = questionText;
        this.options      = options;
        this.correctIndex = correctIndex;
    }

    public String   getQuestionText() { return questionText; }
    public String[] getOptions()      { return options; }
    public boolean  isCorrect(int selected) { return selected == correctIndex; }
    public int      getCorrectIndex() { return correctIndex; }
}
