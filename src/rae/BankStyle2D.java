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
		if (dimensions >= 100000)
			return new Color(200, 0, 0);
		else if (dimensions >= 40000)
			return new Color(0, 200, 0);
		return new Color(0, 0, 200);

	}

	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		int dimensions = ((Bank) agent).getAssets() / 1000;
		spatial = shapeFactory.createRectangle(dimensions, dimensions);
		if (dimensions > 300) {
			spatial = shapeFactory.createRectangle(500, 500);
		}
		if (dimensions <= 10) {
			spatial = shapeFactory.createRectangle(10, 10);
		}
		return spatial;
	}
}
