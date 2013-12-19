package com.johnbear724.linkgame.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.johnbear724.linkgame.R;

public class ImageUtil {
    
    
    public static List<Integer> getImageValues()
    {
        try
        {
            // 得到R.drawable所有的属性, 即获取drawable目录下的所有图片
            Field[] drawableFields = R.drawable.class.getFields();
            List<Integer> resourceValues = new ArrayList<Integer>();
            for (Field field : drawableFields)
            {
                // 如果该Field的名称以p_开头
                if (field.getName().indexOf("p_") != -1)
                {
                    resourceValues.add(field.getInt(R.drawable.class));
                }
            }
            return resourceValues;
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
}
