package prototype;

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import prototype.EJDicGUI.ExitAction;
import prototype.EJDicGUI.SaveAction;
import prototype.EJDicGUI.WordSelect;

import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class MusicPlayer extends JFrame{
	JPanel pane;
	JList list;
	JButton startButton, stopButton, pauseButton, nextButton, previousButton, addButton, removeButton;
	JSlider slider;
	JCheckBox roop;
	Map audioInfo;
	int count = 0;
	
	BasicPlayer player;
	EJDic playList;
	
	public static void main(String[] args){
		JFrame w = new MusicPlayer("MusicPlayer");
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		w.setSize(500, 400);
		w.setVisible(true);
	}
	
	public MusicPlayer(String title){
		super(title);
		pane = (JPanel)getContentPane();
		playList = new EJDic();
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("ファイル");
		menuBar.add(fileMenu);
		JMenuItem item;
		item = new JMenuItem(new OpenAction());
		fileMenu.add(item);
		item = new JMenuItem( new SaveAction() );
		fileMenu.add( item );
		item = new JMenuItem( new ExitAction() );
		fileMenu.add( item );
		
		DefaultListModel listModel = new DefaultListModel();
		list = new JList( listModel );
//		list.addListSelectionListener( new WordSelect() );
		JScrollPane sc = new JScrollPane( list );
		TitledBorder tB = new TitledBorder( "プレイリスト" );
		tB.setTitleColor(Color.WHITE);
		sc.setBorder(tB);
		sc.setBackground(Color.BLACK);
		list.setOpaque(true);
		list.setBackground(Color.BLACK);
		list.setForeground(Color.GREEN);
		pane.add( sc, BorderLayout.CENTER );
		
		JPanel entry = new JPanel();
		entry.setLayout(new GridLayout(1,2));
		addButton = new JButton(new AddAction() );
		addButton.setOpaque(true);
		addButton.setBackground(Color.BLACK);
		entry.add(addButton);
		removeButton = new JButton(new RemoveAction());
		removeButton.setOpaque(true);
		removeButton.setBackground(Color.BLACK);
		entry.add(removeButton);
		pane.add(entry,BorderLayout.NORTH);
		
		JPanel controll = new JPanel();
		JPanel conBt = new JPanel();
		JPanel Bar = new JPanel();
		
		Bar.setLayout(new BoxLayout( Bar, BoxLayout.X_AXIS ) );
		JLabel label = new JLabel("再生位置");
		label.setForeground(Color.WHITE);
		Bar.add(label);
		Bar.setOpaque(true);
		Bar.setBackground(Color.BLACK);
		slider = new JSlider(0,100,0);
		slider.addChangeListener(new getPos());
		slider.setOpaque(true);
		slider.setBackground(Color.BLACK);
		Bar.add(slider);
		roop = new JCheckBox("ループ");
		roop.setOpaque(true);
		roop.setBackground(Color.BLACK);
		roop.setForeground(Color.WHITE);
		Bar.add(roop);
		controll.add(Bar);
		
		controll.setLayout(new BoxLayout( controll, BoxLayout.Y_AXIS ) );
		conBt.setLayout(new GridLayout(1,5));
		previousButton = new JButton(new previousAction());
		previousButton.setOpaque(true);
		previousButton.setBackground(Color.BLACK);
		conBt.add(previousButton);
		startButton = new JButton(new startAction());
		startButton.setOpaque(true);
		startButton.setBackground(Color.BLACK);
		conBt.add(startButton);
		pauseButton = new JButton(new pauseAction());
		pauseButton.setOpaque(true);
		pauseButton.setBackground(Color.BLACK);
		conBt.add(pauseButton);
		stopButton = new JButton(new stopAction());
		stopButton.setOpaque(true);
		stopButton.setBackground(Color.BLACK);
		conBt.add(stopButton);
		nextButton = new JButton(new nextAction());
		nextButton.setOpaque(true);
		nextButton.setBackground(Color.BLACK);
		conBt.add(nextButton);
		controll.add(conBt);
		pane.add(controll,BorderLayout.SOUTH);
		
		player = new BasicPlayer();
		player.addBasicPlayerListener(new playerListener());
	}
	
	class playerListener implements BasicPlayerListener{
		@Override
		public void opened(Object arg0, Map arg1) {
			// TODO 自動生成されたメソッド・スタブ
			audioInfo = arg1;
		}

		@Override
		public void progress(int arg0, long arg1, byte[] arg2, Map arg3) {
			// TODO 自動生成されたメソッド・スタブ
			if(Long.parseLong(audioInfo.get("audio.length.bytes").toString()) > 0){
				int newValue = (int)(arg0 * 100 / Long.parseLong(audioInfo.get("audio.length.bytes").toString()));
				
				if(!slider.getValueIsAdjusting() && slider.getValue() != newValue){
					slider.removeChangeListener(new getPos());
					slider.setValue(newValue);
					slider.addChangeListener(new getPos());
				}
			}
		}

		@Override
		public void setController(BasicController arg0) {
			// TODO 自動生成されたメソッド・スタブ
		}

		@Override
		public void stateUpdated(BasicPlayerEvent arg0) {
			// TODO 自動生成されたメソッド・スタブ
			System.out.println(arg0.getCode());
			if(arg0.getCode() == BasicPlayerEvent.EOM){
				try {
					new roopAction().roopAction();
				} catch (BasicPlayerException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}
	}
	
	class roopAction{
		public void roopAction() throws BasicPlayerException{
			if(roop.isSelected()){
				player.stop();
				player.play();
			}else{
				System.out.println("next");
				new nextAction().next();
			}
		}
	}
	
	
	class OpenAction extends AbstractAction {
		OpenAction() {
			putValue( Action.NAME, "開く" );
			putValue( Action.SHORT_DESCRIPTION, "開く" );
		}
		public void actionPerformed( ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(".");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setDialogTitle("ファイルを選択");
			int ret = fileChooser.showOpenDialog(new JLabel());
			
			if(ret != JFileChooser.APPROVE_OPTION) return;
			String filename = fileChooser.getSelectedFile().getAbsolutePath();
			playList.open(filename);
			DefaultListModel model = (DefaultListModel)list.getModel();
			model.clear();
			count = 0;
			for(String key : playList.keySet()){
				model.addElement(key);
				count++;
			}
		}
	}
	
	class SaveAction extends AbstractAction {
		SaveAction() {
			putValue( Action.NAME, "保存" );
			putValue( Action.SHORT_DESCRIPTION, "保存" );
		}
		public void actionPerformed( ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(".");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setDialogTitle("ファイル名を入力");
			int ret = fileChooser.showSaveDialog(new JLabel());
			
			if(ret != JFileChooser.APPROVE_OPTION) return;
			String filename = fileChooser.getSelectedFile().getAbsolutePath();
			playList.save(filename);
		}
	}
	
	class ExitAction extends AbstractAction {
		ExitAction() {
			putValue( Action.NAME, "終了" );
			putValue( Action.SHORT_DESCRIPTION, "終了" );
		}
		public void actionPerformed( ActionEvent e) {
			int ans = JOptionPane.showConfirmDialog(MusicPlayer.this, "本当に終了しますか？");
			if(ans == JOptionPane.YES_OPTION){
				System.exit(0);
			}else{
				return;
			}
		}
	}
	
	class AddAction extends AbstractAction {
		AddAction() {
			putValue( Action.NAME, "追加" );
			putValue( Action.SHORT_DESCRIPTION, "追加" );
		}
		public void actionPerformed( ActionEvent e) {
			DefaultListModel model = (DefaultListModel)list.getModel();
			JFileChooser fileChooser = new JFileChooser(".");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setDialogTitle("ファイルを選択");
			fileChooser.setFileFilter(new fileFilter());
			int ret = fileChooser.showOpenDialog(new JLabel());
			
			if(ret != JFileChooser.APPROVE_OPTION) return;
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();
			String filename = fileChooser.getSelectedFile().getName();
			playList.put(filename, filePath);
			
			model.addElement(filename);
			count++;
			System.out.println(playList.get(filename));
		}
	}
	
	class RemoveAction extends AbstractAction {
		RemoveAction() {
			putValue( Action.NAME, "削除" );
			putValue( Action.SHORT_DESCRIPTION, "削除" );
		}
		public void actionPerformed( ActionEvent e) {
			int index = list.getSelectedIndex();
			if(index < 0)return;
			
			DefaultListModel model = (DefaultListModel)list.getModel();
			String select = (String)model.get(index);
			Object msg = select+"を削除してもよいですか？";
			int ans = JOptionPane.showConfirmDialog(pane, msg,"削除の確認",JOptionPane.YES_NO_OPTION);
			if(ans == 0){
				playList.remove(select);
				model.remove(index);
				count--;
			}
			if(ans == 1){
				return;
			}
		}
	}
	
	class startAction extends AbstractAction {
		startAction() {
			putValue( Action.NAME, "▶" );
			putValue( Action.SHORT_DESCRIPTION, "▶" );
		}
		public void actionPerformed( ActionEvent e) {
			int status = player.getStatus();
			try{
				if(status == BasicPlayer.PAUSED){
					player.resume();
				}else if(status == BasicPlayer.STOPPED || status == BasicPlayer.UNKNOWN){
					int index = list.getSelectedIndex();
					if(index < 0) return;
					DefaultListModel model = (DefaultListModel)list.getModel();
					String select = (String)model.get(index);
					
					player.open(new File(playList.get(select)));
					player.play();
				}
			}catch(Exception ex){
				System.out.println(ex);
			}
		}
	}
	
	class pauseAction extends AbstractAction {
		pauseAction() {
			putValue( Action.NAME, "❙❙" );
			putValue( Action.SHORT_DESCRIPTION, "❙❙" );
		}
		public void actionPerformed( ActionEvent e) {
			try{
				player.pause();
			}catch(Exception ex){
				System.out.println(ex);
			}
		}
	}
	
	class stopAction extends AbstractAction {
		stopAction() {
			putValue( Action.NAME, "■" );
			putValue( Action.SHORT_DESCRIPTION, "■" );
		}
		public void actionPerformed( ActionEvent e) {
			try{
				player.stop();
			}catch(Exception ex){
				System.out.println(ex);
			}
		}
	}
	
	class nextAction extends AbstractAction {
		nextAction() {
			putValue( Action.NAME, "▶▶|" );
			putValue( Action.SHORT_DESCRIPTION, "▶▶|" );
		}
		public void actionPerformed( ActionEvent e) {
			next();
		}
		
		public void next(){
			try{
				int index = list.getSelectedIndex()+1;
				if(index > count || index < 0) return;
				DefaultListModel model = (DefaultListModel)list.getModel();
				String select = (String)model.get(index);
				list.setSelectedIndex(index);
				player.open(new File(select));
				player.play();
			}catch(Exception ex){
				System.out.println(ex);
			}
		}
	}
	
	class previousAction extends AbstractAction {
		previousAction() {
			putValue( Action.NAME, "|◀◀" );
			putValue( Action.SHORT_DESCRIPTION, "|◀◀" );
		}
		public void actionPerformed( ActionEvent e) {
			try{
				int index = list.getSelectedIndex()-1;
				if(index < -2) return;
				DefaultListModel model = (DefaultListModel)list.getModel();
				String select = (String)model.get(index);
				list.setSelectedIndex(index);
				player.open(new File(select));
				player.play();
			}catch(Exception ex){
				System.out.println(ex);
			}
		}
	}
	
	class getPos implements ChangeListener{
		public void stateChanged(ChangeEvent e){
			if (slider.getValueIsAdjusting()) {
				try{
					if(player.getStatus() == BasicPlayer.STOPPED || player.getStatus() == BasicPlayer.UNKNOWN){
						player.play();
					}
					
					long l = Long.parseLong(audioInfo.get("audio.length.bytes").toString()) * slider.getValue() / 100;
					player.seek(l);
				}catch(BasicPlayerException ex){
					ex.printStackTrace();
				}
			}
		}
	}
	
	class fileFilter extends javax.swing.filechooser.FileFilter{
		String[] ex = {"wav","mp3","m4a"};
		String description = "music file";
		public boolean accept(File f){
			if(f.isDirectory()) return true;
			String name = f.getName().toLowerCase();
			for(int i=0; i<ex.length; i++){
				if(name.endsWith(ex[i])){
					return true;
				}
			}
			return false;
		}
		public String getDescription(){
			return description;
		}
	}
	
}

