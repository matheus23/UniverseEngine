package org.universeengine.opengl.model.modelloader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.universeengine.exceptions.UniGLVersionException;
import org.universeengine.opengl.model.UniMesh;
import org.universeengine.opengl.model.UniModel;
import org.universeengine.opengl.model.renderer.UniInterleavedVBORenderer;
import org.universeengine.opengl.vertex.UniColor3f;
import org.universeengine.opengl.vertex.UniNormal3f;
import org.universeengine.opengl.vertex.UniTexCoord2f;
import org.universeengine.opengl.vertex.UniVertex3f;
import org.universeengine.util.UniPrint;

/**
 * Short: to load UEM-Models, use UEM.load(), to load other formats use the static classes inside.
 * 
 * Long:
 * This is the ModelLoader for loading .uem ModelFiles (UniverseEngine Model)
 * The UEM File Structure can be printed with UEM.print(String filename).
 * If you have not got any UEM Files to print, just call
 * UEM.writeUniCube(String filename). It will create you a test File,
 * which has exactly the same Vertices as the UniCube class.
 * 
 * The final UEM File could look as following:
 * START
 *  MESH
 *  [NUMBER OF MESHES]
 *   ELEMENTS
 *   [NUMBER OF ELEMENTS]
 *    VERTICES
 *     [FLOAT]
 *     [FLOAT]
 *     [FLOAT]
 *    NORMALS
 *     [FLOAT]
 *     [FLOAT]
 *     [FLOAT]
 *    COLORS
 *     [FLOAT]
 *     [FLOAT]
 *     [FLOAT]
 *    TEXCOORDS
 *     [FLOAT]
 *     [FLOAT]
 *     [FLOAT]
 *    END_ELEMENT
 *   [MORE ELEMENTS ...]
 *  INDICES
 *  [NUMBER OF INDICES]
 *   [INT]
 *   [INT]
 *   [INT]
 *   [...]
 *  [MORE MESHES ...]
 * END
 * 
 * REMEMBER: This File structure is BINARY and is not an ASCII File.
 * It cannot be seen in a Text-Editor, however, you can call UEM.print(String file)
 * to print everything, so it looks like above.
 * In the real UEM File, the START/END/MESH/ELEMENTS Flag will be bytes,
 * with the values of the final Fields in this class.
 * 
 * To be added:
 *  - Translations of single Meshes.
 *  - Rotations of single Meshes.
 *  - Skeletal Animation, with skinning.
 *    (Shaders must be written for that first...)
 *  - Many other Optional features.
 * 
 * Also, I might take my time to write a Blender Export script,
 * so that developing games with this Engine will be a lot easier.
 * Right now as an alternative, you can use Blender's standard 
 * OBJ Exporter, and import these models with this OBJ Loader.
 * 
 * @author matheus23
 */
public final class UniModelLoader {
	
	public static class UEM {
		
		public static final byte START = 101;
		public static final byte END = 102;
		public static final byte MESH = 103;
		public static final byte TRANSLATIONS = 104;
		public static final byte ROTATIONS = 105;
		public static final byte ELEMENTS = 106;
		
		public static final byte VERTICES = -101;
		public static final byte NORMALS = -102;
		public static final byte COLORS = -103;
		public static final byte TEXCOORDS = -104;
		public static final byte END_ELEMENT = -105;
		public static final byte INDICES = -106;

		/**
		 * Loads a single .uem Model, and then returns
		 * the UniModel instance.
		 * 
		 * @param filename the filepath to the .uem Model file.
		 * @return the UniModel instance
		 * @throws IOException thrown by fileIO
		 * @throws UniModelLoaderException thrown, if loading errors occur
		 */
		public static UniModel load(String filename) throws IOException, UniModelLoaderException {
			if (!filename.endsWith(".uem")) {
				throw new IOException(String.format("UEM.load() cannot load from files, which aren't .uem: %s", filename));
			} else {
				File file = new File(filename);
				if (!file.exists()) {
					throw new IOException(String.format("The given File %s doesn't exist! Maybe its located somwhere else?", filename));
				}
				return load(file);
			}
		}
		
		public static UniModel load(File file) throws IOException, UniModelLoaderException {
			FileInputStream fis = new FileInputStream(file);
			DataInputStream dis = new DataInputStream(fis);
			return load(dis);
		}
		
		public static UniModel load(DataInputStream dis) throws IOException, UniModelLoaderException {
			byte b;
			if ((b = dis.readByte()) == START) {
				if ((b = dis.readByte()) == MESH) {
					UniMesh[] meshes;
					meshes = loadMeshes(dis);
					return new UniModel(meshes);
				} else {
					throw new UniModelLoaderException(String.format("Unknown Flag after \"START\"-Flag: %d", b));
				}
			} else {
				throw new UniModelLoaderException(String.format("\"START\"-Flag is missing! Instead i got: %d", b));
			}
		}
		
