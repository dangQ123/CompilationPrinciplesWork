package YuFa;

import YuFa.LR.LRTable;
import YuFa.dfa.DFAProduction;
import YuFa.dfa.YuFaDFA;
import YuFa.dfa.YuFaDFANode;
import YuFa.simple.Production;
import YuFa.token.Token;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YuFaMain {

    // 传入文件名称，读取2型文法产生式，输出产生式列表
    public static List<Production> readSynGrammer(){
        String fileName = "src/main/resources/SynGrammar.txt";
        List<Production> productionList = new ArrayList<>();
        // 转换成List<String>, 要注意java.lang.OutOfMemoryError: Java heap space
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(fileName),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int lineCount = 0;
        for (String line : lines) {
            lineCount++;
            String[] split = line.split(":");
            String left = split[0];
            String right = split[1];
            String[] rightList = right.split(" ");
            Production prod = new Production(lineCount,left, Arrays.asList(rightList));
            productionList.add(prod);
        }
        return productionList;
    }

    // 读取token文件，传入文件名称
    public static List<Token> readTokenList(String filename){
        String fileName = filename;
        List<Token> tokenList = new ArrayList<>();
        // 转换成List<String>, 要注意java.lang.OutOfMemoryError: Java heap space
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(fileName),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String line : lines) {
            String[] s = line.split(" ");
            Token token = new Token(Integer.parseInt(s[0]), s[1], s[2]);
            tokenList.add(token);
        }
        return tokenList;
    }

    public static void main(String[] args) {
        List<Production> productionList = YuFaMain.readSynGrammer();
        List<DFAProduction> dfaProdList = DFAProduction.turnProdListToDfaProdList(productionList);
        List<DFAProduction> StartList = dfaProdList.get(0).expandDFAProductionList();
        YuFaDFANode start = new YuFaDFANode("0",StartList);
        YuFaDFA yuFaDFA = new YuFaDFA(start);
        LRTable lrTable = new LRTable(yuFaDFA);
        List<Token> tokenList = readTokenList("src/main/resources/token.txt");
        boolean result = lrTable.analyseTokenList(yuFaDFA, tokenList);
        if (result)
            System.out.println("YES");
        else
            System.out.println("NO");

    }

}
