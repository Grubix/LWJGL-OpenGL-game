package textMeshCreator;

import java.util.ArrayList;
import java.util.List;

public class Word {
	
	private List<Character> characters = new ArrayList<Character>();
	private float width = 0;
	private float fontSize;
	
	protected Word(float fontSize){
		this.fontSize = fontSize;
	}
	
	protected void addCharacter(Character character){
		characters.add(character);
		width += character.getxAdvance() * fontSize;
	}
	protected List<Character> getCharacters(){
		return this.characters;
	}
	protected float getWordWidth(){
		return this.width;
	}

}