		private static UniMesh[] loadMeshes(DataInputStream dis) throws IOException, UniModelLoaderException {
			byte b;
			
			UniMesh[] meshes;
			UniVertex3f[] v;
			UniNormal3f[] n;
			UniColor3f[] c;
			UniTexCoord2f[] t;
			int[] ind;
			
			int number = dis.readInt();
			meshes = new UniMesh[number];
			for (int m = 0; m < number; m++) {
				if ((b = dis.readByte()) == ELEMENTS) {
					int elements = dis.readInt();
					v = new UniVertex3f[elements];
					n = new UniNormal3f[elements];
					c = new UniColor3f[elements];
					t = new UniTexCoord2f[elements];
					for (int e = 0; e < elements;) {
						byte flag = dis.readByte();
						switch(flag) {
						case VERTICES:
							float f1 = dis.readFloat();
							float f2 = dis.readFloat();
							float f3 = dis.readFloat();
							v[e] = new UniVertex3f(f1, f2, f3);
							break;
						case NORMALS:
							float n1 = dis.readFloat();
							float n2 = dis.readFloat();
							float n3 = dis.readFloat();
							n[e] = new UniNormal3f(n1, n2, n3);
							break;
						case COLORS:
							float c1 = dis.readFloat();
							float c2 = dis.readFloat();
							float c3 = dis.readFloat();
							c[e] = new UniColor3f(c1, c2, c3);
							break;
						case TEXCOORDS:
							float t1 = dis.readFloat();
							float t2 = dis.readFloat();
							t[e] = new UniTexCoord2f(t1, t2);
							break;
						case END_ELEMENT:
							e++;
							break;
						default:
							throw new UniModelLoaderException(String.format("Unknown data in ELEMENT: %d", flag));
						}
					}
					if ((b = dis.readByte()) == INDICES) {
						int indices = dis.readInt();
						ind = new int[indices];
						int num = 0;
						for (int i = 0; i < indices; i++) {
							num = dis.readInt();
							System.out.println("   I " + i + " = " + num);
							ind[i] = num;
						}
					} else {
						throw new UniModelLoaderException(String.format("Unknown Flag after \"ELEMENTS\"-Flag: %d", b));
					}
					if (v[0] == null) {
						v = null;
					} if (n[0] == null) {
						n = null;
					} if (c[0] == null) {
						c = null;
					} if (t[0] == null) {
						t = null;
					} if (ind.length == 0) {
						ind = null;
					}
					try {
						UniInterleavedVBORenderer rend = new UniInterleavedVBORenderer(v, n, c, t, ind);
						rend.create();
						meshes[m] = new UniMesh(rend);
					} catch (UniGLVersionException e) {
						UniPrint.printerrf("After Loading Mesh, i could not create it from data.");
						e.printStackTrace();
					}
				} else {
					throw new UniModelLoaderException(String.format("Unknown Flag after \"MESH\"-Flag: %d", b));
				}
			}
			return meshes;
		}
		
		/**
		 * Writes Model data from
		 *  - vertices
		 *  - colors
		 *  - normals
		 *  - textureCoords
		 *  - indices
		 * into a .uem file.
		 * 
		 * colors, normals or textureCoords may be null,
		 * but vertices and indices may not.
		 * 
		 * The data to be given, are the 3-Dimensional
		 * arrays, while the first dimension specifies
		 * the Meshes, the second dimension, the number
		 * of data Element, and the last dimension, the
		 * lastly given data (in floats).
		 * 
		 * For indices-data array, the first dimension
		 * are the Meshes and the second dimension the
		 * data itself.
		 * 
		 * @param filepath the filepath to be written to.
		 * @param vertices the vertices-data-array
		 * @param colors the color-data-array
		 * @param normals the normal-data-array
		 * @param textureCoords the textureCoords-data-array
		 * @param indices the indices-data-array
		 * @throws IOException thrown by fileIO.
		 * @throws UniModelLoaderException thrown, when loading errors occur. 
		 */
		public static void writeModel(String filepath, float[][][] vertices, float[][][] colors, float[][][] normals, float[][][] textureCoords, int[][] indices) throws IOException, UniModelLoaderException {
			if (!filepath.contains(".uem")) {
				throw new UniModelLoaderException("Can only save into files, with .uem suffix");
			}
			File file = new File(filepath);
			if (file.exists()) {
				throw new UniModelLoaderException(String.format("File \"%s\" is already existing", filepath));
			}
			writeModel(file, vertices, colors, normals, textureCoords, indices);
		}
		
