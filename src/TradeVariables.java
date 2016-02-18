// Tradearus implementation
// Copyright (c) 2013, Sherri Goings
//
// This program is free software; you can redistribute it and/or 
// modify it under the terms of version 2 of the GNU General Public 
// License as published by the Free Software Foundation.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

import java.io.*;
import java.util.Properties;
import gpjpp.*;

//extension of GPVariables for Tradearus-specific stuff

public class TradeVariables extends GPVariables{

	public StockData data;
	int[] startingDate = new int[]{2009,8,21};

	public float StartingFunds = 10000;
	
    //number of random Tradearus grids to test each genome on
    public int NumTestTraders = 10;
    
    //number of days trader runs on stocks 
    public int NumSteps = 80;

    public Trader trader;

    //public null constructor required for stream loading
    public TradeVariables() { /*gets default values*/ }

    //ID routine required for streams
    public byte isA() { return GPObject.USERVARIABLESID; }

    public void createTrader() {
        trader = new Trader(data, StartingFunds);
    }

    //get values from properties
    public void load(Properties props) {

        if (props == null)
            return;
        super.load(props);
        StartingFunds = (float)getInt(props, "StartingFunds", (int)StartingFunds);
        NumTestTraders = getInt(props, "NumTestTraders", NumTestTraders);
        NumSteps = getInt(props, "NumSteps", NumSteps);
        try {
			data = new StockData();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    //get values from a stream
    protected void load(DataInputStream is)
        throws ClassNotFoundException, IOException,
            InstantiationException, IllegalAccessException {

        super.load(is);
        StartingFunds = (float)is.readInt();
        NumTestTraders = is.readInt();
        NumSteps = is.readInt();
    }

    //save values to a stream
    protected void save(DataOutputStream os) throws IOException {

        super.save(os);
        os.writeFloat(StartingFunds);
        os.writeInt(NumTestTraders);
        os.writeInt(NumSteps);
    }

    //write values to a text file
    public void printOn(PrintStream os, GPVariables cfg) {

        super.printOn(os, cfg);
        os.println("StartingFunds           = "+StartingFunds);
        os.println("NumTestTraders              = "+NumTestTraders);
        os.println("NumSteps                  = "+NumSteps);
    }
}
