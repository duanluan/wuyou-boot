package top.zhjh.config;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import top.zhjh.service.SystemDictSyncService;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartInit implements ApplicationRunner {

  private final SystemDictSyncService systemDictSyncService;

  @Override
  public void run(ApplicationArguments args) {
    systemDictSyncService.syncOnStartup();
  }
}
