import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//Data goes from 2009-09-21 to 2010-09-20

/**
 * Reference object for all daily stock data over a set period of time
 * @author Mullan
 *
 */
public class StockData {
	Map<String, Stock> stocks;
	
	/**
	 * Class to hold each stock. Contains a mapped set of data for each day.
	 * @author Mullan
	 *
	 */
	public class Stock{
		String symbol;
		
		/**
		 * StockDay objects stored in a 3-tiered hashmap of year-month-day.
		 * {@link StockData#getData(String, Integer, Integer, Integer)}
		 */
		Map<Integer, Map<Integer, Map<Integer, StockDay>>> dates;
		
		/**
		 * Create a new stock item
		 * @param newSymbol - ticker symbol for the given stock
		 */
		public Stock(String newSymbol){
			symbol = newSymbol;
		}
	}
	
	/**
	 * Class to hold each day for a given stock
	 * @author Mullan
	 *
	 */
	public class StockDay{
		float open;
		float high;
		float low;
		float close;
		int volume;
		
		/**
		 * Create a new stock day. 
		 * Note: Specific implementation to the StockData class initialization.
		 * @param inputData - String array representing 4 floats and an int
		 */
		public StockDay(String[] inputData){
			open = Float.parseFloat(inputData[0]);
			high = Float.parseFloat(inputData[1]);
			low = Float.parseFloat(inputData[2]);
			close = Float.parseFloat(inputData[3]);
			volume = Integer.parseInt(inputData[4]);
		}
		
		public String toString(){
			return ""+open+" "+high+" "+low+" "+close+" "+volume;
		}
	}
	
	/**
	 * Parses a pre-set stock file and generates a system of daily stock data
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public StockData() throws NumberFormatException, IOException{
		Path path = Paths.get("/Users/Mullan/Dropbox/workspace/EvoStocks/src/sp500hst.txt");
		stocks = new HashMap<String, Stock>();
		for (String line : Files.readAllLines(path)){
			String[] splitLine = line.split(",");
			String dateString = splitLine[0];
			Integer yearInt = Integer.parseInt(dateString.substring(0, 4));
			Integer monthInt = Integer.parseInt(dateString.substring(4,6));
			Integer dayInt = Integer.parseInt(dateString.substring(6,8));
			
			Map<Integer, Map<Integer, StockDay>> year;
			Map<Integer, StockDay> month;
			StockDay day;
			Stock stock = stocks.get(splitLine[1]);

			if(stock==null){
				stock = new Stock(splitLine[1]);
				stock.dates = new HashMap<Integer, Map<Integer, Map<Integer,StockDay>>>();
				year = new HashMap<Integer, Map<Integer, StockDay>>();
				month = new HashMap<Integer, StockDay>();
				day = new StockDay(Arrays.copyOfRange(splitLine, 2, 7));
				stocks.put(splitLine[1], stock);
				stock.dates.put(yearInt, year);
				year.put(monthInt, month);
				month.put(dayInt, day);
				continue;
			}

			year = stock.dates.get(yearInt);
			if(year==null){
				year = new HashMap<Integer, Map<Integer, StockDay>>();
				month = new HashMap<Integer, StockDay>();
				day = new StockDay(Arrays.copyOfRange(splitLine, 2, 7));
				stock.dates.put(yearInt, year);
				year.put(monthInt,month);
				month.put(dayInt,day);
				continue;
			}
			
			month = year.get(monthInt);
			if(month==null){
				month = new HashMap<Integer, StockDay>();
				day = new StockDay(Arrays.copyOfRange(splitLine, 2, 7));
				year.put(monthInt,month);
				month.put(dayInt,day);
				continue;
			}
			
			day = month.get(dayInt);
			if(day==null){
				day = new StockDay(Arrays.copyOfRange(splitLine, 2, 7));
				month.put(dayInt,day);
				continue;			
			}
			
		}
	}
	
	/**
	 * Returns the stock data for a given day
	 * @param symbol - stock symbol abbreviation
	 * @param year
	 * @param month
	 * @param day
	 * @return - StockDay object representing a given day for a stock
	 */
	public StockDay getData(String symbol, Integer year, Integer month, Integer day){
		return ((Stock)stocks.get(symbol)).dates.get(year).get(month).get(day);
	}
	
	/**
	 * Returns the price of a stock on a given day.
	 * @param symbol - stock symbol abbreviation
	 * @param date
	 * @return
	 */
	public float getPrice(String symbol, int[] date){
		//TODO change this once we know which one is the closing price
		StockDay item;
		try{
			item = ((Stock)stocks.get(symbol)).dates.get(date[0]).get(date[1]).get(date[2]);
		}catch (Exception e){
			return 0;
		}
		
		if(item!=null){
			return item.close;
		}else{
			return -1;
		}
	}
	
	/**
	 * Returns a set of all of the stock symbol strings
	 * @return
	 */
	public Set<String> getStockSet(){
		return stocks.keySet();
	}
	
//	public static void main(String[] args) throws NumberFormatException, IOException{
//		StockData test = new StockData();
//		Object temp = test.stocks;
//		
//		System.out.println(test.getData("A", 2009, 8, 21));
//	}

}
