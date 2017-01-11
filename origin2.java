package prototype;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.List;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
 
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;


public class origin2 extends JFrame{
	// ベーシックプレイヤー
		BasicPlayer player;
	 
		// Swing用変数、定数
		// ボタン用テキスト 停止
		final static String STOP = "Stop";
		// ボタン用テキスト 再生
		final static String PLAY = "Play";
		// ボタン用テキスト 一時停止
		final static String PAUSE = "Pause";
		// タイトル
		final static String TITLE = "BasicPlayerでmp3再生";
		final static String FILE_NAME = "ファイル名：";
		// 再生ボタン
		JButton bPlay;
		// 曲名ラベル
		JLabel label;
	 
		public static void main(String[] args) {
			new origin2();
		}
	 
		// コンストラクタ
		public origin2() {
			setTitle(TITLE);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setBounds(200, 100, 300, 100);
			setLayout(new FlowLayout());
	 
			// ドロップターゲット設定
			new DropTarget(this, DnDConstants.ACTION_COPY,
					new MyDropTargetListener());
	 
			// ラベル設置
			label = new JLabel("　mp3ファイルをドロップしてください。");
			label.setPreferredSize(new Dimension(getWidth() - 10, 20));
			add(label);
			add(getHr(2000, 0));
	 
			// 再生、停止ボタン設置
			bPlay = new JButton(PLAY);
			bPlay.addActionListener(new bPlayAction());
			JButton bStop = new JButton(STOP);
			bStop.addActionListener(new bStopAction());
			add(bPlay);
			add(bStop);
	 
			// BasicPlayerのインスタンス作成
			player = new BasicPlayer();
	 
			setVisible(true);
	 
			addComponentListener(new ComponentAdapter() {
				// ウィンドウサイズが変化したらラベルのサイズ変更
				@Override
				public void componentResized(ComponentEvent e) {
					label.setPreferredSize(new Dimension(getWidth() - 10, 20));
				}
			});
	 
		}
	 
		// 水平線
		public JSeparator getHr(int width, int hight) {
			JSeparator sp = new JSeparator(JSeparator.HORIZONTAL);
			sp.setPreferredSize(new Dimension(width, hight));
			return sp;
		}
	 
		// 再生ボタンのアクションクラス
		class bPlayAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int status = player.getStatus();
					if (status == BasicPlayer.PAUSED) {
						// 一時停止の場合、一時停止を解除します
						resume();
					} else if (status == BasicPlayer.STOPPED) {
						// 停止中の場合、再生開始します
						play();
					} else if (status == BasicPlayer.PLAYING) {
						// 再生中の場合、一時停止します
						pause();
					}
				} catch (Exception ex) {
					// 握りつぶす
				}
			}
		}
	 
		// 停止ボタンのアクションクラス
		class bStopAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					stop();
				} catch (Exception ex) {
					// 握りつぶす
				}
			}
		}
	 
		// 一時停止
		private void pause() throws BasicPlayerException {
			// playMode = PLAY_MODE_PAUSE;
			player.pause();
			bPlay.setText(PLAY);
		}
	 
		// 一時停止解除
		private void resume() throws BasicPlayerException {
			player.resume();
			bPlay.setText(PLAY);
		}
	 
		// 再生
		private void play() throws BasicPlayerException {
			player.play();
			bPlay.setText(PAUSE);
		}
	 
		// 停止
		private void stop() throws BasicPlayerException {
			player.stop();
			bPlay.setText(PLAY);
		}
	 
		// 開く
		private void open(File file) throws BasicPlayerException {
			// ラベル設定
			label.setText(FILE_NAME + file.getName());
			// 取得したファイルを開く
			player.open(file);
			play(); // 再生
		}
	 
		// ドロップターゲットリスナー
		// ドロップされたファイルを受け取り、最初のファイルだけを再生します
		class MyDropTargetListener extends DropTargetAdapter {
			@Override
			public void drop(DropTargetDropEvent dtde) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				boolean b = false;
				try {
					if (dtde.getTransferable().isDataFlavorSupported(
							DataFlavor.javaFileListFlavor)) {
						b = true;
						List<File> list = (List<File>) dtde.getTransferable()
								.getTransferData(DataFlavor.javaFileListFlavor);
	 
						// 最初のファイルだけ取得
						File file = list.get(0);
						open(file);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dtde.dropComplete(b);
				}
			}
		}
}

