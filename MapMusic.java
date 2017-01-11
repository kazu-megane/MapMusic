package prototype;

import java.awt.*;       // AWTコンポーネントを使用するために必要
import java.awt.event.*; // イベントを取り扱うために必要
import java.io.File;
import java.sql.*; // データベースアクセス用

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import prototype.origin2.bPlayAction;
import prototype.origin2.bStopAction;

/**
  JdbcTest4 クラスの定義
  */

public class MapMusic extends Frame implements ActionListener, ItemListener {
  java.awt.List nameList;           // AWTのリストを入れる変数
  TextField numberField, codeField, pref_readingField, city_readingField, area_readingField, prefectureField, cityField, areaField; // テキストフィールドを入れる変数
  TextField codeSearch,citySearch,areaSearch;
  TextField musicUrl,musicName;
  JComboBox combo;
  String[] choice = {"","北海道","青森県","岩手県","宮城県","秋田県","山形県","福島県",
		  			 "茨城県","栃木県","群馬県","埼玉県","千葉県","東京都","神奈川県",
		  			 "新潟県","富山県","石川県","福井県","山梨県","長野県","岐阜県","静岡県","愛知県",
		  			 "三重県","滋賀県","京都府","大阪府","兵庫県","奈良県","和歌山県",
		  			 "鳥取県","島根県","岡山県","広島県","山口県",
		  			 "徳島県","香川県","愛媛県","高知県",
		  			 "福岡県","佐賀県","長崎県","熊本県","大分県","宮崎県","鹿児島県",
		  			 "沖縄県"};
  Button displayButton, updateButton, addButton, deleteButton,bookmarkButton; // ボタンを入れる変数
  Button searchButton;
  Panel leftPanel, rightPanel, bottomPanel,musicPanel;       // パネルを入れる変数
  String bookmarkCommand = "Bookmark", updateCommand = "Update", 
      addCommand = "Add", deleteCommand = "Delete";  // ボタンのコマンド文字列
  String searchCommand = "Search",bookmarkListCommand = "bookmarkList";
  String play = "PLAY", stop = "STOP";

    String driverClassName = "org.postgresql.Driver"; // ここからいつもの
    String url = "jdbc:postgresql://localhost/intern";
    String user = "dbpuser";
    String password = "hogehoge";
    Connection connection;
    ResultSet resultSet;
    
    PreparedStatement prepStmt; // SELECT name 用 (リスト表示)
    PreparedStatement prepStmt_S; // SELECT用
    PreparedStatement prepStmt_I; // INSERT用
    PreparedStatement prepStmt_U; // UPDATE用
    PreparedStatement prepStmt_D; // DELETE用
    PreparedStatement prepStmtSearch;
    PreparedStatement prepBookmark;
    PreparedStatement prepBookmarkList;
    

    String selectStr = "SELECT number FROM postalcode";
    String strPrepSQL_S = "SELECT * FROM postalcode WHERE prefecture = ? and city = ? and area = ?";
    String strPrepSQLSearch = "SELECT * FROM postalcode where code like ? and prefecture like ? and city like ? and area like ?";
    String strPrepSQL_I = "INSERT INTO postalcode VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    String strPrepSQL_U = 
       "UPDATE postalcode SET code = ?, pref_reading = ?, city_reading = ?, area_reading = ?, prefecture = ?, city = ?, area = ?, musicname = ?, musicurl = ? WHERE number = ?";
    String strPrepSQL_D = "DELETE FROM postalcode WHERE number = ?";
    String strPrepbookmark = "UPDATE postalcode SET bookmark = ? where prefecture = ? and city = ? and area = ?";
    String strPrepbookmarklist =  "SELECT * FROM postalcode WHERE bookmark = true";
    
//    StringBuffer strPrepSQLS = new StringBuffer("SELECT * FROM postalcode WHERE");

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
	Button bPlay,bStop;
	// 曲名ラベル
	JLabel label;
    
