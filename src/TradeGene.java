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
    int evaluate(TradeVariables cfg, TradeGP gp) {
        
        int arg1, arg2, arg3, result;
        switch (node.value()) {
            
        case Trader.RANDOM:
            return rgen.nextInt(3);
            
        case Trader.ZERO:
            return 0;
            
        case Trader.ONE: 
            return 1;
            
        case Trader.TWO: 
            return 2;

        case Trader.INC:
            return ( ( (TradeGene)get(0) ).evaluate(cfg, gp) + 1) % 3;

        case Trader.DEC:
            result = ( (TradeGene)get(0) ).evaluate(cfg, gp) - 1;  
            if (result<0) result = 2;
            return result % 3;

        case Trader.ADD:
            result = ( (TradeGene)get(0) ).evaluate(cfg, gp) + ( (TradeGene)get(1) ).evaluate(cfg, gp);
            if (result<0) result = 2;
            return result % 3;

        case Trader.SUB:
            result = ( (TradeGene)get(0) ).evaluate(cfg, gp) - ( (TradeGene)get(1) ).evaluate(cfg, gp);
            if (result<0) result = 2;
            return result % 3;

        case Trader.MAX:
            arg1 = ( (TradeGene)get(0) ).evaluate(cfg, gp);
            arg2 = ( (TradeGene)get(1) ).evaluate(cfg, gp);
            if (arg1 > arg2) return arg1;
            else return arg2;

        case Trader.MIN:
            arg1 = ( (TradeGene)get(0) ).evaluate(cfg, gp);
            arg2 = ( (TradeGene)get(1) ).evaluate(cfg, gp);
            if (arg1 < arg2) return arg1;
            else return arg2;

        case Trader.ITE:
            arg1 = ( (TradeGene)get(0) ).evaluate(cfg, gp);
            arg2 = ( (TradeGene)get(1) ).evaluate(cfg, gp);
            arg3 = ( (TradeGene)get(2) ).evaluate(cfg, gp);
            if (arg1 == 0) return arg3;
            else return arg2;

        default:
            throw new RuntimeException("Undefined function type "+node.value());
        }
    }
}
