package com.sh.impl;

import com.sh.Priority;
import com.sh.ProcessId;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * CM with limited capacity.
 * Any attempt to add a process while in over capacity will result in {@link IllegalStateException}
 */
public class DefaultCapacityManager implements CapacityManager {
  private final int capacity;
  private final Set<ProcessId> reservedIds = new HashSet<>();

  public DefaultCapacityManager(int capacity) {
    this.capacity = capacity;
  }

  @Override
  public synchronized ProcessId reserveCapacityOrSelectForTermination(@NotNull ProcessId pid, @NotNull Priority priority) {
    if (reservedIds.contains(pid)) {
      throw new IllegalStateException("Process exists: " + pid);
    } else if (reservedIds.size() == capacity) {
      throw new IllegalStateException("Cannot reserve capacity: " + pid);
    }
    reservedIds.add(pid);
    return null;
  }

  @Override
  public synchronized boolean freeCapacity(@NotNull ProcessId pid) {
    return reservedIds.remove(pid);
  }
}