		public static void writeModel(File file, float[][][] vert, float[][][] col, float[][][] norm, float[][][] texCoord, int[][] ind) throws IOException, UniModelLoaderException {
			FileOutputStream fos = new FileOutputStream(file);
			DataOutputStream dos = new DataOutputStream(fos);
			
			if (vert == null || vert.length == 0) {
				throw new UniModelLoaderException("Cannot save 0 Meshes. Makes no sense. (vert.length == 0 || vert == null)");
			}  if (ind == null || ind.length == 0) {
				throw new UniModelLoaderException("Cannot save 0 Meshes. Makes no sense. (ind.length == 0 || ind == null)");
			}
			
			if (col != null && col.length != vert.length) {
				throw new UniModelLoaderException("Number of Meshes in vertex-array and color-array is not equal");
			} if (norm != null && norm.length != vert.length) {
				throw new UniModelLoaderException("Number of Meshes in vertex-array and normal-array is not equal");
			} if (texCoord != null && texCoord.length != vert.length) {
				throw new UniModelLoaderException("Number of Meshes in vertex-array and texture-coord-array is not equal");
			} if (ind.length != vert.length) {
				throw new UniModelLoaderException("Number of Meshes in vertex-array and indice-array is not equal");
			}
			
			dos.writeByte(START);
			{
				dos.writeByte(MESH);
				dos.writeInt(vert.length);
				
				for (int i = 0; i < vert.length; i++) {
					writeMesh(dos, vert[i],
							col != null ? col[i] : null,
							norm != null ? norm[i] : null,
							texCoord != null ? texCoord[i] : null,
							ind[i]);
				}
			}
			dos.writeByte(END);
		}
		
		public static void writeMesh(DataOutputStream dos, float[][] vert, float[][] col, float[][] norm, float[][] texCoord, int[] ind) throws IOException, UniModelLoaderException {
			if (vert == null || vert.length == 0) {
				throw new UniModelLoaderException("ModelLoader hasn't got any vertices");
			} if (ind == null || ind.length == 0) {
				throw new UniModelLoaderException("ModelLoader hasn't got any indices");
			}
			
			boolean useCol = col != null && col.length > 0;
			boolean useNorm = norm != null && norm.length > 0;
			boolean useTex = texCoord != null && texCoord.length > 0;
			
			if (useCol && vert.length != col.length) {
				throw new UniModelLoaderException("Cannot write a uem model with vertices number != color number");
			} if (useNorm && vert.length != norm.length) {
				throw new UniModelLoaderException("Cannot write a uem model with vertices number != normal number");
			} if (useTex && vert.length != texCoord.length) {
				throw new UniModelLoaderException("Cannot write a uem model with vertices number != texture coord number");
			}
			
			dos.writeByte(ELEMENTS);
			dos.writeInt(ind.length);
			{
				try {
					for (int i = 0; i < vert.length; i++) {
						{
							dos.writeByte(VERTICES);
							dos.writeFloat(vert[i][0]);
							dos.writeFloat(vert[i][1]);
							dos.writeFloat(vert[i][2]);
						}
						if (useNorm) {
							dos.writeByte(NORMALS);
							dos.writeFloat(norm[i][0]);
							dos.writeFloat(norm[i][1]);
							dos.writeFloat(norm[i][2]);
						}
						if (useCol) {
							dos.writeByte(COLORS);
							dos.writeFloat(col[i][0]);
							dos.writeFloat(col[i][1]);
							dos.writeFloat(col[i][2]);
						}
						if (useTex) {
							dos.writeByte(TEXCOORDS);
							dos.writeFloat(texCoord[i][0]);
							dos.writeFloat(texCoord[i][1]);
						}
						dos.writeByte(END_ELEMENT);
					}
				} catch(ArrayIndexOutOfBoundsException aioobe) {
					UniPrint.printerrf("ModelLoader got an Array Index out of bounds exception, maybe one vertex-element is not filled?");
					aioobe.printStackTrace();
				}
			}
			
			dos.writeByte(INDICES);
			dos.writeInt(ind.length);
			
			for (int i = 0; i < ind.length; i++) {
				dos.writeInt(ind[i]);
			}
		}
		
		public static void print(String filename) throws IOException, UniModelLoaderException {
			if (!filename.contains(".uem")) {
				throw new IOException("loadUNI() cannot load from files, which aren't .uem!");
			} else {
				print(new File(filename));
			}
		}
		
