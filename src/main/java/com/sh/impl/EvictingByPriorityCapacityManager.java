package com.sh.impl;

import com.sh.Priority;
import com.sh.ProcessId;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * CM with limited capacity.
 * Adding a process while in over capacity will return a pid of a process to be killed.
 * Process to kill will be oldest process with lower priority than the one we are adding.
 */
public class EvictingByPriorityCapacityManager implements CapacityManager {
  private final int capacity;
  private final Map<Priority, Set<ProcessId>> reservedIds = new HashMap<>();
  private final Map<ProcessId, Priority> priorityMap = new HashMap<>();

  public EvictingByPriorityCapacityManager(int capacity) {
    this.capacity = capacity;
    for (Priority priority : Priority.values()) {
      reservedIds.put(priority, new LinkedHashSet<>());
    }
  }

  @Override
  public synchronized ProcessId reserveCapacityOrSelectForTermination(@NotNull ProcessId pid, @NotNull Priority priority) {
    if (priorityMap.containsKey(pid)) {
      throw new IllegalStateException("Process exists: " + pid);
    } else if (size() < capacity) {
      reservedIds.get(priority).add(pid);
      priorityMap.put(pid, priority);
      return null;
    }

    // overcapacity
    for (Priority p : Priority.values()) {
      if (p.ordinal() < priority.ordinal() && !reservedIds.get(p).isEmpty()) {
        var pidToTerminate = reservedIds.get(p).iterator().next();
        var priorityToTeminate = priorityMap.remove(pidToTerminate);
        reservedIds.get(priorityToTeminate).remove(pidToTerminate);
        return pidToTerminate;
      }
    }
    throw new IllegalStateException();
  }

  private int size() {
    return reservedIds.values().stream()
        .mapToInt(Set::size)
        .sum();
  }

  @Override
  public synchronized boolean freeCapacity(@NotNull ProcessId pid) {
    var priority = priorityMap.get(pid);
    if (priority == null) {
      return false;
    }
    return reservedIds.get(priority)
        .remove(pid);
  }
}
