package YuFa.LR;

import YuFa.dfa.DFAProduction;
import YuFa.dfa.YuFaDFA;
import YuFa.dfa.YuFaDFANode;
import YuFa.simple.Production;
import YuFa.simple.SpecialListYuFa;
import YuFa.token.Token;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class LRTable {
    // 符号栈
    public Stack<String> SymbolStack = new Stack<>();

    // 状态栈
    public Stack<String> StateStack = new Stack<>();
    public String[][] Action;
    public String[][] Goto;

    public LRTable(YuFaDFA yuFaDFA) {
        DFAProduction accProd = new DFAProduction(new Production(1, "SS", new ArrayList<>(Arrays.asList("S"))), 1, "#");
        int situationCount = yuFaDFA.yuFaDFANodeList.size();

        //specialListYuFa当中VterminatorList和VNon_terminalList的顺序正是俩表中对应符号的顺序
        SpecialListYuFa specialListYuFa = new SpecialListYuFa();
        int VTsize = specialListYuFa.VterminatorList.size()-1;
        int VNsize = specialListYuFa.VNon_terminalList.size();
        Action = new String[situationCount][VTsize];
        Goto = new String[situationCount][VNsize];

        for (YuFaDFANode yuFaDFANode : yuFaDFA.yuFaDFANodeList) {
            for (DFAProduction dfaProduction : yuFaDFANode.dfaProductionList) {
                if (dfaProduction.dot<dfaProduction.production.rightList.size()&&specialListYuFa.isVterminator(dfaProduction.production.rightList.get(dfaProduction.dot))){
                    String VT = dfaProduction.production.rightList.get(dfaProduction.dot);
                    if (VT.equals("$")){
                        String befSearchStr = dfaProduction.befSearchStr;
                        if (befSearchStr.equals("$"))
                            befSearchStr = "#";
                        if (!yuFaDFANode.name.equals("0"))
                            Action[Integer.parseInt(yuFaDFANode.name)][specialListYuFa.getVTnthInList(befSearchStr)] = "r"+dfaProduction.production.pid;
                    }else {
                        Action[Integer.parseInt(yuFaDFANode.name)][specialListYuFa.getVTnthInList(VT)] = "S" + yuFaDFANode.findNextYuFaDFANodeByV(VT).name;
                    }
                } else if (dfaProduction.dot==dfaProduction.production.rightList.size()&&!dfaProduction.isSame(accProd)) {
                    // 这里有差异！我使用的pid是从1开始的，但书上是从0开始
                    Action[Integer.parseInt(yuFaDFANode.name)][specialListYuFa.getVTnthInList(dfaProduction.befSearchStr)] = "r"+dfaProduction.production.pid;
                } else if (dfaProduction.isSame(accProd)) {
                    Action[Integer.parseInt(yuFaDFANode.name)][specialListYuFa.getVTnthInList("#")] = "acc";
                }else {

                }
            }
        }

        for (YuFaDFANode yuFaDFANode : yuFaDFA.yuFaDFANodeList) {
            for (String VN : specialListYuFa.VNon_terminalList) {
                YuFaDFANode nextYuFaDFANodeByV = yuFaDFANode.findNextYuFaDFANodeByV(VN);
                if (nextYuFaDFANodeByV!=null){
                    Goto[Integer.parseInt(yuFaDFANode.name)][specialListYuFa.getVNnthInList(VN)] = nextYuFaDFANodeByV.name;
                }
            }

        }
    }
    public static void clearInfoForFile(String fileName) {
        File file =new File(fileName);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void showStack(Stack<String> stack , String filePath){
        try {
            FileWriter myWriter = new FileWriter(filePath,true);
            for (String s:stack) {
                myWriter.write(s+" ");
            }
            myWriter.write("\n");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void writeErrorInfo(Token token , String filePath){
        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write("error! 在第"+token.lineId+"行发现错误: "+"'"+token.wordSelf+"'");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public  boolean analyseTokenList(YuFaDFA yuFaDFA , List<Token> tokenList){
        SpecialListYuFa specialListYuFa = new SpecialListYuFa();
        LRTable lrTable = new LRTable(yuFaDFA);
        // 初始化两栈
        StateStack.push("0");
        SymbolStack.push("#");
        String processionTokenFile = "src/main/resources/processionToken.txt";
        clearInfoForFile(processionTokenFile);
        showStack(StateStack,processionTokenFile);
        showStack(SymbolStack,processionTokenFile);
        tokenList.add(new Token(-1,"-1","#"));
        for (int tokenCount =0;tokenCount<tokenList.size();tokenCount++) {
            Token token = tokenList.get(tokenCount);
            String topSym = SymbolStack.peek();
            String topState = StateStack.peek();
            String VT = token.wordSelf;
            if (token.classification.equals("identifier")||token.classification.equals("number"))
                VT = token.classification;
            String action = lrTable.Action[Integer.parseInt(topState)][specialListYuFa.getVTnthInList(VT)];
            if (action==null){
//                System.out.println("error! 在第"+token.lineId+"行发现错误: "+"'"+token.wordSelf+"'");
                writeErrorInfo(token,"src/main/resources/error.txt");
                return false;
            }
            if (action.charAt(0)=='S'){
                String nextState = action.substring(1);
                SymbolStack.push(VT);
                StateStack.push(nextState);
            } else if (action.charAt(0)=='r') {
                String pidStr = action.substring(1);
                int neededPid = Integer.parseInt(pidStr);
                Production prod = yuFaDFA.findProductionByPid(neededPid);
                for (int i=0;i<prod.rightList.size();i++){
                    SymbolStack.pop();
                    StateStack.pop();
                }
                SymbolStack.push(prod.left);
                String situ = Goto[Integer.parseInt(StateStack.peek())][specialListYuFa.getVNnthInList(prod.left)];
                StateStack.push(situ);
                tokenCount--;
            } else if (action.equals("acc")) {
//                System.out.println("分析成功！");
                return true;
            }else {
                writeErrorInfo(token,"src/main/resources/error.txt");
            }
            showStack(StateStack,processionTokenFile);
            showStack(SymbolStack,processionTokenFile);
        }
        return false;
    }
}
