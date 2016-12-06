package com.viduus.util.debug;

/**
 * This class is responsible for handling where this applications output goes. Wether that be a file or
 * System.out
 * 
 * @author Ethan Toney
 *
 */
public class OutputHandler {

	private long start_time;
	private static ThreadLocal<Integer> tab_count = new ThreadLocal<>();
	
	/**
	 * Prints data to the output stream (File or System.out)
	 * 
	 * @param data
	 */
	public synchronized static void print( String data ){
		System.out.print( getPrintString(data) );
	}
	
	/**
	 * Prints data to the output stream (File or System.out). However adds a return character at the end
	 * 
	 * @param data
	 */
	public synchronized static void println( String data ){
		System.out.print( getPrintString(data) + "\n" );
	}
	
	/**
	 * 
	 */
	public OutputHandler(){
		start_time = System.currentTimeMillis();
	}
	
	/**
	 * Starts a timer when String is printed. Useful to keep track of how long processes take.
	 * 
	 * @param data
	 */
	public void startTimedPrint( String data ){
		start_time = System.currentTimeMillis();
		print(data);
	}
	
	/**
	 * End a timer when String is printed. Useful to keep track of how long processes take.
	 * Will print out the given output with time in parenthesis afterwards.
	 * 
	 * ex)
	 * 	your output... (100ms)
	 * 
	 * @param data
	 */
	public void endTimedPrint( String data ){
		long dt = System.currentTimeMillis() - start_time;
		println(data+"... ("+dt+"ms)\n");
	}
	
	/**
	 * Starts a timer when String is printed. Useful to keep track of how long processes take.
	 * 
	 * @param data
	 */
	public void startTimedPrintln( String data ){
		start_time = System.currentTimeMillis();
		println(data);
	}
	
	/**
	 * End a timer when String is printed. Useful to keep track of how long processes take.
	 * Will print out the given output with time in parenthesis afterwards.
	 * 
	 * ex)
	 * 	your output... (100ms)
	 * 
	 * @param data
	 */
	public void endTimedPrintln( String data ){
		long dt = System.currentTimeMillis() - start_time;
		println(data+"... ("+dt+"ms)\n");
	}
	
	public synchronized static void addTab(){
		tab_count.set( tab_count.get() + 1 );
	}
	
	public synchronized static void removeTab(){
		int val = tab_count.get();
		if( val > 0 )
			tab_count.set( val - 1 );
	}
	
	private static String genTabs(){
		StringBuilder b = new StringBuilder();
		
		// Init the value if it hasn't been yet
		if( tab_count.get() == null )
			tab_count.set(0);
		
		for( int i=0 ; i<tab_count.get() ; i++ ){
			b.append("    ");
		}
		return b.toString();
	}
	
	public static String getPrintString( String str ){
		return Thread.currentThread().getName()+" - "+genTabs()+str;
	}
	
}
