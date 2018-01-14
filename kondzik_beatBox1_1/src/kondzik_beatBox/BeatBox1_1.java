package kondzik_beatBox;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BeatBox1_1 {

	//Boolean boxState = false;
	BufferedImage icon;
	JPanel mainPanel;
	JCheckBox c = new JCheckBox();
	JPanel outside = new JPanel(); // left to right panel
	JPanel inside = new JPanel(); // TOP to BUTTOM panel
	Dimension d = new Dimension(200, 40);
	ArrayList<JCheckBox> checkboxList;
	Sequencer sequencer;
	Sequence sequence;
	Track track;
	JFrame theFrame;
	
	String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", 
													"Acoustic Snare", "Crash Cymbai", "Hand Clap", 
													"High Tom", "Hi Bongo", "Maracas", 
													"Whistle", "Low Conga", "Cowbell", 
													"Vibraslap", "Low-mid Tom", "High Agogo", "Opne High Conga"};
	int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63}; // MIDI # for above instruments
	
	public BeatBox1_1(){
		loadImage("/jupiterIcon.png");
	}
	
	public static void main(String[] args) {
		
		new BeatBox1_1().buildGUI();
		
	}
	
	
	
	public void buttonPanel(){
		
		
		
		outside.setLayout(new BoxLayout(outside, BoxLayout.LINE_AXIS));
		inside.setLayout(new BoxLayout(inside, BoxLayout.PAGE_AXIS));
		
		// OUTSIDE PANEL
		outside.add(Box.createHorizontalStrut(10)); // it is an empty space
		outside.add(inside);
		outside.add(Box.createHorizontalStrut(20));
		
		
		
		// INSIDE PANEL
		inside.add(Box.createVerticalStrut(6));
		
		// "Start" button
		JButton start = new JButton("Start/Dodaj");
		start.setSize(d);
		start.setMinimumSize(d);
		start.setMaximumSize(d);
		start.setPreferredSize(d);
		start.addActionListener(new MyStartListener());
		
		// "Wyczyœæ" button
		JButton clear = new JButton("Wyczyœæ");
		clear.setSize(d);
		clear.setMinimumSize(d);
		clear.setMaximumSize(d);
		clear.setPreferredSize(d);
		clear.addActionListener(new MyClearListener());
		
		//"Stop" button
		JButton stop = new JButton("Stop");
		stop.setSize(d);
		stop.setMinimumSize(d);
		stop.setMaximumSize(d);
		stop.setPreferredSize(d);
		stop.addActionListener(new MyStopListener());
		
		//"Zwiêksz têmpo" button
		JButton upTempo = new JButton("Zwiêksz tempo");
		upTempo.setSize(d);
		upTempo.setMinimumSize(d);
		upTempo.setMaximumSize(d);
		upTempo.setPreferredSize(d);
		upTempo.addActionListener(new MyUpTempoListener());
		
		//"Zmniejsz têpo" button
		JButton downTempo = new JButton("Zmniejsz tempo");
		downTempo.setSize(d);
		downTempo.setMinimumSize(d);
		downTempo.setMaximumSize(d);
		downTempo.setPreferredSize(d);
		downTempo.addActionListener(new MyDownTempoListener());
		
		// adding the buttons to the inside Panel
		inside.add(start);
		inside.add(Box.createVerticalStrut(5));
		inside.add(clear);
		inside.add(Box.createVerticalStrut(5));
		inside.add(stop);
		inside.add(Box.createVerticalStrut(5));
		inside.add(upTempo);
		inside.add(Box.createVerticalStrut(5));
		inside.add(downTempo);
		inside.add(Box.createVerticalStrut(5));
		
		inside.add(Box.createHorizontalStrut(40));
	}
	
	public void checkBoxes(){
			for(int i = 0; i<256; i++){ //  that is WOW, adding 256 items using a bit of code :)))
			
		
			c = new JCheckBox();
			c.setSelected(false);
			
			checkboxList.add(c);
			mainPanel.add(c);
			
			
		}
	}
	
	public void buildGUI(){
		//Set the Frame 01
		
		theFrame = new JFrame("Kondzik's BeatBox");
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theFrame.setResizable(false);
		theFrame.setIconImage(icon);

		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,10)); // creates an empty border to makes it looks better
		
		checkboxList = new ArrayList<JCheckBox>(); // an arrayList in the ArrayList?

		Box nameBox = new Box(BoxLayout.Y_AXIS);
		for(int i = 0; i <16; i++){
			nameBox.add(new Label(instrumentNames[i])); // check API for Label 
		}
		
		buttonPanel(); // creates the panel with the centralized buttons
		
		background.add(BorderLayout.EAST, outside);
		background.add(BorderLayout.WEST, nameBox);
		
		theFrame.getContentPane().add(background); // that's add the JPanel as background to the JFrame
		
		GridLayout grid = new GridLayout(16,16);
		grid.setVgap(1);
		grid.setHgap(2);
		mainPanel = new JPanel(grid);
		background.add(BorderLayout.CENTER, mainPanel);
		
		checkBoxes();
	
		
		setUpMidi();
		// Set The Frame 02
		theFrame.setBounds(50,50,600,600);
		theFrame.pack();
		theFrame.setLocationRelativeTo(null);
		theFrame.setVisible(true);

	}
	
	public void loadImage(String str){
		try {
			icon = ImageIO.read(BeatBox1_1.class.getResource(str));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setUpMidi(){
		
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequence = new Sequence(Sequence.PPQ, 4);
			track = sequence.createTrack();
			sequencer.setTempoInBPM(120);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void buildTrackAndStart(){
		
		int[] trackList = null;
		
		sequence.deleteTrack(track);
		track = sequence.createTrack();
		
		for(int i = 0; i <16; i++){
			trackList = new int[16];
			int key = instruments[i];
			
			for(int j = 0; j<16; j++){
				
				JCheckBox jc = (JCheckBox) checkboxList.get(j + (16*i));
				if(jc.isSelected()){
					trackList[j] = key;
				}
				else{
					trackList[j] = 0;
				}		
			}		
			
			makeTracks(trackList);
			track.add(makeEvent(176, 1, 127, 0, 16));
			
		}
		
		track.add(makeEvent(192,9,1,0,15));
		
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(120);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
	
	public class MyStartListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			buildTrackAndStart();
		}
	}
	
	public class MyClearListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			for (JCheckBox boxes : checkboxList) {
				if(boxes.isSelected()){
					boxes.doClick();
				}
			}
			
		}
	}
	
	public class MyStopListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			sequencer.stop();
		}
	}
	public class MyUpTempoListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float) (tempoFactor*1.03));
		}
	}
	public class MyDownTempoListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float) (tempoFactor*0.97));
		}
	}
	
	public void makeTracks(int[] list){
		
		for(int i = 0; i <16; i++){
			int key = list[i];
			
			if(key!=0){
				track.add(makeEvent(144,9,key,100,i));
				track.add(makeEvent(128,9,key,100,i+1));
			}
		}
	}
	
	public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
		MidiEvent event = null;
		
		try {
				ShortMessage a = new ShortMessage();
				a.setMessage(comd, chan, one, two);
				event = new MidiEvent(a, tick);
		} catch (Exception e) {
				e.printStackTrace();
		}
		return event;
	}
	
}
