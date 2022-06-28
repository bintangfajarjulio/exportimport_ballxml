package XMLBall;

import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class Ball {
	Random rand = new Random();

	private int x = rand.nextInt(880);
	private int y = rand.nextInt(560);

	private int dx;
	private int dy;

	private boolean moveStatus = false;
	private static boolean newSpawnMove = false;

	private boolean verticalMove;
	private boolean horizontalMove;

	Color color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));

	private String shape;
	private String speedStatus;

	private int XSIZE;
	private int YSIZE;

	Ball(String shape, String direction, String color, int width, int height, String speed, String status, int x,
			int y) {
		this.shape = shape;

		if (direction.equals("Diagonal"))
			setDiagonalMove();

		if (direction.equals("Vertical"))
			setVerticalMove();

		if (direction.equals("Horizontal"))
			setHorizontalMove();

		this.color = Color.decode(color);

		XSIZE = width;
		YSIZE = height;

		speedStatus = speed;

		if (speedStatus.equals("fast")) {
			dx = 3;
			dy = dx;
		}

		if (speedStatus.equals("medium")) {
			dx = 2;
			dy = dx;
		}

		if (speedStatus.equals("slow")) {
			dx = 1;
			dy = dx;
		}

		if (status.equals("run"))
			moveStatus = true;

		if (status.equals("stop"))
			moveStatus = false;

		this.x = x;
		this.y = y;
	}

	Ball(String shape, String large, String direction, String speed) {
		this.shape = shape;

		if (large.equals("large")) {
			XSIZE = 40;
			YSIZE = 40;
		}

		if (large.equals("medium")) {
			XSIZE = 25;
			YSIZE = 25;
		}

		if (large.equals("small")) {
			XSIZE = 15;
			YSIZE = 15;
		}

		if (direction.equals("diagonal"))
			setDiagonalMove();

		if (direction.equals("vertical"))
			setVerticalMove();

		if (direction.equals("horizontal"))
			setHorizontalMove();

		speedStatus = speed;

		if (speedStatus.equals("fast")) {
			dx = 3;
			dy = dx;
		}

		if (speedStatus.equals("medium")) {
			dx = 2;
			dy = dx;
		}

		if (speedStatus.equals("slow")) {
			dx = 1;
			dy = dx;
		}
	}

	public String getSpeedStatus() {
		return speedStatus;
	}

	public String getShape() {
		return shape;
	}

	public Color getColor() {
		return color;
	}

	public void move(Rectangle2D bounds) {
		if (horizontalMove)
			x += dx;

		if (verticalMove)
			y += dy;

		if (x < bounds.getMinX()) {
			x = (int) bounds.getMinX();
			dx = -dx;
		}

		if (x + XSIZE >= bounds.getMaxX()) {
			x = (int) bounds.getMaxX() - XSIZE;
			dx = -dx;
		}

		if (y < bounds.getMinY()) {
			y = (int) bounds.getMinY();
			dy = -dy;
		}

		if (y + YSIZE >= bounds.getMaxY()) {
			y = (int) bounds.getMaxY() - YSIZE;
			dy = -dy;
		}
	}

	public Ellipse2D getEllipse() {
		return new Ellipse2D.Double(x, y, XSIZE, YSIZE);
	}

	public Rectangle2D getRectangle() {
		return new Rectangle2D.Double(x, y, XSIZE, YSIZE);
	}

	public void setVerticalMove() {
		verticalMove = true;
		horizontalMove = false;
	}

	public void setHorizontalMove() {
		verticalMove = false;
		horizontalMove = true;
	}

	public void setDiagonalMove() {
		verticalMove = true;
		horizontalMove = true;
	}

	public boolean getMoveStatus() {
		return moveStatus;
	}

	public void setMoveStatus(boolean moveStatus) {
		this.moveStatus = moveStatus;
	}

	public static boolean getNewSpawnMove() {
		return newSpawnMove;
	}

	public static void setNewSpawnMove(boolean newSpawnMove) {
		Ball.newSpawnMove = newSpawnMove;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getXSIZE() {
		return XSIZE;
	}

	public int getYSIZE() {
		return YSIZE;
	}

	public String getDirection() {
		String direction;

		if (verticalMove == true && horizontalMove == false) {
			direction = "Vertical";
		}

		else if (verticalMove == false && horizontalMove == true) {
			direction = "Horizontal";
		}

		else {
			direction = "Diagonal";
		}

		return direction;
	}

	public String getRunStatus() {
		String status;

		if (moveStatus) {
			status = "run";
		}

		else {
			status = "stop";
		}

		return status;
	}

	public void run(BallComponent comp, int DELAY) {
		Runnable r = () -> {
			try {
				while (moveStatus) {
					move(comp.getBounds());
					comp.repaint();
					Thread.sleep(DELAY);
				}
			}

			catch (InterruptedException e) {

			}
		};

		Thread t = new Thread(r);
		t.start();
	}
}