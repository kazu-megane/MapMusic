package prototype;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.event.*;

public class EJDicGUI extends JFrame {
	JTextField english, japanese;
	JList list;
	JButton addButton, removeButton, updateButton;
	JPanel pane;
	EJDic dictionary;

	public static void main( String[] args ){
		JFrame w = new EJDicGUI( "EJDicGUI" );
		w.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		w.setSize( 300, 300 );
		w.setVisible( true );
	}

	public EJDicGUI( String title ){
		super( title );
		dictionary = new EJDic();
		pane = (JPanel)getContentPane();

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar( menuBar );
		JMenu fileMenu = new JMenu( "ファイル" );
		menuBar.add( fileMenu );
		JMenuItem item;
		item = new JMenuItem( new OpenAction() );
		fileMenu.add( item );
		item = new JMenuItem( new SaveAction() );
		fileMenu.add( item );
		fileMenu.addSeparator();
		item = new JMenuItem( new ExitAction() );
		fileMenu.add( item );

		JPanel fields = new JPanel(new GridLayout(1, 2));
		english = new JTextField();
		english.setBorder( new TitledBorder( "英語" ) );
		fields.add(english);
		japanese = new JTextField();
		japanese.setBorder( new TitledBorder( "日本語" ) );
		fields.add(japanese);
		pane.add( fields, BorderLayout.SOUTH );

		DefaultListModel listModel = new DefaultListModel();
		list = new JList( listModel );
		list.addListSelectionListener( new WordSelect() );
		JScrollPane sc = new JScrollPane( list );
		sc.setBorder( new TitledBorder( "項目一覧" ) );
		pane.add( sc, BorderLayout.CENTER );

		JPanel buttons = new JPanel();
		buttons.setLayout( new GridLayout(1, 3) );
		addButton = new JButton( new AddAction() );
		buttons.add( addButton );
		updateButton = new JButton( new UpdateAction() );
		buttons.add( updateButton );
		removeButton = new JButton( new RemoveAction() );
		buttons.add( removeButton ); 
		pane.add( buttons, BorderLayout.NORTH );
	}

	class WordSelect implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			JList li = (JList)e.getSource();
			if(e.getValueIsAdjusting() == false){
				String select = (String)li.getSelectedValue();
				english.setText(select);
				japanese.setText(dictionary.get(select));
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
			dictionary.open(filename);
			DefaultListModel model = (DefaultListModel)list.getModel();
			model.clear();
			for(String key : dictionary.keySet()){
				model.addElement(key);
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
			dictionary.save(filename);
		}
	}

	class ExitAction extends AbstractAction {
		ExitAction() {
			putValue( Action.NAME, "終了" );
			putValue( Action.SHORT_DESCRIPTION, "終了" );
		}
		public void actionPerformed( ActionEvent e) {
			int ans = JOptionPane.showConfirmDialog(EJDicGUI.this, "本当に終了しますか？");
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
			if(!model.contains(english.getText()) && english.getText()!=null && japanese.getText()!=null){
				dictionary.put(english.getText(), japanese.getText());
				model.addElement(english.getText());
				english.setText(null);
				japanese.setText(null);
			}
		}
	}

	class UpdateAction extends AbstractAction {
		UpdateAction() {
			putValue( Action.NAME, "更新" );
			putValue( Action.SHORT_DESCRIPTION, "更新" );
		}
		public void actionPerformed( ActionEvent e) {
			DefaultListModel model = (DefaultListModel)list.getModel();
			if(model.contains(english.getText()) && english.getText() != null && japanese.getText() != null){
				dictionary.put(english.getText(), japanese.getText());
			}
			
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
				dictionary.remove(select);
				model.remove(index);
			}
			if(ans == 1){
				return;
			}
		}
	}
}