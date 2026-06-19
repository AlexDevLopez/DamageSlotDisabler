package com.metal.damageslotdisabler.player;

import java.util.List;

public interface ILockedSlotsPlayer {
    List<Integer> getLockedSlots();
    void lockSlot(int slotId);
    void unlockSlot(int slotId);
    void unlockAll();
    boolean isSlotLocked(int slotId);
}
