package test;

import java.io.IOException;

import org.universeengine.opengl.model.modelloader.UniModelLoader;
import org.universeengine.opengl.model.modelloader.UniModelLoaderException;

public class MeshTest {
	
	public static void main(String[] args) {
		try {
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
