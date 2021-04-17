package com.sh;

import org.jetbrains.annotations.NotNull;

/**
 * Process Identifier
 */
public interface ProcessId {
  /**
   * @return string representation of ID
   */
  @NotNull String id();
}
