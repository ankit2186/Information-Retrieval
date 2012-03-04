import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Submission {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String strQuerry = "";
        String strTerminate = "EXIT";
        ArrayList arrQuery = new ArrayList();
        ArrayList arrResult = new ArrayList();
        ArrayList arrlIndex = new ArrayList();
        ArrayList arrlDocTable = new ArrayList();
        DocsTable objDocTb = new DocsTable();
        Dictionary objDict = new Dictionary();
        Postings objPL = new Postings();
        arrlDocTable = objDocTb.readDocTable();
        arrlIndex.add(arrlDocTable);
        arrlIndex = objDict.readDictionary(arrlIndex);
        arrlIndex = objPL.readPostingListFile(arrlIndex);
        System.out.print("Enter Query or \"EXIT\": ");
        strQuerry = br.readLine();
        if (strQuerry.equals("EXIT"))
        {
            System.exit(1);
        }
        
    do
    {   
        Query objQuery = new Query();
        Calculation objScore = new Calculation();
        Results objPrintRes = new Results();
        ArrayList arrlFinalResults = new ArrayList();
        arrQuery = objQuery.parseQuery(strQuerry);
        arrResult = objQuery.processQuery(arrQuery, arrlIndex);
        arrlFinalResults = objScore.Calc(arrQuery, arrlIndex, (int[])arrResult.get(0));
        arrlFinalResults.add(strQuerry);
        objPrintRes.printResults(arrlFinalResults);
        strQuerry = "";
        objQuery = null;
        System.out.print("\nEnter Query or \"EXIT\": ");
        strQuerry = br.readLine();
    }while(!strTerminate.equals(strQuerry.trim()));
    
    System.out.println("Results are written to 'output.txt'");
    }
}