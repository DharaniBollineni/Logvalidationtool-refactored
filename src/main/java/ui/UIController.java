package ui;

import java.awt.Desktop;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;

import constant.ClusterAttribute;
import constant.ClusterType;
import handler.LogHandler;
import handler.MainHandler;
import handler.PageHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import storage.DataManager;
import util.ClusterImp;
import util.LogReader;


public class UIController {

	private static Server server = null;
	private ToggleGroup inputMode = new ToggleGroup();

	@FXML private Parent root;
	@FXML private RadioButton cacheRB;
	@FXML private RadioButton rawRB;

	@FXML private GridPane cacheFields;
	@FXML private TextField cacheFileField;
	private FileChooser cacheFC=new FileChooser();
	private File cacheFile=null;
	@FXML private Text cacheFileError;

	@FXML private GridPane rawFields;
	@FXML private TextField logFolderField;
	private DirectoryChooser logDC=new DirectoryChooser();
	private File logsDir=null;
	@FXML private Text logFolderError;

	@FXML private TextField courseFolderField;
	private DirectoryChooser courseDC=new DirectoryChooser();
	private File courseDir=null;
	@FXML private Text courseFolderError;

	@FXML private TextField surveyFilesField;
	private FileChooser surveyFC=new FileChooser();
	private List<File> surveyFiles=null;
	@FXML private Text surveyFilesError;

	@FXML private TextField newCacheFileField;
	private FileChooser newCacheFC=new FileChooser();
	private File newCacheFile=null;
	@FXML private Text newCacheFileError;

	@FXML private TextField userIgnoreListField;
	private FileChooser userIgnoreListFC=new FileChooser();
	private File userIgnoreList=null;	//file with list of usernames to ignore
	@FXML private Text userIgnoreListError;

	@FXML private TextField customFilterListField;
	private FileChooser customFilterListFC=new FileChooser();
	private File customFilterList=null;	//file with list of user IDs used by custom filter
	@FXML private Text customFilterListError;

	@FXML private TextField portField;
	@FXML private Text portError;

	@FXML private Button startBtn;
	@FXML private Button stopBtn;

	@FXML private Text progressText;
	@FXML private Text exceptionText;
	
	
	@FXML private CheckBox clusteringRB;
	@FXML private ChoiceBox algorithmChoice;
	@FXML private CheckBox expectedMovement;
	@FXML private CheckBox forwardMovement;
	@FXML private CheckBox backwardMovement;
	@FXML private CheckBox visited;
	@FXML private CheckBox revisited;
	@FXML private CheckBox viewByMovement;
	@FXML private CheckBox postByStatement;
	@FXML private CheckBox statementByView;
	
	@FXML private TextField clusterNum;
	
	private String algorithm = null;
	private int clusterNumber;
	private List<String> choosenAttr = new ArrayList<String>();


	/**Helper method to update UI after selecting a file or folder from a chooser window.
	 * @param	chooser		The FileChooser or DirectoryChooser used to pick selectedFile.
	 * 						Can be null, just won't change initial directory if it's null.*/
	private void updateUI(File selectedFile, Object chooser, TextField fileField, Text errorText) {
		if(selectedFile!=null) {
			fileField.setText(selectedFile.getPath());
			fileField.positionCaret(fileField.getLength());	//scrolls to end

			if(chooser instanceof FileChooser)
				((FileChooser) chooser).setInitialDirectory(selectedFile.getParentFile());
			else if(chooser instanceof DirectoryChooser)
				((DirectoryChooser) chooser).setInitialDirectory(selectedFile.getParentFile());

			errorText.setText("");
		}
		else
			fileField.setText("");
	}


	@FXML protected void selectCacheFile(ActionEvent event){
		cacheFile=cacheFC.showOpenDialog(root.getScene().getWindow());
		updateUI(cacheFile, cacheFC, cacheFileField, cacheFileError);
	}


	@FXML protected void selectLogFolder(ActionEvent event){
		logsDir=logDC.showDialog(root.getScene().getWindow());
		updateUI(logsDir, logDC, logFolderField, logFolderError);
	}


