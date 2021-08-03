package replication.election;

import util.Configuration;

/**
 * the format of AnswerMessage is answer[separator]senderID
 */
public class Answer {

    private String procID;

    public Answer(String procID) {
        this.procID = procID;
    }

    public String toString() {
        return "answer" + Configuration.getSeparator() + procID;
    }
}
