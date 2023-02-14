package org.black_ixx.playerpoints.locale;

import dev.rosewood.rosegarden.locale.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

public class GreekLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "gr_GR";
    }

    @Override
    public String getTranslatorName() {
        return "ba10";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<String, Object>() {{
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix", "&7[<g:#E8A230:#ECD32D>PlayerPoints&7] ");

            this.put("#1", "Currency");
            this.put("currency-singular", "Πόντος");
            this.put("currency-plural", "Πόντοι");
            this.put("currency-separator", ",");
            this.put("currency-decimal", ".");
            this.put("number-abbreviation-thousands", "k");
            this.put("number-abbreviation-millions", "m");
            this.put("number-abbreviation-billions", "b");

            this.put("#2", "Misc");
            this.put("no-permission", "&cΔεν έχετε πρόσβαση για αυτή την ενέργεια!");
            this.put("no-console", "&cΜόνο οι παίκτες μπορούν να εκτελέσουν αυτήν την εντολή.");
            this.put("invalid-amount", "&cΤο ποσό θα πρέπει να είναι θετικός ακέραιος αριθμός.");
            this.put("unknown-player", "&cΟ παίκτης: &b%player% δεν βρέθηκε!");
            this.put("unknown-command", "&cΑγνωστη εντολή: &b%input%");
            this.put("votifier-voted", "&eΕυχαριστούμε που μας ψηφίσατε στο %service%! &b%amount% &eέχουν προστεθεί στο υπόλοιπο σας.");
            this.put("leaderboard-empty-entry", "Μπορεί να είσαι και εσύ!!");

            this.put("#3", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eΧρησημοποίησε &b/points help &eγια πληροφορίες εντολών.");

            this.put("#4", "Help Command");
            this.put("command-help-description", "&8 - &d/points help &7- Εμφανίζει το μενού βοήθειας... Φτάσατε");
            this.put("command-help-title", "&eΔιαθέσιμες εντολές:");

            this.put("#5", "Give Command");
            this.put("command-give-description", "&8 - &d/points give &7- Δώστε πόντους σε έναν παίκτη");
            this.put("command-give-usage", "&cΧρήση: &e/points give <παίκτης> <ποσό>");
            this.put("command-give-success", "&b%player% &aΔόθηκε &b%amount% &a%currency%.");
            this.put("command-give-received", "&eΕλαβες &b%amount% &e%currency%.");

            this.put("#6", "Give All Command");
            this.put("command-giveall-description", "&8 - &d/points giveall &7- Δίνει πόντους σε όλους τους παίκτες που είναι online");
            this.put("command-giveall-usage", "&cΧρήση: &e/points giveall <ποαό> [*]");
            this.put("command-giveall-success", "&aΈδωσε &b%amount% &a%currency% σε όλους τους παίκτες που είναι online.");

            this.put("#7", "Take Command");
            this.put("command-take-description", "&8 - &d/points take &7- Πάρτε πόντους από έναν παίκτη");
            this.put("command-take-usage", "&cΧρήση: &e/points take <παίκτης> <ποσό>");
            this.put("command-take-success", "&aΠήρε &b%amount% &a%currency% από &b%player%&a.");
            this.put("command-take-lacking-funds", "&b%player% &cδεν έχει αρκετά %currency% για αυτό, το υπόλοιπό τους έχει οριστεί στο 0.");

            this.put("#8", "Look Command");
            this.put("command-look-description", "&8 - &d/points look &7- Δες τους πόντους ενός παίκτη");
            this.put("command-look-usage", "&cΧρήση: &e/points look <παίκτη>");
            this.put("command-look-success", "&b%player% &e εχει &b%amount% &e%currency%.");

            this.put("#9", "Pay Command");
            this.put("command-pay-description", "&8 - &d/points pay &7- Πληρώστε έναν παίκτη");
            this.put("command-pay-usage", "&cΧρήση: &e/points pay <παίκτης> <ποσό>");
            this.put("command-pay-sent", "&aΠλήρωσες &b%player% %amount% &a%currency%.");
            this.put("command-pay-received", "&eΈχετε πληρωθεί &b%amount% &e%currency% απο τον/την &b%player%&e.");
            this.put("command-pay-lacking-funds", "&cΔεν έχετε αρκετά %currency%.");

            this.put("#10", "Set Command");
            this.put("command-set-description", "&8 - &d/points set &7- Ορίστε τους πόντους ενός παίκτη .");
            this.put("command-set-usage", "&cΧρήση: &e/points set <παίκτης> <ποσό>");
            this.put("command-set-success", "&aΕπιλέξτε  %currency% για τον παίκτη &b%player% &a με ποσό &b%amount%&a.");

            this.put("#11", "Reset Command");
            this.put("command-reset-description", "&8 - &d/points reset &7- Επαναφέρετε τους πόντους ενός παίκτη.");
            this.put("command-reset-usage", "&cΧρήση: &e/points reset <παίκτη>");
            this.put("command-reset-success", "&aΕπαναφέρετε %currency% του παίκτη &b%player% &a.");

            this.put("#12", "Me Command");
            this.put("command-me-description", "&8 - &d/points me &7-Δείτε τους πόντους σας");
            this.put("command-me-usage", "&cΧρήση: &d/points me");
            this.put("command-me-success", "&eΈχετε &b%amount% &e%currency%.");

            this.put("#13", "Lead Command");
            this.put("command-lead-description", "&8 - &d/points lead &7- Δείτε τον βαθμολογικό πίνακα");
            this.put("command-lead-usage", "&cΧρήση: &e/points lead [next|prev|#]");
            this.put("command-lead-title", "&eΒαθμολογικό Πίνακας &7(Page #%page%/%pages%)");
            this.put("command-lead-entry", "&b%position%). &e%player% &7- &6%amount% %currency%");

            this.put("#14", "Broadcast Command");
            this.put("command-broadcast-description", "&8 - &d/points broadcast &7- Μετάδωσε τα πόιντς ενώς παίκτη.");
            this.put("command-broadcast-usage", "&Χρήση: &e/points broadcast <παίκτη>");
            this.put("command-broadcast-message", "&b%player% &e έχει &b%amount% &e%currency%.");

            this.put("#15", "Reload Command");
            this.put("command-reload-description", "&8 - &d/points reload &7- Επαναφόρτωση του plugin");
            this.put("command-reload-usage", "&cΧρήση: &e/points reload");
            this.put("command-reload-success", "&aΤα Configuration και τα  locale files επαναφορτώθηκαν.");

            this.put("#16", "Export Command");
            this.put("command-export-description", "&8 - &d/points export &7- Εξαγωγή δεδομένων στο storage.yml");
            this.put("command-export-usage", "&cΧρήση: &e/points export");
            this.put("command-export-success", "&aΤα δεδομένα αντιγράφων ασφαλείας έχουν εξαχθεί στο storage.yml.");
            this.put("command-export-warning", "&cΣημείωση: υπάρχει ήδη αρχείο storage.yml. Εάν θέλετε να το αντικαταστήσετε, χρησιμοποιήστε το &b/points export confirm&c.");

            this.put("#17", "Import Command");
            this.put("command-import-description", "&8 - &d/points import &7- Εισαγωγή δεδομένων από storage.yml");
            this.put("command-import-usage", "&cΧρήση: &e/points import");
            this.put("command-import-success", "&aΈγινε εισαγωγή δεδομένων αντιγράφων ασφαλείας από storage.yml.");
            this.put("command-import-no-backup", "&cΔεν είναι δυνατή η εισαγωγή, το storage.yml δεν υπάρχει. " +
                    "Μπορείτε να δημιουργήσετε ένα με εξαγωγή &b/points &cand και να το χρησιμοποιήσετε για να μεταφέρετε δεδομένα μεταξύ τύπων βάσεων δεδομένων.");
            this.put("command-import-warning", "&cΣημείωση: Αυτή η λειτουργία θα αφαιρέσει όλα τα δεδομένα από την ενεργή βάση δεδομένων και θα τα αντικαταστήσει με τα περιεχόμενα του storage.yml. " +
                    "&cΟ τρέχων ενεργός τύπος βάσης δεδομένων είναι &b&o&l%type%&c. " +
                    "&cΕάν είστε απολύτως σίγουροι, χρησιμοποιήστε &b/points import confirm&c.");

            this.put("#18", "Convert Command");
            this.put("command-convert-description", "&8 - &d/points convert &7- Φόρτωση δεδομένων πόντων από άλλo plugin");
            this.put("command-convert-usage", "&cΧρήση: &e/points convert <plugin>");
            this.put("command-convert-invalid", "&b%plugin% &cδεν είναι συμβατό με αυτό το πλαγκιν.");
            this.put("command-convert-success", "&aΤα δεδομένα πόντων από το &b%plugin% &a έχουν μετατραπεί.");
            this.put("command-convert-failure", "&cΠαρουσιάστηκε σφάλμα κατά την προσπάθεια μετατροπής των δεδομένων. " +
                    "Ελέγξτε τo console σας και αναφέρετε τυχόν σφάλματα στον συντάκτη του PlayerPoints plugin.");
            this.put("command-convert-warning", "&cΣημείωση: Αυτό θα αφαιρέσει όλα τα δεδομένα από την ενεργή βάση δεδομένων και θα τα αντικαταστήσει με δεδομένα από το &b%plugin%&c. " +
                    "&cΕάν είστε απολύτως σίγουροι, χρησιμοποιήστε το &b/points convert %plugin% επιβεβαίωση&c.");

            this.put("#19", "Import Legacy Command");
            this.put("command-importlegacy-description", "&8 - &d/points importlegacy &7- Εισαγάγετε ένα legacy table.");
            this.put("command-importlegacy-usage", "&cΧρήση: &e/points importlegacy <table>");
            this.put("command-importlegacy-success", "&aΤα δεδομένα του legacy table εισήχθησαν με επιτυχία από το &b%table%&a.");
            this.put("command-importlegacy-failure", "&cΣφάλμα κατά την εισαγωγή δεδομένων απο τον legacy panel: &b%table%&c.Υπάρχει αυτό το table;?");
            this.put("command-importlegacy-only-mysql", "&cΑυτή η εντολή είναι διαθέσιμη μόνο εάν έχετε ενεργοποιημένη τη MySQL.");

            this.put("#20", "Version Command");
            this.put("command-version-description", "&8 - &d/points version &7- Εμφανίζει πληροφορίες της έκδοσης του PlayerPoints");
        }};
    }

}