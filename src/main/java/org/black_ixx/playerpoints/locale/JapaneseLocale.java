package org.black_ixx.playerpoints.locale;

import dev.rosewood.rosegarden.locale.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

public class JapaneseLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "ja_JP";
    }

    @Override
    public String getTranslatorName() {
        return "Mori01231";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<String, Object>() {{
            this.put("#0", "プラグイン Prefix");
            this.put("prefix", "&7[<g:#E8A230:#ECD32D>PlayerPoints&7] ");

            this.put("#1", "通貨");
            this.put("currency-singular", "ポイント");
            this.put("currency-plural", "ポイント");
            this.put("currency-separator", ",");

            this.put("#2", "その他");
            this.put("no-permission", "&c権限不足です！");
            this.put("no-console", "&cこのコマンドはコンソールでは実行できません。プレイヤーが実行してください。");
            this.put("invalid-amount", "&c値は正の整数で指定してください。");
            this.put("unknown-player", "&c&b%player% という名前のプレイヤーは見つかりませんでした。");
            this.put("unknown-command", "&c無効なコマンド: &b%input%");
            this.put("votifier-voted", "&e%service% での投票ありがとうございます。 &b%amount% &eポイントがアカウントに追加されました。");

            this.put("#3", "基礎コマンドメッセージ");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eプラグインの使い方に関しては &b/points help &e で解説されています。");

            this.put("#4", "ヘルプコマンド");
            this.put("command-help-description", "&8 - &d/points help &7- ヘルプメニューを表示、、、ヘルプメニューへようこそ！");
            this.put("command-help-title", "&e使用可能なコマンド:");

            this.put("#5", "Give コマンド");
            this.put("command-give-description", "&8 - &d/points give &7- プレイヤーにポイントを付与する。");
            this.put("command-give-usage", "&c構文: &e/points give <player> <amount>");
            this.put("command-give-success", "&b%player% &aに &b%amount% &e%currency% &a付与しました。");
            this.put("command-give-received", "&eあなたは &b%amount% &e%currency% &e受け取りました。");

            this.put("#6", "Give All コマンド");
            this.put("command-giveall-description", "&8 - &d/points giveall &7- 鯖内の全プレイヤーにポイントを付与する。");
            this.put("command-giveall-usage", "&c構文: &e/points giveall <amount>");
            this.put("command-giveall-success", "&a現在ログインしている全プレイヤーに &b%amount% &a%currency% 付与しました。");

            this.put("#7", "Take コマンド");
            this.put("command-take-description", "&8 - &d/points take &7- プレイヤーからポイントを剥奪する。");
            this.put("command-take-usage", "&c構文: &e/points take <player> <amount>");
            this.put("command-take-success", "&b%player%&a から &b%amount% &a%currency% 剥奪しました。");
            this.put("command-take-lacking-funds", "&b%player% &cの所持%currency%が足りないため、所持%currency%が0に設定されました。");

            this.put("#8", "Look Command");
            this.put("command-look-description", "&8 - &d/points look &7- プレイヤーの所持ポイントを確認する。");
            this.put("command-look-usage", "&c構文: &e/points look <player>");
            this.put("command-look-success", "&b%player% &eは &b%amount% &e%currency% 所持しています。");

            this.put("#9", "Pay Command");
            this.put("command-pay-description", "&8 - &d/points pay &7- プレイヤーにポイントを支払う。");
            this.put("command-pay-usage", "&c構文: &e/points pay <player> <amount>");
            this.put("command-pay-sent", "&aあなたは &b%player% に %amount% &a%currency% 支払いました。");
            this.put("command-pay-received", "&eあなたは &b%player% から &b%amount% &e%currency% 受け取りました。");
            this.put("command-pay-lacking-funds", "&c%currency% 不足です。");

            this.put("#10", "Set Command");
            this.put("command-set-description", "&8 - &d/points set &7- プレイヤーのポイントを新たな値に設定する。");
            this.put("command-set-usage", "&c構文: &e/points set <player> <amount>");
            this.put("command-set-success", "&b%player% &aの &e%currency% &aを &b%amount% &aに設定しました。");

            this.put("#11", "Reset Command");
            this.put("command-reset-description", "&8 - &d/points reset &7- プレイヤーのポイントをリセットする。");
            this.put("command-reset-usage", "&c構文: &e/points reset <player>");
            this.put("command-reset-success", "&b%player% &aの %currency% &aをリセットしました。");

            this.put("#12", "Me Command");
            this.put("command-me-description", "&8 - &d/points me &7- 自分の所持ポイントを確認する。");
            this.put("command-me-usage", "&c構文: &d/points me");
            this.put("command-me-success", "&eあなたは &b%amount% &e%currency% 所持しています。");

            this.put("#13", "Lead Command");
            this.put("command-lead-description", "&8 - &d/points lead &7- ポイントランキングを確認する。");
            this.put("command-lead-usage", "&c構文: &e/points lead [next|prev|#]");
            this.put("command-lead-title", "&eランキング &7(ページ #%page%/%pages%)");
            this.put("command-lead-entry", "&b%position%). &e%player% &7- &6%amount% %currency%");

            this.put("#14", "Broadcast Command");
            this.put("command-broadcast-description", "&8 - &d/points broadcast &7- プレイヤーの所持ポイントを公表する。");
            this.put("command-broadcast-usage", "&c構文: &e/points broadcast <player>");
            this.put("command-broadcast-message", "&b%player% &eは &b%amount% &e%currency% 所持しています。");

            this.put("#15", "Reload Command");
            this.put("command-reload-description", "&8 - &d/points reload &7- プラグインをリロードする。");
            this.put("command-reload-usage", "&c構文: &e/points reload");
            this.put("command-reload-success", "&a設定ファイルと言語ファイルをリロードしました。");

            this.put("#16", "Export Command");
            this.put("command-export-description", "&8 - &d/points export &7- storage.ymlにデータを書き出す。");
            this.put("command-export-usage", "&c構文: &e/points export");
            this.put("command-export-success", "&aデータがstorage.ymlに書き出されました。");
            this.put("command-export-warning", "&c注意: すでにstorage.ymlファイルが存在しています。上書きする場合は &b/points export confirm &cコマンドを実行してください。");

            this.put("#17", "Import Command");
            this.put("command-import-description", "&8 - &d/points import &7- storage.ymlからデータを読み込む。");
            this.put("command-import-usage", "&c構文: &e/points import");
            this.put("command-import-success", "&aデータがstorage.ymlから読み込まれました。");
            this.put("command-import-no-backup", "&cstorage.ymlが存在しないため、読み込みが失敗しました。&b/points export &cで新たにstorage.ymlを生成し、データ形式の変換に使用できます。");
            this.put("command-import-warning", "&c注意：この処理は現在使用中のデータベースのデータを削除し、storage.ymlの中身で置き換えます。&c現在使用中のデータベース形式は &b&o&l%type%&cです。 &cデータのバックアップを取ったうえで本当にこの処理を実行したい場合のみ &b/points import confirm &cコマンドを使用してください。");

            this.put("#18", "Convert Command");
            this.put("command-convert-description", "&8 - &d/points convert &7- ほかのプラグインから通貨のデータを読み込む。");
            this.put("command-convert-usage", "&c構文: &e/points convert <plugin>");
            this.put("command-convert-invalid", "&b%plugin% &cからの通貨の読み込みははサポートされていません。");
            this.put("command-convert-success", "&b%plugin% &aから通貨データが読み込まれました。");
            this.put("command-convert-failure", "&cデータの読み込み中にエラーが発生しました。コンソールでエラーを確認し、PlayerPointsの作者に報告してください。");
            this.put("command-convert-warning", "&c注意：この処理は現在使用中のデータベースのデータを削除し、&b%plugin%&cの中身で置き換えます。 &cデータのバックアップを取ったうえで本当にこの処理を実行したい場合のみ &b/points convert %plugin% confirm&cコマンドを使用してください。");
        }};
    }

}
