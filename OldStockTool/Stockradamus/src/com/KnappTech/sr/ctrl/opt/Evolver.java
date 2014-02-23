package com.KnappTech.sr.ctrl.opt;

import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.user.Investor;

public class Evolver {
	private static boolean printToConsole = true;
	
	private SimulatedPortfolios simulatedPortfolios = new SimulatedPortfolios();
	private Investor investor = null;
	
	private int minIterations = 1000;
	private int maxIterations = 10000;
	private double convergenceSpan = 0.1;
	
	private byte maxMinutes = 30;
	
	private double maxInvestmentInSingleStock = 0;
	
	private OffsetDeterminer od = new OffsetDeterminer();
	
	private int iterations = 0;
	
	public Evolver(Companies companies,Investor investor) {
		this.investor = investor;
		double d = investor.getMaxInvestedPercentInSingleStock();
		double m = investor.getDollarsAvailable()*d;
		maxInvestmentInSingleStock = m;
		
		// initialize the guesses with the companies.
		System.out.println("Producing the initial guesses.");
		simulatedPortfolios = new SimulatedPortfolios(companies,investor,10);
		System.out.println("Finished producing the initial guesses.");
	}
	
	public SimulatedPortfolio solve() {
		int factor = 10;
		double maxOffset = 100;
		long startTime = System.currentTimeMillis();
		long maxEndTime = startTime+(60*1000*maxMinutes);
		double bestFinalValue = -1;
		int iterationsAtFinalValue = 0;
		while (iterations<maxIterations && 
				(!hasConverged() || iterations<minIterations)) {
			double maxDistance = simulatedPortfolios.findMaxPriceDistanceBetweenTwoPositions();
			maxOffset = od.determineOffset(iterations, maxDistance,maxInvestmentInSingleStock);
			SimulatedPortfolios newGuesses = 
				simulatedPortfolios.spawnChildPortfolios(maxOffset, factor,investor);
			simulatedPortfolios.addAll(newGuesses);
			simulatedPortfolios.retainBestTen();
			
			iterations++;
			if (iterations%30==0 && iterations>0) {
				if (printToConsole) {
					System.out.println("The solver has currently performed "+iterations+" iterations, " +
							"the best solution so far is below:");
					simulatedPortfolios.findBestPortfolio().printDetails(investor);
					System.out.println("The solver will continue searching for a better answer.");
				}
			}
			
			if (System.currentTimeMillis()>maxEndTime) {
				if (printToConsole) {
					System.out.println("The solver has run for "+maxMinutes+ ", so it " +
							"will stop now and deliver its best guess.");
				}
				break;
			}
			
			double newFinalValue = simulatedPortfolios.findBestPortfolio().getFinalValue();
			if (newFinalValue==bestFinalValue) {
				iterationsAtFinalValue++;
			} else {
				iterationsAtFinalValue=0;
			}
			if (iterationsAtFinalValue>60) {
				break;
			}
		}
		System.out.println("Problem solved after "+iterations+" iterations, max offset is "+maxOffset);
		SimulatedPortfolio bestGuess = simulatedPortfolios.findBestPortfolio();
		bestGuess.printDetails(investor);
		return bestGuess;
	}

	private boolean hasConverged() {
		if (simulatedPortfolios.size()>=10) {
			SimulatedPortfolios bestTen = simulatedPortfolios.getBestTenGuesses();
			double span = bestTen.findRangeOfPortfolioEstimates();
			double distanceSpan = bestTen.findMaxDistanceBetweenPortfolios();
			return (span<convergenceSpan || distanceSpan<5);
		}
		return false;
	}

	public void setMaxMinutes(byte maxMinutes) {
		this.maxMinutes = maxMinutes;
	}

	public byte getMaxMinutes() {
		return maxMinutes;
	}

	public static void setPrintToConsole(boolean printToConsole) {
		Evolver.printToConsole = printToConsole;
	}

	public static boolean isPrintToConsole() {
		return printToConsole;
	}

	public void setOd(OffsetDeterminer od) {
		this.od = od;
	}

	public OffsetDeterminer getOd() {
		return od;
	}
}