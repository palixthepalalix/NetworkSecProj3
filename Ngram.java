
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.*;

public class Ngram {
	
	Map<ByteBuffer, Integer> countMap = new HashMap<ByteBuffer, Integer>();
	
	
	private void createMap(byte[] buffer, int n, int s) {
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
		FileInputStream inStream = null;
		byte[] buffer = new byte[1000];
		try {
			inStream = new FileInputStream(inF);
			int read = 0;
			while((read = inStream.read(buffer)) != -1) {
				/*
				 * add that shit to hashmap or whatever here
				 */
				createMap(buffer, n, s);
				
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
        if(args.length != 4) {
            System.out.println("Usage: {exe} {length of ngrams} " + 
                    "{length of slide} {file to analyze} {name of output file}");
            return;
        }
        
        System.out.println(args.length);
		String inFile = args[2];
		System.out.println(inFile);
		//String outfile = System.getProperty("user.dir") + "/src/examples/out";
		OutputStream out = null;
		if(args[3].equals("stdout")) {
			out = System.out;
		}
		else {
			try {
				out = new FileOutputStream(args[3]);
			} catch(FileNotFoundException e) {
				System.out.println("Output file " + args[3] + " was not found.");
				return;
			}
		}
		int n = 0, s = 0;
		try {
			n = Integer.valueOf(args[0]);
			s = Integer.valueOf(args[1]);
		} catch(NumberFormatException e) {
			System.out.println(args[0] + " and " + args[1] + " are not valid integers");
			return;
		}
		if(s > n || s < 0 || n > 3 || n < 0) {
			System.out.println(n + " and " + s + " are invalid values for n and s.");
			return;
		}
		Ngram ng = new Ngram();
		ng.initNgram(n, s, inFile, out);
		ng.writeToFile(out);
	}

}
