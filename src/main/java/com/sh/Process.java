package com.sh;

/**
 * Process
 */
public interface Process {
  ProcessId pid();

  Priority priority();

  Long creationTimeStamp();
}
