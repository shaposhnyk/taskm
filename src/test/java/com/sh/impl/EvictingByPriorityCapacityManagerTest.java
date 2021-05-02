package com.sh.impl;

import com.sh.Priority;
import com.sh.ProcessId;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class EvictingByPriorityCapacityManagerTest {
  @Test
  public void testOverCap() {
    CapacityManager mgr = new EvictingByPriorityCapacityManager(2);
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(1), Priority.HIGH)).isNull();
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(2), Priority.LOW)).isNull();
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(3), Priority.STD)).isEqualTo(pid(2));
  }

  @Test
  public void testOverCap2() {
    CapacityManager mgr = new EvictingByPriorityCapacityManager(2);
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(1), Priority.HIGH)).isNull();
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(2), Priority.LOW)).isNull();
    mgr.freeCapacity(pid(2));
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(3), Priority.STD)).isNull();
    try {
      assertThat(mgr.reserveCapacityOrSelectForTermination(pid(4), Priority.LOW)).isEqualTo(pid(1));
      fail("Should throw an exception");
    } catch (IllegalStateException e) {
      assertThat(e).isNotNull();
    }
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(5), Priority.HIGH)).isEqualTo(pid(3));
  }

  @Test
  public void testEvictingOverCapacity_nothing_to_evict() {
    EvictingByPriorityCapacityManager mgr = new EvictingByPriorityCapacityManager(2);

    mgr.reserveCapacityOrSelectForTermination(pid(1), Priority.LOW);
    mgr.reserveCapacityOrSelectForTermination(pid(2), Priority.LOW);

    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(3), Priority.STD)).isEqualTo(pid(1));
    mgr.freeCapacity(pid(1));
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(4), Priority.LOW)).isEqualTo(pid(2));
  }

  private ProcessId pid(int i) {
    return new TaskManagerImpl.PID(Integer.toString(i));
  }
}