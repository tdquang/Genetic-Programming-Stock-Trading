import java.io.IOException;
import java.io.PrintStream;

import gpjpp.*;
public class TradeGP extends GP {

    //public null constructor required during stream loading only
    public TradeGP() {}

    //this constructor called when new GPs are created
    TradeGP(int genes) { super(genes); }

    //this constructor called when GPs are cloned during reproduction
    TradeGP(TradeGP gpo) { super(gpo); }

    //called when GPs are cloned during reproduction
    protected Object clone() { return new TradeGP(this); }

    //ID routine required for streams
    public byte isA() { return GPObject.USERGPID; }

    //must override GP.createGene to create LawnGene instances
    public GPGene createGene(GPNode gpo) { return new TradeGene(gpo); }

    //must override GP.evaluate to return standard fitness
    public double evaluate(GPVariables cfg) {

        TradeVariables tcfg = (TradeVariables)cfg;

        double totFit = 0;
        // test GP on N random boards
        for (int k=0; k<tcfg.NumTestTraders; k++) {
            //create new random traders
            tcfg.createTrader();
            
            //evaluate main tree for 80 steps of the dozer
            //TODO determine actions based on output
            for (int i=0; i<tcfg.NumSteps; i++) {
            	for(String stock : tcfg.data.getStockSet()){
            		if(stock=="#")continue;
            		float result = ((TradeGene)get(0)).evaluate(tcfg, stock, this);
            		try{
	            		if(result>50) tcfg.trader.buy(stock);
	            		else if(result<-50) tcfg.trader.sell(stock);
	            		else continue;
            		} catch (Exception e){
            			e.printStackTrace();
            		}
            		
            	}
            	tcfg.trader.nextDay();
            }
            totFit += tcfg.trader.calcFitness();
        }
        totFit = totFit/tcfg.NumTestTraders;
        if (cfg.ComplexityAffectsFitness)
            //add length into fitness to promote small trees
            totFit += length()/1000.0;

        //return standard fitness
        return totFit;
    }

    //optionally override GP.printOn to print lawn-specific data
    public void printOn(PrintStream os, GPVariables cfg) {
        super.printOn(os, cfg);
    }

    //optionally override GP.drawOn to draw lawn-specific data
    public void drawOn(GPDrawing ods, String fnameBase, 
        GPVariables cfg) throws IOException {

        //store the result trees to gif files
        super.drawOn(ods, fnameBase, cfg);
    }

    public void printTree(PrintStream os, GPVariables cfg) {
        //super.printTree(os, cfg);
        
        // write grid at each step for this genome
        TradeVariables tcfg = (TradeVariables)cfg;
//        TradeGene gene = (TradeGene)get(0);

        double totFit = 0;
        // run this genome on some number of test grids, printing the resulting grid at each step
        for (int j=0; j<tcfg.NumTestTraders; j++) {
            //create new random grid
            tcfg.createTrader();
            os.println("\n---------------------------------");
            os.println("Trader Behavior on Market starting on "+tcfg.trader.getStartDate());
            os.println("---------------------------------");
            //evaluate main tree for 80 steps of the dozer, printing grid after each move
            //TODO determine actions based on output
            for (int i=0; i<tcfg.NumSteps; i++) {          
            	for(String stock : tcfg.data.getStockSet()){
            		if(stock=="#")continue;
            		float result = ((TradeGene)get(0)).evaluate(tcfg, stock, this);
            		try{
	            		if(result>50) tcfg.trader.buy(stock);
	            		else if(result<-50) tcfg.trader.sell(stock);
	            		else continue;
            		} catch (Exception e){
            			e.printStackTrace();
            		}
            		
            	}
            	tcfg.trader.nextDay();
            }
            tcfg.trader.print(os);
            float curGridFit = tcfg.trader.calcFitness();
            totFit += curGridFit;
            os.println("TRADER FITNESS = "+curGridFit);
        }

        totFit = totFit/tcfg.NumTestTraders;
        if (cfg.ComplexityAffectsFitness)
            //add length into fitness to promote small trees
            totFit += length()/1000.0;
        os.println("FINAL FITNESS = "+totFit);
    }

}
