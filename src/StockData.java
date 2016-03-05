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
	
	static final int DPS = 0;
	static final int EPS = 1;
	static final int BPS = 2;
	static final int SPS = 3;
	static final int ROE = 4;
	static final int NETF = 5; //net financial
	static final int NONCI = 6; //non-controlling interest
	static final int EBITDA = 7;
	static final int REVENUE = 8;
	static final int OPINC = 9; //operating income
	static final int NETINC = 10; //net income
	static final int OPCASH = 11; //operating cashflow
	static final int FREECASH = 12; //free cashflow
	/**
	 * Class to hold each stock. Contains a mapped set of data for each day.
	 * @author Mullan
	 *
	 */
	public class Stock{
		String symbol;
		float[] dps;
		float[] eps;
		float[] bps;
		float[] sps;
		float[] roe;
		float[] netCap;
		float[] netFinancial;
		float[] nonCInterest; //non-controlling interest
		float[] ebitda;
		float[] revenue;
		float[] opIncome; //operating income
		float[] netIncome;
		float[] opCashflow; //operating cashflow
		float[] freeCashflow;
		
		
		/**
		 * StockDay objects stored in a 3-tiered hashmap of year-month-day.
		 * {@link StockData#getDailyData(String, Integer, Integer, Integer)}
		 */
		Map<Integer, Map<Integer, Map<Integer, StockDay>>> priceDates;
		Map<Integer, Map<Integer, StockMonth>> stockData;
		
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
	 * Class that holds month specific data for a given stock
	 */
	public class StockMonth{
		float netCap;
		float netFinancial;
		float nonControllingInterest;
		float EBITDA;
		float revenue;
		float operatingIncome;
		float netIncome;
		float operatingCashflow;
		float freeCashflow;
	}
	
	/**
	 * Parses a pre-set stock file and generates a system of daily stock data
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	// Location of the data file
//	final String data_location = "C:/comps/StockTrading/src/sp500hst.txt";
//	final String netCapPath = "C:/comps/sandp/data/netCap.csv";
//	final String DPSPath = "C:/comps/sandp/data/DPS.csv";
//	final String financialDeptPath = "C:/comps/sandp/data/financialDept.csv";
	final String netCapPath = "/Users/Mullan/Desktop/EvoStocks/netCap.csv";
	final String DPSPath = "/Users/Mullan/Desktop/EvoStocks/DPS.csv";
	final String financialDeptPath = "/Users/Mullan/Desktop/EvoStocks/financialDept.csv";
	final String data_location = "/Users/Mullan/Dropbox/workspace/EvoStocks/src/sp500hst.txt";
	public StockData() throws NumberFormatException, IOException{
		Path path = Paths.get(data_location);
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
				stock.priceDates = new HashMap<Integer, Map<Integer, Map<Integer,StockDay>>>();
				year = new HashMap<Integer, Map<Integer, StockDay>>();
				month = new HashMap<Integer, StockDay>();
				day = new StockDay(Arrays.copyOfRange(splitLine, 2, 7));
				stocks.put(splitLine[1], stock);
				stock.priceDates.put(yearInt, year);
				year.put(monthInt, month);
				month.put(dayInt, day);
				continue;
			}

			year = stock.priceDates.get(yearInt);
			if(year==null){
				year = new HashMap<Integer, Map<Integer, StockDay>>();
				month = new HashMap<Integer, StockDay>();
				day = new StockDay(Arrays.copyOfRange(splitLine, 2, 7));
				stock.priceDates.put(yearInt, year);
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
		Path path2 = Paths.get(DPSPath);
		int index = 0;
		for (String line : Files.readAllLines(path2)){
			String[] splitLine = line.split(",");
			if(index<2){
				continue;
			}
			Stock stock = stocks.get(splitLine[1]);
			if(stock==null){
				stock = new Stock(splitLine[1]);
				stocks.put(splitLine[1],stock);
			}
			stock.dps = new float[32];
			stock.eps = new float[32];
			stock.bps = new float[32];
			stock.sps = new float[32];
			stock.roe = new float[32];
			
			for(int i = 0; i< 32; i++){
				if(splitLine[4+i]!=""){
					stock.dps[i] = Float.parseFloat(splitLine[4+i]);
				}else{
					stock.dps[i] = -1;
				}
			}
			for(int i = 0; i<32; i++){
				if(splitLine[36+i]!=""){
					stock.eps[i] = Float.parseFloat(splitLine[36+i]);
				}else{
					stock.eps[i] = -1;
				}
			}
			for(int i = 0; i<32; i++){
				if(splitLine[68+i]!=""){
					stock.bps[i] = Float.parseFloat(splitLine[68+i]);
				}else{
					stock.bps[i] = -1;
				}
			}
			for(int i = 0; i<32; i++){
				if(splitLine[100+i]!=""){
					stock.sps[i] = Float.parseFloat(splitLine[100+i]);
				}else{
					stock.sps[i] = -1;
				}
			}
			for(int i = 0; i<32; i++){
				if(splitLine[132+i]!=""){
					stock.roe[i] = Float.parseFloat(splitLine[132+i]);
				}else{
					stock.roe[i] = -1;
				}
			}

		}
		Path path3 = Paths.get(netCapPath);
		index = 0;
		for (String line : Files.readAllLines(path3)){
			String[] splitLine = line.split(",");
			if(index<2){
				continue;
			}
			Stock stock = stocks.get(splitLine[1]);
			if(stock==null){
				stock = new Stock(splitLine[1]);
				stocks.put(splitLine[1],stock);
			}
			stock.netCap = new float[84];
			for(int i = 0; i<84; i++){
				if(splitLine[i+4]!=""){
					stock.netCap[i] = Float.parseFloat(splitLine[i+4]);
				}
			}
		}
		Path path4 = Paths.get(financialDeptPath);
		index = 0;
		for (String line : Files.readAllLines(path4)){
			String[] splitLine = line.split(",");
			if(index<2){
				continue;
			}
			Stock stock = stocks.get(splitLine[1]);
			if(stock==null){
				stock = new Stock(splitLine[1]);
				stocks.put(splitLine[1],stock);
			}
			stock.netFinancial = new float[32];
			stock.nonCInterest = new float[32];
			stock.ebitda = new float[32];
			stock.revenue = new float[32];
			stock.opIncome = new float[32];
			stock.opCashflow = new float[32];
			stock.freeCashflow = new float[32];
			
			for(int i = 0; i< 32; i++){
				if(splitLine[4+i]!=""){
					stock.netFinancial[i] = Float.parseFloat(splitLine[4+i]);
				}else{
					stock.netFinancial[i] = -1;
				}
			}
			for(int i = 0; i<32; i++){
				if(splitLine[36+i]!=""){
					stock.nonCInterest[i] = Float.parseFloat(splitLine[36+i]);
				}else{
					stock.nonCInterest[i] = -1;
				}
			}
			for(int i = 0; i<32; i++){
				if(splitLine[68+i]!=""){
					stock.ebitda[i] = Float.parseFloat(splitLine[68+i]);
				}else{
					stock.ebitda[i] = -1;
				}
			}
			for(int i = 0; i<32; i++){
				if(splitLine[100+i]!=""){
					stock.revenue[i] = Float.parseFloat(splitLine[100+i]);
				}else{
					stock.revenue[i] = -1;
				}
			}
			for(int i = 0; i<32; i++){
				if(splitLine[132+i]!=""){
					stock.opIncome[i] = Float.parseFloat(splitLine[132+i]);
				}else{
					stock.opIncome[i] = -1;
				}
			}
			for(int i = 0; i<32; i++){
				if(splitLine[164+i]!=""){
					stock.netIncome[i] = Float.parseFloat(splitLine[164+i]);
				}else{
					stock.netIncome[i] = -1;
				}
			}
			for(int i = 0; i<32; i++){
				if(splitLine[196+i]!=""){
					stock.opCashflow[i] = Float.parseFloat(splitLine[196+i]);
				}else{
					stock.opCashflow[i] = -1;
				}
			}
			for(int i = 0; i<32; i++){
				if(splitLine[228+i]!=""){
					stock.freeCashflow[i] = Float.parseFloat(splitLine[228+i]);
				}else{
					stock.freeCashflow[i] = -1;
				}
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
	public StockDay getDailyData(String symbol, Integer year, Integer month, Integer day){
		return ((Stock)stocks.get(symbol)).priceDates.get(year).get(month).get(day);
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
			item = ((Stock)stocks.get(symbol)).priceDates.get(date[0]).get(date[1]).get(date[2]);
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
	 * Gets the quarterly data for the given stock, date, and data type
	 * @param symbol
	 * @param date
	 * @param dataType - static ints from StockData
	 * @return
	 */
	public float getQuarterlyData(String symbol, int[] date, int dataType){
		Stock stock = stocks.get(symbol);
		if(stock==null){
			return -1;
		}
		float[] data;
		switch (dataType){
			case DPS:
				data = stock.dps;
				break;
			case EPS:
				data = stock.eps;
				break;
			case BPS:
				data = stock.bps;
				break;
			case SPS:
				data = stock.sps;
				break;
			case ROE:
				data = stock.roe;
				break;
			case NETF:
				data = stock.netFinancial;
				break;
			case NONCI:
				data = stock.nonCInterest;
				break;
			case EBITDA:
				data = stock.ebitda;
				break;
			case REVENUE:
				data = stock.revenue;
				break;
			case OPINC:
				data = stock.opIncome;
				break;
			case NETINC:
				data = stock.netIncome;
				break;
			case OPCASH:
				data = stock.opCashflow;
				break;
			case FREECASH:
				data = stock.freeCashflow;
				break;
			default:
				return -1;
				
		}
		int year = date[0];
		int month = date[1];
		month = month/3;
		year = year-2008;
		if(month==0){
			month = 12;
			year -=1;
		}
		if(month<0 || year<0){
			return -1;
		}
		int index = year*4+month-1;
		try{
			while(data[index]==-1){
				index-=1;
				if(index<0){
					return -1;
				}
			}
			return data[index];
		}catch(Exception e){
			return -1;
		}
	}
	
	/**
	 * Gives the DPS for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getDPS(String symbol, int[] date){
		return getQuarterlyData(symbol,date, DPS);
	}
	
	/**
	 * Gives the EPS for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getEPS(String symbol, int[] date){
		return getQuarterlyData(symbol,date, EPS);
	}
	
	/**
	 * Gives the BPS for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getBPS(String symbol, int[] date){
		return getQuarterlyData(symbol,date, BPS);
	}
	
	/**
	 * Gives the SPS for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getSPS(String symbol, int[] date){
		return getQuarterlyData(symbol,date, SPS);
	}
	
	/**
	 * Gives the ROE for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getROE(String symbol, int[] date){
		return getQuarterlyData(symbol,date, ROE);
	}
	
	/**
	 * Gives the net financial for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getNetFinancial(String symbol, int[] date){
		return getQuarterlyData(symbol,date, NETF);
	}

	/**
	 * Gives the non-controlling interest for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getNonCInterest(String symbol, int[] date){
		return getQuarterlyData(symbol,date, NONCI);
	}

	/**
	 * Gives the EBITDA for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getEBITDA(String symbol, int[] date){
		return getQuarterlyData(symbol,date, EBITDA);
	}

	/**
	 * Gives the revenue for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getRevenue(String symbol, int[] date){
		return getQuarterlyData(symbol,date, REVENUE);
	}

	/**
	 * Gives the operatinc income for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getOperatingInc(String symbol, int[] date){
		return getQuarterlyData(symbol,date, OPINC);
	}

	/**
	 * Gives the net income for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getNetInc(String symbol, int[] date){
		return getQuarterlyData(symbol,date, NETINC);
	}

	/**
	 * Gives the operating cash for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getOpCash(String symbol, int[] date){
		return getQuarterlyData(symbol,date, OPCASH);
	}

	/**
	 * Gives the free cash for the given stock for the most recent quarterly update
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getFreeCash(String symbol, int[] date){
		return getQuarterlyData(symbol,date, FREECASH);
	}
	
	/**
	 * Gives the Net Capitalization for the given date
	 * @param symbol
	 * @param date - [year, month]
	 * @return
	 */
	public float getNetCap(String symbol, int[] date){
		Stock stock = stocks.get(symbol);
		if(stock==null){
			return -1;
		}
		int year = date[0];
		int month = date[1];
		year = year-2009;
		int index = year*12+month-1;
		if(index<0){
			return -1;
		}
		try{
			return stock.netCap[index];
		}catch(Exception e){
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
