package view;
/**
 * 
 * @author MinuteJ
 * @version 1.0.0
 */
import model.Car;
import model.Location;
import model.Simulator;

import java.awt.*;

public class GarageView extends AbstractView {

    private static final long serialVersionUID = -3032548983692121385L;
    private Dimension size;
    private Image carParkImage;
    private final Simulator simulator;

    /**
     * Constructor for objects of class CarPark
     */
    public GarageView(Simulator simulator) {
        this.simulator = simulator;
        simulator.addView(this);
        size = new Dimension(0, 0);
    }

    /**
     * Overridden. Tell the GUI manager how big we would like to be.
     */
    public Dimension getPreferredSize() {
        return new Dimension(900, 500);
    }

    /**
     * Overriden. The car park view component needs to be redisplayed. Copy the
     * internal image to screen.
     */
    public void paintComponent(Graphics g) {
        if (carParkImage == null) {
            return;
        }

        Dimension currentSize = getSize();
        if (size.equals(currentSize)) {
            g.drawImage(carParkImage, 0, 0, null);
        } else {
            // Rescale the previous image.
            g.drawImage(carParkImage, 0, 0, currentSize.width, currentSize.height, null);
        }
    }

    @Override
    public void updateView() {
        // Create a new car park image if the size has changed.
        if (!size.equals(getSize())) {
            size = getSize();
            carParkImage = createImage(size.width, size.height);
        }
        Graphics graphics = carParkImage.getGraphics();
        for (int floor = 0; floor < simulator.getGarageModel().getNumberOfFloors(); floor++) {
            for (int row = 0; row < simulator.getGarageModel().getNumberOfRows(); row++) {
                for (int place = 0; place < simulator.getGarageModel().getNumberOfPlaces(); place++) {
                    Location location = new Location(floor, row, place);
                    Car car = simulator.getGarageModel().getCarAt(location);
                    if (location.getReserved()) {
                        Color color = car == null ? Color.lightGray : car.getColor();
                        drawPlace(graphics, location, color);
                    } else {
                        Color color = car == null ? Color.white : car.getColor();
                        drawPlace(graphics, location, color);
                    }
                }
            }
        }
        repaint();
    }

    /**
     * Paint a place on this car park view in a given color.
     */
    private void drawPlace(Graphics graphics, Location location, Color color) {
        graphics.setColor(color);
        graphics.fillRect(location.getFloor() * 260 + (1 + (int) Math.floor(location.getRow() * 0.5)) * 75
                        + (location.getRow() % 2) * 20,
                location.getPlace() * 10,
                20 - 1,
                10 - 1); // TODO use dynamic size or constants
    }
}
