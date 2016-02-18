import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Trader {
	
	//custom exceptions
	public class InvalidSellException extends Exception{
		public InvalidSellException(String message){
			super(message);
		}
	}
	public class InvalidBuyException extends Exception{
		public InvalidBuyException(String message){
			super(message);
		}
	}
	
    //functions and terminals
    public final static int ZERO = 0;
    public final static int ONE = 1; 
    public final static int TWO = 2;  
    public final static int INC = 3;  
    public final static int DEC = 4;  
    public final static int ADD = 5;  
    public final static int SUB = 6;  
    public final static int MAX = 7;  
    public final static int MIN = 8;  
    public final static int ITE = 9; 
    public final static int RANDOM = 10;
	
    //Trader private vars
    private Map<String, Integer> stocks;
    private Random rgen;
    private int[] start;
    private int[] date; //[year,month,day]
    private float startFunds;
    private float funds;
    private StockData stockData;
    
    public Trader(StockData data, float startingFunds, int[] startingDate){
    	stocks = new HashMap<String,Integer>();
    	rgen = new Random();
    	start = startingDate.clone();
    	date = startingDate;
    	startFunds = startingFunds;
    	funds = startingFunds;
    	stockData = data;
    }
    
    public void buy(String stockName) throws InvalidBuyException{
    	float price = stockData.getPrice(stockName, date);
    	if(price>funds || price==0){
    		//TODO should this just not do anything?
    		return;
//    		throw new InvalidBuyException("Not enough funds to buy "+stockName);
    	}
    	Integer number = stocks.get(stockName);
    	if(number!=null){
    		stocks.put(stockName, number+1);
    	}else{
    		stocks.put(stockName,1);
    	}
    	funds-=price;
    }
    public void sell(String stockName) throws InvalidSellException{
    	Integer number = stocks.get(stockName);
    	float price = stockData.getPrice(stockName, date);
    	if(number!=null){
    		if(number-1<=0){
    			stocks.remove(stockName);
    		}else{
    			stocks.put(stockName, number-1);
    		}
    	}else{
    		//throw new InvalidSellException("Can't sell "+stockName);'
    		return;
    	}
    	funds+=price;
    }
    
    public float priceXDaysAgo(String stockName, int days){
    	int[] historyDate = date;
    	for(int i = days; i>0; i++){
        	do{
        		historyDate[2]-=1;
    	    	if(historyDate[2]<=0){
    	    		historyDate[1]-=1;
    	    		historyDate[2]=31;
    	    		if(historyDate[1]<=0){
    	    			historyDate[0]-=1;
    	    			historyDate[1] = 12;
    	    		}
    	    	}
    	    	if(historyDate[0]<2009){
    	    		historyDate = new int[]{2009,8,21};
    	    	}else if(historyDate[0]==2009){
    	    		if(historyDate[1]<8){
    	    			historyDate = new int[]{2009,8,21};
    	    		}else if(historyDate[1]==8){
    	    			if(historyDate[2]<21){
    	    				historyDate = new int[]{2009,8,21};
    	    			}
    	    		}
    	    	}
        	}while(stockData.getPrice(stockName, historyDate)==-1);
    	}
    	return stockData.getPrice(stockName, historyDate);
    	
    }
    
    public float calcFitness(){
    	float fit = funds-startFunds;
    	float stockValue = 0;
    	for(String key : stocks.keySet()){
    		stockValue += (stockData.getPrice(key, date) * stocks.get(key));
//    		System.out.println(key+" :: "+stockData.getPrice(key, date));
    	}
//    	System.out.println(stockValue+" :: "+((float)funds-startFunds)+" :: "+date[0]+"/"+date[1]+"/"+date[2]+" :: "+start[0]+"/"+start[1]+"/"+start[2]);
    	
    	//rounds to 3 decimal points because of precision error with floats
    	return fit+stockValue;
    }
    
    public void print(){
    	print(System.out);
    }
    
    public void print(PrintStream os){
    	os.println("Date: "+date[0]+"/"+date[1]+"/"+date[2]);
    	os.println("Funds: "+funds);
    	for(String key : stocks.keySet()){
    		os.println(key+": "+stocks.get(key));
    	}
    	os.println();
    }
    
    /**
     * Increments the date variable to the day when stocks were traded. Uses the stock "A" as a guide.
     */
    public void nextDay(){
    	String stockName = "A";
    	int[] tomorrow = date;
    	do{
	    	tomorrow[2]+=1;
	    	if(tomorrow[2]>=31){
	    		tomorrow[1]+=1;
	    		tomorrow[2]=1;
	    		if(tomorrow[1]>12){
	    			tomorrow[0]+=1;
	    			tomorrow[1] = 1;
	    		}
	    	}
	    	if(tomorrow[0]>=2011){
	    		tomorrow = new int[]{2010,8,20};
	    	}else if(tomorrow[0]==2010){
	    		if(tomorrow[1]>=9){
	    			tomorrow = new int[]{2010,8,20};
	    		}else if(tomorrow[1]==8){
	    			if(tomorrow[2]>=20){
	    				tomorrow = new int[]{2010,8,20};
	    			}
	    		}
	    	}
    	}while(stockData.getPrice(stockName, tomorrow)==-1);
    	date = tomorrow;
    }
    
    public void printDate(){
    	System.out.println(date[0]+"/"+date[1]+"/"+date[2]);
    }
    
	public static void main(String[] args) throws NumberFormatException, IOException, InvalidBuyException, InvalidSellException {
		// TODO Auto-generated method stub
		int startingFunds = 10000;
		int numberOfTraders = 1;
		float[] fitnessList = new float[numberOfTraders];
		StockData data = new StockData();
		int numStocks = data.getStockSet().size();
		Random random = new Random();
		for(int i = 0; i<numberOfTraders; i++){
			int[] date = new int[3];
			date[0] = 2009;
			date[1] = random.nextInt(3)+10;
			date[2] = random.nextInt(30)+1;
			Trader trader = new Trader(data, startingFunds, date);
			trader.nextDay();
			for(int j = 0; j< 40; j++){
				for(String stock : data.getStockSet()){
					if(trader.rgen.nextInt(numStocks*2)==42){
						//TODO add in to buy a random amount of a stock
						trader.buy(stock);
					}
				}
				ArrayList<String> toRemove = new ArrayList<String>();
				for(String stock : trader.stocks.keySet()){
					if(trader.rgen.nextInt(10)==7){
						//TODO add in to sell a random amount of a stock
						toRemove.add(stock);
					}
				}
				for(String item: toRemove){
					trader.sell(item);
				}
				trader.nextDay();
//				trader.print();
			}
			fitnessList[i] = trader.calcFitness();
		}
		
		float sum = 0;
		float best = -1*startingFunds;
		for(float fit : fitnessList){
			if(fit>best){
				best = fit;
			}
			sum+=fit;
		}
		System.out.println("Average fitness: "+sum/numberOfTraders);
		System.out.println("Best fitness: "+(float)(Math.round(best*1000)/1000.0));
		
	}

}
