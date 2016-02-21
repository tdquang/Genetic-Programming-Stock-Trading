import java.util.Random;

import gpjpp.*;

public class TradeGene extends GPGene{
	public TradeGene() {
        
    }
    
    private Random rgen = new Random();
        //rgen = new Random();

    //this constructor called when new genes are created
    TradeGene(GPNode gpo) { super(gpo); }

    //this constructor called when genes are cloned during reproduction
    TradeGene(TradeGene gpo) { super(gpo); }

    //called when genes are cloned during reproduction
    protected Object clone() { return new TradeGene(this); }

    //ID routine required for streams
    public byte isA() { return GPObject.USERGENEID; }

    //must override GPGene.createChild to create TradeGene instances
    public GPGene createChild(GPNode gpo) { return new TradeGene(gpo); }

    //called by TradeGP.evaluate() for main branch of each GP
    float evaluate(TradeVariables cfg, String stock, TradeGP gp) {
        
        float arg1, arg2, arg3, arg4, result;
        switch (node.value()) {
            
        case Trader.PRICE:
        	return cfg.trader.priceXDaysAgo(stock, 0);
        
        case Trader.INVERT:
        	return ( (TradeGene)get(0) ).evaluate(cfg, stock, gp) * -1;
        	
        case Trader.RANDOM:
            return rgen.nextInt(100)-50;
            
        case Trader.ZERO:
            return 0;
            
        case Trader.ONE: 
            return -50;
            
        case Trader.TWO: 
            return 50;

        case Trader.INC:
            return ( (TradeGene)get(0) ).evaluate(cfg, stock, gp) + 10;

        case Trader.DEC:
            result = ( (TradeGene)get(0) ).evaluate(cfg, stock, gp) - 10;  
            return result;

        case Trader.ADD:
            result = ( (TradeGene)get(0) ).evaluate(cfg, stock, gp) + ( (TradeGene)get(1) ).evaluate(cfg, stock, gp);
            return result;

        case Trader.SUB:
            result = ( (TradeGene)get(0) ).evaluate(cfg, stock, gp) - ( (TradeGene)get(1) ).evaluate(cfg, stock, gp);
            return result;

        case Trader.MAX:
            arg1 = ( (TradeGene)get(0) ).evaluate(cfg, stock, gp);
            arg2 = ( (TradeGene)get(1) ).evaluate(cfg, stock, gp);
            if (arg1 > arg2) return arg1;
            else return arg2;

        case Trader.MIN:
            arg1 = ( (TradeGene)get(0) ).evaluate(cfg, stock, gp);
            arg2 = ( (TradeGene)get(1) ).evaluate(cfg, stock, gp);
            if (arg1 < arg2) return arg1;
            else return arg2;

        case Trader.ITE:
            arg1 = ( (TradeGene)get(0) ).evaluate(cfg, stock, gp);
            arg2 = ( (TradeGene)get(1) ).evaluate(cfg, stock, gp);
            arg3 = ( (TradeGene)get(2) ).evaluate(cfg, stock, gp);
            arg4 = ( (TradeGene)get(3) ).evaluate(cfg, stock, gp);
            if (arg1 < arg2) return arg3;
            else return arg2;

        case Trader.PAST1M:
        	return cfg.trader.priceXDaysAgo(stock, 30);
        	
        case Trader.AVG1M:
        	float sum1m = 0;
        	for (int i = 0; i < 30; i++){
        		sum1m += cfg.trader.priceXDaysAgo(stock, i);
        	}
        	return sum1m/30;
        	
        case Trader.AVG1W:
        	float sum1w = 0;
        	for (int i = 0; i < 5; i++){
        		sum1w += cfg.trader.priceXDaysAgo(stock, i);
        	}
        	return sum1w/7;
        	
        default:
            throw new RuntimeException("Undefined function type "+node.value());
        }
    }
}
