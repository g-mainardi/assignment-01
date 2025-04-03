package pcd.ass01;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.util.Hashtable;

public class BoidsView implements ChangeListener {

	public static final String START = "start";
	public static final String STOP = "stop";
	public static final String SUSPEND = "suspend";
	public static final String RESUME = "resume";
	private final BoidsPanel boidsPanel;
	private JSlider cohesionSlider, separationSlider, alignmentSlider;
	private JTextField numBoidsField;
	private JButton startButton, suspendResumeButton;
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
        startButton = makeStartAndStopButton();
		suspendResumeButton = makeSuspendResumeButton();
		this.disableSuspendResumeButton();
		this.resetBoidsNumberField();

		controlPanel.add(new JLabel("Separation"));
		controlPanel.add(separationSlider);
		controlPanel.add(new JLabel("Alignment"));
		controlPanel.add(alignmentSlider);
		controlPanel.add(new JLabel("Cohesion"));
		controlPanel.add(cohesionSlider);
		controlPanel.add(new JLabel("Count"));
		controlPanel.add(numBoidsField);
		controlPanel.add(startButton);
		controlPanel.add(suspendResumeButton);

		return controlPanel;
	}

	private JButton makeStartAndStopButton() {
		final JButton button = new JButton(START);
		button.addActionListener(e -> {
			var btnText = button.getText();
			if(btnText.equals(START)) {
				var inputText = numBoidsField.getText();
				try {
					int newBoidsNumber = Integer.parseInt(inputText);
					if (newBoidsNumber <= 0) {
						throw new IllegalArgumentException();
					}
					startAction(newBoidsNumber);
				} catch (NumberFormatException ex1) {
					System.out.println("Input format not allowed!");
				} catch (IllegalArgumentException ex2) {
					System.out.println("Only positive numbers allowed!");
				}
			} else if (btnText.equals(STOP)){
				stopAction();
			}
		});
		return button;
	}

	private JButton makeSuspendResumeButton() {
		final JButton button = new JButton(SUSPEND);
		button.addActionListener(e -> {
			var btnText = button.getText();
			if(btnText.equals(SUSPEND)) {
				suspendAction();
			} else if (btnText.equals(RESUME)){
				resumeAction();
			}
		});
		return button;
	}

	private void stopAction() {
		this.disableStartAndStopButton();
		this.enableNumBoidsField();
		model.turnOff();
		this.resetBoidsNumberField();
		startButton.setText(START);
		this.disableSuspendResumeButton();
	}

	private void startAction(int newBoidsNumber) {
		this.disableStartAndStopButton();
		this.disableNumBoidsField();
		model.setBoidsNumber(newBoidsNumber);
		model.turnOn();
		numBoidsField.setText("");
		startButton.setText(STOP);
		this.enableSuspendResumeButton();
	}

	private void resumeAction() {
		this.disableSuspendResumeButton();
		suspendResumeButton.setText(SUSPEND);
		model.resume();
	}

	private void suspendAction() {
		this.disableSuspendResumeButton();
		suspendResumeButton.setText(RESUME);
		model.suspend();
	}

	private void enableNumBoidsField() {
		numBoidsField.setEnabled(true);
	}

	private void disableNumBoidsField() {
		numBoidsField.setEnabled(false);
	}

	public void enableStartStopButton() {
		startButton.setEnabled(true);
	}

	private void disableStartAndStopButton() {
		startButton.setEnabled(false);
	}

	public void enableSuspendResumeButton() {
		suspendResumeButton.setEnabled(true);
	}

	private void disableSuspendResumeButton() {
		suspendResumeButton.setEnabled(false);
	}

	private JTextField makeBoidsNumberField() {
		return new JTextField(5);
	}

	private void resetBoidsNumberField() {
		numBoidsField.setText(Integer.toString(BoidsSimulation.N_BOIDS));
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
