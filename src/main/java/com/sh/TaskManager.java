package com.sh;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// if code base is not limited to Koltin, it's better to have java interfaces

/**
 * TaskManager capable to do 3 operations: add, list, and kill
 */
public interface TaskManager {
  /**
   * Creates new process in the system
   *
   * @param startingParameter - whatever it is
   * @param priority          - process priority
   * @return pid of created process
   * @throws IllegalStateException if process cannot be created
   */
  @NotNull
  ProcessId add(@NotNull String startingParameter, Priority priority);

  /**
   * List all running processes, sorting them by given field
   *
   * @param orderBy - field to sort by, if null, items returned in no particular order
   * @return immutable list of processIds
   */
  @NotNull
  List<Process> list(OrderByField orderBy);

  /**
   * Kills a specific process
   *
   * @param pid - process id
   * @throws IllegalArgumentException if PID is unknown
   */
  void kill(@NotNull ProcessId pid);

  /**
   * Kills all processes with a specific priority
   */
  void killGroup(@NotNull Priority priority);

  /**
   * Kills all running processes
   */
  void killAll();
}
