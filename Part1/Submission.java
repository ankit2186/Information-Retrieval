//package Index;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.htmlparser.*;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class Submission {
    private static void copyInputStream(InputStream inputStream, BufferedOutputStream bufferedOutputStream) throws IOException 
    {
        byte[] buffer = new byte[2048];
        int len;
        while((len = inputStream.read(buffer)) >= 0)
        bufferedOutputStream.write(buffer, 0, len);
        inputStream.close();
        bufferedOutputStream.close();
    }
	@SuppressWarnings("unchecked")
    public static void main(String[] args) throws ParserException, IOException 
    {
        Enumeration entries;
        ZipFile zipFile;
        if(args.length != 1) 
        {
            System.err.println("Usage: Unzip zipfile");
            return;
        }
        try 
        {
            zipFile = new ZipFile(args[0]);
            entries = zipFile.entries();
            while(entries.hasMoreElements()) 
            {
                ZipEntry entry = (ZipEntry)entries.nextElement();
                if(entry.isDirectory()) 
                {
                (new File(entry.getName())).mkdir();
                continue;
                }
                copyInputStream(zipFile.getInputStream(entry),
                new BufferedOutputStream(new FileOutputStream(entry.getName())));
            }
            zipFile.close();
        } 
        catch (IOException ioe) 
        {
            System.err.println("Unhandled exception:"+ioe.getMessage());
        }
        int iFileNameCounter = 2;
        int iFileCounter = 1;
        String strFileName;
        String strInputPath = "Reviews//";//args[1];
        String strBaseName = strInputPath;
        String strTitle;
        String strBody;
        ArrayList arrlDocsTable = new ArrayList();
        ArrayList arrlDictionaryObjects = new ArrayList();
        ArrayList arrlPostingListObjects = new ArrayList();
        ArrayList arrlDictionary = new ArrayList();
        Hashtable htDictDf = new Hashtable();
        Hashtable htDictOffset = new Hashtable();
        arrlDictionary.add(arrlDictionaryObjects);
        arrlDictionary.add(htDictDf);
        arrlDictionary.add(htDictOffset);
        arrlDictionary.add(arrlPostingListObjects);
        
        for (; iFileCounter < 121; iFileNameCounter++)
        {
            Document objDoc = new Document();
            Dictionary objDictionary = new Dictionary();
            DocsTable objDocTable = new DocsTable();
            Hashtable htDuplicateTerms = new Hashtable();
            objDoc.strDocNum = Integer.toString(iFileNameCounter);
            if (iFileNameCounter < 10)
            {
                strFileName = "000" + Integer.toString(iFileNameCounter) + ".html";
            }
            else if ((iFileNameCounter >= 10) && (iFileNameCounter < 100))
            {
               strFileName = "00" + Integer.toString(iFileNameCounter) + ".html";
            } 
            else
            {
                strFileName = "0" + Integer.toString(iFileNameCounter) + ".html";
            }
            strFileName = strBaseName + strFileName;//constructing file name
            objDoc.strDocName = strFileName;
            try
            {
                Parser parserChkFileExist = new Parser(strFileName);
                iFileCounter++;
                parserChkFileExist = null;
            }
            catch(Exception ex)
            {
                continue;
            }
            Parser textParser = new Parser(strFileName);
            NodeList listP = textParser.parse(new TagNameFilter("P"));
            Parser parser = new Parser(strFileName);
            NodeList listTitle = parser.parse(new TagNameFilter("TITLE"));
            Parser textBodyParser = new Parser(strFileName);
            NodeList listBody = textBodyParser.extractAllNodesThatMatch(new TagNameFilter("BODY"));
            Tag tagTitle = (Tag)listTitle.elementAt(0);
            strTitle = tagTitle.toPlainTextString();
            strTitle = strTitle.trim();
            strTitle = strTitle.replace("Review for", "");
            strTitle = strTitle.replaceAll("\\'Breaker\\'","breaker");
            strTitle = strTitle.substring(0, (strTitle.lastIndexOf("(")));
            strTitle = strTitle.trim();
            strTitle = strTitle.toLowerCase();
            objDoc.strTitle = strTitle.trim();
            objDoc.strTitle = objDocTable.cleanBodyString(strTitle);
            objDoc.strTitleArray = objDoc.strTitle.split(" ");
            Tag tagBody = (Tag)listBody.elementAt(0);
            strBody = tagBody.toPlainTextString();
            strBody = strBody.trim();
            strBody = strBody.replace(strTitle.trim(), "");
            objDoc.strBody = strBody;
            String strTempCleanedBody = objDocTable.cleanBodyString(strBody);
            strTempCleanedBody = strTempCleanedBody.replace(strTitle.trim(), "");
            strTempCleanedBody = strTempCleanedBody.toLowerCase();
            objDoc.strBodyArray = strTempCleanedBody.split(" ");
            if(listP.size() > 0)
            {
                for(int j=0;j < listP.size();j++)
                {
                    Tag tagP = (Tag)listP.elementAt(j);
                    String strTempPTag = tagP.toPlainTextString().trim();
                    String strTempPTagArray[] = strTempPTag.split(" ");
                    objDoc.arlTags.add(strTempPTag);
                    objDoc.arlTagsArray.add(strTempPTagArray);
                    tagP = null;
                    strTempPTag = null;
                    strTempPTagArray = null;
                }
            }
            objDocTable = objDocTable.findDocContents(objDoc);
            arrlDictionary.add(htDuplicateTerms);
            arrlDictionary = objDictionary.buildDictionary(arrlDictionary, objDoc, false);
            arrlDictionary = objDictionary.buildDictionary(arrlDictionary, objDoc, true);
            String strTemp1 = (String)arrlDictionary.get(5);
            objDoc.strHiphenFwdSlash = strTemp1.trim();
            objDoc.strHiphenFwdSlashArr = objDoc.strHiphenFwdSlash.split(" ");
            arrlDictionary = objDictionary.buildDictionary(arrlDictionary, objDoc, true);
            arrlDocsTable.add(objDocTable);
            parser = null;
            objDoc = null;
            objDocTable = null;
            htDuplicateTerms = null;
            arrlDictionary.remove(4);
            arrlDictionary.remove(4);
            listTitle = null;
            tagTitle = null;
            listP = null;
        }
        int iRet;
        DocsTable objDT = new DocsTable();
        Dictionary objDict = new Dictionary();
        Postings objPL = new Postings();
        try
        {
            iRet = objPL.writeDocsTableEntry((ArrayList)arrlDictionary.get(3));
            iRet = objDT.writeDocsTableEntry(arrlDocsTable);
            iRet = objDict.writeDocsTableEntry((ArrayList)arrlDictionary.get(0));
            System.out.println("TOTAL DOCUMENT NUMBER : " + arrlDocsTable.size());
            System.out.println("TOTAL DICTIONARY TERMS : " + (((ArrayList)(arrlDictionary.get(0))).size()));
            System.out.println("TOTAL POSTINGS : " + (((ArrayList)(arrlDictionary.get(3))).size()));    
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