		public static void print(File file) throws IOException, UniModelLoaderException {
			FileInputStream fis = new FileInputStream(file);
			DataInputStream dis = new DataInputStream(fis);
			print(dis);
		}
		
		public static void print(DataInputStream dis) throws IOException, UniModelLoaderException {
			byte b;
			if ((b = dis.readByte()) == START) {
				System.out.println("START");
				if ((b = dis.readByte()) == MESH) {
					System.out.println(" MESH");
					printMeshes(dis);
				} else {
					throw new UniModelLoaderException(String.format("Unknown Flag after \"START\"-Flag: %d", b));
				}
			} else {
				throw new UniModelLoaderException(String.format("\"START\"-Flag is missing! Instead i got: %d", b));
			}
		}
		
		private static void printMeshes(DataInputStream dis) throws IOException, UniModelLoaderException {
			byte b;
			int number = dis.readInt();
			System.out.println(" NUMBER: " + number);
			for (int m = 0; m < number; m++) {
				if ((b = dis.readByte()) == ELEMENTS) {
					System.out.println("  ELEMENTS");
					int elements = dis.readInt();
					System.out.println("  NUMBER: " + elements);
					for (int e = 0; e < elements;) {
						byte flag = dis.readByte();
						switch(flag) {
						case VERTICES:
							System.out.println("   VERTICES[" + e + "]");
							System.out.println("    F " + dis.readFloat());
							System.out.println("    F " + dis.readFloat());
							System.out.println("    F " + dis.readFloat());
							break;
						case NORMALS:
							System.out.println("   NORMALS[" + e + "]");
							System.out.println("    F " + dis.readFloat());
							System.out.println("    F " + dis.readFloat());
							System.out.println("    F " + dis.readFloat());
							break;
						case COLORS:
							System.out.println("   COLORS[" + e + "]");
							System.out.println("    F " + dis.readFloat());
							System.out.println("    F " + dis.readFloat());
							System.out.println("    F " + dis.readFloat());
							break;
						case TEXCOORDS:
							System.out.println("   TEXCOORDS[" + e + "]");
							System.out.println("    F " + dis.readFloat());
							System.out.println("    F " + dis.readFloat());
							System.out.println("    F " + dis.readFloat());
							break;
						case END_ELEMENT:
							System.out.println("   END OF ELEMENT[" + e + "]\n");
							e++;
							break;
						default:
							throw new UniModelLoaderException(String.format("Unknown data in ELEMENT: %d", flag));
						}
					}
					if ((b = dis.readByte()) == INDICES) {
						System.out.println("  INDICES");
						int indices = dis.readInt();
						System.out.println("  NUMBER: " + indices);
						for (int i = 0; i < indices; i++) {
							System.out.println("   I " + i + " = " + dis.readInt());
						}
					} else {
						throw new UniModelLoaderException(String.format("Unknown Flag after \"ELEMENTS\"-Flag: %d", b));
					}
				} else {
					throw new UniModelLoaderException(String.format("Unknown Flag after \"MESH\"-Flag: %d", b));
				}
				while(dis.readByte() != END) { 
					System.out.println("End of File could not be found yet.");
				}
				System.out.println("END");
			}
		}
		
		public static void writeUniCube(String filename) throws IOException {
			if (!filename.contains(".uem")) {
				throw new IOException("writeUNI() does not allow any other formats, then .uem!");
			} else {
				File file = new File(filename);
				if (!file.createNewFile()) {
					throw new IOException("Either the given File already exists, or the operation failed.");
				} else {
					writeUniCube(new File(filename));
				}
			}
		}
		
