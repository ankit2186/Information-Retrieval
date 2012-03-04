import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
/**
 *
 * @author Ankit,Mahesh
 */
public class DocsTable
{

    String strDocName;
    String strDocNum;
    String strTitle;   

    /* reads DocTable.txt and populate arraylist*/
    @SuppressWarnings("unchecked")
    ArrayList readDocTable() throws Exception 
    {
        ArrayList arrlDocTb = new ArrayList();

        FileReader fr = new FileReader("DocsTable.txt");
        BufferedReader br = new BufferedReader(fr);
        String delims = "[ ]+";
        String strDocTLine = "";
        strDocTLine = br.readLine();
        int flag = 0;

        do
        {
            DocsTable objDocTB = new DocsTable();
            objDocTB.strTitle = "";
            String strArr[] = strDocTLine.split(delims);
            int itermCount = 0;
            flag = 0;
            for (int i = 0; i < strArr.length; i++)      
            {
                    if(itermCount == 0)
                    {
                    objDocTB.strDocNum = strArr[i].trim();
                    itermCount++;
                    continue;
                    }
                    if(((strArr[i].equals("")) || (strArr[i].equals(" "))&&(flag ==0)))
                    {
                        continue;
                    }
                    if(itermCount == 1)
                    {
                        flag = 1;
                    objDocTB.strTitle = objDocTB.strTitle + " " + strArr[i].trim();
                    
                    //itermCount--;
                    }              

            }//end of for
            objDocTB.strTitle = objDocTB.strTitle.trim();
            arrlDocTb.add(objDocTB);

            strDocTLine = "";
            objDocTB = null;
            strDocTLine = br.readLine();
        }while(strDocTLine != null);

        fr.close();
        return arrlDocTb;
    }
}


