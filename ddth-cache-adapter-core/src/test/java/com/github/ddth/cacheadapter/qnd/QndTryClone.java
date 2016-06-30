package com.github.ddth.cacheadapter.qnd;

import java.util.ArrayList;
import java.util.List;

import com.github.ddth.cacheadapter.utils.CacheUtils;

public class QndTryClone {

    public static void main(String[] args) {
        {
            Object org = "Original";
            Object cloned = CacheUtils.tryClone(org);
            System.out.println(org == cloned);
        }
        {
            Object org = 1;
            Object cloned = CacheUtils.tryClone(org);
            System.out.println(org == cloned);
        }
        {
            Object org = new Object[] { "1", "2", "3", "4" };
            Object cloned = CacheUtils.tryClone(org);
            System.out.println(org == cloned);
        }
        {
            Object org = new ArrayList<>();
            {
                ((List) org).add("1");
                ((List) org).add("2");
                ((List) org).add("3");
            }
            Object cloned = CacheUtils.tryClone(org);
            System.out.println(org == cloned);
        }
    }

}
