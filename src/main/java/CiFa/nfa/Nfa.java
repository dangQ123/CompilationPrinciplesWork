package CiFa.nfa;

import CiFa.simple.MyTuple;
import CiFa.simple.Production;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Nfa {
    public NfaNode start = new NfaNode("start");
    public NfaNode end = new NfaNode("end",null);

    public Nfa() {
    }

    public Nfa(NfaNode start, NfaNode end) {
        this.start = start;
        this.end = end;
    }

    public Nfa(NfaNode start, NfaNode end, List<NfaNode> nfaNodes) {
        this.start = start;
        this.end = end;
        this.nfaNodes = nfaNodes;
    }

    // 除了开始和结束符
    public List<NfaNode> nfaNodes = new ArrayList<>();
    public List<String> getLinesFromFile(String filepath){
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

    // 判断文法所有的非终结符是否被包含进来了
    public boolean isExitInNfaNodes(String test){
        for (NfaNode nf:this.nfaNodes){
            if (test.equals(nf.name)){
                return true;
            }
        }
        return false;
    }

    public int findInNfaNodes(String test){
        for (int i=0;i<this.nfaNodes.size();i++){
            if (test.equals(this.nfaNodes.get(i).name)){
                return i;
            }
        }
        return -1;
    }

    // 从文件中生成NFA
    public Nfa(String filename){
        List<String> linesFromFile = getLinesFromFile(filename);
        ArrayList<Production> productions = new ArrayList<>();

        for (String line : linesFromFile){
            String[] splits = line.split(":");
            String left = splits[0];
            String right = splits[1];
            // 尝试寻找right中空格
            int posKon = right.indexOf(' ');

            // 先生成左边非终结符
            Production prod = new Production(left);
            // 没找到空格
            if (posKon==-1){
                prod.rightTuple = new MyTuple(right,null);
            }else {
                String[] rightS = right.split(" ");
                prod.rightTuple = new MyTuple(rightS[0],rightS[1]);
            }
            productions.add(prod);
        }
        // TODO 增加状态，也即是NfaNode节点
        for (Production p:productions){
            if (!isExitInNfaNodes(p.leftVN)){
                this.nfaNodes.add(new NfaNode(p.leftVN));
            }
        }
        // TODO 增加关系，也就是弧线
        for (Production p:productions){
            int leftPos = findInNfaNodes(p.leftVN);
            if (p.rightTuple.VN!=null){
//                int rightPos = findInNfaNodes(p.rightTuple.VN);
                this.nfaNodes.get(leftPos).nextNodesTupleList.add(p.rightTuple);
            }else {
                this.nfaNodes.get(leftPos).nextNodesTupleList.add(new MyTuple(p.rightTuple.VT,"end"));
            }
        }
        this.start = this.nfaNodes.get(findInNfaNodes("start"));
        this.nfaNodes.add(this.end);
    }


}
