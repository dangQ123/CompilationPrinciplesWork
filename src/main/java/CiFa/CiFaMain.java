package CiFa;

import CiFa.dfa.Dfa;
import CiFa.dfa.DfaNode;
import CiFa.nfa.Nfa;
import CiFa.simple.CiFaFuncClass;
import CiFa.simple.MyTuple;
import CiFa.simple.SpecialList;
import CiFa.token.Token;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CiFaMain {
    public static List<String> getLinesFromFile(String filepath){
        String fileName = filepath;

        // 转换成List<String>, 要注意java.lang.OutOfMemoryError: Java heap space
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(fileName),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    public static String betterLine(String line){
        for (int i=0;i<line.length();i++){
            if (line.charAt(i)!=' '){
                return line.substring(i);
            }
        }
        return line;
    }

    public static List<Token> sourceFileToTokenList(String filepath , Dfa dfa){
        List<Token> tokenList = new ArrayList<>();
        List<String> linesFromFile = getLinesFromFile(filepath);
        int lineCount = 0;
        for (String line : linesFromFile) {
            lineCount++;
            if (!line.equals("")){
                String[] split = betterLine(line).split(" ");
                for (String s : split) {
                    Token unknown = new Token(lineCount, "Unknown", s);
                    unknown.classification = classFyWord(dfa,unknown.wordSelf);
                    if (unknown.classification.equals("ErrorClass")){
                        return null;
                    }
                    tokenList.add(unknown);
                }
            }
        }
        return tokenList;
    }

    public static boolean isKeyWord(String word){
        for (String keyWord : SpecialList.KeyWords) {
            if (keyWord.equals(word))
                return true;

        }
        return false;
    }

    public static boolean islimiter(String word){
        for (String keyWord : SpecialList.Limiter) {
            if (keyWord.equals(word))
                return true;

        }
        return false;
    }

    public static boolean isOperator(String word){
        for (String keyWord : SpecialList.Operator) {
            if (keyWord.equals(word))
                return true;

        }
        return false;
    }

    public static String turnDfaNodeNameToSpecialName(String dfaNodeName){
        if (dfaNodeName.indexOf("identifier_tail")!=-1)
            return "identifier";
        if (dfaNodeName.indexOf("complex_second_tail")!=-1
            || dfaNodeName.indexOf("integer_tail")!=-1
                || dfaNodeName.indexOf("index_tail")!=-1
        )
            return "number";
        return "error!";
    }

    /*
    *   这就是分类函数！
    *   将word放入dfa中进行检测
    * */
    public static String classFyWord(Dfa dfa , String word){
        if (isKeyWord(word))
            return "keyword";
        if (islimiter(word))
            return "limiter";
        if (isOperator(word))
            return "operator";
        word = word+"$";    // 标志结束
        DfaNode nowPos = dfa.start;
        DfaNode tempNode = nowPos;
        boolean flag = true;
        for (int i=0;i<word.length();i++){
            char c = word.charAt(i);
            if (nowPos.name.equals("@end")){
                break;
            }
            if (CiFaFuncClass.ismyTupleVTequals(CiFaFuncClass.turnDigitAndAlphabet(c,flag),nowPos)){
                for (MyTuple myTuple : nowPos.nextNodesList) {
                    if (myTuple.VT.equals(CiFaFuncClass.turnDigitAndAlphabet(c,flag))){
                        tempNode = nowPos;
                        nowPos = dfa.DfaNodes.get(dfa.findIndexByNameInDfaNodes(dfa.DfaNodes,myTuple.VN));
//                        System.out.println(nowPos.name);
                        break;
                    }
                }
            }else {
                /*
                *   非常关键的代码，详细注释：
                *   flag 的目标很简单，就是当待检测的字符串中如果出现'e'或'i'，
                *   默认为true，即首先判断是否是科学计数或者复数。如果不是的话就进入
                *   下面的代码，flag 置为false，e和i认定为普通字母，重新检测整个字符串
                * */
                if (flag==true && ((word.indexOf("i")!=-1)||(word.indexOf("e")!=-1))
                ){
                    flag = false;
                    i=-1;
                    continue;

                }
                System.out.println("error! 异常字符: "+c);
                break;
            }
        }
//        System.out.println("最终停留在："+nowPos.name);
        // 下面代码进行细分
        if (nowPos.name.equals("@end")){
            return turnDfaNodeNameToSpecialName(tempNode.name);
        }
        else
            return "ErrorClass";
    }

    public static void writeTokenToFile(String filePath,List<Token> tokenList){
        try {
            FileWriter myWriter = new FileWriter(filePath);
            for (Token token : tokenList) {
                myWriter.write(token.toString()+"\n");
            }
            myWriter.close();
            System.out.println("成功将token写入文件。文件路径："+filePath);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // TODO +=不对
        // 根据文法创建nfa
        Nfa nfa = new Nfa("src/main/" +
                "resources/lexical.txt");
        // 根据nfa创建dfa
        Dfa dfa = new Dfa(nfa);
        // 根据源程序创建token列表
        List<Token> tokenList =
                sourceFileToTokenList
                        ("src/main/resources/source.cc"
                                ,dfa);
        if (tokenList!=null)
            // 把token写入文件
            writeTokenToFile("src/main/resources/token.txt",tokenList);
    }

}
