package XMLBall;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.geom.*;
import javax.xml.parsers.*;
import javax.xml.stream.*;
import org.w3c.dom.*;

public class BallComponent extends JPanel {
	Random rand = new Random();

	private static final int DEFAULT_WIDTH = 920;
	private static final int DEFAULT_HEIGHT = 600;

	private java.util.List<Ball> balls = new ArrayList<>();
	private DocumentBuilder builder;

	public BallComponent() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public Document buildDocument() {
		Document doc = builder.newDocument();
		Element ballsElement = doc.createElement("balls");
		doc.appendChild(ballsElement);

		for (Ball b : balls) {
			Color c = b.getColor();
			Element element = doc.createElement("ball");
			element.setAttribute("shape", "" + b.getShape());
			element.setAttribute("x", "" + b.getX());
			element.setAttribute("y", "" + b.getY());
			element.setAttribute("width", "" + b.getXSIZE());
			element.setAttribute("height", "" + b.getYSIZE());
			element.setAttribute("direction", "" + b.getDirection());
			element.setAttribute("speed", "" + b.getSpeedStatus());
			element.setAttribute("status", "" + b.getRunStatus());
			element.setAttribute("fill", String.format("#%06x", c.getRGB() & 0xFFFFFF));
			ballsElement.appendChild(element);
		}

		return doc;
	}

	public void writeDocument(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartDocument();
		writer.writeStartElement("balls");

		for (Ball b : balls) {
			Color c = b.getColor();
			writer.writeEmptyElement("ball");
			writer.writeAttribute("shape", "" + b.getShape());
			writer.writeAttribute("x", "" + b.getX());
			writer.writeAttribute("y", "" + b.getY());
			writer.writeAttribute("width", "" + b.getXSIZE());
			writer.writeAttribute("height", "" + b.getYSIZE());
			writer.writeAttribute("direction", "" + b.getDirection());
			writer.writeAttribute("speed", "" + b.getSpeedStatus());
			writer.writeAttribute("status", "" + b.getRunStatus());
			writer.writeAttribute("fill", String.format("#%06x", c.getRGB() & 0xFFFFFF));
		}

		writer.writeEndDocument();
	}

	public void add(Ball ball) {
		balls.add(ball);
	}

	public void remove() {
		if (balls.size() > 0)
			balls.remove(balls.size() - 1);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		BasicStroke stroke = new BasicStroke(5);

		for (Ball b : balls) {
			g2.setPaint(b.getColor());
			g2.setStroke(stroke);

			if (b.getShape().equals("circle"))
				g2.draw(b.getEllipse());

			if (b.getShape().equals("square"))
				g2.draw(b.getRectangle());
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public java.util.List<Ball> getBalls() {
		return balls;
	}
}
