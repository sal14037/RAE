/**
 * 
 */
package rae;

import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.data2.AggregateDataSource;

/**
 * @author Thomas Salzer
 *
 */
public class LargeBankCountDataSource implements AggregateDataSource {

	@Override
	public String getId() {
		return "Large Bank Count";
	}

	@Override
	public Class<?> getDataType() {
		return double.class;
	}

	@Override
	public Class<?> getSourceType() {
		return Context.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object get(Iterable<?> objs, int size) {
		Context context = (Context) objs.iterator().next();
		double numCells = context.size() - 1;
		Iterator<Bank> iter = context.getObjects(Bank.class).iterator();
		int numBanks = 0;
		while (iter.hasNext()) {
			Bank b = iter.next();
			if (b.getAssets() > 100000) {
				numBanks++;
			}
		}
		return numBanks;
	}

	@Override
	public void reset() {
	}
}
