package controller;

import model.Simulator;

import javax.swing.*;

/**
 * De klasse voor de knoppen van de simulatie
 *
 * @author MinuteJ
 * @version 1.0.0
 */
public class GarageController extends AbstractController {
    private static final long serialVersionUID = 3253955925290497248L;

    /**
     * Maak een nieuw Garagecontroller aan met alle JButtons
     *
     * @param simulator
     */
    public GarageController(Simulator simulator) {
        super();

        JButton startPauze = new JButton("Start/Pauze");
        startPauze.addActionListener(e -> simulator.startPause());

        JButton step = new JButton("1 minuut");
        step.addActionListener(e -> simulator.ffMinute());

        JButton ffHour = new JButton("1 uur");
        ffHour.addActionListener(e -> simulator.ffHour());

        JButton ffDay = new JButton("1 dag");
        ffDay.addActionListener(e -> simulator.ffDay());

        JButton faster = new JButton("Sneller");
        faster.addActionListener(e -> simulator.faster());

        JButton slower = new JButton("Langzamer");
        slower.addActionListener(e -> simulator.slower());

        JButton realTime = new JButton("Realtime");
        realTime.addActionListener(e -> simulator.realTime());

        add(startPauze);
        add(step);
        add(ffHour);
        add(ffDay);
        add(faster);
        add(slower);
        add(realTime);

        // setBackground(Color.BLACK);

        setVisible(true);
    }

}
