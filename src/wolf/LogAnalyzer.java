package wolf;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import wolf.model.Role;

import static java.lang.Integer.parseInt;

public class LogAnalyzer {

  private static final CharMatcher matcher =
      CharMatcher.inRange('a', 'z').or(CharMatcher.is('\''))
          .negate().precomputed();

  private static final Set<String> fillerWords = Sets.newHashSet("the", "to", "a", "is", "for",
      "and", "of", "it", "he", "i", "you", "u", "be", "but", "on", "not", "so", "was", "have",
      "i'm", "im", "me", "if", "do", "as", "just", "who", "what", "would", "ok");

  private static final String PLAYER_FILTER = null;
  private static final int PRINT_COUNT = 50;

  private Iterator<String> iter;
  private String[] buf = new String[2];
  private Map<Role, Map<String, Integer>> counts = Maps.newHashMap();

  private LogAnalyzer() {
    for (Role role : Role.values()) {
      counts.put(role, Maps.<String, Integer>newHashMap());
    }
  }

  private void run() throws IOException {
    File dir = new File("C:/shit/logs");
    for (File f : dir.listFiles()) {
      List<String> lines = Files.readLines(f, Charsets.UTF_8);
      iter = lines.iterator();
      try {
        parse();
      } catch (Exception e) {
        System.out.println("Problem parsing: " + f);
        System.out.println(Files.toString(f, Charsets.UTF_8));
        throw Throwables.propagate(e);
      }
    }

    findWeirdThings();
  }

  private void findWeirdThings() {
    Map<Role, Integer> totalCounts = Maps.newHashMap();
    for (Role role : counts.keySet()) {
      int c = 0;
      for (Integer i : counts.get(role).values()) {
        c += i;
      }
      totalCounts.put(role, c);
    }

    double vTotal = totalCounts.get(Role.VILLAGER);
    double wTotal = totalCounts.get(Role.WOLF);

    System.out.println(String.format("%12s %10s %10s", "Word", "Villager", "Wolf"));
    System.out.println("---------------------------------------------------------");
    for (String word : getWordsSorted(Role.VILLAGER).subList(0, PRINT_COUNT)) {
      double vp = getCount(Role.VILLAGER, word) / vTotal * 100;
      double wp = getCount(Role.WOLF, word) / wTotal * 100;
      if (Math.abs(vp - wp) / vp > .2) {
        System.out.println(String.format("%12s %10.2f %10.2f", word, vp, wp));
      }
    }
  }

  private int getCount(Role role, String word) {
    Map<String, Integer> m = counts.get(role);
    Integer ret = m.get(word);
    if (ret == null) {
      ret = 0;
    }
    return ret;
  }

  private List<String> getWordsSorted(Role role) {
    Map<String, Integer> m = counts.get(role);
    List<Entry<String, Integer>> list = Lists.newArrayList(m.entrySet());
    Collections.sort(list, new Comparator<Entry<String, Integer>>() {
      @Override
      public int compare(Entry<String, Integer> a, Entry<String, Integer> b) {
        return b.getValue() - a.getValue();
      }
    });

    List<String> ret = Lists.newArrayList();
    for (Entry<String, Integer> e : list) {
      ret.add(e.getKey());
    }
    return ret;
  }

  private void print() {
    for (Role role : Role.values()) {
      System.out.println("\n\nStats for: " + role);

      int c = 0;
      for (String s : getWordsSorted(role)) {
        if (fillerWords.contains(s)) {
          continue;
        }

        System.out.println(s);
        if (++c == PRINT_COUNT) {
          break;
        }
      }
    }
  }

  private void parse() {
    iter.next(); // skip the "log started on" line

    boolean rated = Boolean.valueOf(next()[1]);

    Map<String, Role> players = Maps.newHashMap();

    int numPlayers = parseInt(next()[1]);

    if (!rated || numPlayers < 7) {
      return;
    }

    for (int i = 0; i < numPlayers; i++) {
      String[] m = next();
      players.put(m[0], Role.parse(m[1]));
    }

    while (iter.hasNext()) {
      String[] m = next();
      String name = m[0];
      String text = m[1];

      if (name.equals("NARRATOR")) {
        continue;
      }

      if (PLAYER_FILTER != null && !name.equals(PLAYER_FILTER)) {
        continue;
      }

      Role role = players.get(name);

      if (role == null) {
        continue;
      }

      if (text.startsWith("/")) {
        continue;
      }

      process(role, text);
    }
  }

  private void process(Role role, String text) {
    Map<String, Integer> m = counts.get(role);
    for (String s : Splitter.on(matcher).omitEmptyStrings().split(text)) {
      inc(m, s);
    }
  }

  private void inc(Map<String, Integer> m, String s) {
    Integer i = m.get(s);
    if (i == null) {
      i = 0;
    }
    m.put(s, i + 1);
  }

  private String[] next() {
    String s = iter.next().toLowerCase();
    int i = s.indexOf(": ");
    buf[0] = s.substring(0, i);
    buf[1] = s.substring(i + 2);
    return buf;
  }

  public static void main(String[] args) throws Exception {
    new LogAnalyzer().run();
  }

}
