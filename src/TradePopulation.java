
import gpjpp.*;

//extend GPPopulation to create lawn mowing trees
//doesn't really need to be public, but is made so for consistency

public class TradePopulation extends GPPopulation {

    //this constructor called when new populations are created
    TradePopulation(GPVariables gpVar, GPAdfNodeSet adfNs) {
        super(gpVar, adfNs);
    }

    //populations are not cloned in standard runs
    //TradePopulation(TradePopulation gpo) { super(gpo); }
    //protected Object clone() { return new TradePopulation(this); }

    //ID routine required for streams
    public byte isA() { return GPObject.USERPOPULATIONID; }

    //must override GPPopulation.createGP to create TartGP instances
    public GP createGP(int numOfGenes) { return new TradeGP(numOfGenes); }
}
