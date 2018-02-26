package rae;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;

/**
 * 
 * @author Thomas Salzer
 *
 */
public class Bank {

	private final static Logger LOGGER = Logger.getLogger(Bank.class);

	private final int id;

	private int mergeCD;
	private int mergeTime;

	/**
	 * Financial Structure
	 */
	private int assets;
	private int customerLoans;
	private int securities;

	// Share of non performing loans, 0-100
	private int npl;

	private int liabilites;
	private int deposits;
	private int equity;

	private int profit;

	private int revenue;
	private int variableCost;
	private int fixedCost;

	private boolean bailout;

	public Bank(int id) {
		Parameters p = RunEnvironment.getInstance().getParameters();

		this.id = id;

		int mergeTimeMin = (Integer) p.getValue("mergeTimeMin");
		int mergeTimeMax = (Integer) p.getValue("mergeTimeMax");
		this.mergeTime = RandomHelper.nextIntFromTo(mergeTimeMin, mergeTimeMax);

		this.revenue = RandomHelper.nextIntFromTo(1, 100);
		this.fixedCost = RandomHelper.nextIntFromTo(1, 50) + ((Integer) p.getValue("fixedCost"));
		this.variableCost = RandomHelper.nextIntFromTo(1, 50);

		this.profit = revenue - (variableCost + fixedCost);

		// Other distributions Logarithmic, etc.
		this.customerLoans = RandomHelper.nextIntFromTo(1, 500);
		this.securities = RandomHelper.nextIntFromTo(1, 500);
		this.assets = customerLoans + securities;

		this.npl = RandomHelper.nextIntFromTo(1, 25);

		int shareOfEquity = RandomHelper.nextIntFromTo(1, 50);
		this.equity = assets * shareOfEquity / 100;
		this.deposits = assets * (100 - shareOfEquity) / 100;
		this.liabilites = equity + deposits;

		this.bailout = false;

		mergeCD = mergeTime;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		revalue();
		if (!this.isBankcrupt() && !bailout) {
			if (mergeCD == 0) {
				List<Bank> possibleMergers = findPossibleMergers();
				if (possibleMergers.size() > 0) {
					Object obj = possibleMergers.get(0);
					merge((Bank) obj);
					RunState.getInstance().getMasterContext().remove(obj);
					LOGGER.info("MERGE Bank (" + this.getId() + ") & Bank (" + ((Bank) obj).getId() + ")");
				}
				mergeCD = mergeTime;
			} else {
				mergeCD--;
			}
		} else {
			this.bailout = true;
		}

	}

	public void marketFluctuation(double fluctuation) {
		this.setNPL((int) (this.getNPL() * (1 / fluctuation)));
		this.setSecurities((int) (this.getSecurities() * fluctuation));
	}

	private void revalue() {
		this.assets = customerLoans + securities;
		this.liabilites = equity + deposits;
		this.profit = revenue - (variableCost + fixedCost);
	}

	private void merge(Bank b) {
		this.setRevenue(this.revenue + b.getRevenue());
		this.setVariableCost(RandomHelper.nextIntFromTo(this.getVariableCost(), b.getVariableCost()));

		this.setSecurities(this.getSecurities() + b.getSecurities());
		this.setCustomerLoans(this.getCustomerLoans() + b.getCustomerLoans());

		this.setEquity(this.getEquity() + b.getEquity());
		this.setDeposits(this.getDeposits() + b.getDeposits());

		this.setProfit(this.getRevenue() - (this.getVariableCost() + this.getFixedCost()));
		revalue();
	}

	private List<Bank> getAllBanks() {
		Iterator<Bank> iter = RunState.getInstance().getMasterContext().getObjects(Bank.class).iterator();
		List<Bank> allBanks = new ArrayList<Bank>();
		while (iter.hasNext()) {
			Bank b = iter.next();
			if (!b.equals(this)) {
				allBanks.add(b);
			}
		}
		return allBanks;
	}

	private List<Bank> findPossibleMergers() {
		List<Bank> possibleMergers = new ArrayList<Bank>();
		for (Bank bank : getAllBanks()) {
			if (this.getAssets() > bank.getAssets() && calculateSynergy(bank) > 0) {
				possibleMergers.add(bank);
			}
		}
		Collections.shuffle(possibleMergers);
		return possibleMergers;
	}

	private int calculateSynergy(Bank bank) {
		int synergy = 0;
		if (this.variableCost < bank.getVariableCost()) {
			synergy++;
		}
		if ((this.profit + bank.getProfit()) > this.profit) {
			synergy++;
		}
		if ((bank.getRevenue() - bank.getVariableCost()) > 0) {
			synergy++;
		}
		if (bank.debtEquityRatio() > 200) {
			synergy--;
		}
		if (bank.getNPL() > 60) {
			synergy--;
		}
		return synergy;
	}

	public boolean isBankcrupt() {
		if (this.getAssets() < 0)
			return true;
		if (this.getEquity() < 0)
			return true;
		if (this.getNPL() > 80)
			return true;
		return false;
	}

	public int getProfit() {
		return profit;
	}

	public void setProfit(int profit) {
		this.profit = profit;
	}

	public int getAssets() {
		return assets;
	}

	public void setAssets(int assets) {
		this.assets = assets;
	}

	public int getLiabilites() {
		return liabilites;
	}

	public void setLiabilites(int liabilites) {
		this.liabilites = liabilites;
	}

	public int getRevenue() {
		return revenue;
	}

	public void setRevenue(int revenue) {
		this.revenue = revenue;
	}

	public int getFixedCost() {
		return fixedCost;
	}

	public void setFixedCost(int fixedCost) {
		this.fixedCost = fixedCost;
	}

	public int getVariableCost() {
		return variableCost;
	}

	public void setVariableCost(int variableCost) {
		this.variableCost = variableCost;
	}

	public int getCustomerLoans() {
		return customerLoans;
	}

	public void setCustomerLoans(int customerLoans) {
		this.customerLoans = customerLoans;
	}

	public int getSecurities() {
		return securities;
	}

	public void setSecurities(int securities) {
		this.securities = securities;
	}

	public int getNPL() {
		return npl;
	}

	public void setNPL(int nPL) {
		npl = nPL;
	}

	public int getDeposits() {
		return deposits;
	}

	public void setDeposits(int deposits) {
		this.deposits = deposits;
	}

	public int getEquity() {
		return equity;
	}

	public void setEquity(int equity) {
		this.equity = equity;
	}

	public int getId() {
		return id;
	}

	public int debtEquityRatio() {
		return this.deposits / this.equity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bank other = (Bank) obj;
		return other.id == id;
	}
}
