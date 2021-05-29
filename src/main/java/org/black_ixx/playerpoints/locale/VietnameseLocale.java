package org.black_ixx.playerpoints.locale;

import dev.rosewood.rosegarden.locale.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

public class VietnameseLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "vi_VN";
    }

    @Override
    public String getTranslatorName() {
        return "Tptuaasn";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<String, Object>() {{
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix", "&7[<g:#E8A230:#ECD32D>PlayerPoints&7] ");

            this.put("#1", "Currency");
            this.put("currency-singular", "Point");
            this.put("currency-plural", "Points");
            this.put("currency-separator", ",");

            this.put("#2", "Misc");
            this.put("no-permission", "&cBạn không có quyền để sử dụng!");
            this.put("no-console", "&cChỉ người chơi mới có thể thực hiện lệnh này.");
            this.put("invalid-amount", "&cSố tiền phải là một số nguyên dương.");
            this.put("unknown-player", "&cKhông thể tìm thấy người chơi: &b%player%");
            this.put("unknown-command", "&cLệnh không xác định: &b%input%");
            this.put("votifier-voted", "&eCảm ơn vì đã bỏ phiếu trên %service%! &b%amount% &eđã được thêm vào số dư của bạn.");

            this.put("#3", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eSử dụng &b/points help &echo thông tin các lệnh.");

            this.put("#4", "Help Command");
            this.put("command-help-description", "&8 - &d/points help &7- Hiển thị menu trợ giúp ... Bạn đã đến");
            this.put("command-help-title", "&eCác lệnh có sẵn:");

            this.put("#5", "Give Command");
            this.put("command-give-description", "&8 - &d/points give &7- Trao cho người chơi points");
            this.put("command-give-usage", "&cSử dụng: &e/points give <người chơi> <số lượng>");
            this.put("command-give-success", "&b%player% &ađã nhận được &b%amount% &e%currency%.");
            this.put("command-give-received", "&eBạn đã nhận được &b%amount% &e%currency%.");

            this.put("#6", "Give All Command");
            this.put("command-giveall-description", "&8 - &d/points giveall &7- Trao cho tất cả người chơi trực tuyến points");
            this.put("command-giveall-usage", "&cSử dụng: &e/points giveall <số lượng>");
            this.put("command-giveall-success", "&aTrao &b%amount% &a%currency% cho tất cả người chơi trực tuyến.");

            this.put("#7", "Take Command");
            this.put("command-take-description", "&8 - &d/points take &7- Lấy points từ người chơi");
            this.put("command-take-usage", "&cSử dụng: &e/points take <người chơi> <số lượng>");
            this.put("command-take-success", "&aLấy đi &b%amount% &a%currency% từ &b%player%&a.");
            this.put("command-take-lacking-funds", "&b%player% &ckhông có đủ %currency% cho điều này, vì vậy số dư của họ được đặt thành 0.");

            this.put("#8", "Look Command");
            this.put("command-look-description", "&8 - &d/points look &7- Xem points của người chơi");
            this.put("command-look-usage", "&cSử dụng: &e/points look <người chơi>");
            this.put("command-look-success", "&b%player% &ecó &b%amount% &e%currency%.");

            this.put("#9", "Pay Command");
            this.put("command-pay-description", "&8 - &d/points pay &7- Chuyển points cho người chơi");
            this.put("command-pay-usage", "&cSử dụng: &e/points pay <người chơi> <số lượng>");
            this.put("command-pay-sent", "&aBạn đã chuyển cho &b%player% %amount% &a%currency%.");
            this.put("command-pay-received", "&eBạn đã được chuyển &b%amount% &e%currency% bởi &b%player%&e.");
            this.put("command-pay-lacking-funds", "&cBạn không có đủ %currency% cho điều này.");

            this.put("#10", "Set Command");
            this.put("command-set-description", "&8 - &d/points set &7- Đặt số points của người chơi");
            this.put("command-set-usage", "&cSử dụng: &e/points set <người chơi> <số lượng>");
            this.put("command-set-success", "&aĐặt số %currency% của &b%player% &athành &b%amount%&a.");

            this.put("#11", "Reset Command");
            this.put("command-reset-description", "&8 - &d/points reset &7- Đặt lại số points của người chơi");
            this.put("command-reset-usage", "&cSử dụng: &e/points reset <người chơi>");
            this.put("command-reset-success", "&aĐặt lại số %currency% cho &b%player% &a.");

            this.put("#12", "Me Command");
            this.put("command-me-description", "&8 - &d/points me &7- Xem số points của bạn");
            this.put("command-me-usage", "&cSử dụng: &d/points me");
            this.put("command-me-success", "&eBạn có &b%amount% &e%currency%.");

            this.put("#13", "Lead Command");
            this.put("command-lead-description", "&8 - &d/points lead &7- Xem bảng xếp hạng");
            this.put("command-lead-usage", "&cSử dụng: &e/points lead [next|prev|#]");
            this.put("command-lead-title", "&eBảng Xếp Hạng &7(Trang #%page%/%pages%)");
            this.put("command-lead-entry", "&b%position%). &e%player% &7- &6%amount% %currency%");

            this.put("#14", "Broadcast Command");
            this.put("command-broadcast-description", "&8 - &d/points broadcast &7- Broadcast points của người chơi");
            this.put("command-broadcast-usage", "&cSử dụng: &e/points broadcast <người chơi>");
            this.put("command-broadcast-message", "&b%player% &ecó &b%amount% &e%currency%.");

            this.put("#15", "Reload Command");
            this.put("command-reload-description", "&8 - &d/points reload &7- Tải lại plugin");
            this.put("command-reload-usage", "&cSử dụng: &e/points reload");
            this.put("command-reload-success", "&aCác tệp cấu hình và ngôn ngữ đã được tải lại.");

            this.put("#16", "Export Command");
            this.put("command-export-description", "&8 - &d/points export &7- Xuất dữ liệu sang storage.yml");
            this.put("command-export-usage", "&cSử dụng: &e/points export");
            this.put("command-export-success", "&aLưu dữ liệu đã được xuất sang storage.yml.");
            this.put("command-export-warning", "&cLưu ý: Tệp storage.yml đã tồn tại. Nếu bạn muốn ghi đè nó, sử dụng &b/points export confirm&c.");

            this.put("#17", "Import Command");
            this.put("command-import-description", "&8 - &d/points import &7- Nhập dữ liệu từ storage.yml");
            this.put("command-import-usage", "&cSử dụng: &e/points import");
            this.put("command-import-success", "&aLưu dữ liệu đã được nhập từ storage.yml.");
            this.put("command-import-no-backup", "&cKhông thể nhập, storage.yml không tồn tại. Bạn có thể tạo một cái bằng &b/points export &cvà sử dụng nó để chuyển dữ liệu giữa các loại cơ sở dữ liệu.");
            this.put("command-import-warning", "&cLưu ý: Thao tác này sẽ xóa tất cả dữ liệu khỏi cơ sở dữ liệu đang hoạt động và thay thế nó bằng nội dung của storage.yml. &cLoại cơ sở dữ liệu hiện đang hoạt động là &b&o&l%type%&c. &cNếu bạn hoàn toàn chắc chắn về điều này, hãy sử dụng &b/points import confirm&c.");

            this.put("#18", "Convert Command");
            this.put("command-convert-description", "&8 - &d/points convert &7- Load dữ liệu tiền tệ từ một plugin khác");
            this.put("command-convert-usage", "&cSử dụng: &e/points convert <plugin>");
            this.put("command-convert-invalid", "&b%plugin% &ckhông phải là tên plugin tiền tệ có thể chuyển đổi.");
            this.put("command-convert-success", "&aDữ liệu tiền tệ từ &b%plugin% &ađã được chuyển đổi.");
            this.put("command-convert-failure", "&cĐã xảy ra lỗi khi cố gắng chuyển đổi dữ liệu. Vui lòng kiểm tra bảng điều khiển của bạn và báo cáo bất kỳ lỗi nào cho tác giả plugin PlayerPoints.");
            this.put("command-convert-warning", "&cLưu ý: Thao tác này sẽ xóa tất cả dữ liệu khỏi cơ sở dữ liệu đang hoạt động và thay thế bằng dữ liệu từ &b%plugin%&c. &cNếu bạn hoàn toàn chắc chắn về điều này, hãy sử dụng &b/points convert %plugin% confirm&c.");

        }};
    }

}
