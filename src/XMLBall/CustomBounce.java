package XMLBall;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class CustomBounce {
	public static void main(String[] args) throws IOException {

		EventQueue.invokeLater(() -> {
			JFrame frame = new BounceFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			frame.setResizable(false);
		});

		try (ServerSocket s = new ServerSocket(6789)) {
			try (Socket incoming = s.accept()) {
				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();

				try (Scanner in = new Scanner(inStream, "UTF-8")) {
					PrintWriter out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);

					out.println("Hello MidTester, Type your command");
					out.println("Available commands are add, remove, start, stop, bye !");
					out.println("Command:");

					while (in.hasNextLine()) {
						String line = in.nextLine();
						line = line.toLowerCase();

						try {
							Scanner lineScan = new Scanner(line);

							String addLine = lineScan.next();
							String shapeLine = lineScan.next();
							String largeLine = lineScan.next();
							String directionLine = lineScan.next();
							String speedLine = lineScan.next();

							if (addLine.equals("add") && (shapeLine.equals("square") || shapeLine.equals("circle"))
									&& (largeLine.equals("small") || largeLine.equals("medium")
											|| largeLine.equals("large"))
									&& (directionLine.equals("vertical") || directionLine.equals("horizontal")
											|| directionLine.equals("diagonal"))
									&& (speedLine.equals("fast") || speedLine.equals("medium")
											|| speedLine.equals("slow"))) {

								BounceFrame.addBall(shapeLine, largeLine, directionLine, speedLine);
							}

							else {
								out.println(
										"Wrong command: add [square|circle] [small|medium|large] [diagonal|horizontal|vertical] [fast|medium|slow]");
								out.println("Command:");
							}
						}

						catch (Exception e) {
							if (line.trim().equals("remove"))
								BounceFrame.removeBall();

							else if (line.trim().equals("start"))
								BounceFrame.runBall();

							else if (line.trim().equals("stop"))
								BounceFrame.pauseBall();

							else if (line.trim().equals("bye"))
								System.exit(0);

							else {
								out.println(
										"Wrong command: add [square|circle] [small|medium|large] [diagonal|horizontal|vertical] [fast|medium|slow]");
								out.println("Command:");
							}
						}
					}
				}
			}
		}
	}
}

class BounceFrame extends JFrame {
	private static BallComponent comp;

	private JFileChooser chooser;
	private DocumentBuilder builder;

	public static final int DELAY = 1;

	private static String shape = "Circle";
	private static String large = "Large";
	private static String direction = "Diagonal";
	private static String speed = "Slow";

	private String loadShape;
	private String loadDirection;
	private String loadColor;
	private int loadWidth;
	private int loadHeight;
	private String loadSpeed;
	private String loadStatus;
	private int loadX;
	private int loadY;

	public BounceFrame() {
		setTitle("XML Ball");

		chooser = new JFileChooser();
		comp = new BallComponent();

		JPanel combinePanel = new JPanel();
		combinePanel.setLayout(new GridLayout(2, 1));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		JPanel buttonPanel2 = new JPanel();
		buttonPanel2.setLayout(new FlowLayout());

		addButton(buttonPanel, "Shape: " + shape, event -> changeShape());
		addButton(buttonPanel, "Size: " + large, event -> changeLarge());
		addButton(buttonPanel, "Direction: " + direction, event -> changeDirection());
		addButton(buttonPanel, "Speed: " + speed, event -> changeSpeed());
		addButton(buttonPanel, "Add Ball", event -> addBall());
		addButton(buttonPanel2, "Remove", event -> removeBall());
		addButton(buttonPanel2, "Start ", event -> runBall());
		addButton(buttonPanel2, "Stop", event -> pauseBall());
		addButton(buttonPanel2, "Close", event -> System.exit(0));

		combinePanel.add(buttonPanel);
		combinePanel.add(buttonPanel2);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menu = new JMenu("Export");
		menuBar.add(menu);

		JMenu menu2 = new JMenu("Import");
		menuBar.add(menu2);

		JMenuItem saveItem = new JMenuItem("Export with DOM/XSLT");
		menu.add(saveItem);
		saveItem.addActionListener(event -> saveDocument());

		JMenuItem saveStAXItem = new JMenuItem("Export with StAX");
		menu.add(saveStAXItem);
		saveStAXItem.addActionListener(event -> saveStAX());

		JMenuItem importItem = new JMenuItem("Load XML");
		menu2.add(importItem);
		importItem.addActionListener(event -> openFile());

		add(combinePanel, BorderLayout.SOUTH);
		add(comp, BorderLayout.CENTER);
		pack();
	}

