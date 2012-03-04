import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dictionary {
    String strTerm;
    int iDocFreq;
    int iOffset;
    @SuppressWarnings("unchecked")
    ArrayList readDictionary(ArrayList arrlIndex) 
    {
        try 
        {
            ArrayList arrlDict = new ArrayList();
            Hashtable htDictionary = new Hashtable();
            Hashtable htDictOffset = new Hashtable();
            int iMinDF = -1;
            FileReader fr = new FileReader("Dictionary.txt");
            BufferedReader br = new BufferedReader(fr);
            String strDictLine = "";
            strDictLine = br.readLine();
            int loop = 0;
            do
            {
                Dictionary objDict = new Dictionary();
                objDict.strTerm = "";
                String strArr[] = strDictLine.split(" ");
                int itermCount = 2;
                for(int i = (strArr.length - 1);i >= 0;i--)
                {
                    if((strArr[i].equals("")) || (strArr[i].equals(" ")))
                    {
                    continue;
                    }
                    else
                    {
                        if(itermCount == 2)
                        {
                        objDict.iOffset = Integer.parseInt(strArr[i].trim());
                        itermCount--;
                        continue;
                        }
                        if(itermCount == 1)
                        {
                            if(i == 0)
                            {
                            objDict.iDocFreq = Integer.parseInt(Character.toString(strArr[i].trim().charAt(strArr[i].length()-1)));
                            objDict.strTerm = strArr[i].substring(0, strArr[i].length()-1);
                            break;
                            }
                            else
                            {
                            objDict.iDocFreq = Integer.parseInt(strArr[i].trim());
                            itermCount--;
                            continue;
                            }
                        }
                        if(itermCount == 0)
                        {
                        objDict.strTerm = strArr[i].trim();
                        break;
                        }
                    }
                }
                objDict.strTerm = objDict.strTerm.trim();
                arrlDict.add(objDict);
                htDictionary.put(objDict.strTerm, loop);
                htDictOffset.put(objDict.strTerm, objDict.iOffset);
                if(loop == 0)
                {
                iMinDF = objDict.iDocFreq;
                }
                else
                    if(objDict.iDocFreq < iMinDF)
                    {
                        iMinDF = objDict.iDocFreq;
                    }
                loop++;
                objDict = null;
                strDictLine = "";
                strDictLine = br.readLine();
            }while(strDictLine != null);
            arrlIndex.add(arrlDict);
            arrlIndex.add(htDictionary);
            arrlIndex.add(htDictOffset);
            arrlIndex.add(iMinDF);
            fr.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
        return arrlIndex;
    }
}