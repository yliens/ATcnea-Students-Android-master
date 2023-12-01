package uci.atcnea.student.interfaces;

/**
 * Created by guillermo on 7/02/17.
 */

public interface InteractiveQuestionBase {
    void ClearFragment();
    void sendInteractiveQuestionCmd(String json);
}
