/*
 * Author: Alix Cook
 * amc2316
 *
 * This program computes the ngrams for n=1,2,3 and s=1 to n
 * and writes the hex value, count and percent of ngrams found
 * in file
 * just gives a percentage for the amount of ngram values that are unique
 */

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
	int n;
	int s;
	double totalCount = 0;
	
	public Ngram(int n, int s) {
		this.n = n;
		this.s = s;
	}
	
	public double getTotalCount() {
		return totalCount;
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
			totalCount++;
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
		double unique = 0;
		try {
			//FileOutputStream outStream = new FileOutputStream(outF);
			for(Map.Entry<ByteBuffer, Integer> e : countMap.entrySet()) {
				if(e.getValue() > 1) {
					double percent = (((double)e.getValue()) / totalCount) * 100;
					String output =  formatByteArr(e.getKey().array()) + " " + e.getValue() 
							+ " " + String.format("%.2f", percent) + "%" + "\n";
					outStream.write(output.getBytes());
                }
				else {
					unique++;
				}
			}
			double uniquePercent = (unique / totalCount) * 100;
			String uniqueOutput = String.format("%.2f", uniquePercent) + "% of ngrams are unique.";
			outStream.write(uniqueOutput.getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*
			 * file not found and IO
			 */
			e.printStackTrace();
		}
		
	}
	
	public void getCounts(String inF) {
		FileInputStream inStream = null;
		byte[] buffer = new byte[1000];
		try {
			inStream = new FileInputStream(inF);
			while(inStream.read(buffer) != -1) {
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
	
	public Map<ByteBuffer, Integer> getMap() {
		return countMap;
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
        
		String inFile = args[2];
		//String outfile = System.getProperty("user.dir") + "/src/examples/out";
		OutputStream out = null;
        
		if(args[3].equals("stdout") ) {
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
		int n, s;
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
		Ngram ng = new Ngram(n , s);
		ng.getCounts(inFile);
		ng.writeToFile(out);
	}

}
