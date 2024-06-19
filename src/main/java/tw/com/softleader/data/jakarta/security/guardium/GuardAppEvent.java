package tw.com.softleader.data.jakarta.security.guardium;

import java.util.StringJoiner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/** {@code GuardAppEvent} 封裝每次要寫入 Guardium API 資訊的物件 */
@Value
@Builder
@AllArgsConstructor
public class GuardAppEvent {

  /** 對應 Guardium 的 GuardAppEventUserName, ex: 登入系統之使用者 帳號(ID) */
  String userName;

  /** 對應 Guardium 的 GuardAppEventType, ex: 使用者所使用的 應用系統名稱_模組功能名稱 */
  String type;

  /** 對應 Guardium 的 GuardAppEventStrValue, ex: 使用者 IP 位址 */
  String strValue;

  @Override
  public String toString() {
    return new StringJoiner(",")
        .add("'GuardAppEventUserName:" + userName + "'")
        .add("'GuardAppEventType:" + type + "'")
        .add("'GuardAppEventStrValue:" + strValue + "'")
        .toString();
  }
}
