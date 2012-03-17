package org.universeengine.opengl.model.modelloader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.universeengine.exceptions.UniGLVersionException;
import org.universeengine.opengl.model.UniMesh;
import org.universeengine.opengl.model.UniModel;
import org.universeengine.opengl.model.renderer.UniVBORenderer;
import org.universeengine.opengl.vertex.UniColor3f;
import org.universeengine.opengl.vertex.UniNormal3f;
import org.universeengine.opengl.vertex.UniTexCoord2f;
import org.universeengine.opengl.vertex.UniVertex3f;
import org.universeengine.util.UniPrint;

/**
 * Short: to load UEM-Models, use loadUEM().
 * 
 * Long:
 * This is the ModelLoader for loading .uem ModelFiles (UniverseEngine Model)
 * The UEM File Structure can be printed with printUEM(String filename).
 * If you have not got any UEM Files to print, just call
 * writeUniCubeUEM(String filename). It will create you a test File,
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
 * It cannot be seen in a Text-Editor, however, you can call printUEM(String file)
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

		public static UniModel load(String filename) throws IOException, UniModelLoaderException {
			if (!filename.contains(".uem")) {
				throw new IOException("loadUNI() cannot load from files, which aren't .uem!\n");
			} else {
				return load(new File(filename));
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
					throw new UniModelLoaderException(String.format("Unknown Flag after \"START\"-Flag: %d\n", b));
				}
			} else {
				throw new UniModelLoaderException(String.format("\"START\"-Flag is missing! Instead i got: %d\n", b));
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
							throw new UniModelLoaderException(String.format("Unknown data in ELEMENT: %d\n", flag));
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
						throw new UniModelLoaderException(String.format("Unknown Flag after \"ELEMENTS\"-Flag: %d\n", b));
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
						UniVBORenderer rend = new UniVBORenderer(v, n, c, t, ind);
						rend.create();
						meshes[m] = new UniMesh(rend);
					} catch (UniGLVersionException e) {
						UniPrint.printerrf("After Loading Mesh, i could not create it from data.\n");
						e.printStackTrace();
					}
				} else {
					throw new UniModelLoaderException(String.format("Unknown Flag after \"MESH\"-Flag: %d\n", b));
				}
			}
			return meshes;
		}
		
		public static void print(String filename) throws IOException, UniModelLoaderException {
			if (!filename.contains(".uem")) {
				throw new IOException("loadUNI() cannot load from files, which aren't .uem!\n");
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
					throw new UniModelLoaderException(String.format("Unknown Flag after \"START\"-Flag: %d\n", b));
				}
			} else {
				throw new UniModelLoaderException(String.format("\"START\"-Flag is missing! Instead i got: %d\n", b));
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
							throw new UniModelLoaderException(String.format("Unknown data in ELEMENT: %d\n", flag));
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
						throw new UniModelLoaderException(String.format("Unknown Flag after \"ELEMENTS\"-Flag: %d\n", b));
					}
				} else {
					throw new UniModelLoaderException(String.format("Unknown Flag after \"MESH\"-Flag: %d\n", b));
				}
				while(dis.readByte() != END) { 
					System.out.println("End of File could not be found yet.");
				}
				System.out.println("END");
			}
		}
		
		public static void writeUniCube(String filename) throws IOException {
			if (!filename.contains(".uem")) {
				throw new IOException("writeUNI() does not allow any other formats, then .uem!\n");
			} else {
				File file = new File(filename);
				if (!file.createNewFile()) {
					throw new IOException("Either the given File already exists, or the operation failed.\n");
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
	
	public static class THREEDS {
		
	}
	
}
