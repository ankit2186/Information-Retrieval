import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;

public class Query {
    String strAndOR;
    String strQueryTerms[];
    boolean blnIsOneWordQuery = false;
    boolean blnInText = false;
    boolean blnInTitle = false;
    int iPostingList[];
        
    int[] getPostingList(ArrayList arrlIndex, String strTerm, String strZone)
    {
        int iPL[] = new int[0];
        try // throws Exception
        {
            int iPLLength = 1;
            ArrayList arrlDict = (ArrayList)arrlIndex.get(1);
            Hashtable htDict = (Hashtable)arrlIndex.get(2);
            Dictionary objDict = new Dictionary();
            FileReader fr = new FileReader("Postings.txt");
            BufferedReader br = new BufferedReader(fr);
            String strPLLine = "";
            strPLLine = br.readLine();
            strPLLine = strPLLine.trim();
            int iIndex = -1;
            if(htDict.get(strTerm.trim()) == null)
            {
            return iPL;
            }
            else
            {
            iIndex = Integer.parseInt((htDict.get(strTerm.trim())).toString());
            }
            objDict = (Dictionary)arrlDict.get(iIndex);
            int iOffset = objDict.iOffset;
            int iDF = objDict.iDocFreq;
            int iCount = 0;
            while(strPLLine != null)
            {
                if(iCount < iOffset)
                {
                strPLLine = "";
                strPLLine = br.readLine();
                strPLLine = strPLLine.trim();
                iCount++;
                continue;
                }
                else
                {
                    if(iCount < (iOffset + iDF))
                    {
                        String strArrPLLine[] = strPLLine.split("    ");
                        int iDocID = Integer.parseInt(strArrPLLine[0].trim());
                        if(strZone.equals("INTEXT")) 
                        {
                            iPL = expand(iPL, iPLLength);
                            iPL[iPLLength-1] = iDocID;
                        }
                        if(strZone.equals("INTITLE") )
                        {
                            iPL = expand(iPL, iPLLength);
                            iPL[iPLLength-1] = iDocID;
                        }
                        iCount++;
                        iPLLength++;
                    }
                    else
                    {
                    break;
                    }
                }
            strPLLine = "";
            strPLLine = br.readLine();
            strPLLine = strPLLine.trim();
            }
            fr.close();
            
            
        } catch (IOException ex) {
            Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
        }
        Arrays.sort(iPL);
        return iPL;
    }
    @SuppressWarnings("unchecked")
    ArrayList parseQuery(String strUserQuery)
    {
        ArrayList arrlRet = new ArrayList();
        int iCounter = 0;
        int iCounter1 = 0;
        String strQuery1[] = strUserQuery.split("\\(");
        String strQuery[] = new String[strQuery1.length-1];
        for(int i = 1; i < strQuery1.length; i++)
        {
        strQuery[i-1] = strQuery1[i];
        }
        String strTempQuery[];
        while(iCounter1 < strQuery.length)
        {
        strQuery[iCounter1] = strQuery[iCounter1].trim();
        strQuery[iCounter1] = strQuery[iCounter1].replace(")", "");
        iCounter1++;
        }
        while(iCounter < strQuery.length)
        {
        Query objQuery = new Query();
        strTempQuery = strQuery[iCounter].split(" ");
        if(strTempQuery.length == 2)
        {
            objQuery.strQueryTerms = new String[1];
            {
              objQuery.blnIsOneWordQuery = true;
              objQuery.strQueryTerms[0] = strTempQuery[0];
                if(strTempQuery[strTempQuery.length - 1].equals("INTEXT"))
                {
                objQuery.blnInText = true;
                }
                else
                {
                objQuery.blnInTitle = true;
                }
            }
        }
        else
        {
            int iTempC = 1;
            if(strTempQuery[0].equals("AND"))
            {
            objQuery.strAndOR = "AND";
            }
            else
            {
            objQuery.strAndOR = "OR";
            }
            if(strTempQuery[strTempQuery.length - 1].equals("INTEXT"))
            {
            objQuery.blnInText = true;
            }
            else
            {
            objQuery.blnInTitle = true;
            }
            objQuery.strQueryTerms = new String[strTempQuery.length-2];
            while(iTempC < (strTempQuery.length - 1))
            {
                objQuery.strQueryTerms[iTempC - 1] = strTempQuery[iTempC];
                iTempC++;
            }
        }
        arrlRet.add(objQuery);
        iCounter++;
        }
        return arrlRet;
    }
    @SuppressWarnings("unchecked")
    ArrayList processQuery(ArrayList arrlQuery, ArrayList arrlIndex)
    {
        ArrayList arrlResults = new ArrayList();
        ArrayList arrlInterMedRes = new ArrayList();
        int iCounter = 0;

        while(iCounter < arrlQuery.size())
        {
            Query objQuery = new Query();
            objQuery = (Query)arrlQuery.get(iCounter);
            if(objQuery.blnIsOneWordQuery)
            {
                try {
                    int iPL[] = processSingleTermQuery(objQuery,arrlIndex);
                        if(iPL.length > 0)
                        {
                        arrlInterMedRes.add(iPL);
                        }
                } catch (Exception ex) {
                    Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
            int iPL[] = processMultiTermQuery(objQuery,arrlIndex);
                if(iPL.length > 0)
                {
                arrlInterMedRes.add(iPL);
                }
            }
        iCounter++;
        }
        if(arrlInterMedRes.size() == 1)
        {
        int iPL[] = (int[])arrlInterMedRes.get(0);
        arrlResults.add(iPL);
        }
        else
            if(arrlInterMedRes.size() == 2)
            {
            int iPL[] = intersectPostingLists((int[])arrlInterMedRes.get(0), (int[])arrlInterMedRes.get(1));
            arrlResults.add(iPL);
            }
            else
                if(arrlInterMedRes.size() == 3)
                {
                int iPL1[] = intersectPostingLists((int[])arrlInterMedRes.get(0), (int[])arrlInterMedRes.get(1));
                int iPL2[] = intersectPostingLists(iPL1, (int[])arrlInterMedRes.get(2));
                arrlResults.add(iPL2);
                }
                else
                {
                int iPL[] = new int[0];
                arrlResults.add(iPL);
                }
        return arrlResults;
    }
    @SuppressWarnings("unchecked")
    int[] processSingleTermQuery(Query objQuery, ArrayList arrlIndex) 
    {
       ArrayList arrlInterMedRes = new ArrayList();
   
    if((objQuery.blnInText))
    {
        String strTempTerms[] = processToken(objQuery.strQueryTerms[0].trim());
        for(int i = 0;i < strTempTerms.length;i++)
        {
                try {
                    int iPL[] = getPostingList(arrlIndex, strTempTerms[i].trim(), "INTEXT");
                        if(iPL.length > 0)
                        {
                            arrlInterMedRes.add(iPL);
                        }
                } catch (Exception ex) {
                    Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        if(arrlInterMedRes.size() == 1)
        {
        int iPL[] = (int[])arrlInterMedRes.get(0);
        return iPL;
        }
        else
            if(arrlInterMedRes.size() == 2)
            {
                int iPL[] = mergePostingLists((int[])arrlInterMedRes.get(0), (int[])arrlInterMedRes.get(1));
                return iPL;
            }
            else
                if(arrlInterMedRes.size() == 3)
                {
                int iPL1[] = mergePostingLists((int[])arrlInterMedRes.get(0), (int[])arrlInterMedRes.get(1));
                int iPL2[] = mergePostingLists(iPL1, (int[])arrlInterMedRes.get(2));
                return iPL2;
                }
                else
                {
                    int iPL1[] = new int[0];
                    return iPL1;
                }
    }
    else
    {
        String strTempTerms[] = processToken(objQuery.strQueryTerms[0].trim());
        for(int i = 0;i < strTempTerms.length;i++)
        {
                try {
                    int iPL[] = getPostingList(arrlIndex, strTempTerms[i].trim(), "INTITLE");
                        if(iPL.length > 0)
                        {
                            arrlInterMedRes.add(iPL);
                        }
                } catch (Exception ex) {
                    Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        if(arrlInterMedRes.size() == 1)
        {
        int iPL[] = (int[])arrlInterMedRes.get(0);
        return iPL;
        }
        else
            if(arrlInterMedRes.size() == 2)
            {
                int iPL[] = mergePostingLists((int[])arrlInterMedRes.get(0), (int[])arrlInterMedRes.get(1));
                return iPL;
            }
            else
                if(arrlInterMedRes.size() == 3)
                {
                int iPL1[] = mergePostingLists((int[])arrlInterMedRes.get(0), (int[])arrlInterMedRes.get(1));
                int iPL2[] = mergePostingLists(iPL1, (int[])arrlInterMedRes.get(2));
                return iPL2;
                }
                else
                {
                    int iPL1[] = new int[0];
                    return iPL1;
                }
    }
    }
    @SuppressWarnings("unchecked")
    int[] intersectPostingLists(int iPL1[], int iPL2[])
    {
        int iCount1 = 0;
        int iCount2 = 0;
        int iFinalPLSize = 1;
        int iFinalPL[] = new int[0];
        while((iCount1 < iPL1.length) && (iCount2 < iPL2.length))
        {
            if(iPL1[iCount1] == iPL2[iCount2])
            {
            iFinalPL = expand(iFinalPL, iFinalPLSize);
            iFinalPL[iFinalPLSize - 1] = iPL1[iCount1];
            iCount1++;
            iCount2++;
            iFinalPLSize++;
            }
            else
                if(iPL1[iCount1] < iPL2[iCount2])
                {
                    iCount1++;
                }
                else
                {
                    iCount2++;
                }
        }
        return  iFinalPL;
    }
    @SuppressWarnings("unchecked")
    int[] processMultiTermQuery(Query objQuery, ArrayList arrlIndex)
    {
     int iCount = 0;
     String strZone = "";
     ArrayList arrlInterMedRes = new ArrayList();
     ArrayList arrlFinalRes = new ArrayList();
     Query objQuery1 = new Query();
    if(objQuery.blnInText)
    {
        strZone = "INTEXT";
        objQuery1.blnInText = true;
    }
    else
    {
        strZone = "INTITLE";
        objQuery1.blnInTitle = true;
    }
    objQuery1.blnIsOneWordQuery = true;
    objQuery1.strQueryTerms = new String[1];
    while(iCount < objQuery.strQueryTerms.length)
    {
            try {
                objQuery1.strQueryTerms[0] = objQuery.strQueryTerms[iCount].trim();
                int iPL1[] = processSingleTermQuery(objQuery1, arrlIndex);
                if(iPL1.length > 0)
                {
                arrlInterMedRes.add(iPL1);
                }
            iCount++;
            } catch (Exception ex) {
                Logger.getLogger(Query.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    if(objQuery.strAndOR.equals("AND"))
    {
        if(arrlInterMedRes.size() == 1)
        {
        int iPL[] = (int[])arrlInterMedRes.get(0);
        return iPL;
        }
        else
            if(arrlInterMedRes.size() == 2)
            {
            int iPL[] = intersectPostingLists((int[])arrlInterMedRes.get(0), (int[])arrlInterMedRes.get(1));
            return iPL;
            }
            else
                if(arrlInterMedRes.size() > 2)
                {
                int iPL[] = intersectPostingLists((int[])arrlInterMedRes.get(0), (int[])arrlInterMedRes.get(1));
                arrlFinalRes.add(iPL);
                    for(int i = 2;i < arrlInterMedRes.size();i++)
                    {
                        int itemp[] = (int[])arrlFinalRes.get(0);
                        int iRes[] = intersectPostingLists(itemp, (int[])arrlInterMedRes.get(i));
                        arrlFinalRes.remove(0);
                        arrlFinalRes.add(iRes);
                    }
                return (int[])arrlFinalRes.get(0);
                }
                else
                {
                    int iPL[] = new int[0];
                    return iPL;
                }
    }
    else
    {
        if(arrlInterMedRes.size() == 1)
        {
        int iPL[] = (int[])arrlInterMedRes.get(0);
        return iPL;
        }
        else
            if(arrlInterMedRes.size() == 2)
            {
            int iPL[] = mergePostingLists((int[])arrlInterMedRes.get(0), (int[])arrlInterMedRes.get(1));
            return iPL;
            }
            else
                if(arrlInterMedRes.size() > 2)
                {
                int iPL[] = mergePostingLists((int[])arrlInterMedRes.get(0), (int[])arrlInterMedRes.get(1));
                arrlFinalRes.add(iPL);
                    for(int i = 2;i < arrlInterMedRes.size();i++)
                    {
                        int itemp[] = (int[])arrlFinalRes.get(0);
                        int iRes[] = mergePostingLists(itemp, (int[])arrlInterMedRes.get(i));
                        arrlFinalRes.remove(0);
                        arrlFinalRes.add(iRes);
                    }
                return (int[])arrlFinalRes.get(0);
                }
                else
                {
                    int iPL[] = new int[0];
                    return iPL;
                }
    }
    }

    String[] processToken(String strTerm)
    {
        strTerm = strTerm.trim();
        String strSimilarTerms = "";
        String strFstUpper = "";
        String strFstLetter = "";
        String strTemp = "";

           String strRegXForName = "^[A-Z][[a-z]*][^[a-z]]";
           Pattern pattern3 = Pattern.compile(strRegXForName);
           Matcher matcher3 = pattern3.matcher((CharSequence)strTerm);
           if(matcher3.find())
           {
            String strSearchTerms[] = new String[1];
            strSearchTerms[0] = strTerm.trim();
            return strSearchTerms;
           }
           String strRegXForAllLower = "[^[a-z][a-z]*]";
           Pattern pattern = Pattern.compile(strRegXForAllLower);
           Matcher matcher = pattern.matcher((CharSequence)strTerm);
           if(matcher.find())
           {
           strSimilarTerms = strTerm.toLowerCase() + " " + strTerm.toUpperCase();
           strFstLetter = strTerm.subSequence(0, 1).toString();
           strTemp = strTerm.toLowerCase();
           strFstUpper = strFstLetter.toUpperCase() + strTemp.substring(1);
           strSimilarTerms = strSimilarTerms + " " + strFstUpper;
           String strSearchTerms2[] = strSimilarTerms.split(" ");
           return strSearchTerms2;
           }
           String strSearchTerms4[] = new String[1];
           strSearchTerms4[0] = strTerm.trim();
           return strSearchTerms4;
    }
    
    int[] expand(int[] array, int size)
    {
    int[] temp = new int[size];
    System.arraycopy(array, 0, temp, 0, array.length);
    for(int j = array.length; j < size; j++)
        temp[j] = 0;
    return temp;
    }
    
    int[] mergePostingLists(int iPL1[], int iPL2[])
    {
        int iCount1 = 0;
        int iCount2 = 0;
        int iFinalPLSize = 1;
        int iFinalPL[] = new int[0];
        while((iCount1 < iPL1.length) && (iCount2 < iPL2.length))
        {
            if(iPL1[iCount1] == iPL2[iCount2])
            {
            iFinalPL = expand(iFinalPL, iFinalPLSize);
            iFinalPL[iFinalPLSize - 1] = iPL1[iCount1];
            iCount1++;
            iCount2++;
            iFinalPLSize++;
            }
            else
                if(iPL1[iCount1] < iPL2[iCount2])
                {
                iFinalPL = expand(iFinalPL, iFinalPLSize);
                iFinalPL[iFinalPLSize - 1] = iPL1[iCount1];
                iCount1++;
                iFinalPLSize++;
                }
                else
                {
                iFinalPL = expand(iFinalPL, iFinalPLSize);
                iFinalPL[iFinalPLSize - 1] = iPL2[iCount2];
                iCount2++;
                iFinalPLSize++;
                }            
        }
        if((iCount1 == iPL1.length) && (iCount1 == iPL2.length))
        {
        return iFinalPL;
        }
        else
        {
            if(iCount1 == iPL1.length)
            {
                while(iCount2 < iPL2.length)
                {
                iFinalPL = expand(iFinalPL, iFinalPLSize);
                iFinalPL[iFinalPLSize - 1] = iPL2[iCount2];
                iCount2++;
                iFinalPLSize++;
                }
                return iFinalPL;
            }
            else
            {
                while(iCount1 < iPL1.length)
                {
                iFinalPL = expand(iFinalPL, iFinalPLSize);
                iFinalPL[iFinalPLSize - 1] = iPL1[iCount1];
                iCount1++;
                iFinalPLSize++;
                }
                return iFinalPL;
            }
        }
    }
}
