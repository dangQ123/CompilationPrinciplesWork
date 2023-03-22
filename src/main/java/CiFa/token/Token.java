package CiFa.token;

public class Token {
    public int lineId;  //所在行号
    public String classification;   // 类别
    public String wordSelf; //内容

    /*
    *   不准无参构造
    * */
    public Token(int lineId, String classification, String wordSelf) {
        this.lineId = lineId;
        this.classification = classification;
        this.wordSelf = wordSelf;
    }

    @Override
    public String toString() {
        String res = "";
        res+=Integer.toString(lineId);
        res += " "+classification+" "+wordSelf;
        return res;
    }
}
