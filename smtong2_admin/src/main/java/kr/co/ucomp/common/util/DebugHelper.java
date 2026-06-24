package kr.co.ucomp.common.util;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * Static import를 위한 헬퍼 클래스
 * import static com.yourpackage.util.DebugHelper.*;
 */
public class DebugHelper {

  public static void dump(Object obj) {
    DebugUtil.dump(obj);
  }

  public static void dump(Object obj, String title) {
    DebugUtil.dump(obj, title);
  }

  public static void dumpLog(Object obj) {
    DebugUtil.dumpLog(obj);
  }

  public static void dumpLog(Object obj, String title) {
    DebugUtil.dumpLog(obj, title);
  }

  public static void dumpOneLine(Object obj) {
    DebugUtil.dumpOneLine(obj);
  }
}