package com.sh.impl;


import com.sh.Priority;
import com.sh.ProcessId;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EvictingCapacityManagerTest {
  @Test
  public void testOverCap() {
    CapacityManager mgr = new EvictingCapacityManager(2);
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(1), Priority.HIGH)).isNull();
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(2), Priority.LOW)).isNull();
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(3), Priority.STD)).isEqualTo(pid(1));
  }

  @Test
  public void testOverCap2() {
    CapacityManager mgr = new EvictingCapacityManager(2);
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(1), Priority.HIGH)).isNull();
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(2), Priority.LOW)).isNull();
    mgr.freeCapacity(pid(2));
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(3), Priority.STD)).isNull();
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(4), Priority.STD)).isEqualTo(pid(1));
    assertThat(mgr.reserveCapacityOrSelectForTermination(pid(5), Priority.STD)).isEqualTo(pid(3));
  }

  private ProcessId pid(int i) {
    return new TaskManagerImpl.PID(Integer.toString(i));
  }
}