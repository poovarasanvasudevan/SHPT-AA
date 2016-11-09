package com.poovarasan.miu.util;

import android.content.Context;

/**
 * Created by poovarasanv on 9/11/16.
 */

public class Util {
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
