package com.sh.impl;

import com.sh.Priority;
import com.sh.ProcessId;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;

/**
 * CM with limited capacity.
 * Adding a process while in over capacity will return a pid of a process to be killed
 */
public class EvictingCapacityManager implements CapacityManager {
  private final int capacity;
  private final LinkedHashSet<ProcessId> reservedIds = new LinkedHashSet<>();

  public EvictingCapacityManager(int capacity) {
    this.capacity = capacity;
  }

  @Override
  public synchronized ProcessId reserveCapacityOrSelectForTermination(@NotNull ProcessId pid, @NotNull Priority priority) {
    if (reservedIds.contains(pid)) {
      throw new IllegalStateException("Process exists: " + pid);
    } else if (reservedIds.size() < capacity) {
      reservedIds.add(pid);
      return null;
    }

    ProcessId first = reservedIds.iterator().next();
    reservedIds.remove(first);
    reservedIds.add(pid);
    return first;
  }

  @Override
  public synchronized boolean freeCapacity(@NotNull ProcessId pid) {
    return reservedIds.remove(pid);
  }
}