		public static void writeUniCube(File file) throws IOException {
			FileOutputStream fos = new FileOutputStream(file);
			DataOutputStream dos = new DataOutputStream(fos);
			// Raw Cube Data:
			// Vertices:
			float[][] vert = {
					{  1f,  1f,  1f },
					{  1f,  1f, -1f },
					{  1f, -1f,  1f },
					{  1f, -1f, -1f },
					{ -1f,  1f,  1f },
					{ -1f,  1f, -1f },
					{ -1f, -1f,  1f },
					{ -1f, -1f, -1f }
			};
			// Colors:
			float[][] col = {
					{ 0f, 0f, 0f },
					{ 0f, 0f, 1f },
					{ 0f, 1f, 0f },
					{ 0f, 1f, 1f },
					{ 1f, 0f, 0f },
					{ 1f, 0f, 1f },
					{ 1f, 1f, 0f },
					{ 1f, 1f, 1f }
			};
			// Indices:
			int[] i = { 7, 6, 4, 5, 3, 2, 0, 1, 7, 6, 2, 3,
						5, 4, 0, 1, 7, 5, 1, 3, 6, 4, 0, 2
			};
			// START Flag:
			dos.writeByte(START);
			{
				// MESH Flag:
				dos.writeByte(MESH);
				// Number of Meshes:
				dos.writeInt(1);
				{
					// ELEMENTS Flag (1 Elements = Vertex + Normal + Color + TexCoord)
					// Normal, Color and TexCoord are Optional. The VERTICES Flag
					// shows, that a new Element is started.
					dos.writeByte(ELEMENTS);
					// Number of Elements:
					dos.writeInt(8);
					{
						// The Vertices are hardcoded, yes.
						// I could have used for-Loops,
						// but I didn't do it, to show you
						// how that looks like in the File
						// at the end.
						{
							// Vertex 0:
							dos.writeByte(VERTICES);
							dos.writeFloat(vert[0][0]);
							dos.writeFloat(vert[0][1]);
							dos.writeFloat(vert[0][2]);
							// Color 0:
							dos.writeByte(COLORS);
							dos.writeFloat(col[0][0]);
							dos.writeFloat(col[0][1]);
							dos.writeFloat(col[0][2]);
							dos.writeByte(END_ELEMENT);
						} {
							// Vertex 1:
							dos.writeByte(VERTICES);
							dos.writeFloat(vert[1][0]);
							dos.writeFloat(vert[1][1]);
							dos.writeFloat(vert[1][2]);
							// Color 1:
							dos.writeByte(COLORS);
							dos.writeFloat(col[1][0]);
							dos.writeFloat(col[1][1]);
							dos.writeFloat(col[1][2]);
							dos.writeByte(END_ELEMENT);
						} {
							// Vertex 2:
							dos.writeByte(VERTICES);
							dos.writeFloat(vert[2][0]);
							dos.writeFloat(vert[2][1]);
							dos.writeFloat(vert[2][2]);
							// Color 2:
							dos.writeByte(COLORS);
							dos.writeFloat(col[2][0]);
							dos.writeFloat(col[2][1]);
							dos.writeFloat(col[2][2]);
							dos.writeByte(END_ELEMENT);
						} {
							// Vertex 3:
							dos.writeByte(VERTICES);
							dos.writeFloat(vert[3][0]);
							dos.writeFloat(vert[3][1]);
							dos.writeFloat(vert[3][2]);
							// Color 3:
							dos.writeByte(COLORS);
							dos.writeFloat(col[3][0]);
							dos.writeFloat(col[3][1]);
							dos.writeFloat(col[3][2]);
							dos.writeByte(END_ELEMENT);
						} {
							// Vertex 4:
							dos.writeByte(VERTICES);
							dos.writeFloat(vert[4][0]);
							dos.writeFloat(vert[4][1]);
							dos.writeFloat(vert[4][2]);
							// Color 4:
							dos.writeByte(COLORS);
							dos.writeFloat(col[4][0]);
							dos.writeFloat(col[4][1]);
							dos.writeFloat(col[4][2]);
							dos.writeByte(END_ELEMENT);
						} {
							// Vertex 5:
							dos.writeByte(VERTICES);
							dos.writeFloat(vert[5][0]);
							dos.writeFloat(vert[5][1]);
							dos.writeFloat(vert[5][2]);
							// Color 5:
							dos.writeByte(COLORS);
							dos.writeFloat(col[5][0]);
							dos.writeFloat(col[5][1]);
							dos.writeFloat(col[5][2]);
							dos.writeByte(END_ELEMENT);
						} {
							// Vertex 6:
							dos.writeByte(VERTICES);
							dos.writeFloat(vert[6][0]);
							dos.writeFloat(vert[6][1]);
							dos.writeFloat(vert[6][2]);
							// Color 6:
							dos.writeByte(COLORS);
							dos.writeFloat(col[6][0]);
							dos.writeFloat(col[6][1]);
							dos.writeFloat(col[6][2]);
							dos.writeByte(END_ELEMENT);
						} {
							// Vertex 7:
							dos.writeByte(VERTICES);
							dos.writeFloat(vert[7][0]);
							dos.writeFloat(vert[7][1]);
							dos.writeFloat(vert[7][2]);
							// Color 7:
							dos.writeByte(COLORS);
							dos.writeFloat(col[7][0]);
							dos.writeFloat(col[7][1]);
							dos.writeFloat(col[7][2]);
							dos.writeByte(END_ELEMENT);
						}
					}
					// INDICES Flag:
					dos.writeByte(INDICES);
					// Number of Indices:
					dos.writeInt(24);
					{
						// All the Indices: (Hardcoded again)
						dos.writeInt(i[0]);
						dos.writeInt(i[1]);
						dos.writeInt(i[2]);
						dos.writeInt(i[3]);
						dos.writeInt(i[4]);
						dos.writeInt(i[5]);
						dos.writeInt(i[6]);
						dos.writeInt(i[7]);
						dos.writeInt(i[8]);
						dos.writeInt(i[9]);
						dos.writeInt(i[10]);
						dos.writeInt(i[11]);
						dos.writeInt(i[12]);
						dos.writeInt(i[13]);
						dos.writeInt(i[14]);
						dos.writeInt(i[15]);
						dos.writeInt(i[16]);
						dos.writeInt(i[17]);
						dos.writeInt(i[18]);
						dos.writeInt(i[19]);
						dos.writeInt(i[20]);
						dos.writeInt(i[21]);
						dos.writeInt(i[22]);
						dos.writeInt(i[23]);
					}
				}
			}
			// END Flag (extreme important!):
			dos.writeByte(END);
		}
		
	}
	
