package com.sh.impl;

import com.sh.Priority;
import com.sh.ProcessId;
import org.jetbrains.annotations.NotNull;

/**
 * Handles capacity reservation in a thread-safe way
 */
public interface CapacityManager {
  /**
   * Reserve required capacity for a new process
   *
   * @param pid      - process id
   * @param priority - process priority
   * @return pid of a process to terminate if overcapacity, null otherwise
   * @throws IllegalStateException if no capacity can be reserved
   */
  ProcessId reserveCapacityOrSelectForTermination(@NotNull ProcessId pid, @NotNull Priority priority);

  /**
   * Free capacity occupied by the process
   *
   * @return true if PID is known
   */
  boolean freeCapacity(@NotNull ProcessId pid);
}
