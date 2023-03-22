package YuFa.simple;

import YuFa.dfa.YuFaDFANode;

public class YuFaTuple {
    // 通过任意符V，前往其他的yuFaDFANode
    public String V;
    public YuFaDFANode yuFaDFANode;

    public YuFaTuple(String v, YuFaDFANode yuFaDFANode) {
        V = v;
        this.yuFaDFANode = yuFaDFANode;
    }
}
