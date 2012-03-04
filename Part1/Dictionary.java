//package Index;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 *
 * @author Ankit,Mahesh
 */
public class Dictionary {

    String strTerm;
    int iDocFreq;
    int iOffset;

    String getNextTermInDictionary(Hashtable ht, String strCurrTerm)
    {
        String strNextTerm = "";
        String strTemp = "";
        int iCounter = 0;

        Enumeration en = ht.keys();
        ht.keys();

        strCurrTerm = strCurrTerm.trim();

        while(en.hasMoreElements())
        {
            strTemp = (String)en.nextElement();
            if((strCurrTerm.equals(strTemp)) && (iCounter < ht.size()))
            {
                strNextTerm = (String)en.nextElement();
                break;
            }
            iCounter++;
        }
        return strNextTerm;
    }

    boolean findDuplicateInHashTable(Hashtable ht, String strTerm)
    {
        boolean blnReturn = false;

        strTerm = strTerm.trim();

        if(ht.containsKey(strTerm))
        {
            blnReturn = true;
        }
        return blnReturn;
    }
    @SuppressWarnings("unchecked")
    ArrayList buildDictionary(ArrayList arrlDictionary, Document objDoc, boolean blnIsBody)
    {
        Document objAckSkip = new Document();
        Document objAckClean = new Document();
        Document objAckUpdateDictionary = new Document();
        String strTermsHiphenFwdSlash = "";
        String strCleanedTerm = "";
        ArrayList arrlReturn = new ArrayList();
        ArrayList arrlDictionaryObjects = new ArrayList();
        ArrayList arrlPostingListObjects = new ArrayList();
        Hashtable htDuplicateTerms = new Hashtable();
        htDuplicateTerms = (Hashtable)arrlDictionary.get(4);
        Hashtable htDictDf = new Hashtable();
        Hashtable htDictOffset = new Hashtable();
        
        arrlDictionaryObjects = (ArrayList)arrlDictionary.get(0);
        htDictDf = (Hashtable)arrlDictionary.get(1);
        htDictOffset = (Hashtable)arrlDictionary.get(2);
        arrlPostingListObjects = (ArrayList)arrlDictionary.get(3);
        String strBodyArr[];
        
        if(blnIsBody)
        {
            if(objDoc.strHiphenFwdSlash.length() > 1)
                strBodyArr = objDoc.strHiphenFwdSlashArr;
            else
                strBodyArr = objDoc.strBodyArray;
            
        }
        else
            strBodyArr = objDoc.strTitleArray;
        
        String strSkippedTerms = "";
        int iBodyArrayIndex = 0;
        int iNoOfTermsSkipped = 0;
        int iPostingListLength = -1;//to hold length of posting list
        int iPostingListIndex = -1;//holds index of arraylists at which new entry will be added
        int iOldOffset = -1;
        int iOldDf = -1;

        for(;iBodyArrayIndex < strBodyArr.length;iBodyArrayIndex++)
        {
        Postings objPostingList = new Postings();
        Dictionary objDictionary = new Dictionary();

    
        objAckSkip = skipTerm(strBodyArr[iBodyArrayIndex], blnIsBody);

        
        if((!objAckSkip.blnIsValid))
        {
            objAckClean = cleanTerm(strBodyArr[iBodyArrayIndex],iBodyArrayIndex, strBodyArr, blnIsBody);
            strCleanedTerm = objAckClean.strNewTerm;
            
            if(htDictDf.isEmpty())
            {
	        htDictDf.put(strCleanedTerm, new Integer(1));
                objDictionary.strTerm = strCleanedTerm;
                objDictionary.iDocFreq = 1;
                objDictionary.iOffset = 0;
                arrlDictionaryObjects.add(objDictionary);
                htDictOffset.put(strCleanedTerm, new Integer(0));
                objPostingList.strTerm = strCleanedTerm;
                objPostingList = calculateTermWeights(objPostingList, objDoc);
                htDuplicateTerms.put(strCleanedTerm, new Integer(objPostingList.iWtText + objPostingList.iWtTitle));
                objPostingList.strDocID = objDoc.strDocNum;
                arrlPostingListObjects.add(objPostingList);
            }
            else
            {
            
            if(!findDuplicateInHashTable(htDuplicateTerms, strCleanedTerm))
            {
                if(findDuplicateInHashTable(htDictDf, strCleanedTerm))
                {
                
                    iOldDf = (Integer)htDictDf.get(strCleanedTerm);
                    iOldOffset = (Integer)htDictOffset.get(strCleanedTerm);
                    objAckUpdateDictionary = getNextTermAndUpdateDictionary(arrlDictionaryObjects, strCleanedTerm);
                    arrlDictionaryObjects = null;
                    arrlDictionaryObjects = objAckUpdateDictionary.arrlUpdatedDictionary;
                    objPostingList.strTerm = strCleanedTerm;
                    objPostingList = calculateTermWeights(objPostingList, objDoc);
                    htDuplicateTerms.put(strCleanedTerm, new Integer(objPostingList.iWtText + objPostingList.iWtTitle));
                    objPostingList.strDocID = objDoc.strDocNum;
                    iPostingListIndex = objAckUpdateDictionary.iOldOffSet + objAckUpdateDictionary.iOldDf;//index at which this posting list entry gets added
                    arrlPostingListObjects.add(iPostingListIndex, objPostingList);

                }
                else
                {
                    Dictionary objNewDictTerm = new Dictionary();
                    objNewDictTerm.strTerm = strCleanedTerm;
                    objNewDictTerm.iDocFreq = 1;
                    objNewDictTerm.iOffset = arrlPostingListObjects.size();
                    arrlDictionaryObjects.add(objNewDictTerm);
                    htDictDf.put(strCleanedTerm, new Integer(1));
                    iPostingListLength = arrlPostingListObjects.size();
                    htDictOffset.put(strCleanedTerm, new Integer(iPostingListLength));
                    htDuplicateTerms.put(strCleanedTerm, new Integer(1));
                    objPostingList.strTerm = strCleanedTerm;
                    objPostingList = calculateTermWeights(objPostingList, objDoc);
                    htDuplicateTerms.put(strCleanedTerm, new Integer(objPostingList.iWtText + objPostingList.iWtTitle));
                    objPostingList.strDocID = objDoc.strDocNum;
                    arrlPostingListObjects.add(objPostingList);
                }
            }
            }
        }
        else
        {
            iNoOfTermsSkipped++;
            strSkippedTerms = strSkippedTerms + " " + strBodyArr[iBodyArrayIndex];
        }
        if((objAckSkip != null) && (objAckSkip.strTermsHiphenFwdSlash != null))
        {
            strTermsHiphenFwdSlash = strTermsHiphenFwdSlash + objAckSkip.strTermsHiphenFwdSlash;// + " ";
        }
        objPostingList = null;
        objDictionary = null;
        objAckClean = null;
        }
        arrlReturn.add(arrlDictionaryObjects);
        arrlReturn.add(htDictDf);
        arrlReturn.add(htDictOffset);
        arrlReturn.add(arrlPostingListObjects);
        arrlReturn.add(htDuplicateTerms);
        arrlReturn.add(strTermsHiphenFwdSlash);
        return arrlReturn;
    }
    Document cleanTerm(String strTerm, int iCurrIndex, String strBody[], boolean blnIsBody)
    {
        String strRegXForName = "";
        String strTemp = "";
        String strTempPrev = "";
        String strTermsWithDotInbetween = "";
        boolean blnConvertToLower = false;
        int iCounter1 = 0;
        Document objAck = new Document();
        objAck.strTermsHiphenFwdSlash = "";
        strTerm = strTerm.trim();

        if(strTerm.matches("^[^\\w][a-zA-Z0-9]+"))
        {
            strTerm = strTerm.replaceFirst("[^\\w]", "");
        }
        if((iCurrIndex < (strBody.length - 1)))
        {
            objAck.strNextTerm = strBody[iCurrIndex + 1].trim();// by default.
        }
        if(blnIsBody)
        {
           strRegXForName = "^[A-Z][[a-z]*][^[a-z]]";
           Pattern pattern3 = Pattern.compile(strRegXForName);
           Matcher matcher3 = pattern3.matcher((CharSequence)strTerm);
           if(iCurrIndex > 0){
           strTempPrev = strBody[iCurrIndex - 1];
        }
        if(matcher3.find())
        {
               blnConvertToLower = true;
               if((!(strTempPrev.endsWith(".")) && (!(isBeginNewPara(iCounter1, strBody)))))
                {
                    blnConvertToLower = false;
                }
                {
                    iCounter1 = 1;
                    while(iCounter1 < strBody.length)
                 {
                    strTemp = strBody[iCounter1];
                    strTemp = strTemp.trim();
                    if(iCounter1 > 0)
                    {
                    strTempPrev = strBody[iCounter1 - 1];
                    strTempPrev = strTempPrev.trim();
                    }
                    if(strTerm.equals(strTemp))
                    {
                            if((!(isBeginNewPara(iCounter1, strBody))))
                            {
                                blnConvertToLower = false;
                                break;
                            }
                     }
                    strTemp = "";
                    strTempPrev = "";
                    iCounter1++;
                 }
            }
            }
           if(blnConvertToLower)
             {
             strTerm = strTerm.toLowerCase();
             }
        }     
        while((strTerm.endsWith(".") || strTerm.endsWith("?")))// this loop is for terms like "right....." removes all extra dots
        {
            strTerm = (strTerm.subSequence(0, (strTerm.length() - 1))).toString();
        }
        if((strTerm.startsWith("'")) || (strTerm.endsWith("'")))
        {
            strTerm = strTerm.replaceAll("'", "");
        }        
        objAck.strTermsHiphenFwdSlash = strTermsWithDotInbetween;
        objAck.strNewTerm = strTerm;
        return objAck;
    }
    @SuppressWarnings("unchecked")
    Document getNextTermAndUpdateDictionary(ArrayList arrlDict, String strCurrTerm)
    {
        Document objAck = new Document();
        String strNextTerm = "";
        int iCounter = 0;
        int iNextTermOS = -1;
        int iCurrTermDf = -1;
        boolean blnIsNextTerm = false;
        boolean blnIncrementOS = false;     
        ArrayList arrlUpdatedDictionary = new ArrayList();
        strCurrTerm = strCurrTerm.trim();
        while(iCounter < arrlDict.size())
        {
            Dictionary objDict3 = new Dictionary();
            objDict3 = (Dictionary)arrlDict.get(iCounter);
            objDict3.strTerm = objDict3.strTerm.trim();
            if(blnIncrementOS)
            {
                objDict3.iOffset = objDict3.iOffset + 1; //incrementing offset by 1
                if(blnIsNextTerm)
                {
                strNextTerm = objDict3.strTerm;
                objAck.strNextTerm = objDict3.strTerm;
                iNextTermOS = objDict3.iOffset;
                objAck.iNextTermOS = objDict3.iOffset;
                blnIsNextTerm = false;
                }
            }
            if(objDict3.strTerm.equals(strCurrTerm))
            {
                objAck.iOldDf = objDict3.iDocFreq; //old df
                objAck.iOldOffSet = objDict3.iOffset; //offset value of current term
                objDict3.iDocFreq = objDict3.iDocFreq + 1; // increment df by 1 = new df
                iCurrTermDf = objDict3.iDocFreq;
                blnIsNextTerm = true;
                blnIncrementOS = true;
            }
        arrlUpdatedDictionary.add(objDict3);
        objDict3 = null;
        iCounter++;
        }
        objAck.arrlUpdatedDictionary = arrlUpdatedDictionary;
        return objAck;
    }
    @SuppressWarnings("unchecked")
    Postings calculateTermWeights(Postings objPL, Document objDoc)
    {
        int iTitleCounter = 0;
        int iBodyCounter = 0;
        String strTerm = objPL.strTerm.trim();
        String strTitleArr[] = objDoc.strTitleArray;
        String strBodyArr[] = objDoc.strBodyArray;
        objPL.iWtTitle = -1;
        objPL.iWtText = 0;
        objPL.Zone0 = 0;
        objPL.Zone1 = 0;
        while(iTitleCounter < strTitleArr.length)
        {
            if(strTerm.equals(strTitleArr[iTitleCounter].trim()))
            {
                objPL.iWtTitle = 0;
                objPL.Zone0 = 1;
                objPL.arlTitlePosition.add(iTitleCounter);
                objPL.Flag = 1;
            }
        iTitleCounter++;
        }
        while(iBodyCounter < strBodyArr.length)
        {
            if(strTerm.equals(strBodyArr[iBodyCounter].trim()))
            {
                objPL.iWtTitle = 1;
                objPL.iWtText = objPL.iWtText + 1;
                objPL.arlBodyPosition.add(iBodyCounter);
                objPL.Zone1 = 1;
                objPL.Flag = 2 ;
            }
            if(strBodyArr[iBodyCounter].endsWith("."))
            {
                if(strBodyArr[iBodyCounter].contains(strTerm))
                {
                    objPL.iWtTitle = 1;
                    objPL.iWtText = objPL.iWtText + 1;
                    objPL.arlBodyPosition.add(iBodyCounter);
                    objPL.Flag = 2 ;
                    objPL.Zone1 = 1;
                }
            }
        iBodyCounter++;
        }
    return objPL;
    }
    @SuppressWarnings("unchecked")
    boolean isBeginNewPara(int iCurrIndex, String strArrBody[])
    {
        boolean blnIsBeginNewParaOrSentense = false;
        int iCounter = iCurrIndex;
        int iNullTermCounter = 0;
        if(iCurrIndex == 0)
        {
            blnIsBeginNewParaOrSentense = true;
        }
        else
        {
            while((strArrBody[iCounter - 1].equals("") || strArrBody[iCounter].equals(" ")) && (iCounter > 0))
            {
                iNullTermCounter++;
                if(strArrBody[iCounter].equals("You")) {}
                iCounter--;
            }
            if(strArrBody[iCounter - 1].endsWith(".") || strArrBody[iCounter - 1].endsWith("?"))
            {
                blnIsBeginNewParaOrSentense = true;
            }
            else
            {
                if(iNullTermCounter > 3)
                {
                    blnIsBeginNewParaOrSentense = true;
                }
            }
        }
        return blnIsBeginNewParaOrSentense;
    }
	@SuppressWarnings("unchecked")
    int writeDocsTableEntry(ArrayList arrlDC)
    {
        int iReturn = -1;
        int iCounter = 0;
        String strFileLine = "";
        String strTemp = "";
        
        try
        {
            iReturn = 1;
            BufferedWriter out = new BufferedWriter(new FileWriter("Dictionary.txt"));
//            out.write("Term            Df     Offset \n");
//            out.write("-------------------------------\n");
            for(;iCounter < arrlDC.size();iCounter++)
            {
                Dictionary objDict = new Dictionary();
                objDict = (Dictionary)arrlDC.get(iCounter);
                strTemp = objDict.strTerm;
                while(strTemp.length() <= 15)
                {
                    strTemp = strTemp + " ";
                }
                strFileLine = strTemp + objDict.iDocFreq + "    " + objDict.iOffset;
                out.write(strFileLine);
                out.newLine();
                objDict = null;
                strFileLine = "";
                strTemp = "";
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
    @SuppressWarnings("unchecked")
    Document skipTerm(String strTerm, boolean blnIsBody)
    {
        Document objAck = new Document();
        objAck.blnIsValid = false;
        objAck.strReson = "";
        String strRegXIsNumber;
        String strRegSpecCharSeq;
        String strRegXPunctuation;       
        String strRegXQuote;
        String strTermsHiphFwdSlash = "";
        String strTermsWithDotInbetween = "";
        String strWithDotInbetweenArr[];
        try
        {
            if((strTerm.equals("")) || (strTerm.equals(" ")) || (strTerm.equals("\r")) || (strTerm.equals("\n")) || (strTerm.equals("\r\n")))
            {
                objAck.blnIsValid = true;
                objAck.strReson = "has blank char or space";
                return  objAck;
            }
            strTerm = strTerm.trim();
            if(blnIsBody)
            {
                if((strTerm.equals("a")) || (strTerm.equals("the")) || (strTerm.equals("is")) || (strTerm.equals("in")) ||
               (strTerm.equals("it")) || (strTerm.equals("that")) || (strTerm.equals("this")) || (strTerm.equals("if")) ||
               (strTerm.equals("be")) || (strTerm.equals("do")) || (strTerm.equals("then")) || (strTerm.equals("there")) ||
               (strTerm.equals("and")) || (strTerm.equals("or")) || (strTerm.equals("rate")))
                {
                objAck.blnIsValid = true;
                objAck.strReson = "term is one of the stop words.";
                return  objAck;
                }
            }
            strRegXIsNumber = "[+-]?[0-9]+";
            Pattern pattern = Pattern.compile(strRegXIsNumber);
            Matcher matcher = pattern.matcher((CharSequence)strTerm);
            if(matcher.find())
            {
                objAck.blnIsValid = true;
                objAck.strReson = "term is number";
                return objAck;
            }
            if(strTerm.startsWith("'") && (strTerm.endsWith("'")))
            {
                strTerm = strTerm.replace("'", "");
            }
            strRegSpecCharSeq = "^[^\\w][^\\w]*";
            Pattern pattern1 = Pattern.compile(strRegSpecCharSeq);
            Matcher matcher1 = pattern1.matcher((CharSequence)strTerm);
            if(matcher1.find())
            {   
                objAck.blnIsValid = true;
                objAck.strReson = "term is sequence or single special char";
                return objAck;
            }
            if(strTerm.contains("@") && strTerm.contains("."))
                if(strTerm.indexOf("@") < strTerm.lastIndexOf("."))
                    {
                    objAck.blnIsValid = true;
                    objAck.strReson = "term is email-id";
                    return objAck;
                    }
           strRegXQuote = "^[a-zA-Z]*'[^[a-zA-Z]*]";
           Pattern pattern4 = Pattern.compile(strRegXQuote);
           Matcher matcher4 = pattern4.matcher(strTerm);
           if(matcher4.find())
           {
            objAck.blnIsValid = true;
            objAck.strReson = "term is with punctuation, having quote ' ";
            return objAck;
           }
           strRegXPunctuation = "[a-zA-Z]\\.[a-zA-Z]\\.";
           Pattern pattern2 = Pattern.compile(strRegXPunctuation);
           Matcher matcher2 = pattern2.matcher((CharSequence)strTerm);
           if(matcher2.find())
           {
           objAck.blnIsValid = true;
           objAck.strReson = "term is with punctuation, U.S.A or I.B.M";
           return objAck;
           }
           String strTempArr[];
           String strTempArrFwSlash[];
           int iCounter = 0;
           int iCounterSlash = 0;
           if(strTerm.contains("-"))
           {
               objAck.blnIsValid = true;
               strTempArr = strTerm.split("-");
               if(strTempArr.length == 1)
               {
                    strTermsHiphFwdSlash = strTempArr[0] + " ";
               }
                while(iCounter < (strTempArr.length - 1))
               {
                    strTermsHiphFwdSlash = strTermsHiphFwdSlash + strTempArr[iCounter] + strTempArr[iCounter + 1] + " ";
                    iCounter++;
               }
           objAck.strTermsHiphenFwdSlash = strTermsHiphFwdSlash;
           return objAck;
           }
           if(strTerm.contains("/"))
           {
               objAck.blnIsValid = true;
               strTempArrFwSlash = strTerm.split("/");
               if(strTempArrFwSlash.length == 1)
               {
               strTermsHiphFwdSlash = strTempArrFwSlash[0] + " ";
               }
               while(iCounterSlash < (strTempArrFwSlash.length))
               {
               strTermsHiphFwdSlash = strTermsHiphFwdSlash + strTempArrFwSlash[iCounterSlash] + " ";
               iCounterSlash++;
               }
           objAck.strTermsHiphenFwdSlash = strTermsHiphFwdSlash;
           return objAck;
           }
           if(strTerm.contains("."))
           {
           objAck.blnIsValid = true;
           int iCounterDots = 0;
           strTerm = strTerm.replaceAll("[\\.]+", " ");
           strWithDotInbetweenArr = strTerm.split(" ");
                if(strWithDotInbetweenArr.length == 1)
               {
               strTermsWithDotInbetween = strWithDotInbetweenArr[0] + " ";
               }
               while(iCounterDots < (strWithDotInbetweenArr.length))
               {
               strTermsWithDotInbetween = strTermsWithDotInbetween + strWithDotInbetweenArr[iCounterDots] + " ";
               iCounterDots++;
               }
           objAck.strTermsHiphenFwdSlash = strTermsWithDotInbetween;
           return objAck;
           }
        }
        catch(Exception ex)
        {
        System.out.println("Exception : " + ex.getMessage());
        }
       return  objAck;
    }

}
