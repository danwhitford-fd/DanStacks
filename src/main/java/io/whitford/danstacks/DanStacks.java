package io.whitford.danstacks;

import java.util.Scanner;

public class DanStacks {
	private static final String header = """
 ____              ____  _             _       \s
|  _ \\  __ _ _ __ / ___|| |_ __ _  ___| | _____\s
| | | |/ _` | '_ \\\\___ \\| __/ _` |/ __| |/ / __|
| |_| | (_| | | | |___) | || (_| | (__|   <\\__ \\
|____/ \\__,_|_| |_|____/ \\__\\__,_|\\___|_|\\_\\___/
											   \s
""";

	public static void main(String... args) {
		var scanner = new Scanner( System.in );
		scanner.useDelimiter( "\\s+" );
		var interpreter = new Interpreter(scanner);

		System.out.print(DanStacks.header);
		interpreter.run();
	}
}
