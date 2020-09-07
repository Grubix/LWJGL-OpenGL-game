package textMeshCreator;

import java.util.ArrayList;
import java.util.List;

import guis.GuiText;
import models.Model;
import renderEngine.Loader;
import text.Text;

public class TextFont {

	private static final int SPACE_ASCII = 32;
	
	private Loader loader;
	private MetaData metaData;
	private int textureAtlasID;

	public TextFont(Loader loader, MetaData metaData, int textureAtlasID) {
		this.loader = loader;
		this.metaData = metaData;
		this.textureAtlasID = textureAtlasID;
	}

	//TODO zmienic to na statica albo wepchnac to jakos do loadera ?
	
	public Model createTextModel(GuiText guiText) {
		List<Line> lines = createTextLines(guiText.getTextString(), guiText.getFontSize(), guiText.getMaxLineWidth());
		guiText.setNumberOfLines(lines.size());
		return createModel(guiText.getFontSize(), false ,lines);
	}
	public Model createTextModel(Text text) {
		List<Line> lines = createTextLines(text.getTextString(), text.getFontSize(), text.getMaxLineWidth());
		text.setNumberOfLines(lines.size());
		return createModel(text.getFontSize(), true, lines);
	}
	
	private List<Line> createTextLines(String textString, float fontSize, float maxLineWidth) {
		char[] chars = textString.toCharArray();
		List<Line> lines = new ArrayList<Line>();
		Line currentLine = new Line(metaData.getSpaceWidth(), fontSize, maxLineWidth);
		Word currentWord = new Word(fontSize);
		boolean wordAddedToLine;
		
		for(char c : chars) {
			int asciiCode = (int) c;
			if (asciiCode == SPACE_ASCII) {
				wordAddedToLine = currentLine.attemptToAddWord(currentWord);
				if (!wordAddedToLine) {
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), fontSize, maxLineWidth);
					currentLine.attemptToAddWord(currentWord);
				}
				currentWord = new Word(fontSize);
				continue;
			}
			currentWord.addCharacter(metaData.getCharacter(asciiCode));
		}
		
		wordAddedToLine = currentLine.attemptToAddWord(currentWord);
		if (!wordAddedToLine) {
			lines.add(currentLine);
			currentLine = new Line(metaData.getSpaceWidth(), fontSize, maxLineWidth);
			currentLine.attemptToAddWord(currentWord);
		}
		lines.add(currentLine);
		
		return lines;
	}
	private Model createModel(float fontSize, boolean centered, List<Line> lines) {
		double curserX = 0f;
		double curserY = 0f;
		List<Float> vertices = new ArrayList<Float>();
		List<Float> textureCoords = new ArrayList<Float>();
		for (Line line : lines) {
			if(centered) {
				curserX = (line.getMaxWidth() - line.getLineWidth()) / 2;	
			}
			for (Word word : line.getWords()) {
				for (Character character : word.getCharacters()) {
					addVerticesForCharacter(curserX, curserY, character, fontSize, vertices);
					addTexCoords(textureCoords, character.getxTextureCoord(), character.getyTextureCoord(),
							character.getXMaxTextureCoord(), character.getYMaxTextureCoord());
					curserX += character.getxAdvance() * fontSize;
				}
				curserX += metaData.getSpaceWidth() * fontSize;
			}
			curserX = 0;
			curserY += MetaData.LINE_HEIGHT * fontSize;
		}		
		return loader.loadModel(listToArray(vertices), 2, listToArray(textureCoords));
	}
	private static void addVerticesForCharacter(double curserX, double curserY, Character character, double fontSize,
			List<Float> vertices) {
		double x = curserX + (character.getxOffset() * fontSize);
		double y = curserY + (character.getyOffset() * fontSize);
		double maxX = x + (character.getSizeX() * fontSize);
		double maxY = y + (character.getSizeY() * fontSize);
		double properX = (2 * x) - 1;
		double properY = (-2 * y) + 1;
		double properMaxX = (2 * maxX) - 1;
		double properMaxY = (-2 * maxY) + 1;
		addVertices(vertices, properX, properY, properMaxX, properMaxY);
	}
	private static void addVertices(List<Float> vertices, double x, double y, double maxX, double maxY) {
		vertices.add((float) x);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) y);
	}
	private static void addTexCoords(List<Float> texCoords, double x, double y, double maxX, double maxY) {
		texCoords.add((float) x);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) y);
	}
	private static float[] listToArray(List<Float> listOfFloats) {
		float[] array = new float[listOfFloats.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = listOfFloats.get(i);
		}
		return array;
	}
	
	public MetaData getMetaData() {
		return this.metaData;
	}
	public int getTextureAtlas() {
		return textureAtlasID;
	}
	
}