	/**
	 * This 3ds Loader is currently NOT WORKING!
	 * (Tested).
	 * For experiments I created UniModelLoader.printHEXData(String)
	 */
	public static class L3DS {
		
		public static final int MAIN_CHUNK = 0x4D4D;
		public static final int L3D_EDITOR_CHUNK = 0x3D3D;
		public static final int OBJECT_BLOCK = 0x4000;
		public static final int TRIANGULAR_MESH = 0x4100;
		public static final int VERTICES_LIST = 0x4110;
		public static final int FACES_DESCRIPTION = 0x4120;
		public static final int FACES_MATERIAL = 0x4130;
		public static final int MAPPING_COORDINATES_LIST = 0x4140;
		public static final int SMOOTHING_GROUP_LIST = 0x4150;
		public static final int LOCAL_COORDINATES_SYSTEM = 0x4160;
		public static final int LIGHT = 0x4600;
		public static final int SPOTLIGHT = 0x4610;
		public static final int CAMERA = 0x4700;
		public static final int MATERIAL_BLOCK = 0xAFFF;
		public static final int MATERIAL_NAME = 0xA000;
		public static final int AMBIENT_COLOR = 0xA010;
		public static final int DIFFUSE_COLOR = 0xA020;
		public static final int SPECULAR_COLOR = 0xA030;
		public static final int TEXTURE_MAP_1 = 0xA2000;
		public static final int BUMP_MAP = 0xA230;
		public static final int REFLECTION_MAP = 0xA220;
		public static final int MAPPING_FILENAME = 0xA300;
		public static final int MAPPING_PARAMETERS = 0xA351;
		public static final int KEYFRAMER_CHUNK = 0xB000;
		public static final int MESH_INFORMATION_BLOCK = 0xB002;
		public static final int SPOT_LIGHT_INFORMATION_BLOCK = 0xB007;
		public static final int FRAMES = 0xB008;
		public static final int OBJECT_NAME = 0xB010;
		public static final int OBJECT_PIVOT_POINT = 0xB013;
		public static final int POSITION_TRACK = 0xB020;
		public static final int ROTATION_TRACK = 0xB021;
		public static final int SCALE_TRACK = 0xB022;
		public static final int HIERACHY_POSITION = 0xB030;
		
		public static void print(String filename) throws IOException, UniModelLoaderException {
			if (!filename.endsWith(".3ds")) {
				throw new UniModelLoaderException(String.format
						("The filepath %s, is not from type 3ds!\n", filename));
			}
			File file = new File(filename);
			if (!file.exists()) {
				throw new IOException(String.format("The given File %s does not exist!", filename));
			}
			print(file);
		}
		
		public static void print(File file) throws IOException, UniModelLoaderException {
			FileInputStream fis = new FileInputStream(file);
			DataInputStream dis = new DataInputStream(fis);
			print(dis);
		}
		
