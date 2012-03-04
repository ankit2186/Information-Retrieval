import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Results  {

    String strDocID;
    String strTitle; 
    static int cnt = 1;
    void printResults(ArrayList arrlResults)
    {
        BufferedWriter bw = null;
        try // throws Exception
        {
            bw = new BufferedWriter(new FileWriter("output.txt",true));
            bw.write("Query "+ cnt + ": ");
            cnt++;
            bw.write((String)arrlResults.get(arrlResults.size()- 1));
            bw.newLine();
            arrlResults.remove(arrlResults.size()- 1);
            if(arrlResults.isEmpty())
            {
                bw.write("NO RESULT");
                System.out.println("NO RESULT");
                bw.newLine();
                bw.close();
                return;
            }
                for(int i = 0;i < (arrlResults.size());i++)
                {
                    Results objRes = (Results)arrlResults.get(i);
                    String strRes = "";
                    strRes = objRes.strDocID +"  "+ objRes.strTitle;
                    System.out.println(strRes);
                    bw.write(strRes);
                    bw.newLine();
                }
     
            bw.newLine();
            bw.newLine();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Results.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Results.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
class Calculation
{   @SuppressWarnings("unchecked")
    ArrayList Calc(ArrayList arrlQuery,ArrayList arrlIndex,int[] iPostingList)
    {
        ArrayList arrlResults = new ArrayList();
        int iCounter = 0;
        if(iPostingList.equals(0))
            return arrlResults;
        while(iCounter < iPostingList.length)
        {
            Results objRes = findPostings(iPostingList[iCounter], arrlQuery, arrlIndex);
            arrlResults.add(objRes);
            iCounter++;
        }
        return arrlResults;
    }
    
    Results findPostings(int iDocID, ArrayList arrlQuery,ArrayList arrlIndex)
    {
    Results objResult = new Results();
    ArrayList arrlDocTb = (ArrayList)arrlIndex.get(0);
    for(int i = 0; i < arrlDocTb.size(); i++)
    {
        DocsTable objDocTb = (DocsTable)arrlDocTb.get(i);
        int iID = (Integer.parseInt(objDocTb.strDocNum));
        if(iDocID == iID)
        {
            objResult.strDocID = objDocTb.strDocNum;
            objResult.strTitle = objDocTb.strTitle;
            break;
        }
    }
    return objResult;
    }

    int getOffset(String strTerm, ArrayList arrlIndex)
    {
    int iOffset = -1;
    int iIndex = -1;
    ArrayList arrlDict = (ArrayList)arrlIndex.get(1);
    Hashtable htDict = (Hashtable)arrlIndex.get(2);
    Dictionary objDict = new Dictionary();
    if(htDict.get(strTerm.trim()) == null)
        return iOffset;
    else
        iIndex = Integer.parseInt((htDict.get(strTerm.trim())).toString());
    objDict = (Dictionary)arrlDict.get(iIndex);
    iOffset = objDict.iOffset;
    return iOffset;
    }
}