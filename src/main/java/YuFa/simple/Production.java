package YuFa.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Production {
    public int pid;
    // 2型文法左侧为非终结符
    public String left;

    // 产生式右侧可能为任意情况
    public List<String> rightList;

    public Production(int pid , String left, List<String> rightList) {
        this.pid = pid;
        this.left = left;
        this.rightList = rightList;
    }

    public Production(String left) {
        this.left = left;
        this.rightList = new ArrayList<>(Arrays.asList("$"));
    }

    @Override
    public String toString() {
        String s ="pid: "+this.pid +" |||| left: "+this.left+" |||| rightList: ";
        String temp="";
        for (String s1 : rightList) {
            temp+=s1+" ";
        }
        s+=temp;
        return s;
    }
}
