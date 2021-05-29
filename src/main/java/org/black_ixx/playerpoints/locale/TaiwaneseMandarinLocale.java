package org.black_ixx.playerpoints.locale;

import dev.rosewood.rosegarden.locale.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

public class TaiwaneseMandarinLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "zh_TW";
    }

    @Override
    public String getTranslatorName() {
        return "Command1";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<String, Object>() {{
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix", "&7[<g:#E8A230:#ECD32D>PlayerPoints&7] ");

            this.put("#1", "Currency");
            this.put("currency-singular", "點數");
            this.put("currency-plural", "點數");
            this.put("currency-separator", ",");

            this.put("#2", "Misc");
            this.put("no-permission", "&c你沒有權限!");
            this.put("no-console", "&c只有玩家可以使用此指令.");
            this.put("invalid-amount", "&c數量必須是一個正整數.");
            this.put("unknown-player", "&c找不到此玩家: &b%player%");
            this.put("unknown-command", "&c未知指令: &b%input%");
            this.put("votifier-voted", "&e謝謝你對於 %service% &e的投票! &b%amount% &e點%currency%已進入你的點數庫.");

            this.put("#3", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&e使用 &b/points help &e顯示幫助畫面.");

            this.put("#4", "Help Command");
            this.put("command-help-description", "&8 - &d/points help &7- 顯示幫助畫面... 你已經做到了");
            this.put("command-help-title", "&e可使用指令:");

            this.put("#5", "Give Command");
            this.put("command-give-description", "&8 - &d/points give <玩家> <數量> &7- 給予玩家點數");
            this.put("command-give-usage", "&c用法: &e/points give <玩家> <數量>");
            this.put("command-give-success", "&b%player% &a給你 &b%amount% &a點&a%currency%.");
            this.put("command-give-received", "&e你獲得了 &b%amount% &e點&e%currency%.");

            this.put("#6", "Give All Command");
            this.put("command-giveall-description", "&8 - &d/points giveall <數量> &7- 給予所有玩家點數");
            this.put("command-giveall-usage", "&c用法: &e/points giveall <數量>");
            this.put("command-giveall-success", "&a給予所有玩家 &b%amount% &a點&a%currency%.");

            this.put("#7", "Take Command");
            this.put("command-take-description", "&8 - &d/points take  <玩家> <數量> &7- 拿走玩家點數");
            this.put("command-take-usage", "&c用法: &e/points take <玩家> <數量>");
            this.put("command-take-success", "&a從 &b%player% &a拿走 &b%amount% &a點&a%currency%.");
            this.put("command-take-lacking-funds", "&b%player% &c沒有足夠的%currency%, 所以他的%currency%將會被設為 0.");

            this.put("#8", "Look Command");
            this.put("command-look-description", "&8 - &d/points look <玩家> &7- 查看玩家的點數");
            this.put("command-look-usage", "&c用法: &e/points look <玩家>");
            this.put("command-look-success", "&b%player% &e擁有 &b%amount% &e點&e%currency%.");

            this.put("#9", "Pay Command");
            this.put("command-pay-description", "&8 - &d/points pay &7- 支付玩家點數");
            this.put("command-pay-usage", "&c用法: &e/points pay <玩家> <數量>");
            this.put("command-pay-sent", "&a你支付了 &b%player% %amount% &a點&a%currency%.");
            this.put("command-pay-received", "&b%player% &e支付了 &b%amount% &e點&e%currency%給你&e.");
            this.put("command-pay-lacking-funds", "&c你沒有足夠的%currency%.");

            this.put("#10", "Set Command");
            this.put("command-set-description", "&8 - &d/points set <玩家> <數量> &7- 設定玩家點數");
            this.put("command-set-usage", "&c用法: &e/points set <玩家> <數量>");
            this.put("command-set-success", "&a設定 &b%player% &a的&a%currency%&a為 &b%amount%&a.");

            this.put("#11", "Reset Command");
            this.put("command-reset-description", "&8 - &d/points reset <玩家> &7- 重設玩家點數");
            this.put("command-reset-usage", "&c用法: &e/points reset <玩家>");
            this.put("command-reset-success", "&a重設了 &b%player% &a的&a%currency%.");

            this.put("#12", "Me Command");
            this.put("command-me-description", "&8 - &d/points me &7- 查看自己的點數");
            this.put("command-me-usage", "&c用法: &d/points me");
            this.put("command-me-success", "&e你擁有 &b%amount% &e點&e%currency%.");

            this.put("#13", "Lead Command");
            this.put("command-lead-description", "&8 - &d/points lead [next|prev|#] &7- 查看排行榜");
            this.put("command-lead-usage", "&c用法: &e/points lead [next|prev|#]");
            this.put("command-lead-title", "&e排行榜 &7(第 #%page%/%pages% 頁)");
            this.put("command-lead-entry", "&b(%position%). &e%player% &7- &6%amount% 點%currency%");

            this.put("#14", "Broadcast Command");
            this.put("command-broadcast-description", "&8 - &d/points broadcast &7- 公告玩家的點數");
            this.put("command-broadcast-usage", "&c用法: &e/points broadcast <玩家>");
            this.put("command-broadcast-message", "&e玩家 &b%player% &e擁有 &b%amount% &e點&e%currency%.");

            this.put("#15", "Reload Command");
            this.put("command-reload-description", "&8 - &d/points reload &7- 重載此插件");
            this.put("command-reload-usage", "&c用法: &e/points reload");
            this.put("command-reload-success", "&a設定檔和語言檔已重載.");

            this.put("#16", "Export Command");
            this.put("command-export-description", "&8 - &d/points export &7- 輸出資料至 storage.yml");
            this.put("command-export-usage", "&c用法: &e/points export");
            this.put("command-export-success", "&a資料已成功輸出至 storage.yml.");
            this.put("command-export-warning", "&c警告: storage.yml 已經存在. 如果你想要覆寫他, 輸入 &b/points export confirm&c.");

            this.put("#17", "Import Command");
            this.put("command-import-description", "&8 - &d/points import &7- 從 storage.yml 輸入資料");
            this.put("command-import-usage", "&c用法: &e/points import");
            this.put("command-import-success", "&a資料已成功從 storage.yml 輸入.");
            this.put("command-import-no-backup", "&c無法輸入資料, storage.yml 不存在. 你可以使用 &b/points export &c生成一個, 並使用他修改內部資料.");
            this.put("command-import-warning", "&c警告: 此動作將會刪除數據庫內所有資料, 並將數據庫替換為 storage.yml 的資料. &c當前使用的資料庫類型為 &b&o&l%type%&c. &c如果你確定要繼續, 請使用 &b/points import confirm&c.");

            this.put("#18", "Convert Command");
            this.put("command-convert-description", "&8 - &d/points convert &7- 從另一個插件加載數據庫");
            this.put("command-convert-usage", "&c用法: &e/points convert <插件>");
            this.put("command-convert-invalid", "&b%plugin% &c不是可以轉換數據庫的插件.");
            this.put("command-convert-success", "&a來自 &b%plugin% &a的數據庫已成功轉移.");
            this.put("command-convert-failure", "&c嘗試轉換數據庫時發生錯誤. 請檢查你的控制台並且回報錯誤給 PlayerPoints 的作者.");
            this.put("command-convert-warning", "&c警告: 此動作將會刪除數據庫內所有資料, 並將數據庫替換為 &b%plugin%&c 的資料. &c如果你確定要繼續, 請使用 &b/points convert %plugin% confirm&c.");
        }};
    }

}
