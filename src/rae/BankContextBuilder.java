package rae;

import java.util.Iterator;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

/**
 * 
 * @author Thomas Salzer
 *
 */
public class BankContextBuilder extends DefaultContext implements ContextBuilder<Bank> {

	private final static Logger LOGGER = Logger.getLogger(BankContextBuilder.class);

	private int tick;

	@Override
	public Context build(Context<Bank> context) {
		Parameters p = RunEnvironment.getInstance().getParameters();

		int gridWidth = (Integer) p.getValue("gridWidth");
		int gridHeight = (Integer) p.getValue("gridHeight");
		int initialBanks = (Integer) p.getValue("initialBanks");
		this.tick = 0;

		NetworkBuilder<Bank> netBuilder = new NetworkBuilder<Bank>("Bank network", context, true);
		netBuilder.buildNetwork();

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Bank> grid = gridFactory.createGrid("Grid", context, GridBuilderParameters
				.singleOccupancy2D(new RandomGridAdder(), new WrapAroundBorders(), gridWidth, gridHeight));

		for (int i = 0; i < initialBanks; i++) {
			context.add(new Bank(i));
		}

		if (RunEnvironment.getInstance().isBatch()) {
			RunEnvironment.getInstance().endAt(50);
		}

		scheduleGlobalStep();

		return context;
	}

	private void scheduleGlobalStep() {
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters params = ScheduleParameters.createRepeating(1, 1, 1);
		schedule.schedule(params, this, "globalStep");
	}

	public void globalStep() {
		Parameters p = RunEnvironment.getInstance().getParameters();

		int startTickDisruption = (int) p.getValue("start_tick_disruption");
		Iterator<Bank> iter = RunState.getInstance().getMasterContext().getObjects(Bank.class).iterator();
		System.out.println(tick);
		System.out.println(startTickDisruption);
		double fluctuation = generateFluctuation();
		if (tick == startTickDisruption && tick != 0) {
			fluctuation = generateDisruption();
			LOGGER.info("CRASH!");
		}
		while (iter.hasNext()) {
			Bank b = iter.next();
			b.marketFluctuation(fluctuation);
		}
		tick++;
	}

	public double generateFluctuation() {
		Parameters p = RunEnvironment.getInstance().getParameters();
		double maxPositiveFluctuation = (Double) p.getValue("max_positive_fluctuation");
		double maxNegativeFluctuation = (Double) p.getValue("max_negative_fluctuation");

		double fluctuation = 1;
		int direction = RandomHelper.nextIntFromTo(-1, 1);
		if (direction == -1) {
			double fluctuationMinus = RandomHelper.nextDoubleFromTo(0.99999, maxNegativeFluctuation);
			fluctuation = fluctuationMinus;
			LOGGER.info("Market - " + fluctuationMinus);
		} else {
			double fluctuationPlus = RandomHelper.nextDoubleFromTo(1.00001, maxPositiveFluctuation);
			fluctuation = fluctuationPlus;
			LOGGER.info("Market + " + fluctuationPlus);
		}
		return fluctuation;
	}

	public double generateDisruption() {
		Parameters p = RunEnvironment.getInstance().getParameters();
		double magnitudeDisruption = (Double) p.getValue("magnitude_disruption");
		return magnitudeDisruption;
	}

}
