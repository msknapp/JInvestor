package com.KnappTech.sr.ctrl.opt;

import java.util.ArrayList;

import com.KnappTech.sr.model.comp.Companies;
import com.KnappTech.sr.model.user.Investor;

public class SimulatedPortfolios extends ArrayList<SimulatedPortfolio> {
	private static final long serialVersionUID = 201007122229L;
	
	public SimulatedPortfolios() {
		
	}
	
	public SimulatedPortfolios(Companies companies,Investor investor, int count) {
		SimulatedPortfolio portfolio = new SimulatedPortfolio();
		SimulatedPositions positions = new SimulatedPositions(companies);
		portfolio.setShares(positions);
		portfolio.evaluate(investor);
		add(portfolio);
		addAll(portfolio.spawnChildPortfolios(1, 9, investor));
	}

	public SimulatedPortfolios getBestTenGuesses() {
		return getBestPortfolios(10);
	}
	
	public SimulatedPortfolios getBestPortfolios(int count) {
		SimulatedPortfolios bestGuesses = new SimulatedPortfolios();
		for (SimulatedPortfolio guess : this) {
			if (bestGuesses.size()<count) {
				bestGuesses.add(guess);
			} else {
				SimulatedPortfolio lostGuess = bestGuesses.findWorstPortfolio();
				if (guess.isBetterThan(lostGuess)) {
					bestGuesses.remove(lostGuess);
					bestGuesses.add(guess);
				}
			}
		}
		return bestGuesses;
	}


	public SimulatedPortfolio findWorstPortfolio() {
		SimulatedPortfolio badGuess = get(0);
		for (SimulatedPortfolio guess : this) {
			if (guess.getLowEstimate()<badGuess.getLowEstimate()) {
				badGuess = guess;
			}
		}
		return badGuess;
	}

	public SimulatedPortfolio findBestPortfolio() {
		SimulatedPortfolio goodGuess = get(0);
		for (SimulatedPortfolio guess : this) {
			if (guess.getLowEstimate()>goodGuess.getLowEstimate()) {
				goodGuess = guess;
			}
		}
		return goodGuess;
	}

	public void retainBestTen() {
		retainBest(10);
	}
	
	public void retainBestHundred() {
		retainBest(100);
	}
	
	public void retainBest(int count) {
		SimulatedPortfolios best = getBestPortfolios(count);
		retainAll(best);
	}
	
	public double findRangeOfPortfolioEstimates() {
		SimulatedPortfolio badGuess = findWorstPortfolio();
		SimulatedPortfolio goodGuess = findBestPortfolio();
		return goodGuess.getLowEstimate()-badGuess.getLowEstimate();
	}
	
	public SimulatedPortfolios spawnChildPortfolios(double maxOffset,
			int factor,int useTopGuesses,Investor investor) {
		SimulatedPortfolios newGuesses = new SimulatedPortfolios();
		SimulatedPortfolios bestGuesses = getBestPortfolios(useTopGuesses);
		for (SimulatedPortfolio guess : bestGuesses) {
			newGuesses.addAll(guess.spawnChildPortfolios(maxOffset, factor,investor));
		}
		return newGuesses;
	}
	
	public SimulatedPortfolios spawnChildPortfolios(double maxOffset,int factor,Investor investor) {
		SimulatedPortfolios newGuesses = new SimulatedPortfolios();
		for (SimulatedPortfolio guess : this) {
			newGuesses.addAll(guess.spawnChildPortfolios(maxOffset, factor,investor));
		}
		return newGuesses;
	}

	public void evaluate(Investor investor) {
		for (SimulatedPortfolio p : this) {
			p.evaluate(investor);
		}
	}
	
	public double findMaxDistanceBetweenPortfolios() {
		double maxDistance = -1;
		for (int i = 0;i<size();i++) {
			SimulatedPortfolio guess1 = get(i);
			for (int j = i+1;j<size();j++) {
				SimulatedPortfolio guess2 = get(j);
				maxDistance = Math.max(guess1.findDistance(guess2),maxDistance);
			}
		}
		return maxDistance;
	}
	
	public double findMaxPriceDistanceBetweenTwoPositions() {
		double maxDistance = -1;
		for (int i = 0;i<size();i++) {
			SimulatedPortfolio guess1 = get(i);
			for (int j = i+1;j<size();j++) {
				SimulatedPortfolio guess2 = get(j);
				maxDistance = Math.max(
					guess1.findMaxValueDifferenceBetweenTwoPositions(guess2),maxDistance);
			}
		}
		return maxDistance;
	}
}