	@FXML protected void selectCourseFolder(ActionEvent event){
		courseDir=courseDC.showDialog(root.getScene().getWindow());
		updateUI(courseDir, courseDC, courseFolderField, courseFolderError);

		//if logs folder not set, auto-set logs folder based on course folder
		if(logsDir==null/*logFolderField.getText().isEmpty()*/) {
			File logsDir_=new File(courseDir, "logs");
			if(logsDir_.exists()) {	//note: not case sensitive
				logsDir=logsDir_;
				updateUI(logsDir, logDC, logFolderField, logFolderError);
			}
		}
	}


	@FXML protected void selectSurveyResultFiles(ActionEvent event) {
		surveyFiles=surveyFC.showOpenMultipleDialog(root.getScene().getWindow());

		if(surveyFiles!=null) {
			StringBuilder sb = new StringBuilder(100);
			for(File surveyFile : surveyFiles) {
				sb.append(surveyFile.getPath());
				sb.append("; ");
			}
			sb.delete(sb.length()-2, sb.length());	//remove last "; "

			surveyFilesField.setText(sb.toString());
			surveyFilesField.positionCaret(surveyFilesField.getLength());	//scrolls to end
			surveyFC.setInitialDirectory(surveyFiles.get(0).getParentFile());
			surveyFilesError.setText("");
		}
		else
			surveyFilesField.setText("");
	}


	@FXML protected void selectCacheOutFile(ActionEvent event){
		newCacheFile=newCacheFC.showSaveDialog(root.getScene().getWindow());
		updateUI(newCacheFile, newCacheFC, newCacheFileField, newCacheFileError);
	}


	@FXML protected void selectUserIgnoreList(ActionEvent event){
		userIgnoreList=userIgnoreListFC.showOpenDialog(root.getScene().getWindow());
		updateUI(userIgnoreList, userIgnoreListFC, userIgnoreListField, userIgnoreListError);
	}


	@FXML protected void selectCustomFilterList(ActionEvent event) {
		customFilterList=customFilterListFC.showOpenDialog(root.getScene().getWindow());
		updateUI(customFilterList, customFilterListFC, customFilterListField, customFilterListError);
	}


	@FXML protected void handlePortChange(ObservableValue<? extends String> ov, String prev, String curr){
		portError.setText("");
	}

	@FXML protected void handleStopServer(ActionEvent event) throws Exception {
		serverStop();
		stopBtn.setDisable(true);
		startBtn.setDisable(false);
		startBtn.setText("START");
	}
	
    protected void handleRadioChange(ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) {
		if(newToggle==cacheRB){
			cacheFields.setDisable(false);
		}
		else if(newToggle==rawRB){
			rawFields.setDisable(false);
		}

		if(oldToggle==cacheRB){
			cacheFields.setDisable(true);
			cacheFileError.setText("");
		}
		else if(oldToggle==rawRB){
			rawFields.setDisable(true);
			logFolderError.setText("");
			courseFolderError.setText("");
			newCacheFileError.setText("");
			userIgnoreListError.setText("");
		}
	}


