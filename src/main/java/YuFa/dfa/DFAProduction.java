package YuFa.dfa;

import YuFa.simple.Production;
import YuFa.simple.SpecialListYuFa;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DFAProduction {
    /*
     *   这个类相当于强化版的Production，需要
     *   1.  production
     *   2.  dot点的位置，用int表示，数值含义为在第几个符号之前，不包含向前搜索符
     *   3.  向前搜索符，默认为'#'
     * */
    public Production production;
    public int dot;
    public String befSearchStr;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DFAProduction d = (DFAProduction) o;
        return production.pid == d.production.pid &&
                dot==d.dot&&befSearchStr.equals(d.befSearchStr);
    }

    @Override
    public int hashCode() {

        return Objects.hash(production.pid,dot,befSearchStr);
    }

    @Override
    public String toString() {
        String s = "";
        s += "------------------------\n" + production + "\n" + "dot: " + dot + "\n" + "befSearchStr: " + befSearchStr + "\n------------------------\n";
        return s;
    }

    public DFAProduction(Production production) {
        this.production = production;
        dot = 0;
        befSearchStr = "#";
    }

    public DFAProduction(Production production, int dot, String befSearchStr) {
        this.production = production;
        this.dot = dot;
        this.befSearchStr = befSearchStr;
    }

    public static List<DFAProduction> turnProdListToDfaProdList(List<Production> productionList) {
        List<DFAProduction> res = new ArrayList<>();
        for (Production production : productionList) {
            DFAProduction dfaProduction = new DFAProduction(production);
            res.add(dfaProduction);
        }
        return res;
    }

    public boolean isSame(DFAProduction dfaProduction) {
        if (dfaProduction.production.pid == this.production.pid
                && (dfaProduction.dot == this.dot)
                && (dfaProduction.befSearchStr.equals(this.befSearchStr))
        ) {
            return true;
        } else
            return false;
    }

    public static boolean isDfaProdInList(List<DFAProduction> dfaProductionList, DFAProduction dfaProduction) {
        for (DFAProduction production : dfaProductionList) {
            if (production.isSame(dfaProduction)) {
                return true;
            }
        }
        return false;
    }
    /****************************************************************/
    public List<DFAProduction> expandDFAProductionList(){
        List<DFAProduction> res = new ArrayList<>();
        SpecialListYuFa specialListYuFa = new SpecialListYuFa();
        // 进行扩充，返回的集合包含自己，且产生式不能有重复
        res.add(this);
        if (this.dot>=this.production.rightList.size()){
            return res;
        }
        else if (this.dot==this.production.rightList.size()-1) {
            String Vdot = this.production.rightList.get(this.dot);
            if (specialListYuFa.isVterminator(Vdot)){
                // Vdot是终结符
                return res;
            }else {
                // Vdot不是终结符
                List<Production> leftIsVNProduction = SpecialListYuFa.findLeftIsVNProduct(Vdot);
                // 将是非终结符的产生式全部找出来，并转化为DFAProduction，这个时候记住！向前搜索符还没修改
                // dfaProductionList一定是非重复的！
//                List<DFAProduction> dfaProductionList = turnProdListToDfaProdList(leftIsVNProduction);
                String befS = this.befSearchStr;
//                if (befS.equals("#")){
//                    befS = "$";
//                }
                List<String> firstList = SpecialListYuFa.First(befS);
//                for (int i=0;i<firstList.size();i++) {
//                    if (firstList.get(i).equals("$")){
//                        firstList.set(i, "#");
//                    }
//                }
                List<DFAProduction> temp = new ArrayList<>();
                for (String f : firstList) {
                    for (Production production1 : leftIsVNProduction) {
                        DFAProduction dfaProduction = new DFAProduction(production1);
                        dfaProduction.befSearchStr = f;
                        temp.add(dfaProduction);
                    }
                }

                List<DFAProduction> tempRes = new ArrayList<>();
                for (DFAProduction dfaProduction : temp) {
                    if (QuickTable.quickTable.containsKey(dfaProduction)){
                        tempRes.addAll(QuickTable.quickTable.get(dfaProduction));
                    }else {
                        List<DFAProduction> dfaProductionList = dfaProduction.expandDFAProductionList();
                        tempRes.addAll(dfaProductionList);
                        QuickTable.quickTable.put(dfaProduction,dfaProductionList);
                    }
                }
                for (DFAProduction tempRe : tempRes) {
                    if (!isDfaProdInList(res,tempRe)){
                        res.add(tempRe);
                    }
                }
            }
        }
        else {
            String Vdot = this.production.rightList.get(this.dot);
            if (specialListYuFa.isVterminator(Vdot)){
                // Vdot是终结符
                return res;
            }else {
                // Vdot不是终结符
                List<Production> leftIsVNProduction = SpecialListYuFa.findLeftIsVNProduct(Vdot);
                // 将是非终结符的产生式全部找出来，并转化为DFAProduction，这个时候记住！向前搜索符还没修改
//                List<DFAProduction> dfaProductionList = turnProdListToDfaProdList(leftIsVNProduction);
                String befS = this.befSearchStr;
//                if (befS.equals("#")){
//                    befS = "$";
//                }
                // 文法保证，无$
                List<String> firstList = SpecialListYuFa.First(this.production.rightList.get(this.dot+1));
//                for (int i=0;i<firstList.size();i++) {
//                    if (firstList.get(i).equals("$")){
//                        firstList.set(i, "#");
//                    }
//                }
                List<DFAProduction> temp = new ArrayList<>();

                for (String f : firstList) {
                    for (Production production1 : leftIsVNProduction) {
                        DFAProduction dfaProduction = new DFAProduction(production1);
                        dfaProduction.befSearchStr = f;
                        temp.add(dfaProduction);
                    }

                }
                List<DFAProduction> tempRes = new ArrayList<>();
                for (DFAProduction dfaProduction : temp) {
                    if (QuickTable.quickTable.containsKey(dfaProduction)){
                        tempRes.addAll(QuickTable.quickTable.get(dfaProduction));
                    }else{
                        List<DFAProduction> dfaProductionList = dfaProduction.expandDFAProductionList();
                        tempRes.addAll(dfaProductionList);
                        QuickTable.quickTable.put(dfaProduction,dfaProductionList);
                    }

                }
                for (DFAProduction tempRe : tempRes) {
                    if (!isDfaProdInList(res,tempRe)){
                        res.add(tempRe);
                    }
                }
            }
        }
        return res;
    }
}