  MapMusic() {            // コンストラクタ
    setSize(700, 500);                 // フレームのサイズ設定
    setTitle("original_gui");   // フレームのタイトル設定
    setLayout(new GridLayout(2, 2));   // フレームのレイアウト設定

    leftPanel = new Panel();               // 左のパネルの生成
    leftPanel.setLayout(new GridLayout()); // 左のパネルのレイアウト設定
    add(leftPanel);                        // フレームに左のパネルを追加

    nameList = new java.awt.List(10);  // リストの生成
    nameList.addItemListener(this);    // フレームをリスナにする
    leftPanel.add(nameList);  // 左のパネルに追加
    
    rightPanel = new Panel();                   // 右のパネルの生成
    rightPanel.setLayout(new GridLayout(12, 2)); // 右のパネルのレイアウト設定
    add(rightPanel);                            // フレームに右のパネルを追加
    
    bottomPanel = new Panel();
    bottomPanel.setLayout(new GridLayout(10, 2));
    add(bottomPanel);
    
    musicPanel = new Panel();
    musicPanel.setLayout(new GridLayout(3,1));
    add(musicPanel);

    rightPanel.add(new Label("number"));        // 右のパネルにnameラベル追加
    numberField = new TextField(15);            // nameフィールドの生成
    rightPanel.add(numberField);                // 右のパネルにnameフィールド追加
    rightPanel.add(new Label("code"));     // 右のパネルにaddressラベル追加
    codeField = new TextField(15);         // addressフィールドの生成
    rightPanel.add(codeField);             // 右のパネルにaddressフィールド追加
    rightPanel.add(new Label("都道府県読み仮名"));
    pref_readingField = new TextField(15);
    rightPanel.add(pref_readingField);
    rightPanel.add(new Label("市区読み仮名"));
    city_readingField = new TextField(15);
    rightPanel.add(city_readingField);
    rightPanel.add(new Label("町村読み仮名"));
    area_readingField = new TextField(15);
    rightPanel.add(area_readingField);
    rightPanel.add(new Label("都道府県"));
    prefectureField = new TextField(15);
    rightPanel.add(prefectureField);
    rightPanel.add(new Label("市区"));
    cityField = new TextField(15);
    rightPanel.add(cityField);
    rightPanel.add(new Label("町村"));
    areaField = new TextField(15);
    rightPanel.add(areaField);
    rightPanel.add(new Label("曲名"));
    musicName = new TextField(15);
    rightPanel.add(musicName);
    rightPanel.add(new Label("URL"));
    musicUrl = new TextField(15);
    rightPanel.add(musicUrl);
    
    
    displayButton = new Button(bookmarkCommand);   // displayボタンの生成
    displayButton.addActionListener(this);        // フレームをリスナにする
    rightPanel.add(displayButton);            // 右のパネルにdisplayボタン追加
    updateButton = new Button(updateCommand); // updateボタンの生成
    updateButton.addActionListener(this);     // フレームをリスナにする
    rightPanel.add(updateButton);             // 右のパネルにupdateボタン追加
    addButton = new Button(addCommand);       // addボタンの生成
    addButton.addActionListener(this);        // フレームをリスナにする
    rightPanel.add(addButton);                // 右のパネルにaddボタン追加
    deleteButton = new Button(deleteCommand); // deleteボタンの生成
    deleteButton.addActionListener(this);     // フレームをリスナにする
    rightPanel.add(deleteButton);             // 右のパネルにdeleteボタン追加
    
    bottomPanel.add(new Label("郵便番号"));
    codeSearch = new TextField(15);
    bottomPanel.add(codeSearch);
    bottomPanel.add(new Label("都道府県"));
    combo = new JComboBox(choice);
    bottomPanel.add(combo);
    bottomPanel.add(new Label("市区"));
    citySearch = new TextField(15);
    bottomPanel.add(citySearch);
    bottomPanel.add(new Label("町村"));
    areaSearch = new TextField(15);
    bottomPanel.add(areaSearch);
    
    searchButton = new Button(searchCommand);
    searchButton.addActionListener(this);
    bottomPanel.add(searchButton);
    bookmarkButton = new Button(bookmarkListCommand);
    bookmarkButton.addActionListener(this);
    bottomPanel.add(bookmarkButton);
    
    
    label = new JLabel("地域を選択してください");
    bPlay = new Button(play);
    bPlay.addActionListener(this);
	bStop = new Button(stop);
	bStop.addActionListener(this);
	musicPanel.add(label);
	musicPanel.add(bPlay);
	musicPanel.add(bStop);
	
	player = new BasicPlayer();
	
    addWindowListener ( new WindowAdapter() {
	    public void windowClosing(WindowEvent we) {
		try { // 後処理
		    prepStmt.close();
		    prepStmt_S.close();
		    prepStmt_I.close();
		    prepStmt_U.close();
		    prepStmt_D.close();
		    prepStmtSearch.close();
		    prepBookmark.close();
		    prepBookmarkList.close();
		    connection.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		System.exit(0);
	    }
	} ) ; // ウィンドウを閉じる処理 

        try { // ドライバマネージャとコネクション
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url, user, password);

            prepStmt = connection.prepareStatement(selectStr);
            prepStmt_S = connection.prepareStatement(strPrepSQL_S);
            prepStmt_I = connection.prepareStatement(strPrepSQL_I);
            prepStmt_U = connection.prepareStatement(strPrepSQL_U);
            prepStmt_D = connection.prepareStatement(strPrepSQL_D);
            prepStmtSearch = connection.prepareStatement(strPrepSQLSearch);
            prepBookmark = connection.prepareStatement(strPrepbookmark);
            prepBookmarkList = connection.prepareStatement(strPrepbookmarklist);
        } catch (Exception e) {
            e.printStackTrace();
        }
	
  }

