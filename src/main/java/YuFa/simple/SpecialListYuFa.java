package YuFa.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static YuFa.YuFaMain.readSynGrammer;

public class SpecialListYuFa {

     // 终结符集合
    public  List<String> VterminatorList = new ArrayList<>();
    // 非终结符集合
    public  List<String> VNon_terminalList = new ArrayList<>();

    public boolean isVterminator(String Vterminator){
        for (String s : this.VterminatorList) {
            if (s.equals(Vterminator)){
                return true;
            }
        }
        return false;
    }

    public boolean isVNon_terminal(String VNon_terminal){
        for (String s : this.VNon_terminalList) {
            if (s.equals(VNon_terminal)){
                return true;
            }
        }
        return false;
    }

    public SpecialListYuFa() {
        List<Production> productionList = readSynGrammer();
        List<String> all = new ArrayList<>();
        for (Production production : productionList) {
            if (!isVNon_terminal(production.left)){
                VNon_terminalList.add(production.left);
                all.add(production.left);
            }

            for (String s : production.rightList) {
                if (!isVterminator(s)){
                    VterminatorList.add(s);
                    all.add(s);
                }
            }
        }
        all.removeAll(VNon_terminalList);
        VterminatorList = all;
        VterminatorList.add("#");
        VterminatorList.add("$");
    }

    // 找到以VN开头的产生式
    public static List<Production> findLeftIsVNProduct(String VN){
        List<Production> res = new ArrayList<>();
        List<Production> productionList = readSynGrammer();
        for (Production production : productionList) {
            if (production.left.equals(VN))
            {
                res.add(production);
            }
        }
        return res;
    }

    public static boolean isStrListContainS(List<String> strList , String s){
        for (String s1 : strList) {
            if (s1.equals(s))
                return true;
        }
        return false;
    }

    // 把$从stringList排除
    public static List<String> exceptEout(List<String> stringList){
        List<String> res = new ArrayList<>();
        for (String s : stringList) {
            if (!s.equals("$")){
                res.add(s);
            }
        }
        return res;
    }

    // list中是否有$
    public static boolean isEInList(List<String> stringList){
        for (String s : stringList) {
            // TODO 可能重大影响
            if (s.equals("$")){
                return true;
            }
        }
        return false;
    }

    public static boolean isVInList(List<String> stringList,String V){
        for (String s : stringList) {
            if (s.equals(V)){
                return true;
            }
        }
        return false;
    }


    public static List<String> eceptAddALLStr(List<String> stringList , List<String> addList){
        List<String> res = stringList;
        for (String s : addList) {
            if (!isVInList(res,s)){
                res.add(s);
            }
        }
        return res;
    }

    public static boolean isAllYContainsE(List<List<String>> rightListY){
        if (rightListY.size()==0) return false;
        for (List<String> stringList : rightListY) {
            if (!isEInList(stringList)){
                return false;
            }
        }
        return true;
    }


    // 求解单个字符的first集
    public static List<String> First(String X){
        List<String> res = new ArrayList<>();
        SpecialListYuFa specialListYuFa = new SpecialListYuFa();
        if (specialListYuFa.isVterminator(X)){
            res.add(X);
            return res;
        }else {
            // 找出左侧为X的所有产生式
            List<Production> leftIsXProduct = findLeftIsVNProduct(X);
            for (Production production : leftIsXProduct) {
                int count=0;
                List<List<String>> allY = new ArrayList<>();
                for (String Y : production.rightList) {
                    if (specialListYuFa.isVterminator(Y)){
                        res.add(Y);
                        break;
                    }else {
                        count++;
                        List<String> firstY = First(Y);
                        allY.add(firstY);
                        List<String> firstYEcep =exceptEout(firstY);
                        if (count==production.rightList.size()&&isAllYContainsE(allY)){
//                res.add("$");
                            res.add("#");
                        }
                        res = eceptAddALLStr(res,firstYEcep);
                        if (!isEInList(firstY)){
                            break;
                        }
                    }
                }
            }
        }

        return res;
    }

    // 求解串的first集
    public static List<String> First(List<String> strList){
        SpecialListYuFa specialListYuFa = new SpecialListYuFa();
        if (specialListYuFa.isVterminator(strList.get(0))){
            return Arrays.asList(strList.get(0));
        }

        List<String> res = new ArrayList<>();
        int count=0;
        List<List<String>> allX = new ArrayList<>();
        for (String X : strList) {
            count++;
            List<String> firstX = First(X);
            allX.add(firstX);
            List<String> firstXEcep =exceptEout(firstX);
            res = eceptAddALLStr(res,firstXEcep);
            if (!isEInList(firstX)){
                break;
            }if (count==strList.size()&&isAllYContainsE(allX)){
//                res.add("$");
                res.add("#");
            }
        }

        return res;
    }

    public int getVTnthInList(String VT){
        for (int i=0;i<this.VterminatorList.size();i++){
            if (this.VterminatorList.get(i).equals(VT)){
                return i;
            }
        }
        System.out.println("error!");
        return -1;
    }

    public int getVNnthInList(String VN){
        for (int i=0;i<this.VNon_terminalList.size();i++){
            if (this.VNon_terminalList.get(i).equals(VN)){
                return i;
            }
        }
        System.out.println("error!");
        return -1;
    }

}
