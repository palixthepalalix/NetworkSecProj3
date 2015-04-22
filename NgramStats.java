import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class NgramStats {
	
	private class MapEntry implements Comparable {
		ByteBuffer value;
		Integer count;
		public MapEntry(ByteBuffer b, Integer c) {
			value = b;
			count = c;
		}
		public ByteBuffer getBytes() {
			return value;
		}
		public Integer getCount() {
			return count;
		}
		@Override
		public int compareTo(Object arg0) {
			if(arg0 instanceof MapEntry) {
				MapEntry other = (MapEntry) arg0;
				if(count.equals(other.getCount())) {

					return value.compareTo(other.getBytes());
				}
				return count.compareTo(other.count);
			}
			else {
				return -1;
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
	
	public void getStats(OutputStream out, String inFile, Ngram ng) throws IOException {
		ng.getCounts(inFile);
		double totalCount = (double) ng.getTotalCount();
		double percent;
		Map<ByteBuffer, Integer> countMap = ng.getMap();
		ArrayList<MapEntry> list = new ArrayList<MapEntry>();
		
		for(Entry e : countMap.entrySet()) {
			MapEntry entry = new MapEntry((ByteBuffer) e.getKey(), (Integer) e.getValue());
			list.add(entry);
		}
		Collections.sort(list); 
		int iter = list.size() - 1;
		for(int i = 0; i < 20; i++) {
			MapEntry ent = list.get(iter);
			percent = (((double)ent.getCount()) / totalCount) * 100;
			String output =  formatByteArr(ent.getBytes().array()) + " " + ent.getCount() 
					+ " " + String.format("%.2f", percent) + "%" + "\n";
			out.write(output.getBytes());
			iter--;
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		/*
		 * {exe} {n} {s} {infiles...}
		 */
		
		if(args.length < 2) {
			System.out.println("usage");
			return;
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
		String outname = args[2];
        String progDir = args[3];
		NgramStats ngs = new NgramStats();
		for(int i = 4; i < args.length; i++) {
		    Ngram ng = new Ngram(n, s);
			String inf = progDir + args[i];
            String[] tmp = inf.split("/");

            String progName = tmp[tmp.length - 1];
			OutputStream out = new FileOutputStream(outname + progName + "_" + n + "_" + s);
			ngs.getStats(out, inf, ng);
		}
		
	}

}