  public void itemStateChanged(ItemEvent ie) { // 選択項目が変化した時の処理
      displayData(); // 各フィールドにデータ表示
  }

  public void clearList() { // リストクリア
      nameList.removeAll(); // リストの項目をすべて削除
  }

  public void displayList() { // リスト項目表示
      try {
	  resultSet = prepStmt.executeQuery(); // 名前の列だけ抜き出す
	  while (resultSet.next()) {
	      String number = resultSet.getString("number");
	      nameList.add(number); // リストに名前を追加
	  } 
	  resultSet.close();
      } catch (Exception e) {
	  e.printStackTrace();
      }
  }

  public void displayData() { // 選択項目データ再表示
      String[] name = nameList.getSelectedItem().split(" "); // 名前はリストの選択項目 
      String number = "",
    		  code = "",
		      pref_reading = "",
		      city_reading = "",
		      area_reading = "",
		      prefecture = (String)combo.getSelectedItem(),
		      city = "",
		      area = "",
      		 musicname = "",
      		 musicurl = "";
      
      try {
    	  prepStmt_S.setString(1, name[0]);
    	  prepStmt_S.setString(2, name[1]);
    	  prepStmt_S.setString(3, name[2]);
    	  resultSet = prepStmt_S.executeQuery();
	  while (resultSet.next()) { // 同じ名前の場合は最後が有効
	      number = resultSet.getString("number");
	      code = resultSet.getString("code");
	      pref_reading = resultSet.getString("pref_reading");
	      city_reading = resultSet.getString("city_reading");
	      area_reading = resultSet.getString("area_reading");
	      prefecture = resultSet.getString("prefecture");
	      city = resultSet.getString("city");
	      area = resultSet.getString("area");
	      musicname = resultSet.getString("musicname");
	      musicurl = resultSet.getString("musicurl");
	  }
	  numberField.setText(number); // 各フィールドに値をセット
	  codeField.setText(code);
	  pref_readingField.setText(pref_reading);
	  city_readingField.setText(city_reading);
	  area_readingField.setText(area_reading);
	  prefectureField.setText(prefecture);
	  cityField.setText(city);
	  areaField.setText(area);
	  musicName.setText(musicname);
	  musicUrl.setText(musicurl);

	  resultSet.close();
      } catch (Exception e) {
	  e.printStackTrace();
      }
  }
  
