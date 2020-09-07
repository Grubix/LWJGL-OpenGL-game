package textMeshCreator;

import java.util.ArrayList;
import java.util.List;

public class Line {

	private float maxWidth;
	private float spaceWidth;

	private List<Word> words = new ArrayList<Word>();
	private float curretLineWidth = 0;
	
	protected Line(float spaceWidth, float fontSize, float maxWidth) {
		this.spaceWidth = spaceWidth * fontSize;
		this.maxWidth = maxWidth;
	}

	protected boolean attemptToAddWord(Word word) {
		float wordWidth = word.getWordWidth();
		wordWidth += !words.isEmpty() ? spaceWidth : 0;
		if (curretLineWidth + wordWidth <= maxWidth) {
			curretLineWidth += wordWidth;
			words.add(word);
			return true;
		} else {
			return false;
		}
	}
	protected float getMaxWidth() {
		return maxWidth;
	}
	protected double getLineWidth() {
		return curretLineWidth;
	}
	protected List<Word> getWords() {
		return words;
	}

}
