package objLoader;

public class ModelData {
 
    private float[] vertices;
    private float[] textureCoords;
    private float[] normals;
    private float[] tangents;
    private int[] indices;
    private float furthestPoint;
 
    public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices,
            float furthestPoint) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.furthestPoint = furthestPoint;
    }
    public ModelData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, 
    		int[] indices, float furthestPoint) {
        this(vertices, textureCoords, normals, indices, furthestPoint);
        this.tangents = tangents;
    }
 
    public float[] getVertices() {
        return vertices;
    }
    public float[] getTextureCoords() {
        return textureCoords;
    }
    public float[] getNormals() {
        return normals;
    }
    public float[] getTangents() {
    	if(tangents == null) {
    		System.out.print("Error: null pointer");
    	}
    	return tangents;
    }
    public int[] getIndices() {
        return indices;
    }
    public float getFurthestPoint() {
        return furthestPoint;
    }
 
}
