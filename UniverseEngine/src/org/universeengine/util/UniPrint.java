package org.universeengine.util;

import java.io.PrintStream;

public final class UniPrint {
	
	public static boolean enabled = true;
	public static PrintStream eps = System.err;
	public static PrintStream ops = System.out;
	
	public static void printoutf(UniPrintable up, String format, Object... args) {
		printf(up, ops, format, args);
	}
	
	public static void printerrf(UniPrintable up, String format, Object... args) {
		printf(up, eps, format, args);
	}
	
	public static void printf(UniPrintable up, PrintStream ps, String format, Object... args) {
		if (enabled) ps.printf(up.getClassName() + ":\n >> " + format, args);
	}
	
	public static void printf(String format, Object... args) {
		if (enabled) ops.printf(format, args);
	}

	public static void printerrf(String format, Object... args) {
		if (enabled) eps.printf(format, args);
	}
}
