package wolf.rankings;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import wolf.OS;

public class Config {

  private final Map<String, String> map = Maps.newLinkedHashMap();

  private Config(File f) {
    if (!f.exists()) {
      return;
    }

    List<String> lines;
    try {
      lines = Files.readLines(f, Charsets.UTF_8);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }

    for (String line : lines) {
      int i = line.indexOf("=");
      if (i == -1) {
        continue;
      }
      String key = line.substring(0, i).trim().toLowerCase();
      String value = line.substring(i + 1).trim();
      map.put(key, value);
    }
  }

  public String get(String key) {
    return map.get(key);
  }

  public static Config load(String app) {
    File f = new File(OS.getLocalAppFolder(app));
    f = new File(f, "config.txt");
    return new Config(f);
  }

}