	@FXML protected void handleStart(ActionEvent event) {

		//front-end validation - just check that required fields aren't empty
		// check always-required fields
		if(portField.getText().isEmpty()){
			portError.setText("Port required");
			return;
		}

		// check sometimes-required fields
		if(inputMode.getSelectedToggle()==cacheRB){
			//check fields required with the cacheRB radio button
			if(cacheFile==null){
				cacheFileError.setText("Required");
				return;
			}
		}
		
		else if(inputMode.getSelectedToggle()==rawRB){
			//check fields required with the rawRB radio button
			if(logsDir==null){
				logFolderError.setText("Required");
				return;
			}
			
			if(courseDir==null){
				courseFolderError.setText("Required");
				return;
			}
		}


		startBtn.setText("Reading data...");
		startBtn.setDisable(true);
		stopBtn.setDisable(false);
		exceptionText.setText("");

		//from https://stackoverflow.com/questions/26554814/javafx-updating-gui
		//also see https://docs.oracle.com/javase/8/javafx/api/javafx/concurrent/Task.html
		Task<Void> task=new Task<Void>(){
			@Override
			protected Void call() throws Exception {
				//create data structures used to respond to http requests
				//TODO: can this be done asynchronously from the server start?

				//Call extractData OR loadProcessedData, depending on radio button setting.
				// Either will put the required data into DataManager's static fields.
				if(inputMode.getSelectedToggle()==cacheRB)
					DataManager.loadProcessedData(cacheFile, customFilterList);

				else if(inputMode.getSelectedToggle()==rawRB) {
					//DataManager.extractData(courseDir, logsDir, surveyFiles, newCacheFile, userIgnoreList, customFilterList);
					DataManager.extractData(courseDir, logsDir, surveyFiles, newCacheFile, userIgnoreList, customFilterList);
					//System.out.println("customerFilterList "+ customFilterList);
				
				}
				
				if(clusteringRB.isSelected()) {
					choosenAttr = this.getChoosenList();
					clusterNumber = new Integer(clusterNum.getText());
					algorithm = algorithmChoice.getSelectionModel().getSelectedItem().toString();
					DataManager.clusterImp(algorithm, clusterNumber, choosenAttr);
				}
				//start jetty server //TODO: can PageHandler be replaced by something else that comes with Jetty?
				server = new Server(Integer.parseInt(portField.getText()));
				server.setHandler(new HandlerCollection(
						new HandlerList(new PageHandler(), new MainHandler(), new DefaultHandler()),
						new LogHandler()
						));

				server.start();		//throws java.net.BindException if port is unavailable
				//server.join();	//don't know what this does

				
				return null;
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				startBtn.setText("Server Started");

				//open default browser to view diagrams
				// from https://stackoverflow.com/questions/17581455/opening-a-url-in-the-default-browser
				try {
					Desktop.getDesktop().browse(
							new URI("http://localhost:"+portField.getText()+"/index.html"));
				}
				catch (IOException e) {
					exceptionText.setText("Could not launch default browser.");
				}
				catch (URISyntaxException e) {
					exceptionText.setText(e.getClass()+" : "+e.getMessage());
				}
				catch (Exception e) {
					exceptionText.setText(e.getClass()+" : "+e.getMessage());
				}
			}

			@Override
			protected void failed() {
				super.failed();
				try{
					throw this.getException();
				}
				catch(java.net.BindException jbe){
					portError.setText("This port is not available");
				}
				catch(Throwable th){
					exceptionText.setText(th.toString());

					//print entire stack trace to a file
					try(PrintWriter errorLog = new PrintWriter(new File("errorLog.txt"))){
						th.printStackTrace(errorLog);
					}
					catch (FileNotFoundException e) {}	//if could not create error log file, just continue
				}

				startBtn.setText("Start");
				startBtn.setDisable(false);
			}
			
			public List<String> getChoosenList(){
				List<String> choosenAttributes = new ArrayList<String>();
				
				if(expectedMovement.isSelected()) 
					choosenAttributes.add(ClusterAttribute.EXPECTEDMOVEMENT);
				if(forwardMovement.isSelected())
					choosenAttributes.add(ClusterAttribute.FORWARDMOVEMENT);
				if(backwardMovement.isSelected())
					choosenAttributes.add(ClusterAttribute.BACKWARDMOVEMENT);
				if(visited.isSelected())
					choosenAttributes.add(ClusterAttribute.VISITED);
				if(revisited.isSelected())
					choosenAttributes.add(ClusterAttribute.REVISITED);
				if(viewByMovement.isSelected())
					choosenAttributes.add(ClusterAttribute.VIEWBYMOVEMENT);
				if(postByStatement.isSelected())
					choosenAttributes.add(ClusterAttribute.POSTBYSTATEMENT);
				if(statementByView.isSelected())
					choosenAttributes.add(ClusterAttribute.STATEMENTBYVIEW);
				return choosenAttributes;
			}

		};

		//register event handler for progress changes
//		try {
//		DataManager.logsDoneProperty().addListener(
//				(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) -> {
//					progressText.setText(newValue+" of "+LogReader.numLogs+" logs read.");
//				});
//		}catch(Exception e) {
//			System.out.println("error");
//		}
		new Thread(task).start();
	
	}


