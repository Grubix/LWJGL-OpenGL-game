package textMeshCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import renderEngine.Main;

public class MetaData {

	private static final int PAD_TOP = 0;
	private static final int PAD_LEFT = 1;
	private static final int PAD_BOTTOM = 2;
	private static final int PAD_RIGHT = 3;

	private static final int DESIRED_PADDING = 3;
	private static final int SPACE_ASCII = 32;
	public static final float LINE_HEIGHT = 0.03f;

	private static final String SPLITTER = " ";
	private static final String NUMBER_SEPARATOR = ",";

	private float aspectRatio;

	private float verticalPerPixelSize;
	private float horizontalPerPixelSize;
	private float spaceWidth;
	private int[] padding;
	private int paddingWidth;
	private int paddingHeight;

	private Map<Integer, Character> metaData = new HashMap<Integer, Character>();
	private Map<String, String> lineValues = new HashMap<String, String>();
	private BufferedReader reader;

	public MetaData(String fontMetaFile) {
		this.aspectRatio = (float)Main.getWindowWidth() / (float)Main.getWindowHeight();
		openMetaFile(new File(fontMetaFile));
		
		processNextLine(); 	//1st line
		this.padding = getIntegerValuesOfVariable("padding");
		this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
		this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
	
		processNextLine(); 	//2nd line
		int lineHeightPixels = getIntegerValueOfVariable("lineHeight") - paddingHeight;
		verticalPerPixelSize = LINE_HEIGHT / (float) lineHeightPixels;
		horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
		int imageWidth = getIntegerValueOfVariable("scaleW");
		int imageHeight = getIntegerValueOfVariable("scaleH");
		
		processNextLine(); 	//3rd line
		processNextLine(); 	//4th line
		
		loadCharacterData(imageWidth, imageHeight);
		closeFile();
	}

	private void openMetaFile(File fontMetaFile) {
		try {
			reader = new BufferedReader(new FileReader(fontMetaFile));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Couldn't read font meta file!");
		}
	}
	private boolean processNextLine() {
		lineValues.clear();
		String line = null;
		
		try {
			line = reader.readLine();
		} catch (IOException e) {
			Main.printlnLog("Read line error!");
		}
		
		if (line == null) {
			return false;
		}
		
		for (String linePart : line.split(SPLITTER)) {
			String[] linePartValues = linePart.split("=");
			if (linePartValues.length == 2) {
				lineValues.put(linePartValues[0], linePartValues[1]);
			}
		}
		
		return true;
	}
	private int getIntegerValueOfVariable(String variable) {
		return Integer.parseInt(lineValues.get(variable));
	}
	private int[] getIntegerValuesOfVariable(String variable) {
		String[] stringValues = lineValues.get(variable).split(NUMBER_SEPARATOR);
		int[] integerValues = new int[stringValues.length];
		
		for (int i=0 ; i<integerValues.length ; i++) {
			integerValues[i] = Integer.parseInt(stringValues[i]);
		}
		
		return integerValues;
	}
	private String getStringValueOfVariable(String variable) {
		String value = lineValues.get(variable);
		String pattern = "^\".*\"$";
		
		if(variable.matches(pattern)) {
			value.replaceAll("\"", "");
		}
		
		return value;
	}
	private void loadCharacterData(int imageWidth, int imageHeight) {
		while (processNextLine()) {
			Character c = loadCharacter(imageWidth, imageHeight);
			if (c != null) {
				metaData.put(c.getID(), c);
			}
		}
	}
	private Character loadCharacter(int imageWidth, int imageHeight) {
		int id = getIntegerValueOfVariable("id");
		if (id == SPACE_ASCII) {
			this.spaceWidth = (getIntegerValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
			return null;
		}
		double xTex = ((double) getIntegerValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageWidth;
		double yTex = ((double) getIntegerValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageHeight;
		int width = getIntegerValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
		int height = getIntegerValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING));
		double quadWidth = width * horizontalPerPixelSize;
		double quadHeight = height * verticalPerPixelSize;
		double xTexSize = (double) width / imageWidth;
		double yTexSize = (double) height / imageHeight;
		double xOff = (getIntegerValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
		double yOff = (getIntegerValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING)) * verticalPerPixelSize;
		double xAdvance = (getIntegerValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
		return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
	}
	private void closeFile() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Character getCharacter(int ascii) {
		return metaData.get(ascii);
	}
	public float getSpaceWidth() {
		return spaceWidth;
	}

}
