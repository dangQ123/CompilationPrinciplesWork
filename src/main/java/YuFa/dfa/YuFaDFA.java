package YuFa.dfa;

import YuFa.simple.Production;
import YuFa.simple.YuFaTuple;

import java.util.ArrayList;
import java.util.List;

public class YuFaDFA {
    // LR项目集规范族
    public List<YuFaDFANode> yuFaDFANodeList = new ArrayList<>();
    public YuFaDFANode start;

    public Production findProductionByPid(int pid){
        for (YuFaDFANode yuFaDFANode : this.yuFaDFANodeList) {
            for (DFAProduction dfaProduction : yuFaDFANode.dfaProductionList) {
                if (dfaProduction.production.pid==pid){
                    return dfaProduction.production;
                }
            }
        }
        return null;
    }


    // 当前YuFaDFA是否包含某个YuFaDFANode
    public boolean isContainYuFaDFANode(YuFaDFANode yuFaDFANode){
        for (YuFaDFANode faDFANode : this.yuFaDFANodeList) {
            if (yuFaDFANode.isSame(faDFANode)){
                return true;
            }
        }
        return false;
    }

    public List<YuFaDFANode> copyListYuFaDFANode(List<YuFaDFANode> yuFaDFANodeList){
        List<YuFaDFANode> res = new ArrayList<>();
        for (YuFaDFANode yuFaDFANode : yuFaDFANodeList) {
            res.add(yuFaDFANode);
        }
        return res;
    }

    public int findYuFaDFANodeInList(List<YuFaDFANode> yuFaDFANodeList , YuFaDFANode yuFaDFANode){
        for (int i=0;i<yuFaDFANodeList.size();i++){
            if (yuFaDFANodeList.get(i).isSame(yuFaDFANode)){
                return i;
            }
        }
        return -1;
    }

    public YuFaDFA (YuFaDFANode start) {
        int nameCount = 0;
        this.start = start;
        this.yuFaDFANodeList.add(start);
        int lastyuFaDFANodeListSize = yuFaDFANodeList.size()-1;
        while (lastyuFaDFANodeListSize!=yuFaDFANodeList.size()){
            lastyuFaDFANodeListSize = yuFaDFANodeList.size();
            // 生成临时List<YuFaDFANode>
            List<YuFaDFANode> yuFaDFANodeListTemp = copyListYuFaDFANode(this.yuFaDFANodeList);
            for (YuFaDFANode yuFaDFANode : yuFaDFANodeListTemp) {
                if (yuFaDFANode.isOver == false){
                    List<String> availableV = yuFaDFANode.getAvailableV();
                    for (String sav : availableV) {
                        YuFaDFANode nextYuFaDFANode = yuFaDFANode.getNextYuFaDFANode(sav);
                        if (!isContainYuFaDFANode(nextYuFaDFANode)){
                            // 修改名字
                            nameCount++;
                            nextYuFaDFANode.name = String.valueOf(nameCount);
                            this.yuFaDFANodeList.add(nextYuFaDFANode);
                            // 增加关系
                            yuFaDFANode.nextYuFaDFANode.add(new YuFaTuple(sav,nextYuFaDFANode));
                        }else {
                            // 只增加关系
                            int i = findYuFaDFANodeInList(this.yuFaDFANodeList, nextYuFaDFANode);
                            yuFaDFANode.nextYuFaDFANode.add(new YuFaTuple(sav,this.yuFaDFANodeList.get(i)));
                        }
                    }
                }
                yuFaDFANode.isOver = true;
            }
        }
    }
}