	/**Stop the server.
	 * @throws	NullPointerException	if the Server object hasn't yet been created.*/
	public static void serverStop() throws Exception{
		//DataManager.initial();
		server.stop();
		server.destroy();	
		DataManager.deleteDataInstance();
	}
	

	
	/**This gets called automatically once the matching .fxml file has been loaded.*/
	public void initialize(){
		System.out.println("UIController.initialize() called");

		File currentDir=new File(System.getProperty("user.dir"));

		//setup file choosers
		cacheFC.setTitle("Select cached data file");
		cacheFC.setInitialDirectory(currentDir);
		cacheFC.getExtensionFilters().addAll(
				new ExtensionFilter("Serialized Object files", "*.ser"),
				new ExtensionFilter("All files", "*"));

		logDC.setTitle("Select folder containing log files");
		logDC.setInitialDirectory(currentDir);

		courseDC.setTitle("Select folder containing course data files");
		courseDC.setInitialDirectory(currentDir);

		surveyFC.setTitle("Select all files containing Qualtrix survey results");
		surveyFC.setInitialDirectory(currentDir);
		surveyFC.getExtensionFilters().addAll(
				new ExtensionFilter("Comma-separated values files", "*.csv"),
				new ExtensionFilter("All files", "*"));

		newCacheFC.setTitle("Save processed data as...");
		newCacheFC.setInitialDirectory(currentDir);
		newCacheFC.getExtensionFilters().addAll(
				new ExtensionFilter("Serialized Object files", "*.ser"));

		userIgnoreListFC.setTitle("Select file with list of users to ignore");
		userIgnoreListFC.setInitialDirectory(currentDir);
		userIgnoreListFC.getExtensionFilters().addAll(
				new ExtensionFilter("Text files", "*.txt"),
				new ExtensionFilter("All files", "*"));

		customFilterListFC.setTitle("Select file with list of users for custom filter");
		customFilterListFC.setInitialDirectory(currentDir);
		customFilterListFC.getExtensionFilters().addAll(
				new ExtensionFilter("Text files", "*.txt"),
				new ExtensionFilter("All files", "*"));
		
		cacheRB.setToggleGroup(inputMode);
		rawRB.setToggleGroup(inputMode);
		
		inputMode.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldToggle, Toggle newToggle) {
		         if (inputMode.getSelectedToggle() != null) {

				if(newToggle==cacheRB){
					System.out.print("1");
					cacheFields.setDisable(false);
				}
				else if(newToggle==rawRB){
					System.out.print("2");

					rawFields.setDisable(false);
				}

				if(oldToggle==cacheRB){
					System.out.print("3");

					cacheFields.setDisable(true);
					cacheFileError.setText("");
				}
				else if(oldToggle==rawRB){
					System.out.print("4");

					rawFields.setDisable(true);
					logFolderError.setText("");
					courseFolderError.setText("");
					newCacheFileError.setText("");
					userIgnoreListError.setText("");
				}
				
		         }
			}
			
		});
		
		rawRB.setSelected(true);
		cacheFields.setDisable(true);
		expectedMovement.setSelected(true);
		forwardMovement.setSelected(true);
		backwardMovement.setSelected(true);
		visited.setSelected(true);
		revisited.setSelected(true);
		viewByMovement.setSelected(true);
		postByStatement.setSelected(true);
		statementByView.setSelected(true);
		
		expectedMovement.setTooltip(new Tooltip("Students follow the order from the previous "
				+ "section to the next section defined as a expected movement.\n"
				+ "The value is the proportion of expected movements to total movements"));
		forwardMovement.setTooltip(new Tooltip("Students skip next section and jump forward to other section defined as "
				+ "forward movment.\n"
				+ "The value is the proportion of forward movements to total movments"));
		backwardMovement.setTooltip(new Tooltip("Students go back to the previous sections defined as backward movement.\n"
				+ "The value is the proportion of backward movements to total movements "));
		visited.setTooltip(new Tooltip("Number of visits is the amount of sections that students first visits.\n"
				+ "The value is the proportion of number of visits to number of total visits "));
		revisited.setTooltip(new Tooltip("Number of revisits is the amount of sections that students revisits.\n"
				+ "The value is the proportion of number of revisits to number of total visits "));
		viewByMovement.setTooltip(new Tooltip("The value is the propotion of times of viewing discussion post to total movments"));
		postByStatement.setTooltip(new Tooltip("The value is the propotion of post times to total statements"));
		statementByView.setTooltip(new Tooltip("The value is the propotion of total statmens to times of viewing discussion post.\n"
				+ "Regarding to some students post a statement without viewing other posts,\n"
				+ "the value is equal to -1 if the viewing time is 0"));
		algorithmChoice.setItems(FXCollections.observableArrayList(
				ClusterType.EM,
				ClusterType.KMEAN,
				ClusterType.CANOPY
				));
		stopBtn.setDisable(true);
		
		algorithmChoice.getSelectionModel().selectFirst();
		algorithmChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				//set the value in DataManager
			}
			
		});
	}
}

