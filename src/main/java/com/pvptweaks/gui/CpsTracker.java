package com.pvptweaks.gui;

import java.util.LinkedList;
import java.util.Queue;

public class CpsTracker {
    private static final Queue<Long> leftClicks = new LinkedList<>();
    private static final Queue<Long> rightClicks = new LinkedList<>();
    
    private static boolean lastLeftDown = false;
    private static boolean lastRightDown = false;

    public static void update(boolean leftDown, boolean rightDown) {
        long now = System.currentTimeMillis();
        if (leftDown && !lastLeftDown) {
            synchronized(leftClicks) { leftClicks.add(now); }
        }
        if (rightDown && !lastRightDown) {
            synchronized(rightClicks) { rightClicks.add(now); }
        }
        lastLeftDown = leftDown;
        lastRightDown = rightDown;
    }

    public static int getLeftCps() {
        long now = System.currentTimeMillis();
        synchronized(leftClicks) {
            while (!leftClicks.isEmpty() && now - leftClicks.peek() > 1000) leftClicks.poll();
            return leftClicks.size();
        }
    }

    public static int getRightCps() {
        long now = System.currentTimeMillis();
        synchronized(rightClicks) {
            while (!rightClicks.isEmpty() && now - rightClicks.peek() > 1000) rightClicks.poll();
            return rightClicks.size();
        }
    }
}
