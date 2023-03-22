package CiFa.dfa;

import CiFa.simple.MyTuple;

import java.util.ArrayList;
import java.util.List;

public class DfaNode {
    public String name;
    public List<MyTuple> nextNodesList = new ArrayList<>();

    public DfaNode(String name, List<MyTuple> nextNodesList) {
        this.name = name;
        this.nextNodesList = nextNodesList;
    }

    public DfaNode(String name) {
        this.name = name;
    }

    public DfaNode() {
    }
}
