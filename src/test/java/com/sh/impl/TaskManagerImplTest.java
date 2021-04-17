package com.sh.impl;

import com.sh.Process;
import com.sh.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class TaskManagerImplTest {
  @Test
  public void testList() {
    TaskManager mgr = defaultMgr(10);
    ProcessId p1 = mgr.add("ls", Priority.STD);
    assertThat(mgr.list(OrderByField.ID))
        .extracting(Process::pid).containsExactly(p1);

    ProcessId p2 = mgr.add("rm -rf", Priority.STD);
    assertThat(mgr.list(OrderByField.ID))
        .extracting(Process::pid).containsExactly(p1, p2);

    mgr.kill(p1);
    assertThat(mgr.list(OrderByField.ID))
        .extracting(Process::pid).containsExactly(p2);
  }

  @Test
  public void testKillAll() {
    TaskManager mgr = defaultMgr(10);

    ProcessId p1 = mgr.add("ls", Priority.STD);
    ProcessId p2 = mgr.add("rm -rf", Priority.LOW);

    assertThat(mgr.list(OrderByField.ID))
        .extracting(Process::pid).containsExactly(p1, p2);

    mgr.killAll();
    assertThat(mgr.list(OrderByField.ID)).isEmpty();
  }

  @Test
  public void testKill() {
    TaskManager mgr = defaultMgr(10);

    ProcessId p1 = mgr.add("ls", Priority.STD);
    ProcessId p2 = mgr.add("ls", Priority.STD);
    assertThat(mgr.list(OrderByField.ID))
        .extracting(Process::pid).containsExactly(p1, p2);

    mgr.kill(p1);
    assertThat(mgr.list(OrderByField.ID))
        .extracting(Process::pid).containsExactly(p2);

    try {
      mgr.kill(p1);
      fail("Should throw an exception");
    } catch (IllegalArgumentException e) {
      assertThat(e).isNotNull();
    }
  }


  @Test
  public void testKillByPriority() {
    TaskManager mgr = defaultMgr(10);

    ProcessId p1 = mgr.add("ls", Priority.STD);
    ProcessId p2 = mgr.add("rm -rf", Priority.LOW);
    ProcessId p3 = mgr.add("ps", Priority.HIGH);

    assertThat(mgr.list(OrderByField.ID))
        .extracting(Process::pid).containsExactly(p1, p2, p3);

    mgr.killGroup(Priority.LOW);
    assertThat(mgr.list(OrderByField.ID))
        .extracting(Process::pid).containsExactly(p1, p3);
  }

  @Test
  public void testListNoOrder() {
    TaskManager mgr = defaultMgr(10);

    ProcessId p1 = mgr.add("ls", Priority.HIGH);
    ProcessId p2 = mgr.add("rm -rf", Priority.LOW);
    assertThat(mgr.list(null))
        .extracting(Process::pid).contains(p2, p1);
  }

  @Test
  public void testListOrderByPriority() {
    TaskManager mgr = defaultMgr(10);

    ProcessId p1 = mgr.add("ls", Priority.HIGH);
    ProcessId p2 = mgr.add("rm -rf", Priority.LOW);
    assertThat(mgr.list(OrderByField.PRIORITY))
        .extracting(Process::pid).containsExactly(p2, p1);

    ProcessId p3 = mgr.add("rm -rf", Priority.STD);
    List<Process> list = mgr.list(OrderByField.PRIORITY);
    assertThat(list)
        .extracting(Process::pid).containsExactly(p2, p3, p1);
  }

  @Test
  public void testOverCapacity() {
    TaskManager mgr = defaultMgr(2);

    ProcessId p1 = mgr.add("ls", Priority.HIGH);
    ProcessId p2 = mgr.add("rm -rf", Priority.LOW);
    assertThat(mgr.list(OrderByField.PRIORITY))
        .extracting(Process::pid).containsExactly(p2, p1);

    try {
      mgr.add("shutdown", Priority.HIGH);
      Assertions.fail("Should throw an exception");
    } catch (IllegalStateException e) {
      assertThat(e).isNotNull();
    }
    mgr.kill(p2);
    ProcessId p3 = mgr.add("shutdown", Priority.HIGH);
    assertThat(mgr.list(OrderByField.PRIORITY))
        .extracting(Process::pid).containsExactly(p1, p3);
  }

  @Test
  public void testEvictingOverCapacity() {
    TaskManager mgr = createTerminating(2);

    ProcessId p1 = mgr.add("ls", Priority.HIGH);
    ProcessId p2 = mgr.add("rm -rf", Priority.LOW);
    assertThat(mgr.list(OrderByField.ID))
        .extracting(Process::pid).containsExactly(p1, p2);

    ProcessId p3 = mgr.add("shutdown", Priority.STD);
    assertThat(mgr.list(OrderByField.ID))
        .extracting(Process::pid).containsExactly(p2, p3);

    ProcessId p4 = mgr.add("shutdown -now", Priority.LOW);
    assertThat(mgr.list(OrderByField.ID))
        .extracting(Process::pid).containsExactly(p3, p4);
  }

  public static TaskManager defaultMgr(int capacity) {
    return new TaskManagerImpl(new DefaultCapacityManager(capacity));
  }

  public static TaskManager createTerminating(int capacity) {
    return new TaskManagerImpl(new EvictingCapacityManager(capacity));
  }
}
