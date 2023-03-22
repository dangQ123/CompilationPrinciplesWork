package CiFa.simple;

import CiFa.nfa.NfaNode;

import java.util.List;

public class ClosureWithFlag {
    public List<NfaNode> nfaNodeList;

    // 是否被标记，默认没有标记
    public boolean isFlag = false;

    public ClosureWithFlag(List<NfaNode> nfaNodeList) {
        this.nfaNodeList = nfaNodeList;
    }
}
