package org.oostethys.sos;

public class ParserException extends Exception {
	
	 /** Undefined error. */
    public static final int UNDEFINED_ERROR   = 0;
    /** Unknown error. */
    public static final int UNKNOWN_ERROR     = 1;
    /** The file (URL) specified by OOSTETHYS configuration does not exist or is cannot be read */
    public static final int NOT_ABLE_TO_OPEN_FILE      = 2;
    /** The variable specified in the OOSTETHYS configuration does not exist. */
    public static final int NO_SUCH_VARIABLE  = 3;
    /** The expression specified in the DODS URL is not valid. */
    
    public static String[] messages= {"UNDEFINED_ERROR","UNKNOWN_ERROR","NOT_ABLE_TO_OPEN_FILE","NO_SUCH_VARIABLE"};
    
    /** The error code. 
     * @serial
     */
     private int errorCode;
     /** The error message. 
     * @serial
     */
     private String errorMessage;
     /** The program type. 
     * @serial
     */


	public ParserException(int type, String arg0) {
		super(getMessageForError(type)+" "+arg0);
		// TODO Auto-generated constructor stub
	}



	
	public static String getMessageForError(int typeOfError){
		return messages[typeOfError];
	}
	

}
