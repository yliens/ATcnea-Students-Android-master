package uci.atcnea.student.model;

import java.util.ArrayList;

/**
 * Created by koyi on 31/05/16.
 */
public class TextColumn {
    private ArrayList<TextDraw> texts;
    private ArrayList<DividerDraw> dividerDraws;

    public TextColumn() {
        texts = new ArrayList<>();
        dividerDraws = new ArrayList<>();
    }

    public TextColumn(ArrayList<TextDraw> texts, ArrayList<DividerDraw> dividerDraws) {
        this.texts = texts;
        this.dividerDraws = dividerDraws;
    }

    public void addTextDraw(TextDraw textDraw){
        texts.add(textDraw);
    }

    public void addDivider(DividerDraw dividerDraw){
        dividerDraws.add(dividerDraw);
    }

    public ArrayList<TextDraw> getTexts() {
        return texts;
    }

    public void setTexts(ArrayList<TextDraw> texts) {
        this.texts = texts;
    }

    public ArrayList<DividerDraw> getDividerDraws() {
        return dividerDraws;
    }

    public void setDividerDraws(ArrayList<DividerDraw> dividerDraws) {
        this.dividerDraws = dividerDraws;
    }
}
