package io.whitford.danstacks;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Interpreter {
	private final Deque<Integer> dataStack = new ArrayDeque<>();
	private final Iterator<String> inputStream;
	private final Deque<String> words = new ArrayDeque<>();
	private final Map<String, Runnable> primitives = new HashMap<>();
	private final Map<String, List<String>> dictionary = new HashMap<>();

	public Interpreter( Iterator<String> inputStream ) {
		this.inputStream = inputStream;
		initPrimitives();
		initDict();
	}

	private void initPrimitives() {
		primitives.put( "stack", () -> {
			var li = dataStack.stream().map( Object::toString ).collect( Collectors.toList() );
			System.out.println( "head -> [ " + String.join( ", ", li ) + " ]");
		} );
		primitives.put( ":?", () -> System.out.println(primitives.keySet()) );

		primitives.put( ".", () -> System.out.println( dataStack.pop() ) );
		primitives.put( "emit", () -> System.out.print( (char) dataStack.pop().intValue() ) );
		primitives.put( "cr", () -> System.out.println() );

		primitives.put( "+", () -> dataStack.push( dataStack.pop() + dataStack.pop() ) );
		primitives.put( "-", () -> dataStack.push( dataStack.pop() - dataStack.pop() ) );
		primitives.put( "*", () -> dataStack.push( dataStack.pop() * dataStack.pop() ) );
		primitives.put( "/", () -> dataStack.push( dataStack.pop() / dataStack.pop() ) );
	}

	private void initDict() {
		dictionary.put("space", List.of("32", "emit"));
	}

	public void run() {
		while ( !words.isEmpty() || inputStream.hasNext() ) {
			if (words.isEmpty()) {
				words.push( inputStream.next() );
			}
			var word = words.pop();

			try {
				if ( primitives.containsKey( word ) ) {
					primitives.get( word ).run();
					continue;
				}

				if (dictionary.containsKey( word )) {
					words.addAll( dictionary.get( word ) );
					continue;
				}

				var val = Integer.parseInt( word );
				dataStack.push( val );

			} catch ( NumberFormatException e ) {
				System.out.printf( "❌ '%s' is not an int\n", word );
			} catch ( NoSuchElementException e ) {
				System.out.println( "❌ stack underflow" );
			}
		}
	}
}
