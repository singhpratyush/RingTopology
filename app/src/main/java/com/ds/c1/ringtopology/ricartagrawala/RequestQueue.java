package com.ds.c1.ringtopology.ricartagrawala;

import android.support.annotation.NonNull;

import java.util.PriorityQueue;

import static com.ds.c1.ringtopology.utils.IPAddress.getIPAddress;

public class RequestQueue {

    static class RequestItem implements Comparable<RequestItem> {

        String IP;
        long time;

        RequestItem(String IP, long time) {
            this.IP = IP;
            this.time = time;
        }

        @Override
        public int compareTo(@NonNull RequestItem other) {
            return (int) (other.time - this.time);
        }
    }

    private static PriorityQueue<RequestItem> queue = new PriorityQueue<>();

    public static void add(String IP, long time) {
        queue.add(new RequestItem(IP, time));
    }

    public static boolean meAtTop() {
        return getIPAddress(true).equals(queue.peek().IP);
    }

    public static long topTime() {
        return queue.peek().time;
    }

    public static String pop() {
        return queue.poll().IP;
    }

    public static int size() {
        return queue.size();
    }

    public static boolean isEmpty() {
        return queue.isEmpty();
    }
}