	public void openFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("dom"));
		chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("XML files", "xml"));
		int r = chooser.showOpenDialog(this);
		if (r != JFileChooser.APPROVE_OPTION)
			return;
		final File file = chooser.getSelectedFile();

		new SwingWorker<Document, Void>() {
			protected Document doInBackground() throws Exception {
				if (builder == null) {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					builder = factory.newDocumentBuilder();
				}
				return builder.parse(file);
			}

			protected void done() {
				while (comp.getBalls().size() > 0) {
					removeBall();
				}

				try {
					Document doc = get();
					NodeList nodeList = doc.getElementsByTagName("ball");
					for (int x = 0, size = nodeList.getLength(); x < size; x++) {
						loadShape = nodeList.item(x).getAttributes().getNamedItem("shape").getNodeValue().toString();
						loadDirection = nodeList.item(x).getAttributes().getNamedItem("direction").getNodeValue()
								.toString();
						loadColor = nodeList.item(x).getAttributes().getNamedItem("fill").getNodeValue().toString();
						loadWidth = Integer
								.parseInt(nodeList.item(x).getAttributes().getNamedItem("width").getNodeValue());
						loadHeight = Integer
								.parseInt(nodeList.item(x).getAttributes().getNamedItem("height").getNodeValue());
						loadSpeed = nodeList.item(x).getAttributes().getNamedItem("speed").getNodeValue().toString();
						loadStatus = nodeList.item(x).getAttributes().getNamedItem("status").getNodeValue().toString();
						loadX = Integer.parseInt(nodeList.item(x).getAttributes().getNamedItem("x").getNodeValue());
						loadY = Integer.parseInt(nodeList.item(x).getAttributes().getNamedItem("y").getNodeValue());

						Ball ball = new Ball(loadShape, loadDirection, loadColor, loadWidth, loadHeight, loadSpeed,
								loadStatus, loadX, loadY);

						if (loadStatus.equals("run"))
							ball.run(comp, DELAY);

						comp.add(ball);
						comp.repaint();
					}
					validate();
				} catch (Exception e) {
					System.out.print(e);
				}
			}
		}.execute();
	}

	public void saveDocument() {
		try {
			if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
				return;
			File file = chooser.getSelectedFile();
			Document doc = comp.buildDocument();
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			t.transform(new DOMSource(doc), new StreamResult(Files.newOutputStream(file.toPath())));
		} catch (TransformerException | IOException ex) {
			ex.printStackTrace();
		}
	}

	public void saveStAX() {
		if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		File file = chooser.getSelectedFile();
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		try {
			XMLStreamWriter writer = factory.createXMLStreamWriter(Files.newOutputStream(file.toPath()));
			try {
				comp.writeDocument(writer);
			} finally {
				writer.close();
			}
		} catch (XMLStreamException | IOException ex) {
			ex.printStackTrace();
		}
	}

	public void addButton(Container c, String title, ActionListener listener) {
		JButton button = new JButton(title);
		c.add(button);

		if (title.equals("Shape: " + shape)) {
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					button.setText("Shape: " + shape);
				}
			});
		}

		if (title.equals("Size: " + large)) {
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					button.setText("Size: " + large);
				}
			});
		}

		if (title.equals("Direction: " + direction)) {
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					button.setText("Direction: " + direction);
				}
			});
		}

		if (title.equals("Speed: " + speed)) {
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					button.setText("Speed: " + speed);
				}
			});
		}

		button.addActionListener(listener);
	}

	public static void addBall() {
		Ball ball = new Ball(shape.toLowerCase(), large.toLowerCase(), direction.toLowerCase(), speed.toLowerCase());
		comp.add(ball);
		comp.repaint();

		if (Ball.getNewSpawnMove() == true)
			runBall();
	}

	public static void addBall(String shapeLine, String largeLine, String directionLine, String speedLine) {
		Ball ball = new Ball(shapeLine, largeLine, directionLine, speedLine);
		comp.add(ball);
		comp.repaint();

		if (Ball.getNewSpawnMove() == true)
			runBall();
	}

	public static void changeShape() {
		if (shape == "Circle")
			shape = "Square";

		else
			shape = "Circle";
	}

	public static void changeLarge() {
		if (large == "Large")
			large = "Medium";

		else if (large == "Medium")
			large = "Small";

		else
			large = "Large";
	}

	public static void changeDirection() {
		if (direction == "Diagonal")
			direction = "Horizontal";

		else if (direction == "Horizontal")
			direction = "Vertical";

		else
			direction = "Diagonal";
	}

	public static void changeSpeed() {
		if (speed == "Slow")
			speed = "Medium";

		else if (speed == "Medium")
			speed = "Fast";

		else
			speed = "Slow";
	}

	public static void removeBall() {
		comp.remove();
		comp.repaint();
	}

	public static void pauseBall() {
		Ball.setNewSpawnMove(false);
		java.util.List<Ball> balls = comp.getBalls();
		for (Ball b : balls) {
			b.setMoveStatus(false);
		}
	}

	public static void runBall() {
		Ball.setNewSpawnMove(true);
		java.util.List<Ball> balls = comp.getBalls();
		for (Ball b : balls) {
			if (b.getMoveStatus() == false) {
				b.setMoveStatus(true);
				b.run(comp, DELAY);
			}
		}
	}
}
