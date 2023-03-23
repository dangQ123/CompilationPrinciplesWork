package Test;

import CiFa.CiFaMain;
import CiFa.dfa.Dfa;
import CiFa.nfa.Nfa;
import CiFa.token.Token;
import YuFa.LR.LRTable;
import YuFa.YuFaMain;
import YuFa.dfa.DFAProduction;
import YuFa.dfa.YuFaDFA;
import YuFa.dfa.YuFaDFANode;
import YuFa.simple.Production;

import java.util.List;

import static CiFa.CiFaMain.sourceFileToTokenList;
import static CiFa.CiFaMain.writeTokenToFile;
import static YuFa.YuFaMain.readTokenList;

// 一键执行类
public class TestAll {
    public static void main(String[] args) {

        Nfa nfa = new Nfa("src/main/" +
                "resources/lexical.txt");
        // 根据nfa创建dfa
        Dfa dfa = new Dfa(nfa);
        // 根据源程序创建token列表
        List<Token> tokenList =
                sourceFileToTokenList
                        ("src/main/resources/source.cc"
                                ,dfa);
        if (tokenList!=null){
            // 把token写入文件
            writeTokenToFile("src/main/resources/token.txt",tokenList);
            List<Production> productionList = YuFaMain.readSynGrammer();
            List<DFAProduction> dfaProdList = DFAProduction.turnProdListToDfaProdList(productionList);
            List<DFAProduction> StartList = dfaProdList.get(0).expandDFAProductionList();
            YuFaDFANode start = new YuFaDFANode("0",StartList);
            YuFaDFA yuFaDFA = new YuFaDFA(start);
            LRTable lrTable = new LRTable(yuFaDFA);
            List<YuFa.token.Token> tokenListFromFile = readTokenList("src/main/resources/token.txt");
            boolean result = lrTable.analyseTokenList(yuFaDFA, tokenListFromFile);
            if (result)
                System.out.println("YES");
            else
                System.out.println("NO");
        }else {
            System.out.println("词法分析有误！");
        }

    }
}
