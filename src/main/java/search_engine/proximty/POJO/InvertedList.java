package search_engine.proximty.POJO;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.plaf.metal.OceanTheme;

/**
 * 
 * @author Divyavijay Sahay
 *
 */
public class InvertedList extends TreeMap<String,PostingData> {

	/**
	 * Constant representing version id
	 */
	private static final long serialVersionUID = -9127432547081070014L;
	
	private Iterator<String> lineIterator;
	private LinkedHashSet<Integer> positions;
	private PostingData postingData;
	
	public InvertedList() {
		this.lineIterator = null;
		this.positions = null;
	}
	

	public InvertedList(Iterator<String> lineIterator) {
		super();
		this.lineIterator = lineIterator;
		generate();
	}




	/**
	 * Generate an inverted list from the given string representation of it.
	 * 
	 */
	public void generate() {
		String docID = "";
		int termFrequency;		
		
		while(lineIterator.hasNext()) {
			postingData = new PostingData();
			docID = lineIterator.next();
			String tf = lineIterator.next();
			termFrequency = Integer.valueOf(tf);
			postingData.setTermFrequency(termFrequency);
//			System.out.print(docID + " " + termFrequency+" ");
			if(termFrequency>0) {
				String nop = lineIterator.next();
				postingData.setNoOfPositions(Integer.valueOf(nop));
//				updatePositionValues(postingData.getNoOfPositions());
//				System.out.print(postingData.getNoOfPositions()+" ");
				int count = postingData.getNoOfPositions() ;
				while(count > 0) {
					count -= 1;
					String pos = lineIterator.next();
//					System.out.println(pos);
					this.postingData.getPositions().add(Integer.valueOf(pos));
				}
			} else {
				postingData.setNoOfPositions(0);
			}
//			System.out.print(postingData.getPositions());
//			System.out.println();
			this.put(docID,postingData);
		}
	}


	private void updatePositionValues(int noOfPositions) {
		
		while(noOfPositions >= 0) {
			noOfPositions--;
			this.postingData.getPositions().add(Integer.valueOf(lineIterator.next()));
		}
	}
	
	@Override
	public String toString() {
		
		Set<String> keys = this.keySet();
		Iterator<String> iterator = keys.iterator();
		while(iterator.hasNext()) {
			
		}
		
		return super.toString();
	}
	
}
