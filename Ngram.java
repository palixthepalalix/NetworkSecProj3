package ngram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.*;

public class Ngram {
	
	private File inFile;
	private int n;
	private int s;
	private File outFile;
	private int[] count = new int[256];
	
	private NgramTree tree;
	Map<ByteBuffer, Integer> countMap = new HashMap<ByteBuffer, Integer>();
	
	private void writeToArray(byte[] buffer) {
		for(byte b : buffer) {
			// byte takes values from -128 to 127
			count[128 +b]++;
		}
	}
	
	private void createMap(byte[] buffer) {
		int finish = buffer.length - n + 1;
		for(int i = 0; i < finish; i+=s) {
			byte[] subArr = new byte[n];
			int slideEnd = i + n;
			/*
			 * copy the slide into the new array
			 */
			for(int j = i; j<slideEnd; j++) {
				subArr[j - i] = buffer[j];
			}
			/*
			 * add counts to map
			 */
			ByteBuffer wrapper = ByteBuffer.wrap(subArr);
			if(countMap.containsKey(wrapper)) {
				countMap.put(wrapper, countMap.get(wrapper) + 1);
			}
			else {

				countMap.put(wrapper, 1);
			}
		}
		
	}
	
	
	private void writeArray(OutputStream outStream) {
		try {
			for(int i = 0; i < count.length; i++) {
				byte b = (byte) (i - 128);
				outStream.write(b);
				String s = " " + Integer.toString(count[i]) + "\n";
				outStream.write(s.getBytes());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void writeTree(OutputStream outStream) {
		tree.writeStats(outStream);
	}
	
	private String formatByteArr(byte[] byteArr) {
		StringBuilder sb = new StringBuilder();
		sb.setLength(0); // reset
        for (byte b : byteArr)
            sb.append(String.format("%02x", b)); // Notice for better looking, I didn't add the 0x part, so 0x2b is 2b.
        return sb.toString();
		
	}
	
	public void writeToFile(OutputStream outStream) {
		
		try {
			//FileOutputStream outStream = new FileOutputStream(outF);
			for(Map.Entry<ByteBuffer, Integer> e : countMap.entrySet()) {
				if(e.getValue() > 1)
					System.out.println( formatByteArr(e.getKey().array()) + " " + e.getValue());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*
			 * file not found and IO
			 */
			e.printStackTrace();
		}
		
	}
	
	public void initNgram(int n, int s, String inF, OutputStream outF) {
		this.n = n;
		this.s = s;
		FileInputStream inStream = null;
		byte[] buffer = new byte[1000];
		try {
			inStream = new FileInputStream(inF);
			int read = 0;
			tree = new NgramTree(n);
			while((read = inStream.read(buffer)) != -1) {
				/*
				 * add that shit to hashmap or whatever here
				 */
				createMap(buffer);
				
			}
			inStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*
			 * file not found
			 * also for input stream, io exception
			 */
			e.printStackTrace();
		}
		
		
	}
	
	
	public static void main(String[] args) {
		/*
		 * takes 4 inputs
		 * Usage: {exe} {length of ngrams} {length of slide} {file to analyze} {name of output file}
		 */
		String inFile = System.getProperty("user.dir") + 
				"/src/examples/prog1";
		System.out.println(inFile);
		//String outfile = System.getProperty("user.dir") + "/src/examples/out";
		OutputStream out = System.out;
		int n = 3;
		int s = 1;
		Ngram ng = new Ngram();
		ng.initNgram(n, s, inFile, out);
		System.out.println("done making tree");
		ng.writeToFile(out);
	}

}
