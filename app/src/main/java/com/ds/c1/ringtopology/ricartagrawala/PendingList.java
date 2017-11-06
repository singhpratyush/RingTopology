package com.ds.c1.ringtopology.ricartagrawala;

import java.util.ArrayList;
import java.util.Collection;

public class PendingList {
    private static ArrayList<String> pendingList = new ArrayList<>();

    public static void add(String str) {
        pendingList.add(str);
    }

    public static boolean remove(String str) {
        boolean status = pendingList.remove(str);
        if (!status) {
            return false;
        }
        if (RicartAgrawala.requestingCriticalSection() && pendingList.isEmpty()) {
            RicartAgrawala.enterCriticalSection();
        }
        return true;
    }

    public static void addAll(Collection<String> str) {
        pendingList.addAll(str);
    }

    public static boolean removeAll(Collection<String> str) {
        return pendingList.removeAll(str);
    }

    public boolean isEmpty() {
        return pendingList.isEmpty();
    }
}
