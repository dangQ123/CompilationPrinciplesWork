package CiFa.simple;

public class Production {
    // 非终结符
    public String leftVN;

    public MyTuple rightTuple;

    public Production(String leftVN, MyTuple rightTuple) {
        this.leftVN = leftVN;
        this.rightTuple = rightTuple;
    }

    public Production(String leftVN) {
        this.leftVN = leftVN;
    }
}
