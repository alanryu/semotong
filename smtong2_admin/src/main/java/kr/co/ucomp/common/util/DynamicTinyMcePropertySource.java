package kr.co.ucomp.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class DynamicTinyMcePropertySource extends PropertySource<String> implements ApplicationRunner {
  @Autowired
  private ConfigurableEnvironment environment;

  private final List<String> availableKeys = Arrays.asList(
      "yo1dk44p8erx3a2rf6bprnb4zlnnq6gk2olhbakkno8t2h7t", // github@ucomp.co.kr
      "pf0yclpn7jrxudxxx0ybmvy9vnbib08acp6alc0f9bd6v1kw", // 기존2
      "msoz9q348s6y1ti7az3ytf0x2ka1hhdkai8zsr8osnz6506n", // 기존1
      "j4d697kj1fbwoojkxx8ofo5k459y680knp9ds4abehjnvrhg", // lkh811204@gmail.com
      "hs4eicxfwfmtmdmgaw27483ir4dfhl2qhzvam162wgagg7by", // github@naver.com
      "kwp5a1sz2dzruj23zt25uena48j21yiksw11eqe7sw77xfmc" // sdpp811204@gmail.com

  );

  public DynamicTinyMcePropertySource() {
    super("dynamic-tinymce-realtime");
  }

  @Override
  public Object getProperty(String name) {
    if ("tinymce.apikey".equals(name)) {
      // 매번 호출될 때마다 실시간으로 주차 계산
      int currentWeek = LocalDate.now().get(WeekFields.ISO.weekOfYear());
      int keyIndex = currentWeek % availableKeys.size();
      String selectedKey = availableKeys.get(keyIndex);

      log.info("TinyMCE API key requested for week {}: {}...",
          currentWeek, selectedKey.substring(0, 8));

      return selectedKey;
    }
    return null;
  }

  @Override
  public void run(ApplicationArguments args) {
    // 서버 시작 시 이 PropertySource를 가장 높은 우선순위로 등록
    MutablePropertySources propertySources = environment.getPropertySources();
    propertySources.addFirst(this);

    log.info("Dynamic TinyMCE PropertySource registered - API key will rotate weekly based on current week");

    // 현재 설정된 키 정보 출력
    int currentWeek = LocalDate.now().get(WeekFields.ISO.weekOfYear());
    int keyIndex = currentWeek % availableKeys.size();
    log.info("Current week: {}, Using API key: {}...", currentWeek, availableKeys.get(keyIndex).substring(0, 8));
  }
}
