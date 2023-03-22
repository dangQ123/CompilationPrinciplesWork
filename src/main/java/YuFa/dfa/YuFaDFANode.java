package YuFa.dfa;

import YuFa.simple.SpecialListYuFa;
import YuFa.simple.YuFaTuple;

import java.util.ArrayList;
import java.util.List;

public class YuFaDFANode {
    // 这就是项目集
    //项目集的名称
    public String name;
    // 项目集里各个强化版产生式
    public List<DFAProduction> dfaProductionList;

    public boolean isOver = false;

    // 找到下一个项目集，通过任意符号连接
    public List<YuFaTuple> nextYuFaDFANode = new ArrayList<>();

    public YuFaDFANode copyYuFaDFANode(YuFaDFANode yuFaDFANode){
        return new YuFaDFANode(yuFaDFANode.name,yuFaDFANode.dfaProductionList);
    }

    public YuFaDFANode(String name, List<DFAProduction> dfaProductionList) {
        this.name = name;
        this.dfaProductionList = dfaProductionList;
    }

    public YuFaDFANode(String name, List<DFAProduction> dfaProductionList, List<YuFaTuple> nextYuFaDFANode) {
        this.name = name;
        this.dfaProductionList = dfaProductionList;
        this.nextYuFaDFANode = nextYuFaDFANode;
    }

    protected static boolean isDfaProductionIn(DFAProduction dfaProduction,YuFaDFANode other){
        for (DFAProduction dfaprod : other.dfaProductionList) {
            // 产生式本身是否相同的判断：只判断行号，也就是pid
            if ((dfaprod.production.pid == dfaProduction.production.pid)
                && (dfaprod.dot == dfaProduction.dot)
                    && dfaprod.befSearchStr.equals(dfaProduction.befSearchStr)
            ){
                return true;
            }
        }
        return false;
    }

    // 判断与另外一个项目集是否相同
    // 只需要知道产生式plus版是不是全都一样就行
    public boolean isSame(YuFaDFANode other){
        if (this.dfaProductionList.size()!=other.dfaProductionList.size())
            return false;
        else {
            for (DFAProduction dfaProduction : this.dfaProductionList) {
                if (!isDfaProductionIn(dfaProduction,other)){
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isStrInStrList(List<String> stringList,String str){
        for (String s : stringList) {
            if (s.equals(str)){
                return true;
            }
        }
        return false;

    }

    // 获取状态转移所使用到的符号
    public List<String> getAvailableV(){
        List<String> res = new ArrayList<>();
        for (DFAProduction dfaProduction : this.dfaProductionList) {
            if (dfaProduction.dot<dfaProduction.production.rightList.size()){
                String s = dfaProduction.production.rightList.get(dfaProduction.dot);
                if (!isStrInStrList(res,s))
                    res.add(s);
            }
        }
        for (String re : res) {
            if (re.equals("$")){
                res.remove(re);
                break;
            }
        }
        return res;
    }

    // 检测所有的DFAProduction，对于那些dot不处于size位置处的检测所有的DFAProduction，dot的位置后对应的字符均为终结符
    // 这样，可以作为循环判断的终结条件！！！（求闭包）
    // 现在可以不用
    public static boolean isAllDotBeforeVT(List<DFAProduction> dfaProductionList){
        SpecialListYuFa specialListYuFa = new SpecialListYuFa();
        for (DFAProduction dfaProduction : dfaProductionList) {
            if (dfaProduction.dot<dfaProduction.production.rightList.size()){
                String s = dfaProduction.production.rightList.get(dfaProduction.dot);
                if (!specialListYuFa.isVterminator(s)){
                    return false;
                }
            }else {
                // dot已经位于末尾的产生式，不需要再考虑
            }
        }
        return true;
    }

    // 将dot在非终结符前的DFAProduction拿出来
    public static List<DFAProduction> getDFAProdDotBeforeVN(List<DFAProduction> dfaProductionList){
        List<DFAProduction> res = new ArrayList<>();
        SpecialListYuFa specialListYuFa = new SpecialListYuFa();
        for (DFAProduction dfaProduction : dfaProductionList) {
            if (dfaProduction.dot<dfaProduction.production.rightList.size()){
                String s = dfaProduction.production.rightList.get(dfaProduction.dot);
                if (specialListYuFa.isVNon_terminal(s)){
                    res.add(dfaProduction);
                }
            }else {
                // dot已经位于末尾的产生式，不需要再考虑
            }
        }
        return res;
    }

    public static DFAProduction findDFAProductionByPid(List<DFAProduction> dfaProductionList,int pid){
        return dfaProductionList.get(pid-1);
    }

     //根据当前的任意符号，产生一个新的项目集，其实就是闭包算法
    public YuFaDFANode getNextYuFaDFANode(String V) {
        if (V.equals("$")){
            System.out.println("发现$符的转换，异常！");
            return null;
        }
        List<DFAProduction> prodList = new ArrayList<>();
        for (DFAProduction dfaProduction : this.dfaProductionList) {
            if (dfaProduction.dot<dfaProduction.production.rightList.size()&& dfaProduction.production.rightList.get(dfaProduction.dot).equals(V)){
                DFAProduction dfaProduction1 = new DFAProduction(dfaProduction.production,dfaProduction.dot+1,dfaProduction.befSearchStr);
                List<DFAProduction> dfaProductionList1 = dfaProduction1.expandDFAProductionList();
                for (DFAProduction dfaprod : dfaProductionList1) {
                    if (!DFAProduction.isDfaProdInList(prodList,dfaprod)){
                        prodList.add(dfaprod);
                    }
                }
            }
        }
        if (prodList.size()==0) return null;
        YuFaDFANode yuFaDFANode = new YuFaDFANode(String.valueOf(Integer.parseInt(this.name) + 1), prodList);
        return yuFaDFANode;
    }

    // 根据传来的符号找到下一个YuFaDFANode
    public YuFaDFANode findNextYuFaDFANodeByV(String V){
        for (YuFaTuple yuFaTuple : this.nextYuFaDFANode) {
            if (yuFaTuple.V.equals(V)){
                return yuFaTuple.yuFaDFANode;
            }
        }
        return null;
    }

}
