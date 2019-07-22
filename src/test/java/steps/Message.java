package steps;

import io.cucumber.java.en.Given;

public class Message {

	@Given("^Create message$")
	public String CreateMessage(String param){			
		String sFile=null;
		String sParameterFile = null;
		String sCase=null;
			
		String[] aParams = param.split(";");
		
		//Extract case from parameters
		try {			
			sCase = tools.findValue(aParams,"Case=");	
		} catch (Exception e) {}
		if(sCase == null){//if not specified take name from TC
			sCase= System.getProperty("currentTC");
		}
		
		String[] vParams = findRowData(sCase);
		try{
			CurrentMessage = sCase;						
		}
		catch(Exception e){	}
		
		//Find file name (this variable is mandatory)	
		sFile = findCellData(sCase,"File");
		if(sFile==null) {
			logger.error(String.format("ERROR-PrepareMessage: Case never found in Data spreadsheet Case=[%s] ",sCase));
			return "ERROR";
		}
		
		String[] aFiles = sFile.split(",");
		
		//check if parameter file is present
		Boolean isParameterFile = false;
		if(tools.findPosition(vParams,"ParameterFile=")!=-1){
			try{
				sParameterFile = tools.findValue(vParams,"ParameterFile=");
				if(sParameterFile.equals("%NOUSED%")) isParameterFile = false;
				else isParameterFile = true;
			}
			catch(Exception e){
				//e.printStackTrace(); 
			}
		}
		
		String sPath = null;
		try{
			sPath = tools.findValue(vParams,"Path=");
		}
		catch(Exception e){
			//e.printStackTrace(); 
		}
		
    	DateFormat df = new SimpleDateFormat("MMddhhmmss");   
    	String pattern = df.format(new Date());
		
		String outMessageName = null;
		String sFilesGenerated = null;
		
		for(int i =0; i < aFiles.length; i++){
			//check file existence
			File f = new File(Caller.TEMPLATE_FOLDER + sPath + aFiles[i]);
			if(!f.exists()) { 
				logger.error(String.format("ERROR-PrepareMessage: Template didnt found=[%s] ",aFiles[i]));
				return "ERROR";
			}
			
			String tmpMessage = filterFile(sPath + aFiles[i],vParams,0);	
			logger.info(String.format("INFO-PrepareMessage: Temporary Attachment prepared: [%s] ",tmpMessage));
			if(tmpMessage == "ERROR"){ //not all variables defined in steps data
				return "ERROR";
			}	
			
			
			if(aFiles.length != 1){ //take orginal file names
				outMessageName = aFiles[i];
				if(sFilesGenerated == null){
					sFilesGenerated = outMessageName;
				}else {
					sFilesGenerated = sFilesGenerated + "," + outMessageName;
				}				
				
			}else{
				outMessageName = CurrentMessage + "_" + pattern + ".txt";
				sFilesGenerated = outMessageName;
			}
			
			tools.copyfile(Caller.TEMPORARY_FOLDER+tmpMessage,Caller.GENERATED_FOLDER + outMessageName);
			logger.info(String.format("INFO-PrepareMessage: Attachment prepared: [%s] ",outMessageName));
			System.setProperty("LAST_FILE_GENERATED", "MESSAGE=" + outMessageName);
			
			//to check if there is expected parameter
			
			if(!param.contains("Expected=NONE")){
				try {
					StoreCheck(param);
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}
			
			
		}				
		
		if(isParameterFile){
			
			FileReader fParameterFile=null;
			String ParameterFileContent = null;
			
			String tmpParameterFile = filterFile(sParameterFile,vParams,0);
			logger.info(String.format("INFO-PrepareMessage: ParameterFile prepared: [%s] ",tmpParameterFile));
			
			try {
				fParameterFile = new FileReader(Caller.TEMPORARY_FOLDER+tmpParameterFile);
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			} 
			
			try {
				ParameterFileContent = readFile(fParameterFile);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(aFiles.length == 1){
				ParameterFileContent = ParameterFileContent.replace("%GENERATED_FILE_NAME%", outMessageName);
			}
 			
			String outParameterFileName = "ParameterFile_" +  CurrentMessage + pattern + ".txt";			
			
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(Caller.GENERATED_FOLDER + outParameterFileName));
				out.write(ParameterFileContent);
				out.close();
				fParameterFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}												
			
			logger.info(String.format("INFO-PrepareMessage: Parameter prepared for Case=[%s] ",sCase));
			
			String sPair = "MESSAGE=" + sFilesGenerated + ";PARAMETERFILE=" + outParameterFileName;			
			System.setProperty("LAST_FILE_GENERATED", sPair);
		
		}else{
			System.setProperty("LAST_FILE_GENERATED", "MESSAGE=" + sFilesGenerated);
		}		
				
		return "NORMAL";		
	}


}
