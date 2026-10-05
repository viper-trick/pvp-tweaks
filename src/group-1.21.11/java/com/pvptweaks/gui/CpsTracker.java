package com.pvptweaks.gui;

import java.util.LinkedList;
import java.util.Queue;

public class CpsTracker {
    private static final Queue<Long> leftClicks = new LinkedList<>();
    private static final Queue<Long> rightClicks = new LinkedList<>();

    public static void registerClick(int button) {
        long now = System.nanoTime();
        if (button == 0) {
            synchronized (leftClicks) { leftClicks.add(now); }
        } else if (button == 1) {
            synchronized (rightClicks) { rightClicks.add(now); }
        }
    }

    public static int getLeftCps() {
        long threshold = System.nanoTime() - 1_000_000_000L;
        synchronized (leftClicks) {
            while (!leftClicks.isEmpty() && leftClicks.peek() < threshold) leftClicks.poll();
            return leftClicks.size();
        }
    }

    public static int getRightCps() {
        long threshold = System.nanoTime() - 1_000_000_000L;
        synchronized (rightClicks) {
            while (!rightClicks.isEmpty() && rightClicks.peek() < threshold) rightClicks.poll();
            return rightClicks.size();
        }
    }
}
