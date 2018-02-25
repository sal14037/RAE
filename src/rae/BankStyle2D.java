package rae;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;

public class BankStyle2D extends DefaultStyleOGL2D {

	private ShapeFactory2D shapeFactory;

	@Override
	public void init(ShapeFactory2D factory) {
		this.shapeFactory = factory;
	}

	@Override
	public Color getColor(Object agent) {
		int dimensions = ((Bank) agent).getAssets();
		if (dimensions >= 10000)
			return new Color(200, 0, 0);
		else if (dimensions >= 5000)
			return new Color(0, 200, 0);
		return new Color(0, 0, 200);

	}

	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		int dimensions = ((Bank) agent).getAssets() / 100;
		spatial = shapeFactory.createRectangle(dimensions, dimensions);
		return spatial;
	}
}