		public static void print(DataInputStream dis) throws IOException, UniModelLoaderException {
			int i;
			if ((i = dis.readUnsignedShort()) == MAIN_CHUNK) {
				System.out.println("MAIN_CHUNK");
				if ((i = dis.readUnsignedShort()) == L3D_EDITOR_CHUNK) {
					System.out.println(" 3D_EDITOR_CHUNK");
					if ((i = dis.readUnsignedShort()) == OBJECT_BLOCK) {
						System.out.println("  OBJECT_BLOCK");
						char c = 0;
						StringBuffer objectName = new StringBuffer(); 
						do {
							c = dis.readChar();
							objectName.append(c);
						} while (c != '\0');
						System.out.println(objectName.toString());
						if ((i = dis.readUnsignedShort()) == TRIANGULAR_MESH) {
							System.out.println("   TRIANGULAR_MESH");
							if ((i = dis.readUnsignedShort()) == VERTICES_LIST) {
								System.out.println("    VERTICES_LIST");
								int vertices = dis.readUnsignedShort();
								System.out.println("    NUMBER: " + vertices);
								for (int v = 0; v < vertices; v++) {
									System.out.println("     VERTEX[" + v + "]");
									System.out.println("      F " + dis.readFloat());
									System.out.println("      F " + dis.readFloat());
									System.out.println("      F " + dis.readFloat());
									System.out.println();
								}
							} else {
								throw new UniModelLoaderException(String.format("Unknown Flag after TRIANGULAR_MESH: %x", i));
							}
							if ((i = dis.readUnsignedShort()) == FACES_DESCRIPTION) {
								System.out.println("    FACES_DESCRIPTION");
								int descriptions = dis.readUnsignedShort();
								System.out.println("    NUMBER: " + descriptions);
								for (int d = 0; d < descriptions; d++) {
									System.out.println("     FACE DESCRIPTION[" + d + "]");
									System.out.println("      S " + dis.readUnsignedShort());
									System.out.println("      S " + dis.readUnsignedShort());
									System.out.println("      S " + dis.readUnsignedShort());
									System.out.println("      FLAG " + dis.readUnsignedShort());
									System.out.println();
								}
							}
							// Jump to MAPPING_COORDINATES_LIST
							seekShort(dis, MAPPING_COORDINATES_LIST);
							System.out.println("    MAPPING_COORDINATES_LIST");
							int coords = dis.readUnsignedShort();
							System.out.println("    NUMBER: " + coords);
							for (int t = 0; t < coords; t++) {
								System.out.println("     TEX_COOD[" + t + "]");
								System.out.println("      F " + dis.readFloat());
								System.out.println("      F " + dis.readFloat());
								System.out.println();
							}
						} else {
							throw new UniModelLoaderException(String.format("Unkown Flag after OBJEC_BLOCK: %x", i));
						}
					} else {
						throw new UniModelLoaderException(String.format("Unknown Flag after 3D_EDITOR_CHUNK: %x", i));
					}
				} else {
					throw new UniModelLoaderException(String.format("Unknown Flag after MAIN_CHUNK-Flag: %x", i));
				}
			} else {
				throw new UniModelLoaderException(String.format("Unknown Flag at the beginning of the File: %x", i));
			}
		}
		
		public static int seekShort(DataInputStream dis, int s) throws IOException {
			while(dis.readUnsignedShort() != s) {};
			return s;
		}
		
	}
	
	public static class OBJ {
		
		/**
		 * Loads a single .obj file, from the given
		 * filepath and directly returns a UniModel
		 * instance.
		 * 
		 * @param filepath the filepath to the .obj Model file.
		 * @return the UniModel instance.
		 * @throws IOException thrown by fileIO
		 * @throws UniModelLoaderException thrown, if loading errors occur.
		 */
		public static UniModel load(String filepath) throws IOException, UniModelLoaderException {
			if (!filepath.endsWith(".obj")) {
				throw new UniModelLoaderException(String.format("The given File %s is not from type .obj", filepath));
			}
			File file = new File(filepath);
			if (!file.exists()) {
				throw new UniModelLoaderException(String.format("The given File %s does not exist!", filepath));
			}
			return load(file);
		}
		
		public static UniModel load(File file) throws IOException, UniModelLoaderException {
			FileReader reader = new FileReader(file);
			BufferedReader read = new BufferedReader(reader);
			return load(read);
		}
		
