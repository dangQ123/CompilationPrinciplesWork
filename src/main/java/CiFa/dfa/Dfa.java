package CiFa.dfa;

import CiFa.nfa.Nfa;
import CiFa.nfa.NfaNode;
import CiFa.simple.ClosureWithFlag;
import CiFa.simple.MyTuple;
import CiFa.simple.SpecialList;

import java.util.ArrayList;
import java.util.List;

public class Dfa {
    // 大致和Nfa相同，没有什么可变化的
    public DfaNode start = new DfaNode("start");
    public DfaNode end = new DfaNode("end",null);

    public List<DfaNode> DfaNodes = new ArrayList<>();

    public Dfa(){}

    // move(I,a) 操作，这里只有一个转态I的转移
    public List<NfaNode> move(Nfa nfa, NfaNode nfaNode , String VT){
        List<NfaNode> res = new ArrayList<>();
        for (int i = 0; i<nfaNode.nextNodesTupleList.size(); i++) {
            if (nfaNode.nextNodesTupleList.get(i).VT.equals(VT)){
                int inNfaNodes = nfa.findInNfaNodes(nfaNode.nextNodesTupleList.get(i).VN);
                res.add(nfa.nfaNodes.get(inNfaNodes));
            }
        }
        return res;
    }

    public boolean isNfaNodeExit(List<NfaNode> res , NfaNode nfaNode){
        for (NfaNode resNfanode : res) {
            if (resNfanode.name.equals(nfaNode.name)){
                return true;
            }
        }
        return false;

    }

    // move(List I , a) 这才是真正算法中调用的move
    public List<NfaNode> move(Nfa nfa,List<NfaNode> nfaNodeList , String VT){
        List<NfaNode> res = new ArrayList<>();
        for (NfaNode nfaNode : nfaNodeList) {
            // 在这里进行去重
            if (!nfaNode.name.equals("end")){
                List<NfaNode> moveList = move(nfa, nfaNode, VT);
                // 往这里一个一个添加元素
                for (NfaNode node : moveList) {
                    if (!isNfaNodeExit(res,node))
                        res.add(node);
                }
            }
        }
        return res;
    }

    public NfaNode turnVNtoNfaNode(Nfa nfa , String VN){
        for (NfaNode nfaNode : nfa.nfaNodes) {
            if (nfaNode.name.equals(VN)){
                return nfaNode;
            }
        }
        return new NfaNode();
    }

    // 找到节点中可以通过一条E能到达的转态
    // 根据文法一定只有start作为nfaNode时才会有不为空的节点返回
    public List<NfaNode> findENode(Nfa nfa,NfaNode nfaNode){
        List<NfaNode> res = new ArrayList<>();
        for (MyTuple myTuple : nfaNode.nextNodesTupleList) {
            if (myTuple.VT.equals("$")){
                res.add(turnVNtoNfaNode(nfa,myTuple.VN));
            }
        }
        return res;
    }

    // 当前文法下，return move即可
    public ClosureWithFlag E_closure(List<NfaNode> move){
        ClosureWithFlag res = new ClosureWithFlag(move);
        return res;
    }
    // C中存在尚未被标记的子集则返回true
    public boolean isExitNotFlagTinC(List<ClosureWithFlag> C){
        for (ClosureWithFlag closureWithFlag : C) {
            if (closureWithFlag.isFlag==false){
                return true;
            }
        }
        return false;
    }

    public boolean isSameClosure(ClosureWithFlag a,ClosureWithFlag b){
        if (a.nfaNodeList.size()!=b.nfaNodeList.size()) return false;
        for (int i=0;i<a.nfaNodeList.size();i++){
            if (!a.nfaNodeList.get(i).name.equals(b.nfaNodeList.get(i).name)){
                return false;
            }
        }
        return true;
    }

    public boolean isUinC(ClosureWithFlag U,List<ClosureWithFlag> C){
        for (ClosureWithFlag withFlag : C) {
            if (isSameClosure(U,withFlag)){
                return true;
            }
        }
        return false;
    }

    public Dfa(DfaNode start, DfaNode end, List<DfaNode> dfaNodes) {
        this.start = start;
        this.end = end;
        DfaNodes = dfaNodes;
    }

    public String reNameListOfNfaNode(List<NfaNode> nfaNodeList){
        String s = "";
        for (NfaNode nfaNode : nfaNodeList) {
            s+="@";
            s+=nfaNode.name;
        }
        return s;
    }

    public int findIndexByNameInDfaNodes(List<DfaNode> dfaNodes,String name){
        for (int i=0;i<dfaNodes.size();i++){
            if (dfaNodes.get(i).name.equals(name))
                return i;
        }
        return -1;
    }

    // nfa  =>  dfa
    public Dfa(Nfa nfa){
        /*
        *   构造出dfa
        *   要求：对于dfa的每个节点命名，应该为对应closure函数生成的多个（实际最多就两个）
        *   名字相加，看起来好辨认些，也好写代码
        *
        * */
        // 生成开始集合K0
        List<ClosureWithFlag> C = new ArrayList<>();
        List<NfaNode> K0 = new ArrayList<>();
        K0.add(nfa.start);
        ClosureWithFlag closureWithFlag = E_closure(move(nfa,nfa.start,"$"));
        C.add(closureWithFlag);
        this.start = new DfaNode(reNameListOfNfaNode(closureWithFlag.nfaNodeList));
        this.DfaNodes.add(start);


        // C中存在尚未被标记的子集T
        while (isExitNotFlagTinC(C)){
            ClosureWithFlag T = null;
            for (ClosureWithFlag withFlag : C) {
                if (withFlag.isFlag==false){
                    // 将其标记
                    withFlag.isFlag = true;
                    T = withFlag;
                    break;  // 一次只处理一个
                }
            }
            // 对每个输入字母a do
            for (String a : SpecialList.VT) {
                ClosureWithFlag U = E_closure(move(nfa, T.nfaNodeList, a));
                if (U.nfaNodeList.size()>0&&!isUinC(U,C)){
                    // 在这里要生成弧线
                    U.isFlag = false;
                    C.add(U);
                    /*
                    *   首先在dfanode中找到已有的T，U
                    *   往T中添加一条到U的弧线
                    *   Tuple<DfaNode , VT>
                    *
                    */
                    String Tname = reNameListOfNfaNode(T.nfaNodeList);
                    String Uname = reNameListOfNfaNode(U.nfaNodeList);
                    DfaNode Tnode = this.DfaNodes.get(findIndexByNameInDfaNodes(
                            this.DfaNodes,Tname));
                    DfaNode Unode = new DfaNode(Uname);
                    Tnode.nextNodesList.add(new MyTuple(a,Uname));
                    this.DfaNodes.add(Unode);
                } else if (U.nfaNodeList.size()>0&&isUinC(U,C)) {
                    String Tname = reNameListOfNfaNode(T.nfaNodeList);
                    String Uname = reNameListOfNfaNode(U.nfaNodeList);
                    DfaNode Tnode = this.DfaNodes.get(findIndexByNameInDfaNodes(
                            this.DfaNodes,Tname));
                    DfaNode Unode = new DfaNode(Uname);
                    Tnode.nextNodesList.add(new MyTuple(a,Uname));
                }
            }
        }
        for (DfaNode dfaNode : this.DfaNodes) {
            if (!dfaNode.name.equals("@end"))
                dfaNode.nextNodesList.add(new MyTuple("$","@end"));
        }
    }

}
