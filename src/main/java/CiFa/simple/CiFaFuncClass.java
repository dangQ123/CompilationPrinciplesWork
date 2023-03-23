package CiFa.simple;

import CiFa.dfa.DfaNode;

public class CiFaFuncClass {
    public static String turnDigitAndAlphabet(char c , boolean flag){
        if (flag)
            if (c=='e'||c=='i') return c+"";
        if (c>='a'&&c<='z'||c>='A'&&c<='Z'){
            return "alphabet";
        } else if (c>='0'&&c<='9') {
            return "digit";
        }else {
            return c+"";
        }
    }
    public static boolean ismyTupleVTequals(String s, DfaNode dfaNode){
        for (MyTuple myTuple : dfaNode.nextNodesList) {
            if (myTuple.VT.equals(s)){
                return true;
            }
        }
        return false;
    }
}
