package com.sh.impl;

import com.sh.Process;
import com.sh.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Implementation
 */
public class TaskManagerImpl implements TaskManager {
  private final AtomicLong lastPid = new AtomicLong(0L);
  private final Map<ProcessId, Process> processMap;
  private final CapacityManager capacityMgr;

  TaskManagerImpl(CapacityManager capacityMgr) {
    this(new ConcurrentHashMap<>(), capacityMgr);
  }

  TaskManagerImpl(Map<ProcessId, Process> processMap, CapacityManager capacityMgr) {
    this.processMap = processMap;
    this.capacityMgr = capacityMgr;
  }

  @Override
  public @NotNull ProcessId add(@NotNull String startingParameters, Priority priority) {
    var pid = nextPid();
    var pidToTerminate = capacityMgr.reserveCapacityOrSelectForTermination(pid, priority);
    if (pidToTerminate != null) {
      var process = processMap.remove(pidToTerminate);
      stopProcess(process);
    }
    processMap.put(pid, startProcess(pid, priority, startingParameters));
    return pid;
  }

  @Override
  public @NotNull List<Process> list(OrderByField orderBy) {
    if (orderBy == null) {
      return processMap.values().stream()
          .collect(Collectors.toUnmodifiableList());
    }
    return processMap.values().stream()
        .sorted(comparatorOf(orderBy))
        .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public void kill(@NotNull ProcessId pid) {
    if (internalKill(pid) == null) {
      throw new IllegalArgumentException("Process not found: " + pid);
    }
  }

  @Override
  public void killGroup(@NotNull Priority priority) {
    final Collection<Process> list = list(priority);
    for (Process process : list) {
      internalKill(process.pid());
    }
  }

  @Override
  public void killAll() {
    for (ProcessId pid : processMap.keySet()) {
      internalKill(pid);
    }
  }

  // internals

  @NotNull
  private Comparator<Process> comparatorOf(OrderByField orderBy) {
    return switch (orderBy) {
      case ID -> Comparator.comparing((Process p) -> p.pid().id());
      case TIME -> Comparator.comparing(Process::creationTimeStamp);
      case PRIORITY -> Comparator.comparing(Process::priority);
    };
  }

  @NotNull
  private ProcessId nextPid() {
    long nextPid = lastPid.incrementAndGet();
    return new PID(Long.toHexString(nextPid)); // just for fun: hex representation
  }

  @NotNull
  private Collection<Process> list(@NotNull Priority priority) {
    return processMap.values().stream()
        .filter(process -> priority.equals(process.priority()))
        .collect(toList());
  }

  private Process internalKill(@NotNull ProcessId pid) {
    var process = processMap.remove(pid);
    if (process != null) {
      capacityMgr.freeCapacity(process.pid());
      stopProcess(process);
    }
    return process;
  }

  private Process startProcess(ProcessId pid, Priority priority, String startingParameters) {
    // do whatever it is required to start a process
    return new RProcess(pid, priority, startingParameters, System.currentTimeMillis());
  }

  private void stopProcess(Process process) {
    // do whatever it is required to stop the process
  }

  // Data classes

  record PID(String id) implements ProcessId {

  }

  record RProcess(
      ProcessId pid,
      Priority priority,
      String params,
      Long creationTimeStamp
  ) implements Process {

  }
}
