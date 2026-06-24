package kr.co.ucomp.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DebugUtil {
  private static final Logger log = LoggerFactory.getLogger(DebugUtil.class);
  private static final ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  /**
   * 객체를 JSON 형태로 콘솔에 출력
   */
  public static void dump(Object obj) {
    dump(obj, null);
  }

  /**
   * 객체를 JSON 형태로 콘솔에 출력 (제목 포함)
   */
  public static void dump(Object obj, String title) {
    try {
      String json = mapper.writeValueAsString(obj);
      String displayTitle = title != null ? title : obj.getClass().getSimpleName();

      System.out.println("┌─────────────────────────────────");
      System.out.println("│ DEBUG DUMP: " + displayTitle);
      System.out.println("├─────────────────────────────────");
      System.out.println(json);
      System.out.println("└─────────────────────────────────");
    } catch (JsonProcessingException e) {
      System.err.println("❌ Error dumping object: " + e.getMessage());
      System.out.println("Object toString: " + obj.toString());
    }
  }

  /**
   * 로그로 출력
   */
  public static void dumpLog(Object obj) {
    dumpLog(obj, null);
  }

  /**
   * 로그로 출력 (제목 포함)
   */
  public static void dumpLog(Object obj, String title) {
    try {
      String json = mapper.writeValueAsString(obj);
      String displayTitle = title != null ? title : obj.getClass().getSimpleName();
      log.info("DEBUG DUMP [{}]: {}", displayTitle, json);
    } catch (JsonProcessingException e) {
      log.error("Error dumping object: {}", e.getMessage());
      log.info("Object toString: {}", obj.toString());
    }
  }

  /**
   * 한 줄로 출력
   */
  public static void dumpOneLine(Object obj) {
    try {
      ObjectMapper oneLineMapper = new ObjectMapper();
      oneLineMapper.registerModule(new JavaTimeModule());
      oneLineMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      String json = oneLineMapper.writeValueAsString(obj);
      System.out.println("🔍 " + obj.getClass().getSimpleName() + ": " + json);
    } catch (JsonProcessingException e) {
      System.err.println("❌ Error dumping object: " + e.getMessage());
    }
  }

  /**
   * 객체를 JSON 문자열로 반환
   */
  public static String toJson(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      return "Error converting to JSON: " + e.getMessage();
    }
  }

  /**
   * 성능 측정과 함께 출력
   */
  public static void dumpWithPerformance(Object obj, String title) {
    long startTime = System.currentTimeMillis();
    dump(obj, title);
    long endTime = System.currentTimeMillis();
    System.out.println("⏱️ Dump time: " + (endTime - startTime) + "ms");
  }
}