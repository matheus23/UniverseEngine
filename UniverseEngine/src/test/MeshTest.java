package test;

import java.io.IOException;

import org.universeengine.opengl.model.modelloader.UniModelLoader;
import org.universeengine.opengl.model.modelloader.UniModelLoaderException;

public class MeshTest {
	
	public static void main(String[] args) {
		try {
			// Uncomment only, if you haven't got cube_model.uem already
			// If you have it and Uncomment it, you will get an error,
			// try it out :P
//			UniModelLoader.writeUniCubeUEM("cube_model.uem");
			UniModelLoader.printUEM("cube_model.uem");
		} catch (IOException e) {
			System.err.println("IOException:");
			e.printStackTrace();
		} catch (UniModelLoaderException umle) {
			System.err.println("UniModelLoaderException:");
			umle.printStackTrace();
		}
	}

}
