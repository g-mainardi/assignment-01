package pcd.ass01;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.util.Hashtable;

public class BoidsView implements ChangeListener {

    private final BoidsPanel boidsPanel;
	private JSlider cohesionSlider, separationSlider, alignmentSlider;
	private JTextField numBoidsField;
    private BoidsModel model;
	private int width, height;

	public BoidsView(BoidsModel model, int width, int height) {
		this.model = model;
		this.width = width;
		this.height = height;

        JFrame frame = new JFrame("Boids Simulation");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel cp = new JPanel();
		cp.setLayout(new BorderLayout());

        boidsPanel = new BoidsPanel(this, model);
		cp.add(BorderLayout.CENTER, boidsPanel);
		cp.add(BorderLayout.SOUTH, makeControlPanel());

		frame.setContentPane(cp);
        frame.setVisible(true);
	}

	private JPanel makeControlPanel() {
		JPanel controlPanel = new JPanel();

		cohesionSlider = makeSlider();
		separationSlider = makeSlider();
		alignmentSlider = makeSlider();
		numBoidsField = makeBoidsNumberField();
        JButton startButton = makeStartAndStopButton();

		controlPanel.add(new JLabel("Separation"));
		controlPanel.add(separationSlider);
		controlPanel.add(new JLabel("Alignment"));
		controlPanel.add(alignmentSlider);
		controlPanel.add(new JLabel("Cohesion"));
		controlPanel.add(cohesionSlider);
		controlPanel.add(new JLabel("Count"));
		controlPanel.add(numBoidsField);
		controlPanel.add(startButton);

		return controlPanel;
	}

	private JButton makeStartAndStopButton() {
		final JButton startButton = new JButton("start");
		startButton.addActionListener(e -> {
			var btnText = startButton.getText();
			if(btnText.equals("start")) {
				var inputText = numBoidsField.getText();
				try {
					int newBoidsNumber = Integer.parseInt(inputText);
					model.setBoidsNumber(newBoidsNumber);
					model.turnOn();
					numBoidsField.setText("");
					startButton.setText("stop");
				} catch (NumberFormatException ignored) {}
			} else {
				model.turnOff();
				startButton.setText("start");
			}
		});
		return startButton;
	}

	private JTextField makeBoidsNumberField() {
		final JTextField numBoidsField;
		numBoidsField = new JTextField(5);
		numBoidsField.setText(Integer.toString(BoidsSimulation.N_BOIDS));
		return numBoidsField;
	}

	private JSlider makeSlider() {
		var slider = new JSlider(JSlider.HORIZONTAL, 0, 20, 10);        
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
		labelTable.put( 0, new JLabel("0") );
		labelTable.put( 10, new JLabel("1") );
		labelTable.put( 20, new JLabel("2") );
		slider.setLabelTable( labelTable );
		slider.setPaintLabels(true);
        slider.addChangeListener(this);
		return slider;
	}
	
	public void update(int frameRate) {
		boidsPanel.setFrameRate(frameRate);
		boidsPanel.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == separationSlider) {
			var val = separationSlider.getValue();
			model.setSeparationWeight(0.1*val);
		} else if (e.getSource() == cohesionSlider) {
			var val = cohesionSlider.getValue();
			model.setCohesionWeight(0.1*val);
		} else {
			var val = alignmentSlider.getValue();
			model.setAlignmentWeight(0.1*val);
		}
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
