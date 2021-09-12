package com.sh.finder;


import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TransitionFinderTest {
  private static final List<String> WORDS = Arrays.asList("i", "n", "s", "in", "sin", "nis", "sing", "sting", "string");
  private static final List<String> WORDS1 = IntStream.range(0, 26).mapToObj(i -> new String(new char[]{(char) ('a' + i)})).collect(Collectors.toList());
  private static final List<String> WORDSL2 = mix(WORDS1, WORDS1);
  private static final List<String> WORDSL3 = mix(WORDSL2, WORDS1);
  private static final List<String> WORDSL4 = mix(WORDSL3, WORDS1);
  private static final List<String> WORDS5_ONLY = readLines("/words.txt");
  private static final List<String> WORDS2 = Stream.concat(WORDS1.stream(), WORDSL2.stream()).collect(Collectors.toList());
  private static final List<String> WORDS4 = Stream.concat(
      Stream.concat(WORDS1.stream(), WORDSL2.stream()),
      Stream.concat(WORDSL3.stream(), WORDSL4.stream())
  ).collect(Collectors.toList());
  private static final List<String> WORDS_BIG = Stream.concat(WORDS4.stream(), WORDS5_ONLY.stream()).collect(Collectors.toList());
  private final TransitionFinder finder;

  public TransitionFinderTest(TransitionFinder finder) {
    this.finder = finder;
  }

  private static List<String> mix(List<String> words1, List<String> words2) {
    return words1.stream()
        .flatMap(w -> words2.stream().map(c -> w + c))
        .collect(Collectors.toList());
  }

  private static List<String> readLines(String resourceName) {
    try {
      final URL resource = TransitionFinderTest.class.getResource(resourceName);
      return Files.readAllLines(Path.of(resource.toURI()));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> instancesToTest() {
    return Arrays.asList(
        new Object[]{new TransitionFinderV1()},
        new Object[]{new TransitionFinderV3()},
        new Object[]{new TransitionFinderV2()}
    );
  }

  @Test
  public void testEmpty() {
    assertThat(finder.longestTransition(Arrays.asList("two", "three"))).isEqualTo("");
  }

  @Test
  public void testSuffix() {
    assertThat(finder.longestTransition(Arrays.asList("a", "ab", "abc", "abcd", "abcdef"))).isEqualTo("abcd");
  }

  @Test
  public void testPrefix() {
    assertThat(finder.longestTransition(Arrays.asList("a", "ba", "cba", "dcba", "fedcba"))).isEqualTo("dcba");
  }

  @Test
  public void testInfix() {
    assertThat(finder.longestTransition(Arrays.asList("a", "ba", "bca", "bcda", "bcefda"))).isEqualTo("bcda");
  }

  @Test
  public void testSample() {
    assertThat(finder.longestTransition(WORDS)).isEqualTo("string");
  }

  @Test
  public void testBig() {
    assertThat(finder.longestTransition(WORDS2)).isEqualTo("aa");
  }

  @Test
  @Ignore
  public void testBigV3() {
    // V3 - 5s; V1/V2 - env. 50m
    assertThat(new TransitionFinderV3().longestTransition(WORDS_BIG)).isEqualTo("kaaba");
  }
}