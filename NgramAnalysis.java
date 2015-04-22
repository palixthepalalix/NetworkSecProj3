package ngram;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



public class NgramAnalysis {
	
	private class NgramCount {
		int count;
		String percent;
		String hexVal;
		
		public String getHex() {
			return hexVal;
		}
	}
	
	private static Map<String, Integer> readInFile(File inFile) {
		Map<String, Integer> buffer = new HashMap<String, Integer>();
	
		try {
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			for(String line; (line = br.readLine())!= null;) {
				String[] values = line.split(" ");
				String hexValue = values[0];
				Integer count = Integer.valueOf(values[1]);
				buffer.put(hexValue, count);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buffer;
	}
	
	private static Set<String> getHexSet(ArrayList<NgramCount> count) {
		Set<String> set = new HashSet<String>();
		for(NgramCount c : count) {
			set.add(c.getHex());
		}
		return set;
	}
	
	public static void intersectRate(Map<String, Integer> counts1, String fileName1,
			Map<String, Integer> counts2, String fileName2)
	{
		Set<String> set1 = counts1.keySet();
		Set<String> set2 = counts2.keySet();
		Set<String> intersect = new HashSet<String>(set1);
		intersect.retainAll(set2); // computes the intersect of set1 and set2
		float set1CommonRate = intersect.size() / (float) set1.size();
		float set2CommonRate = intersect.size() / (float) set2.size();
		int accuDiff = 0;
		for (String gram : intersect)
			accuDiff += Math.abs(counts1.get(gram) - counts2.get(gram));
		System.out.printf("%s has %d grams, %d of them are in common with %s, common rate: %f\n" +
				"%s has %d grams, %d of them are in common with %s, common rate: %f\n" +
				"average difference: %f\n\n",
		fileName1, set1.size(), intersect.size(), fileName2, set1CommonRate,
		fileName2, set2.size(), intersect.size(), fileName1, set2CommonRate,
		accuDiff / (float) intersect.size());
	}

	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("ur an idiot");
		}
		File file1 = new File(args[0]);
		File file2 = new File(args[1]);
		Map<String, Integer> map1 = readInFile(file1);
		Map<String, Integer> map2 = readInFile(file2);
		intersectRate(map1, args[0], map2, args[1]);
		
		
	}
}