  public void updateData() { // 項目データ更新
	  String number = "", // 名前はリストの選択項目 
	  		  code = "",
		      pref_reading = "",
		      city_reading = "",
		      area_reading = "",
		      prefecture = "",
		      city = "",
		      area = "",
	  		  musicname = "",
	  		  musicurl = "";
     number = numberField.getText(); // 念のため名前フィールドを元に
      code = codeField.getText(); // 残りの各データをもらう
      pref_reading = pref_readingField.getText();
      city_reading = city_readingField.getText();
      area_reading = area_readingField.getText();
      prefecture = prefectureField.getText();
      city = cityField.getText();
      area = areaField.getText();
      musicname = musicName.getText();
      musicurl = musicUrl.getText();
      
      try { // 新データに更新
	  prepStmt_U.setString(1, code);
	  prepStmt_U.setString(2, pref_reading);
	  prepStmt_U.setString(3, city_reading);
	  prepStmt_U.setString(4, area_reading);
	  prepStmt_U.setString(5, prefecture);
	  prepStmt_U.setString(6, city);
	  prepStmt_U.setString(7, area);
	  prepStmt_U.setString(8, musicname);
	  prepStmt_U.setString(9, musicurl);
	  prepStmt_U.setInt(10, Integer.parseInt(number));
	  prepStmt_U.executeUpdate(); 
      } catch (Exception e) {
	  e.printStackTrace();
      }
  }
  
  public void bookmark(){
	  String[] select = nameList.getSelectedItem().split(" ");
	  boolean bookmark = false;
	  try{
		  prepStmt_S.setString(1, select[0]);
    	  prepStmt_S.setString(2, select[1]);
    	  prepStmt_S.setString(3, select[2]);
    	  resultSet = prepStmt_S.executeQuery();
    	  while(resultSet.next()){
    		  bookmark = resultSet.getBoolean("bookmark");
    	  }
    	  resultSet.close();
    	  System.out.println(bookmark);
    	  if(bookmark == true){
    		  prepBookmark.setBoolean(1, false);  
    	  }else if(bookmark == false){
    		  prepBookmark.setBoolean(1, true); 
    	  }
    	  prepBookmark.setString(2, select[0]);
    	  prepBookmark.setString(3, select[1]);
    	  prepBookmark.setString(4, select[2]);
    	  prepBookmark.executeUpdate();
	  }catch (Exception e) {
	  e.printStackTrace();
      }
  }

  public void addData() { // 項目データ追加
      String number = "", code = "", pref_reading = "", city_reading = "", area_reading = "", prefecture = "", city = "", area = "";
      number = numberField.getText(); // 各データをもらう
      code = codeField.getText();
      pref_reading = pref_readingField.getText();
      city_reading = city_readingField.getText();
      area_reading = area_readingField.getText();
      prefecture = prefectureField.getText();
      city = cityField.getText();
      area = areaField.getText();
      try { // 新データを追加
	  prepStmt_I.setInt(1, Integer.parseInt(number));
	  prepStmt_I.setString(2, code);
	  prepStmt_I.setString(3, pref_reading);
	  prepStmt_I.setString(4, city_reading);
	  prepStmt_I.setString(5, area_reading);
	  prepStmt_I.setString(6, prefecture);
	  prepStmt_I.setString(7, city);
	  prepStmt_I.setString(8, area);
	  prepStmt_I.executeUpdate(); 
      } catch (Exception e) {
	  e.printStackTrace();
      }
      clearList(); // リストをクリア
      displayList(); // リストを表示
  }

  public void deleteData() { // 選択項目データ削除
      String[] select = nameList.getSelectedItem().split(" "); // 名前をもらう
      int number = 0;
      try { // 名前の行を削除
	  
      } catch (Exception e) {
	  e.printStackTrace();
      } 
      try{
		  prepStmt_S.setString(1, select[0]);
    	  prepStmt_S.setString(2, select[1]);
    	  prepStmt_S.setString(3, select[2]);
    	  resultSet = prepStmt_S.executeQuery();
    	  while(resultSet.next()){
    		  number = resultSet.getInt("number");
    	  }
    	  prepStmt_D.setInt(1, number);
    	  prepStmt_D.executeUpdate(); 
    	  resultSet.close();
    	 
	  }catch (Exception e) {
	  e.printStackTrace();
      }
      clearList(); // リストのクリア
      displayList(); // リストの表示
  }
  
