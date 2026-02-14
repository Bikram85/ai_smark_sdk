package com.market.alphavantage.util;

import java.util.Arrays;
import java.util.Objects;

public class AnalyticsMath {

    public static double avgLastN(Double[] arr, int n) {
        if (arr == null || arr.length == 0) return 0;

        int start = Math.max(0, arr.length - n);

        return Arrays.stream(arr, start, arr.length)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
    }

    public static int consecutiveUp(Long[] arr) {
        if (arr == null || arr.length < 2) return 0;

        int count = 0;
        for (int i = arr.length - 1; i > 0; i--) {
            if (arr[i] != null &&
                    arr[i - 1] != null &&
                    arr[i] > arr[i - 1]) count++;
            else break;
        }
        return count;
    }

    public static double liabilityEquityPercent(Long[] l, Long[] e) {
        if (l == null || e == null || l.length == 0 || e.length == 0)
            return 0;

        double liab = l[l.length - 1];
        double eq = e[e.length - 1];

        if (eq == 0) return 0;

        return (liab / eq) * 100;
    }

    public static int positiveCount(Long[] arr) {
        if (arr == null) return 0;

        return (int) Arrays.stream(arr)
                .filter(v -> v != null && v > 0)
                .count();
    }
}

