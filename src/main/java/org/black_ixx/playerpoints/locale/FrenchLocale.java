package org.black_ixx.playerpoints.locale;

import dev.rosewood.rosegarden.locale.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

public class FrenchLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "fr_FR";
    }

    @Override
    public String getTranslatorName() {
        return "Jason54";
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
            this.put("no-permission", "&cVous n'avez pas la permission pour ça!");
            this.put("no-console", "&cSeuls les joueurs peuvent exécuter cette commande.");
            this.put("invalid-amount", "&cLe montant doit être un nombre entier positif.");
            this.put("unknown-player", "&cLe joueur est introuvable: &b%player%");
            this.put("unknown-command", "&cCommande inconnue: &b%input%");
            this.put("votifier-voted", "&eMerci d'avoir voté sur %service%! &b%amount% &ea été ajouté à votre solde.");

            this.put("#3", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eUtilise &b/points help &epour les informations de commande.");

            this.put("#4", "Help Command");
            this.put("command-help-description", "&8 - &d/points help &7- Affiche le menu d'aide ... Vous êtes arrivé");
            this.put("command-help-title", "&eCommandes disponibles:");

            this.put("#5", "Give Command");
            this.put("command-give-description", "&8 - &d/points give &7- Donner des points à un joueur");
            this.put("command-give-usage", "&cUtilise: &e/points give <joueur> <montant>");
            this.put("command-give-success", "&b%player% &aa été donné &b%amount% &e%currency%.");
            this.put("command-give-received", "&eVous avez reçu &b%amount% &e%currency%.");

            this.put("#6", "Give All Command");
            this.put("command-giveall-description", "&8 - &d/points giveall &7- Donne des points à tous les joueurs en ligne");
            this.put("command-giveall-usage", "&cUtilise: &e/points giveall <montant>");
            this.put("command-giveall-success", "&aGave &b%amount% &a%currency% à tous les joueurs en ligne.");

            this.put("#7", "Take Command");
            this.put("command-take-description", "&8 - &d/points take &7- Prenez des points d'un joueur");
            this.put("command-take-usage", "&cUtilise: &e/points take <joueur> <montant>");
            this.put("command-take-success", "&aA pris &b%amount% &a%currency% de &b%player%&a.");
            this.put("command-take-lacking-funds", "&b%player% &cn'a pas assez %currency% pour cela, leur solde a donc été mis à 0.");

            this.put("#8", "Look Command");
            this.put("command-look-description", "&8 - &d/points look &7- Afficher les points d'un joueur");
            this.put("command-look-usage", "&cUtilise: &e/points look <joueur>");
            this.put("command-look-success", "&b%player% &epossède &b%amount% &e%currency%.");

            this.put("#9", "Pay Command");
            this.put("command-pay-description", "&8 - &d/points pay &7- Payer un joueur");
            this.put("command-pay-usage", "&cUtilise: &e/points pay <joueur> <montant>");
            this.put("command-pay-sent", "&aTu as payé &b%player% %amount% &a%currency%.");
            this.put("command-pay-received", "&eTu étais payé &b%amount% &e%currency% par &b%player%&e.");
            this.put("command-pay-lacking-funds", "&cTu n'as pas assez %currency% pour ça.");

            this.put("#10", "Set Command");
            this.put("command-set-description", "&8 - &d/points set &7- Définir les points d'un joueur");
            this.put("command-set-usage", "&cUtilise: &e/points set <joueur> <montant>");
            this.put("command-set-success", "&av %currency% de &b%player% &aà &b%amount%&a.");

            this.put("#11", "Reset Command");
            this.put("command-reset-description", "&8 - &d/points reset &7- Réinitialiser les points d'un joueur");
            this.put("command-reset-usage", "&cUtilise: &e/points reset <joueur>");
            this.put("command-reset-success", "&aRéinitialisez le %currency% pour &b%player% &a.");

            this.put("#12", "Me Command");
            this.put("command-me-description", "&8 - &d/points me &7- Afficher vos points");
            this.put("command-me-usage", "&cUtilise: &d/points me");
            this.put("command-me-success", "&eVous avez &b%amount% &e%currency%.");

            this.put("#13", "Lead Command");
            this.put("command-lead-description", "&8 - &d/points lead &7- Voir le classement");
            this.put("command-lead-usage", "&cUtilise: &e/points lead [next|prev|#]");
            this.put("command-lead-title", "&eLeaderboard &7(Page #%page%/%pages%)");
            this.put("command-lead-entry", "&b%position%). &e%player% &7- &6%amount% %currency%");

            this.put("#14", "Broadcast Command");
            this.put("command-broadcast-description", "&8 - &d/points broadcast &7- Diffuser les points d'un joueur");
            this.put("command-broadcast-usage", "&cUtilise: &e/points broadcast <joueur>");
            this.put("command-broadcast-message", "&b%player% &epossède &b%amount% &e%currency%.");

            this.put("#15", "Reload Command");
            this.put("command-reload-description", "&8 - &d/points reload &7- Recharge le plugin");
            this.put("command-reload-usage", "&cUtilise: &e/points reload");
            this.put("command-reload-success", "&aLes fichiers de configuration et de paramètres régionaux ont été rechargés.");

            this.put("#16", "Export Command");
            this.put("command-export-description", "&8 - &d/points export &7- Exporte les données vers storage.yml");
            this.put("command-export-usage", "&cUtilise: &e/points export");
            this.put("command-export-success", "&aLes données de sauvegarde ont été exportées vers storage.yml.");
            this.put("command-export-warning", "&cRemarque: un fichier storage.yml existe déjà. Si vous souhaitez l'écraser, utilisez &b/points export confirm&c.");

            this.put("#17", "Import Command");
            this.put("command-import-description", "&8 - &d/points import &7- Importe les données depuis storage.yml");
            this.put("command-import-usage", "&cUtilise: &e/points import");
            this.put("command-import-success", "&aLes données de sauvegarde ont été importées depuis storage.yml.");
            this.put("command-import-no-backup", "&cImpossible d'importer, storage.yml n'existe pas. " +
                    "Vous pouvez en générer un avec &b/points export &cet utilisez-le pour transférer des données entre les types de bases de données.");
            this.put("command-import-warning", "&cRemarque: Cette opération supprimera toutes les données de la base de données active et la remplacera par le contenu de storage.yml. " +
                    "&cLe type de base de données actuellement actif est &b&o&l%type%&c. " +
                    "&cSi vous en êtes absolument sûr, utilisez &b/points import confirm&c.");

            this.put("#18", "Convert Command");
            this.put("command-convert-description", "&8 - &d/points convert &7- Charge les données de devise à partir d'un autre plugin");
            this.put("command-convert-usage", "&cUtilise: &e/points convert <plugin>");
            this.put("command-convert-invalid", "&b%plugin% &cn'est pas un nom de plugin de devise convertible.");
            this.put("command-convert-success", "&aDonnées monétaires de &b%plugin% &aa été converti.");
            this.put("command-convert-failure", "&cUne erreur s'est produite lors de la tentative de conversion des données. " +
                    "Veuillez vérifier votre console et signaler toute erreur à l'auteur du plugin PlayerPoints.");
            this.put("command-convert-warning", "&cRemarque: Cette opération supprimera toutes les données de la base de données active et la remplacera par les données de &b%plugin%&c. " +
                    "&cSi vous en êtes absolument sûr, utilisez &b/points convert %plugin% confirm&c.");
        }};
    }

}
