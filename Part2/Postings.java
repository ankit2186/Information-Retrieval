import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Postings 
{
    String strTerm; 
    String strDocID; 
    @SuppressWarnings("unchecked")
    ArrayList readPostingListFile(ArrayList arrlIndex)
    {
        FileReader fr = null;
        try // throws Exception
        {
            ArrayList arrlPostingList = new ArrayList();
            fr = new FileReader("Postings.txt");
            BufferedReader br = new BufferedReader(fr);
            String strPLLine = "";
            String delims = "[ ,]+";
            strPLLine = br.readLine();
            
            while( (strPLLine != null)&& (!"".equals(strPLLine.trim()) ))
            {
                Postings objPL = new Postings();
                strPLLine = strPLLine.trim();
                String strArrPLLine[] = strPLLine.split(delims);
                int iDocID = Integer.parseInt(strArrPLLine[0]);
                objPL.strDocID = Integer.toString(iDocID);
                arrlPostingList.add(objPL);
                strPLLine = "";
                strPLLine = br.readLine();
                               
                objPL = null;
            }
            arrlIndex.add(arrlPostingList);
            
        } catch (IOException ex) {
            Logger.getLogger(Postings.class.getName()).log(Level.SEVERE, null, ex);
        }  finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Postings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return arrlIndex;
    }
}