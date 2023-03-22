package CiFa.nfa;

import CiFa.simple.MyTuple;

import java.util.ArrayList;
import java.util.List;

public class NfaNode {
    public String name;
    public List<MyTuple> nextNodesTupleList = new ArrayList<>();

    public NfaNode(String name, List<MyTuple> nextNodesTupleList) {
        this.name = name;
        this.nextNodesTupleList = nextNodesTupleList;
    }

    public NfaNode() {
        this.name = "no name";
    }

    public NfaNode(String name){
        this.name = name;
    }


}
