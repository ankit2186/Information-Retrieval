//package Index;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
/**
 *
 * @author Ankit,Mahesh
 */
public class Postings {
    String strTerm; 
    String strDocID; 
    int iWtTitle; 
    int iWtText;
    int Zone0;
    int Zone1;
    ArrayList arlBodyPosition = new ArrayList();
    ArrayList arlTitlePosition = new ArrayList();
    int Flag = 0;
    
    int writeDocsTableEntry(ArrayList arrlDC)
    {
        int iReturn = -1;
        int iCounter = 0;
        String strFileLine = "";
        String tempBody = "";
        String tempTitle = "";
        try
        {
            iReturn = 1;
            BufferedWriter out = new BufferedWriter(new FileWriter("Postings.txt"));
 //           out.write("DocID       Zone 1            Zone2  \n");
 //           out.write("           Position         Position  \n");
 //           out.write("----------------------------------------\n");
            for(;iCounter < arrlDC.size();iCounter++)
            {
                Postings objPL = new Postings();
                objPL = (Postings)arrlDC.get(iCounter);
                if (objPL.iWtTitle == 0)
                {
                    tempTitle = convertArrayListToString(objPL.arlTitlePosition);
                    strFileLine = objPL.strDocID + "          " +     objPL.iWtTitle + "         " +tempTitle + "         "+objPL.strTerm;
                }
                if (objPL.iWtTitle == 1)
                {
                    tempBody = convertArrayListToString(objPL.arlBodyPosition);
                    strFileLine = objPL.strDocID + "                      " + tempBody ;
                }    
              
                if (objPL.Zone1 == 1 && objPL.Zone0 == 1){
                    tempTitle = convertArrayListToString(objPL.arlTitlePosition);
                    tempBody = convertArrayListToString1(objPL.arlBodyPosition);
                    strFileLine = objPL.strDocID + "               " +tempTitle + "            "+tempBody;
                }
                out.write(strFileLine);
                out.newLine();
                objPL = null;
                strFileLine = "";
            }
            out.close();
            return iReturn;
        }
        catch(Exception ex)
        {
            iReturn = -1;
            return iReturn;
        }       
    }
    
 String convertArrayListToString (ArrayList arlPosition)
 {
        Iterator itr = arlPosition.iterator();
        String strVal="";
        while(itr.hasNext())
        {
            strVal+= itr.next().toString()+",";
        }
        int lastIndex = strVal.lastIndexOf(",");
        strVal = strVal.substring(0,lastIndex);
        return strVal;
}

String convertArrayListToString1 (ArrayList arlPosition)
 {
        Iterator itr = arlPosition.iterator();
        String strVal="";
        while(itr.hasNext())
        {
            strVal+= itr.next().toString()+",";
        }
        int lastIndex = strVal.lastIndexOf(",");
        strVal = strVal.substring(1,lastIndex);     
        strVal = strVal.replaceFirst(",", "");
        return strVal;
} 
}
