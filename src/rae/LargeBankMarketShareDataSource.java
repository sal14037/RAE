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
public class LargeBankMarketShareDataSource implements AggregateDataSource {

	@Override
	public String getId() {
		return "Large Banks Market Share";
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
		Iterator<Bank> iter = context.getObjects(Bank.class).iterator();
		int largeAssets = 0;
		int totalAssets = 0;
		while (iter.hasNext()) {
			Bank b = iter.next();
			totalAssets += b.getAssets();
			if (b.getAssets() >= 100000) {
				largeAssets += b.getAssets();
			}
		}
		return ((double) largeAssets / totalAssets) * 100;
	}

	@Override
	public void reset() {
	}
}
