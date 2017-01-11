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

// JMF MP3 Plugin
// http://www.oracle.com/technetwork/java/javase/download-137625.html
public class BasicPlayerSample extends JFrame {

	// �x�[�V�b�N�v���C���[
	BasicPlayer player;

	// Swing�p�ϐ��A�萔
	// �{�^���p�e�L�X�g ��~
	final static String STOP = "Stop";
	// �{�^���p�e�L�X�g �Đ�
	final static String PLAY = "Play";
	// �{�^���p�e�L�X�g �ꎞ��~
	final static String PAUSE = "Pause";
	// �^�C�g��
	final static String TITLE = "BasicPlayer��mp3�Đ�";
	final static String FILE_NAME = "�t�@�C�����F";
	// �Đ��{�^��
	JButton bPlay;
	// �Ȗ����x��
	JLabel label;

	public static void main(String[] args) {
		new BasicPlayerSample();
	}

	// �R���X�g���N�^
	public BasicPlayerSample() {
		setTitle(TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(200, 100, 300, 100);
		setLayout(new FlowLayout());

		// �h���b�v�^�[�Q�b�g�ݒ�
		new DropTarget(this, DnDConstants.ACTION_COPY,
				new MyDropTargetListener());

		// ���x���ݒu
		label = new JLabel("�@mp3�t�@�C�����h���b�v���Ă��������B");
		label.setPreferredSize(new Dimension(getWidth() - 10, 20));
		add(label);
		add(getHr(2000, 0));

		// �Đ��A��~�{�^���ݒu
		bPlay = new JButton(PLAY);
		bPlay.addActionListener(new bPlayAction());
		JButton bStop = new JButton(STOP);
		bStop.addActionListener(new bStopAction());
		add(bPlay);
		add(bStop);

		// BasicPlayer�̃C���X�^���X�쐬
		player = new BasicPlayer();

		setVisible(true);

		addComponentListener(new ComponentAdapter() {
			// �E�B���h�E�T�C�Y���ω������烉�x���̃T�C�Y�ύX
			@Override
			public void componentResized(ComponentEvent e) {
				label.setPreferredSize(new Dimension(getWidth() - 10, 20));
			}
		});

	}

	// ������
	public JSeparator getHr(int width, int hight) {
		JSeparator sp = new JSeparator(JSeparator.HORIZONTAL);
		sp.setPreferredSize(new Dimension(width, hight));
		return sp;
	}

	// �Đ��{�^���̃A�N�V�����N���X
	class bPlayAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				int status = player.getStatus();
				if (status == BasicPlayer.PAUSED) {
					// �ꎞ��~�̏ꍇ�A�ꎞ��~���������܂�
					resume();
				} else if (status == BasicPlayer.STOPPED) {
					// ��~���̏ꍇ�A�Đ��J�n���܂�
					play();
				} else if (status == BasicPlayer.PLAYING) {
					// �Đ����̏ꍇ�A�ꎞ��~���܂�
					pause();
				}
			} catch (Exception ex) {
				// ����Ԃ�
			}
		}
	}

	// ��~�{�^���̃A�N�V�����N���X
	class bStopAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				stop();
			} catch (Exception ex) {
				// ����Ԃ�
			}
		}
	}

	// �ꎞ��~
	private void pause() throws BasicPlayerException {
		// playMode = PLAY_MODE_PAUSE;
		player.pause();
		bPlay.setText(PLAY);
	}

	// �ꎞ��~����
	private void resume() throws BasicPlayerException {
		player.resume();
		bPlay.setText(PLAY);
	}

	// �Đ�
	private void play() throws BasicPlayerException {
		player.play();
		bPlay.setText(PAUSE);
	}

	// ��~
	private void stop() throws BasicPlayerException {
		player.stop();
		bPlay.setText(PLAY);
	}

	// �J��
	private void open(File file) throws BasicPlayerException {
		// ���x���ݒ�
		label.setText(FILE_NAME + file.getName());
		// �擾�����t�@�C�����J��
		player.open(file);
		play(); // �Đ�
	}

	// �h���b�v�^�[�Q�b�g���X�i�[
	// �h���b�v���ꂽ�t�@�C�����󂯎��A�ŏ��̃t�@�C���������Đ����܂�
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

					// �ŏ��̃t�@�C�������擾
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
