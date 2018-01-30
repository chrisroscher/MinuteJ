package model;

import model.ViewModel;
import view.AbstractView;

import java.util.Random;

public class Simulator extends ViewModel implements Runnable {

	private static final String AD_HOC = "1";
	private static final String PASS = "2";

	private CarQueue entranceCarQueue;
	private CarQueue entrancePassQueue;
	private CarQueue paymentCarQueue;
	private CarQueue exitCarQueue;
	private GarageModel garageModel;
	private boolean running;
	private Thread t;

	private int day = 0;
	private int hour = 0;
	private int minute = 0;

	public int tickPause = 100;

	int weekDayArrivals = 100; // average number of arriving cars per hour
	int weekendArrivals = 200; // average number of arriving cars per hour
	int weekDayPassArrivals = 50; // average number of arriving cars per hour
	int weekendPassArrivals = 5; // average number of arriving cars per hour

	int enterSpeed = 3; // number of cars that can enter per minute
	int paymentSpeed = 7; // number of cars that can pay per minute
	int exitSpeed = 5; // number of cars that can leave per minute

	public Simulator() {
		entranceCarQueue = new CarQueue();
		entrancePassQueue = new CarQueue();
		paymentCarQueue = new CarQueue();
		exitCarQueue = new CarQueue();
		garageModel = new GarageModel(3, 6, 28);

	}

	public void run() {
		running = true;
		for (int i = 0; i < 10000; i++) {
			tick();
		}
		running = false;
	}

	public void tick() {
		advanceTime();
		handleExit();
		updateViews();
		// Pause.
		try {
			Thread.sleep(tickPause);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		handleEntrance();
	}

	private void advanceTime() {
		// Advance the time by one minute.
		minute++;
		while (minute > 59) {
			minute -= 60;
			hour++;
		}
		while (hour > 23) {
			hour -= 24;
			day++;
		}
		while (day > 6) {
			day -= 7;
		}

	}

	private void handleEntrance() {
		carsArriving();
		carsEntering(entrancePassQueue);
		carsEntering(entranceCarQueue);
	}

	private void handleExit() {
		carsReadyToLeave();
		carsPaying();
		carsLeaving();
	}

	private void updateViews() {
		garageModel.tick();
		// Update the car park view.
		for (AbstractView av : views) {
			av.updateView();
		}
	}

	private void carsArriving() {
		int numberOfCars = getNumberOfCars(weekDayArrivals, weekendArrivals);
		addArrivingCars(numberOfCars, AD_HOC);
		numberOfCars = getNumberOfCars(weekDayPassArrivals, weekendPassArrivals);
		addArrivingCars(numberOfCars, PASS);
	}

	private void carsEntering(CarQueue queue) {
		int i = 0;
		// Remove car from the front of the queue and assign to a parking space.
		while (queue.carsInQueue() > 0 && garageModel.getNumberOfOpenSpots() > 0 && i < enterSpeed) {
			Car car = queue.removeCar();
			Location freeLocation = garageModel.getFirstFreeLocation();
			garageModel.setCarAt(freeLocation, car);
			i++;
		}
	}

	public void start() {
		if(running == false) {
		t = new Thread(this);
		t.start();
		running = true;
		}
	}

	public void pauze() {
		if(running == true) {
		t.stop();
		running = false;
		}
	}

	public void step() {
		if(running == false) {
			tick();
		}
	}
	
	public void steps() {
		if(running == false) {
			for(int i = 0; i <= 100; i++) {
				tick();
			}
		}
	}
	
	public void faster() { 
		if(running == true && tickPause > 20) {
			tickPause = tickPause - 40;
			System.out.println(tickPause);
		}
	}
	
	public void slower() {
		if(running == true && tickPause < 300) {
			tickPause = tickPause + 100;
			System.out.println(tickPause);
		}
	}
	
	private void carsReadyToLeave() {
		// Add leaving cars to the payment queue.
		Car car = garageModel.getFirstLeavingCar();
		while (car != null) {
			if (car.getHasToPay()) {
				car.setIsPaying(true);
				paymentCarQueue.addCar(car);
			} else {
				carLeavesSpot(car);
			}
			car = garageModel.getFirstLeavingCar();
		}
	}

	private void carsPaying() {
		// Let cars pay.
		int i = 0;
		while (paymentCarQueue.carsInQueue() > 0 && i < paymentSpeed) {
			Car car = paymentCarQueue.removeCar();
			// TODO Handle payment.
			carLeavesSpot(car);
			i++;
		}
	}

	private void carsLeaving() {
		// Let cars leave.
		int i = 0;
		while (exitCarQueue.carsInQueue() > 0 && i < exitSpeed) {
			exitCarQueue.removeCar();
			i++;
		}
	}

	private int getNumberOfCars(int weekDay, int weekend) {
		Random random = new Random();

		// Get the average number of cars that arrive per hour.
		int averageNumberOfCarsPerHour = day < 5 ? weekDay : weekend;

		// Calculate the number of cars that arrive this minute.
		double standardDeviation = averageNumberOfCarsPerHour * 0.3;
		double numberOfCarsPerHour = averageNumberOfCarsPerHour + random.nextGaussian() * standardDeviation;
		return (int) Math.round(numberOfCarsPerHour / 60);
	}

	private void addArrivingCars(int numberOfCars, String type) {
		// Add the cars to the back of the queue.
		switch (type) {
		case AD_HOC:
			for (int i = 0; i < numberOfCars; i++) {
				entranceCarQueue.addCar(new AdHocCar());
			}
			break;
		case PASS:
			for (int i = 0; i < numberOfCars; i++) {
				entrancePassQueue.addCar(new ParkingPassCar());
			}
			break;
		}
	}

	private void carLeavesSpot(Car car) {
		garageModel.removeCarAt(car.getLocation());
		exitCarQueue.addCar(car);
	}

	public GarageModel getGarageModel() {
		return garageModel;
	}
}