		public static UniModel load(BufferedReader reader) throws IOException, UniModelLoaderException {
			String line;
			StringTokenizer st;
			String prefix;
			List<UniVertex3f> v = new ArrayList<UniVertex3f>();
			List<UniNormal3f> n = new ArrayList<UniNormal3f>();
			List<UniTexCoord2f> t = new ArrayList<UniTexCoord2f>();
			List<IndOBJ> i = new ArrayList<IndOBJ>();
			int vnumb = 0;
			int nnumb = 0;
			int tnumb = 0;
			int inumb = 0;
			while((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				if (line.charAt(0) == '#') continue;
				st = new StringTokenizer(line, " ");
				
				prefix = st.nextToken();
				if (prefix.equals("mtllib")) {
					continue;
				}
				if (prefix.equals("0")) {
					continue;
				}
				if (prefix.equals("usemtl")) {
					continue;
				}
				if (prefix.equals("s")) {
					continue;
				}
				if (prefix.equals("v")) {
					vnumb++;
					v.add(new UniVertex3f(
							Float.valueOf(st.nextToken()).floatValue(), 
							Float.valueOf(st.nextToken()).floatValue(), 
							Float.valueOf(st.nextToken()).floatValue()));
					continue;
				}
				if (prefix.equals("vn")) {
					nnumb++;
					n.add(new UniNormal3f(
							Float.valueOf(st.nextToken()).floatValue(),
							Float.valueOf(st.nextToken()).floatValue(),
							Float.valueOf(st.nextToken()).floatValue()));
					continue;
				}
				if (prefix.equals("vt")) {
					tnumb++;
					t.add(new UniTexCoord2f(
							Float.valueOf(st.nextToken()).floatValue(),
							Float.valueOf(st.nextToken()).floatValue()));
					continue;
				}
				if (prefix.equals("f")) {
					while (st.hasMoreElements()) {
						i.add(new IndOBJ(st.nextToken()));
						inumb++;
					}
					continue;
				}
			}
			reader.close();
			
			UniVertex3f[] vertices = new UniVertex3f[i.size()];
			UniNormal3f[] normals = null;
			UniTexCoord2f[] texCoords = null;
			
			if (nnumb > 0) {
				normals = new UniNormal3f[i.size()];
			} if (tnumb > 0) {
				texCoords = new UniTexCoord2f[i.size()];
			}
			
			int pos;
			for (pos = 0; pos < i.size(); pos++) {
				vertices[pos] = v.get((i.get(pos).fv)-1);
				if (tnumb > 0) {
					texCoords[pos] = t.get((i.get(pos).ft)-1);
				}
				if (nnumb > 0) {
					normals[pos] = n.get((i.get(pos).fn)-1);
				}
			}
			System.out.printf("Number of Vertices: %d\n" +
					"Number of Normals: %d\n" +
					"Number of Texture Coords: %d\n" +
					"Number of Indices: %d\n",
					vnumb, nnumb, tnumb, inumb);
			try {
				UniInterleavedVBORenderer rend = new UniInterleavedVBORenderer(
						vertices, normals, null, texCoords, null);
				rend.setPrint(false);
				rend.create();
				UniMesh mesh = new UniMesh(rend);
				UniMesh[] meshes = new UniMesh[1];
				meshes[0] = mesh;
				return new UniModel(meshes);
			} catch (UniGLVersionException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public static void print(String filepath) throws IOException, UniModelLoaderException {
			if (!filepath.contains(".obj")) {
				throw new UniModelLoaderException(String.format("The given File %s is not from type .obj", filepath));
			}
			File file = new File(filepath);
			if (!file.exists()) {
				throw new UniModelLoaderException(String.format("The given File %s does not exist!", filepath));
			}
			print(file);
		}
		
		public static void print(File file) throws IOException, UniModelLoaderException {
			FileReader reader = new FileReader(file);
			BufferedReader read = new BufferedReader(reader);
			print(read);
		}
		
		public static void print(BufferedReader reader) throws IOException, UniModelLoaderException {
			String line;
			while((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		}
		
		private static class IndOBJ {
			
			public static final int NAN = 0xFFFFFFFF;
			
			public int fv;
			public int ft;
			public int fn;
			
			public IndOBJ(String str) {
				String[] strs = str.split("/");
				fv = (Integer.valueOf(strs[0])).intValue();
				if (strs.length >= 2 && strs[1] != null && !strs[1].isEmpty()) {
					ft = (Integer.valueOf(strs[1])).intValue();
				}
				if (strs.length >= 3 && strs[2] != null && !strs[2].isEmpty()) {
					fn = (Integer.valueOf(strs[2])).intValue();
				}
				fv = fv == 0 ? NAN : fv;
				ft = ft == 0 ? NAN : ft;
				fn = fn == 0 ? NAN : fn;
			}
			
		}
	}

	public static void printHEXData(String filename) throws IOException {
		File file = new File(filename);
		if (!file.exists()) {
			System.err.println("File does not exist!");
			return;
		}
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		while(true) {
			try {
				System.out.printf("%2x %2x %2x %2x  %2x %2x %2x %2x\n",
						dis.readUnsignedByte(),
						dis.readUnsignedByte(),
						dis.readUnsignedByte(),
						dis.readUnsignedByte(),
						dis.readUnsignedByte(),
						dis.readUnsignedByte(),
						dis.readUnsignedByte(),
						dis.readUnsignedByte());
			} catch(EOFException eof) {
				System.out.println("END");
				break;
			}
		}
	}
	
}
