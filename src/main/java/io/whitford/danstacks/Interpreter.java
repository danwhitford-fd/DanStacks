package io.whitford.danstacks;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Interpreter {
	private final Deque<Integer> dataStack = new ArrayDeque<>();
	private final Iterator<String> inputStream;
	private final Deque<String> words = new ArrayDeque<>();
	private final Map<String, Runnable> primitives = new HashMap<>();
	private final Map<String, List<String>> dictionary = new HashMap<>();
	private final Deque<Boolean> controlFlowStack = new ArrayDeque<>();
	private boolean immediate;
	private AbstractMap.SimpleEntry<String, List<String>> compileFrame;

	public Interpreter( Iterator<String> inputStream ) {
		this.inputStream = inputStream;
		immediate = true;
		initPrimitives();
		initDict();
	}

	private void initPrimitives() {
		primitives.put( "stack", () -> {
			var li = new ArrayList<>(dataStack.stream().map(Object::toString).toList());
			Collections.reverse( li );
			System.out.println( "[ " + String.join( ", ", li ) + " ] <- head" );
		} );
		primitives.put( ":?", () -> {
			System.out.println( primitives.keySet() );
			System.out.println( dictionary.keySet() );
		} );

		primitives.put( ".", () -> System.out.printf( "%d ", dataStack.pop() ) );
		primitives.put( "emit", () -> System.out.print( (char) dataStack.pop().intValue() ) );
		primitives.put( "cr", System.out::println );

		primitives.put( "+", () -> dataStack.push( dataStack.pop() + dataStack.pop() ) );
		primitives.put( "-", () -> dataStack.push( dataStack.pop() - dataStack.pop() ) );
		primitives.put( "*", () -> dataStack.push( dataStack.pop() * dataStack.pop() ) );
		primitives.put( "/", () -> dataStack.push( dataStack.pop() / dataStack.pop() ) );

		primitives.put("over", () -> {
			var a = dataStack.pop();
			var b = dataStack.pop();
			dataStack.push(b);
			dataStack.push(a);
			dataStack.push(b);
		});
		primitives.put("dup", () -> {
			var a = dataStack.pop();
			dataStack.push( a );
			dataStack.push( a );
		});

		primitives.put( ":", () -> {
			this.compileFrame = null;
			immediate = false;
		} );
		primitives.put( ";", () -> {
			dictionary.put( compileFrame.getKey(), compileFrame.getValue() );
			compileFrame = null;
			immediate = true;
		} );

		primitives.put( "if", () -> {
			var conditionWasTrue = !( dataStack.pop() == 0 );
			controlFlowStack.push( conditionWasTrue );
		} );
		primitives.put( "endif", controlFlowStack::pop );
		primitives.put( "else", () -> controlFlowStack.push( !controlFlowStack.pop() ) );
	}

	private void initDict() {
		dictionary.put( "space", List.of( "32", "emit" ) );
	}

	public void run() {
		while ( !words.isEmpty() || inputStream.hasNext() ) {
			if ( words.isEmpty() ) {
				words.push( inputStream.next() );
			}
			var word = words.pop();

			if ( !controlFlowStack.isEmpty() && !controlFlowStack.peek() ) {
				if ( word.equals( "endif" ) ) {
					primitives.get( "endif" ).run();
				} else if ( word.equals( "else" ) ) {
					primitives.get( "else" ).run();
				}
				continue;
			}

			try {
				if ( !immediate ) {
					if ( compileFrame == null ) {
						compileFrame = new AbstractMap.SimpleEntry<>( word, new ArrayList<>() );
					} else if ( dictionary.containsKey( word ) ) {
						compileFrame.getValue().addAll( dictionary.get( word ) );
					} else if ( word.equals( ";" ) ) {
						primitives.get( ";" ).run();
					} else {
						compileFrame.getValue().add( word );
					}
					continue;
				}

				if ( primitives.containsKey( word ) ) {
					primitives.get( word ).run();
					continue;
				}

				if ( dictionary.containsKey( word ) ) {
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
