//package Index;

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
/**
 *
 * @author Ankit,Mahesh
 */

class Document {
    int iOldOffSet;
    int iOldDf;
    int iNextTermOS;
    String strDocName;
    String strDocNum;
    String strTitle;
    String strRate;
    String strBody;
    String strHiphenFwdSlash = "";
    String strTitleArray[];
    String strBodyArray[];
    String strTermsHiphenFwdSlash;
    String strHiphenFwdSlashArr[];
    String strReson;
    String strNewTerm; 
    String strNextTerm;
    ArrayList arlTags = new ArrayList();
    ArrayList arlTagsArray = new ArrayList();
    ArrayList arrlUpdatedDictionary = new ArrayList();
    boolean blnIsValid;
}

public class DocsTable
{
    String strDocName;
    String strDocNum;
    String strTitle;
    String strRate;
    
    String cleanBodyString(String strBodyString)
    {
        String strCleaned;
        String strCleanedArray;
        String strFooter;
        int iIndexFooter = -1;
        strBodyString = strBodyString.trim();
        if(strBodyString.contains("\r\n"))
        {
            strCleaned = strBodyString.replaceAll("[\n\r]", " ");
            strCleaned = strCleaned.replaceAll("[\r\n]", " ");
            strCleaned = strCleaned.replaceAll("\\b\\s{2,}\\b", " ");
        }
        else
        {        
            strCleaned = strBodyString;
            strCleaned = strCleaned.replaceAll("\\b\\s{2,}\\b", " ");
        }
        if(strBodyString.contains("\n"))
        {
            strCleaned = strBodyString.replaceAll("[\n]", " ");
            strCleaned = strCleaned.replaceAll("\\b\\s{2,}\\b", " ");
        }
        if(strBodyString.contains("\r"))
        {
            strCleaned = strBodyString.replaceAll("[\n]", " ");
            strCleaned = strCleaned.replaceAll("\\b\\s{2,}\\b", " ");
        }
        strCleaned = strCleaned.replaceAll(",", " ");
        strCleaned = strCleaned.replaceAll(";", " ");
        strCleaned = strCleaned.replaceAll("[\"]", " ");
        strCleaned = strCleaned.replaceAll("!", " ");
        strCleaned = strCleaned.replaceAll("%", " ");
        strCleaned = strCleaned.replaceAll("\\*", " ");
        strCleaned = strCleaned.replaceAll("\\+", " ");
        strCleaned = strCleaned.replaceAll("\\(", " ");
        strCleaned = strCleaned.replaceAll("\\)", " ");
        strCleaned = strCleaned.replaceAll("\\{", " ");
        strCleaned = strCleaned.replaceAll("\\}", " ");
        strCleaned = strCleaned.replaceAll("\\[", " ");
        strCleaned = strCleaned.replaceAll("\\]", " ");
        strCleaned = strCleaned.replaceAll("\\\\", " ");
        strCleaned = strCleaned.replaceAll("\\<", " ");
        strCleaned = strCleaned.replaceAll("\\>", " ");
        strCleaned = strCleaned.replaceAll(":", " ");
        strCleaned = strCleaned.replaceAll("=", " ");
        strCleaned = strCleaned.replaceAll("\\^", " ");
        strCleaned = strCleaned.replaceAll("--", " ");
        if(strCleaned.contains("\r\n"))
        {
            strCleaned = strCleaned.replaceAll("[\n\r]", " ");
        }
        strCleaned = strCleaned.replaceAll("\\b\\s{2,}\\b", " ");
        if(strCleaned.contains("\n"))
        {
            strCleaned = strCleaned.replaceAll("[\n]", " ");
        }
        if(strCleaned.contains("\r"))
        {
            strCleaned = strCleaned.replaceAll("[\r]", " ");
        }
        iIndexFooter = strCleaned.indexOf("The review above was posted to");
        if(iIndexFooter != -1)
        {
            strFooter = strCleaned.substring(iIndexFooter, strCleaned.length());
            strCleaned = strCleaned.replace(strFooter, " ");
        }
        strCleanedArray = strCleaned;
        return strCleanedArray;
    }

    int writeDocsTableEntry(ArrayList arrlDC)
    {
        int iReturn = -1;
        int iCounter = 0;
        String strFileLine = "";
        String strTemp = "";

        try
        {
            iReturn = 1;
            BufferedWriter out = new BufferedWriter(new FileWriter("DocsTable.txt"));
//            out.write("DocNumber       Title \n");
//            out.write("---------------------------------\n");
            for(;iCounter < arrlDC.size();iCounter++)
            {
                DocsTable objDocTb = new DocsTable();
                objDocTb = (DocsTable)arrlDC.get(iCounter);
                strTemp = objDocTb.strTitle;
                while(strTemp.length() <= 65)
                {
                strTemp = strTemp + " ";
                }
                strFileLine = objDocTb.strDocNum + "            " + strTemp + "    " ;
                out.write(strFileLine);
                out.newLine();
                objDocTb = null;
                strFileLine = "";
                strTemp = "";            
            }           
            out.close();
            return iReturn;
        }
        catch(Exception ex)
        {
            iReturn = -1;
            System.out.println("Error !!"+ex.getMessage());
            return iReturn;

        }
    }
    
    DocsTable findDocContents(Document objDoc)
    {
        DocsTable objDocTable = new DocsTable();
        String strText = "";
        boolean blnHasPhrase = false;
        blnHasPhrase = true;
        strText = objDoc.strBody;
        objDocTable.strDocNum = objDoc.strDocNum;
        objDocTable.strTitle = objDoc.strTitle.trim();
        return objDocTable;
    } 
}
