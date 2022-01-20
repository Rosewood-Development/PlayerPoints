package org.black_ixx.playerpoints.locale;

import dev.rosewood.rosegarden.locale.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

public class HungarianLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "hu_HU";
    }

    @Override
    public String getTranslatorName() {
        return "PatrikX";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<String, Object>() {{
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix", "&7[<g:#E8A230:#ECD32D>PlayerPoints&7] ");

            this.put("#1", "Currency");
            this.put("currency-singular", "Pont");
            this.put("currency-plural", "Pont");
            this.put("currency-separator", ",");
            this.put("currency-decimal", ".");
            this.put("number-abbreviation-thousands", "k");
            this.put("number-abbreviation-millions", "m");
            this.put("number-abbreviation-billions", "b");

            this.put("#2", "Misc");
            this.put("no-permission", "&cNincs jogod ehhez!");
            this.put("no-console", "&cCsak játékosok használhatják ezt a parancsot.");
            this.put("invalid-amount", "&cMennyiségnek pozitív egész számnak kell lennie.");
            this.put("unknown-player", "&cJátékos nem található: &b%player%");
            this.put("unknown-command", "&cIsmeretlen parancs: &b%input%");
            this.put("votifier-voted", "&eKöszönjük hogy szavaztál ránk itt %service%! &b%amount% &ehozzáadva az egyenlegedhez.");

            this.put("#3", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eHasználd a &b/points help &eparancsot segítségért.");

            this.put("#4", "Help Command");
            this.put("command-help-description", "&8 - &d/points help &7- Előhozza a segítségeket... Ahova megérkeztél");
            this.put("command-help-title", "&eElérhető Parancsok:");

            this.put("#5", "Give Command");
            this.put("command-give-description", "&8 - &d/points give &7- Ad a játékosnak pontot");
            this.put("command-give-usage", "&cHasználat: &e/points give <játékos> <mennyiség>");
            this.put("command-give-success", "&b%player% &akapott &b%amount% &a%currency%ot.");
            this.put("command-give-received", "&eKaptál &b%amount% &e%currency%ot.");

            this.put("#6", "Give All Command");
            this.put("command-giveall-description", "&8 - &d/points giveall &7- Ad az összes elérhető játékosnak pontot");
            this.put("command-giveall-usage", "&cHasználat: &e/points giveall <mennyiség> [*]");
            this.put("command-giveall-success", "&aAdtál &b%amount% &a%currency%ot az összes elérhető játékosnak.");

            this.put("#7", "Take Command");
            this.put("command-take-description", "&8 - &d/points take &7- Elvesz pontot a játékostól");
            this.put("command-take-usage", "&cHasználat: &e/points take <játékos> <mennyiség>");
            this.put("command-take-success", "&aElvettél &b%amount% &a%currency%ot &b%player% &ajátékostól.");
            this.put("command-take-lacking-funds", "&b%player%-nak/nek &cnincs elég %currency%ja ehhez, ezért az egyenlege beállítva 0-ra.");

            this.put("#8", "Look Command");
            this.put("command-look-description", "&8 - &d/points look &7- Játékos pontjainak megtekintése");
            this.put("command-look-usage", "&cHasználat: &e/points look <játékos>");
            this.put("command-look-success", "&b%player%-nak/nek &evan &b%amount% &e%currency%ja.");

            this.put("#9", "Pay Command");
            this.put("command-pay-description", "&8 - &d/points pay &7- Fizetés egy játékosnak");
            this.put("command-pay-usage", "&cHasználat: &e/points pay <játékos> <mennyiség>");
            this.put("command-pay-sent", "&aFizettél &b%player%-nak/nek %amount% &a%currency%ot.");
            this.put("command-pay-received", "&eFizetett neked &b%amount% &e%currency%ot &b%player%&e.");
            this.put("command-pay-lacking-funds", "&cNincs elég %currency%od ehhez.");

            this.put("#10", "Set Command");
            this.put("command-set-description", "&8 - &d/points set &7- Beállítja egy játékos pontját");
            this.put("command-set-usage", "&cHasználat: &e/points set <játékos> <mennyiség>");
            this.put("command-set-success", "&aBeállítottad a %currency%ját &b%player%-nak/nek &aennyire &b%amount%&a.");

            this.put("#11", "Reset Command");
            this.put("command-reset-description", "&8 - &d/points reset &7- Játékos pontjait alaphelyzetbe állítja");
            this.put("command-reset-usage", "&cHasználat: &e/points reset <játékos>");
            this.put("command-reset-success", "&aAlaphelyzetbe állítottad a %currency%jait &b%player%-nak/nek&a.");

            this.put("#12", "Me Command");
            this.put("command-me-description", "&8 - &d/points me &7- Pontjaid megtekintése");
            this.put("command-me-usage", "&cHasználat: &d/points me");
            this.put("command-me-success", "&eNeked van &b%amount% &e%currency%od.");

            this.put("#13", "Lead Command");
            this.put("command-lead-description", "&8 - &d/points lead &7- Ranglista megtekintése");
            this.put("command-lead-usage", "&cHasználat: &e/points lead [next|prev|#]");
            this.put("command-lead-title", "&eRanglista &7(Oldal #%page%/%pages%)");
            this.put("command-lead-entry", "&b%position%). &e%player% &7- &6%amount% %currency%");

            this.put("#14", "Broadcast Command");
            this.put("command-broadcast-description", "&8 - &d/points broadcast &7- Kihírdeti a játékos pontjait");
            this.put("command-broadcast-usage", "&cHasználat: &e/points broadcast <játékos>");
            this.put("command-broadcast-message", "&b%player%-nak/nek &evan &b%amount% &e%currency%ja.");

            this.put("#15", "Reload Command");
            this.put("command-reload-description", "&8 - &d/points reload &7- Újratölti a plugint");
            this.put("command-reload-usage", "&cHasználat: &e/points reload");
            this.put("command-reload-success", "&aKonfiguráció és a fordítások újratöltődtek.");

            this.put("#16", "Export Command");
            this.put("command-export-description", "&8 - &d/points export &7- Exportálja az adatokat a storage.yml fájlba");
            this.put("command-export-usage", "&cHasználat: &e/points export");
            this.put("command-export-success", "&aMentett adatok exportálva a storage.yml fájlba.");
            this.put("command-export-warning", "&cFigyelmeztetés: A storage.yml fájl már létezik. Ha felül szeretnéd írni, használd a &b/points export confirm &cparancsot.");

            this.put("#17", "Import Command");
            this.put("command-import-description", "&8 - &d/points import &7- Importálja az adatokat a storage.yml fájlból");
            this.put("command-import-usage", "&cHasználat: &e/points import");
            this.put("command-import-success", "&aMentett adatok importálva a storage.yml fájlból.");
            this.put("command-import-no-backup", "command-import-no-backup: '&cSikertelen importálás, storage.yml fájl nem létezik. Készíthetsz egyet a &b/points export &cparancsal és használhatod hogy átvidd az adatokat az adatbázis típusok között.'");
            this.put("command-import-warning", "&cFigyelmeztetés: Ez a művelet törli az összes eddigi adatot és importálja az adatokat a storage.yml fájlból. " +
                    "&cA jelenleg aktív adatbázis típusa &b&o&l%type%&c. " +
                    "&cHa teljesen biztos vagy ebben, akkor használd a &b/points import confirm &cparancsot.");

            this.put("#18", "Convert Command");
            this.put("command-convert-description", "&8 - &d/points convert &7- Betölti a valuta adatot egy másik pluginból");
            this.put("command-convert-usage", "&cHasználat: &e/points convert <plugin>");
            this.put("command-convert-invalid", "&b%plugin% &cnem konvertálható valuta plugin.");
            this.put("command-convert-success", "&aValuta adat a &b%plugin%-ból/ből &aát konvertálva.");
            this.put("command-convert-failure", "&cHiba történt az adat konvertálás közben. " +
                    "Kérlek ellenőrizd a konzolt és jelezd a hibákat a PlayerPoints plugin készítőjének.");
            this.put("command-convert-warning", "&cFigyelmeztetés: Ez a művelet törli az összes eddigi adatot és importálja az adatokat a &b%plugin%-ból/ből&c. " +
                    "&cHa teljesen biztos vagy ebben, akkor használd a &b/points convert %plugin% confirm &cparancsot.");

            this.put("#19", "Import Legacy Command");
            this.put("command-importlegacy-description", "&8 - &d/points importlegacy &7- Importálja a régebbi táblát");
            this.put("command-importlegacy-usage", "&cHasználat: &e/points importlegacy <table>");
            this.put("command-importlegacy-success", "&aSikeresen importálva a régi adatok a &b%table%-ból/ből&a.");
            this.put("command-importlegacy-failure", "&cSikertelen régi adat importálás a &b%table%-ból/ből&c. Létezik az tábla?");
            this.put("command-importlegacy-only-mysql", "&cEz a parancs csak akkor elérhető ha a MySQL engedélyezve van.");

            this.put("#20", "Version Command");
            this.put("command-version-description", "&8 - &d/points version &7- Kiírja a verzió információt a PlayerPointsról");
        }};
    }

}
