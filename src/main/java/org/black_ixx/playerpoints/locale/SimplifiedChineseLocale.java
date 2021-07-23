package org.black_ixx.playerpoints.locale;

import dev.rosewood.rosegarden.locale.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimplifiedChineseLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "zh_CN";
    }

    @Override
    public String getTranslatorName() {
        return "ahdg";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<String, Object>() {{
            this.put("#0", "插件信息前缀");
            this.put("prefix", "&7[<g:#E8A230:#ECD32D>PlayerPoints&7] ");

            this.put("#1", "货币名称");
            this.put("currency-singular", "个点劵");
            this.put("currency-plural", "多个点劵");
            this.put("currency-separator", ",");

            this.put("#2", "杂项");
            this.put("no-permission", "&c您没有权限那么做!");
            this.put("no-console", "&c只有玩家才能执行该命令。");
            this.put("invalid-amount", "&c数值必须为正数。");
            this.put("unknown-player", "&c找不到玩家: &b%player%");
            this.put("unknown-command", "&c未知命令: &b%input%");
            this.put("votifier-voted", "&e感谢您在 %service% 上为我们投票! &b%amount% &e已经被添加到您的账户。");

            this.put("#3", "基础命令信息");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&e使用 &b/points help &e来获取命令信息。");

            this.put("#4", "帮助命令");
            this.put("command-help-description", "&8 - &d/points help &7- 显示帮助菜单... 就是您现在看着的这个");
            this.put("command-help-title", "&e可用命令:");

            this.put("#5", "给予命令");
            this.put("command-give-description", "&8 - &d/points give &7- 给一名玩家点劵");
            this.put("command-give-usage", "&c用法: &e/points give <玩家> <数值>");
            this.put("command-give-success", "&b%player% &a被给予了 &b%amount% &e%currency%。");
            this.put("command-give-received", "&e您被给予了 &b%amount% &e%currency%。");

            this.put("#6", "给予全体命令");
            this.put("command-giveall-description", "&8 - &d/points giveall &7- 给予所有在线玩家点劵");
            this.put("command-giveall-usage", "&c用法: &e/points giveall <数值> [*]");
            this.put("command-giveall-success", "&a已成功给予 &b%amount% &a%currency% 给所有在线玩家。");

            this.put("#7", "拿去命令");
            this.put("command-take-description", "&8 - &d/points take &7- 从玩家那取走点劵");
            this.put("command-take-usage", "&c用法: &e/points take <玩家> <数值>");
            this.put("command-take-success", "&a成功从玩家 &b%player% 那拿走 &b%amount% &a%currency% 。");
            this.put("command-take-lacking-funds", "&b%player% &c没有足够的点劵 %currency% 来被拿走, 所以账户余额已设为 0。");

            this.put("#8", "查看命令");
            this.put("command-look-description", "&8 - &d/points look &7- 查看一名玩家的点卷数");
            this.put("command-look-usage", "&c用法: &e/points look <玩家>");
            this.put("command-look-success", "&b%player% &e有 &b%amount% &e%currency%。");

            this.put("#9", "转账命令");
            this.put("command-pay-description", "&8 - &d/points pay &7- 给一名玩家转账");
            this.put("command-pay-usage", "&c用法: &e/points pay <玩家> <数值>");
            this.put("command-pay-sent", "&a您向玩家 &b%player% 转账了 %amount% &a%currency%。");
            this.put("command-pay-received", "&e您收到了来自玩家 &b%player% &e的 &b%amount% &e%currency% 。");
            this.put("command-pay-lacking-funds", "&c您没有多于 %currency% 的点劵来那么做。");

            this.put("#10", "设置命令");
            this.put("command-set-description", "&8 - &d/points set &7- 设置一名玩家的点劵");
            this.put("command-set-usage", "&c用法: &e/points set <玩家> <数值>");
            this.put("command-set-success", "&a已设置玩家 &b%player% &a的 %currency% 至 &b%amount%&a。");

            this.put("#11", "重置命令");
            this.put("command-reset-description", "&8 - &d/points reset &7- 重置一名玩家的点劵");
            this.put("command-reset-usage", "&c用法: &e/points reset <玩家>");
            this.put("command-reset-success", "&a已重置 &b%player% &a的 %currency% 。");

            this.put("#12", "自查命令");
            this.put("command-me-description", "&8 - &d/points me &7- 查看您的点劵");
            this.put("command-me-usage", "&c用法: &d/points me");
            this.put("command-me-success", "&e您有 &b%amount% &e%currency%。");

            this.put("#13", "排行");
            this.put("command-lead-description", "&8 - &d/points lead &7- 查看排行榜");
            this.put("command-lead-usage", "&c用法: &e/points lead [next|prev|#]");
            this.put("command-lead-title", "&e排行榜 &7(页 #%page%/%pages%)");
            this.put("command-lead-entry", "&b%position%). &e%player% &7- &6%amount% %currency%");

            this.put("#14", "广播命令");
            this.put("command-broadcast-description", "&8 - &d/points broadcast &7- 广播一名玩家的点劵");
            this.put("command-broadcast-usage", "&c用法: &e/points broadcast <玩家>");
            this.put("command-broadcast-message", "&b%player% &e有 &b%amount% &e%currency%。");

            this.put("#15", "重载命令");
            this.put("command-reload-description", "&8 - &d/points reload &7- 重载插件");
            this.put("command-reload-usage", "&c用法: &e/points reload");
            this.put("command-reload-success", "&a配置和语言文件已被重新加载。");

            this.put("#16", "导出命令");
            this.put("command-export-description", "&8 - &d/points export &7- 导出数据至 storage.yml");
            this.put("command-export-usage", "&c用法: &e/points export");
            this.put("command-export-success", "&a已保存的数据已被导出 storage.yml.");
            this.put("command-export-warning", "&c注意: 一个 storage.yml 文件已经存在。如果您想覆盖文件，请使用 &b/points export confirm&c。");

            this.put("#17", "导入命令");
            this.put("command-import-description", "&8 - &d/points import &7- 从 storage.yml 导入数据");
            this.put("command-import-usage", "&c用法: &e/points import");
            this.put("command-import-success", "&a已保存的数据已从 storage.yml 导入。");
            this.put("command-import-no-backup", "&c无法导入, storage.yml 不存在。您可以使用 &b/points export &c生成一个并使用其来切换数据的存储形式。");
            this.put("command-import-warning", "&c注意: 这个操作会删除现有的所有数据并覆盖成 storage.yml 所包含的数据结构。" +
                    "&c目前活跃的数据类型为 &b&o&l%type%&c。" +
                    "&c如果您知道您要做什么, 请使用 &b/points import confirm&c 来确认操作。");

            this.put("#18", "迁移命令");
            this.put("command-convert-description", "&8 - &d/points convert &7- 从另一个插件迁移数据");
            this.put("command-convert-usage", "&c用法: &e/points convert <插件名称>");
            this.put("command-convert-invalid", "&b%plugin% &c不是一个可转换的插件名。");
            this.put("command-convert-success", "&a来自 &b%plugin% &a的货币数据已被迁移。");
            this.put("command-convert-failure", "&c尝试迁移数据时发生了错误。" +
                    "请检查您的控制台并将您所看到的报告给 PlayerPoint 作者。");
            this.put("command-convert-warning", "&c注意: 这个操作会删除所有现存的数据并使用来自插件 &b%plugin%&c 的数据结构。 " +
                    "&c如果您知道您在做什么，请使用 &b/points convert %plugin% confirm&c 来确认操作。");

            this.put("#19", "Import Legacy Command");
            this.put("command-importlegacy-description", "&8 - &d/points importlegacy &7- 导入一个遗留的表格");
            this.put("command-importlegacy-usage", "&c用法: &e/points importlegacy <table>");
            this.put("command-importlegacy-success", "&a成功地从&b%table%&a导入遗留数据。");
            this.put("command-importlegacy-failure", "&c从&b%table%&c导入遗留数据失败。该表是否存在？");
            this.put("command-importlegacy-only-mysql", "&c这个命令只有在你启用了MySQL后才可用。");

            this.put("#20", "Version Command");
            this.put("command-version-description", "&8 - &d/points version &7- 显示版本信息 PlayerPoints");
        }};
    }

}
