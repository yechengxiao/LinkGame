package com.johnbear724.linkgame.util;

import com.johnbear724.linkgame.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageUtil {
    
    
//    public static List<Integer> getImageValues()
//    {
//        try
//        {
//            // 得到R.drawable所有的属性, 即获取drawable目录下的所有图片
//            Field[] drawableFields = R.drawable.class.getFields();
//            List<Integer> resourceValues = new ArrayList<Integer>();
//            for (Field field : drawableFields)
//            {
//                // 如果该Field的名称以p_开头
//                if (field.getName().indexOf("p_") != -1)
//                {
//                    resourceValues.add(field.getInt(R.drawable.class));
//                }
//            }
//            return resourceValues;
//        }
//        catch (Exception e)
//        {
//            return null;
//        }
//    }

    /**
     * 获取所有绘制元素图片的 id
     * @return id列表
     */
    public static List<Integer> getImageValues()
    {
        Integer[] resourceArray = new Integer[] {
                R.drawable.p_1,
                R.drawable.p_2,
                R.drawable.p_3,
                R.drawable.p_4,
                R.drawable.p_5,
                R.drawable.p_6,
                R.drawable.p_7,
                R.drawable.p_8,
                R.drawable.p_9,
                R.drawable.p_10,
                R.drawable.p_11,
                R.drawable.p_12,
                R.drawable.p_13,
                R.drawable.p_14,
                R.drawable.p_15,
        };
        List<Integer> resourceValues = new ArrayList<Integer>(Arrays.asList(resourceArray));
        return resourceValues;
    }
    
}