  public void searchData(){
	  String code = "", pref = "" ,city = "" ,area = "";
	  code = codeSearch.getText();
	  pref = (String)combo.getSelectedItem();
	  city = citySearch.getText();
	  area = areaSearch.getText();
	  
	  try{
		  if(code.equals("")){
			  prepStmtSearch.setString(1, code + "%");
			  prepStmtSearch.setString(2, pref + "%");
			  prepStmtSearch.setString(3, city + "%");
			  prepStmtSearch.setString(4, area + "%");
		  }else if(!code.equals("")){
			  prepStmtSearch.setString(1, code + "%");
			  prepStmtSearch.setString(2, "%");
			  prepStmtSearch.setString(3, "%");
			  prepStmtSearch.setString(4, "%");
		  }
		  
		  clearList();
		  resultSet = prepStmtSearch.executeQuery();
		  while (resultSet.next()) {
			String result;
			result = resultSet.getString("prefecture") +" "+ resultSet.getString("city") +" "+ resultSet.getString("area");
			nameList.add(result);
		  } 
		  resultSet.close();
	  }catch(Exception e){
		  e.printStackTrace();
	  }
  }
  
  public void bookmarkList(){
	  try{
		  clearList();
		  resultSet = prepBookmarkList.executeQuery();
		  while (resultSet.next()) {
			String result;
			result = resultSet.getString("prefecture") +" "+ resultSet.getString("city") +" "+ resultSet.getString("area");
			nameList.add(result);
		  } 
		  resultSet.close();
	  }catch(Exception e){
		  e.printStackTrace();
	  }
  }
  
//再生ボタンのアクションクラス
		
			public void Play() {
				try {
					int status = player.getStatus();
					System.out.println(status);
					if (status == BasicPlayer.PAUSED) {
						// 一時停止の場合、一時停止を解除します
						resume();
					} else if (status == BasicPlayer.STOPPED) {
						// 停止中の場合、再生開始します
						player.open(new File(musicUrl.getText()));
						play();
					} else if (status == BasicPlayer.PLAYING) {
						// 再生中の場合、一時停止します
						pause();
					}else{
						player.open(new File(musicUrl.getText()));
						play();
					}
				} catch (Exception ex) {
					// 握りつぶす
				}
			}
	 
		// 停止ボタンのアクションクラス
			public void Stop() {
				try {
					stop();
				} catch (Exception ex) {
					// 握りつぶす
				}
			}
	 
		// 一時停止
		private void pause() throws BasicPlayerException {
			// playMode = PLAY_MODE_PAUSE;
			player.pause();
//			bPlay.setText(PLAY);
		}
	 
		// 一時停止解除
		private void resume() throws BasicPlayerException {
			player.resume();
//			bPlay.setText(PLAY);
		}
	 
		// 再生
		private void play() throws BasicPlayerException {
			player.play();
//			bPlay.setText(PAUSE);
		}
	 
		// 停止
		private void stop() throws BasicPlayerException {
			player.stop();
//			bPlay.setText(PLAY);
		}

  public void actionPerformed(ActionEvent ae) { // ボタンが押された時に行う処理
    String command = ae.getActionCommand();   // イベントからアクションコマンドを得る
    if (command.equals(bookmarkCommand)) {     // displayコマンドなら
       bookmark(); // 表示処理
    } else if (command.equals(updateCommand)) { // updateコマンドなら
       // 更新処理
    	updateData();
    } else if (command.equals(addCommand)) { // addコマンドなら
       // 追加処理
    	addData();
    } else if (command.equals(deleteCommand)) { // deleteコマンドなら
       // 削除処理
    	deleteData();
    } else if (command.equals(searchCommand)) {
    	searchData();
    } else if(command.equals(bookmarkListCommand)){
    	bookmarkList();
    }else if(command.equals(play)){
    	label.setText("タイトル名： "+ musicName.getText());
    	Play();
    }else if(command.equals(stop)){
    	label.setText("地域を選択してください");
    	Stop();
    }
  }

  public static void main(String[] argv) {
    MapMusic myFrame = new MapMusic(); // フレームの生成
    myFrame.setVisible(true);          // フレームの可視化
  }
}
