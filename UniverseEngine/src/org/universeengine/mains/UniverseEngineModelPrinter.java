package org.universeengine.mains;

import java.io.IOException;

import org.universeengine.opengl.model.modelloader.UniModelLoader;
import org.universeengine.opengl.model.modelloader.UniModelLoaderException;

public class UniverseEngineModelPrinter {

	public static void main(String[] args) {
		try {
			UniModelLoader.UEM.print(args[0]);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UniModelLoaderException e) {
			e.printStackTrace();
		}
	}

}
