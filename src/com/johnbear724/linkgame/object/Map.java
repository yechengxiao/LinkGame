package com.johnbear724.linkgame.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*
 *该类用于创建地图
 * 先创建一个大小为整个地图一半大小的数组a，
 * 然后再复制数组a获得与地图大小相同的数组,
 * 再把该数组打乱
 */

public class Map {
    
    private int rows;
    private int columns;
    private int availableSize;
    private int typeNum;
    private boolean isEvenNumber;
    
    public Map(int rows, int columns, int typeNum) {
        // TODO Auto-generated constructor stub
        this.rows = rows;
        this.columns = columns;
        this.typeNum = typeNum;
        if( (this.rows * this.columns % 2) == 0 ) {
            isEvenNumber = true;
            availableSize = rows * columns;
        } else {
            isEvenNumber = false;
            availableSize = rows * columns - 1;
        }
    }
    
    public List<Integer> getMap() {
        Random ran = new Random();
        List<Integer> halfList = new ArrayList<Integer> ();
        for(int i = 0; i < (availableSize / 2); i++) {
            halfList.add(ran.nextInt(typeNum));
        }
        
        halfList.addAll(halfList);
        if(!isEvenNumber) halfList.add(-1); 
        
        Collections.shuffle(halfList);
        return halfList;
    }
}
