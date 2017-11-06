package com.zony.multidownload.utils;

import java.text.Collator;
import java.util.ArrayList;

/**
 * 文件名(文件名中数字不能按照字符排序,而应按照数字排序) 采用了与Windows Explorer相同的排序算法
 * 
 * @author zony
 * @time 17-11-3 下午3:14
 */
public class WindowsExplorerSort {

    /**
     * 名称大小比较，大于:1,小于:-1,其他:按照字符重排
     *
     * @param
     * @author zony
     * @time 17-11-3 下午3:00
     */
    public static int compare(String name1, String name2) {
        int cmp = -1;
        try {
            ArrayList<SplitData> a1 = splitByType(name1);
            ArrayList<SplitData> a2 = splitByType(name2);
            cmp = cmpSplitArray(a1, a2);
        } catch (Throwable e) {
            e.printStackTrace();
            cmp = -1;
        }
        return cmp;
    }

    private static boolean isDigit(byte b) {
        return b >= '0' && b <= '9';
    }

    private static class SplitData {
        public boolean isDigit;

        public String str;

        public SplitData(boolean isDigit, String str) {
            this.isDigit = isDigit;
            if (isDigit) {
                this.str = str;
            } else {
                this.str = str.toLowerCase(java.util.Locale.CHINA);
            }
        }
    }

    private static ArrayList<SplitData> splitByType(String name) {
        int i;
        int start = 0;
        String subStr;
        boolean isPrevDigit = false;
        boolean isCurDigit = false;
        byte[] b = name.getBytes();
        ArrayList<SplitData> arr = new ArrayList<SplitData>();

        for (i = 0; i < b.length; i++) {
            isCurDigit = isDigit(b[i]);
            if (i > 0 && isCurDigit != isPrevDigit) {
                // 之前是数字,现在不是数字 / 之前不是数字,现在是数字.需要将之前的字符分成一组,保存其类型。最后修正新分组的起始位置
                subStr = new String(b, start, i - start);
                arr.add(new SplitData(isPrevDigit, subStr));
                start = i;
            }
            // 更新上一个字符的类型
            isPrevDigit = isCurDigit;
        }
        // 最后剩余的字符自动归为一组
        if (start < b.length) {
            subStr = new String(b, start, i - start);
            arr.add(new SplitData(isCurDigit, subStr));
        }
        return arr;
    }

    private static int cmpSplitArray(ArrayList<SplitData> a1, ArrayList<SplitData> a2) {
        int i;
        SplitData data1, data2;
        int cmp = -1;
        int size = Math.min(a1.size(), a2.size());
        Collator collator = null;
        for (i = 0; i < size; i++) {
            // 逐个分组进行比较
            data1 = (SplitData) a1.get(i);
            data2 = (SplitData) a2.get(i);
            if (data1.isDigit && data2.isDigit) {
                // 当都是数字时，先比较数值大小,如果数字相等则再按照原始数字字符串进行比较
                Integer seq1 = Integer.valueOf(data1.str);
                Integer seq2 = Integer.valueOf(data2.str);
                cmp = seq1.compareTo(seq2);
                if (cmp == 0) {
                    cmp = data1.str.compareTo(data2.str);
                }
            } else {
                if (collator == null) {
                    collator = Collator.getInstance(java.util.Locale.CHINA);
                }
                cmp = collator.compare(data1.str, data2.str);
            }
            if (cmp != 0) {
                // 已经区分出大小,不再比较剩余的分组
                break;
            }
        }
        return cmp;
    }
}
