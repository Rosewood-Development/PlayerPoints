package org.black_ixx.playerpoints.locale;

import dev.rosewood.rosegarden.locale.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

public class PortugueseLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "pt_PT";
    }

    @Override
    public String getTranslatorName() {
        return "AtlasGamerZ";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<String, Object>() {{
            this.put("#0", "Mensagem de prefixo do plugin");
            this.put("prefix", "&7[<g:#E8A230:#ECD32D>PlayerPoints&7] ");

            this.put("#1", "Moeda");
            this.put("currency-singular", "Ponto");
            this.put("currency-plural", "Pontos");
            this.put("currency-separator", ",");
            this.put("currency-decimal", ".");
            this.put("number-abbreviation-thousands", "k");
            this.put("number-abbreviation-millions", "m");
            this.put("number-abbreviation-billions", "b");

            this.put("#2", "Misc");
            this.put("no-permission", "&cTu não tens permissão para isso!");
            this.put("no-console", "&cApenas jogadores podem executar este comando.");
            this.put("invalid-amount", "&cQuantidade deve ser um numero inteiro positivo.");
            this.put("unknown-player", "&cJogador não pode ser encontrado: &b%player%");
            this.put("unknown-command", "&cComando desconhecido: &b%input%");
            this.put("votifier-voted", "&eObrigado por votar em %service%! &b%amount% &eforam adicionados ao seu saldo.");

            this.put("#3", "Mensagem de comandos base");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eUse &b/points help &epara obter informação sobre o comando.");

            this.put("#4", "Comando de Ajuda");
            this.put("command-help-description", "&8 - &d/points help &7- Mostra um menu de ajuda... Tu chegaste");
            this.put("command-help-title", "&eComandos disponíveis:");

            this.put("#5", "Comando de Give");
            this.put("command-give-description", "&8 - &d/points give &7- Dá pontos a um jogador");
            this.put("command-give-usage", "&cUso: &e/points give <jogador> <quantidade>");
            this.put("command-give-success", "&aDeste &b%amount% &e%currency% &ea &b%player%.");
            this.put("command-give-received", "&eTu recebeste &b%amount% &e%currency%.");

            this.put("#6", "Comando de Give All");
            this.put("command-giveall-description", "&8 - &d/points giveall &7- Dá pontos a todos os jogadores online");
            this.put("command-giveall-usage", "&cUso: &e/points giveall <quantidade> [*]");
            this.put("command-giveall-success", "&aDeste &b%amount% &a%currency% a todos os jogadores online.");

            this.put("#7", "Command de Take");
            this.put("command-take-description", "&8 - &d/points take &7- Tira pontos de um jogador");
            this.put("command-take-usage", "&cUso: &e/points take <jogador> <quantidade>");
            this.put("command-take-success", "&aTiraste &b%amount% &a%currency% de &b%player%&a.");
            this.put("command-take-lacking-funds", "&b%player% &cnão possui %currency% suficientes para isso, então o saldo foi definido como 0.");

            this.put("#8", "Comando de Look");
            this.put("command-look-description", "&8 - &d/points look &7- Vê os pontos de um jogador");
            this.put("command-look-usage", "&cUso: &e/points look <jogador>");
            this.put("command-look-success", "&b%player% &etem &b%amount% &e%currency%.");

            this.put("#9", "Comando de Pay");
            this.put("command-pay-description", "&8 - &d/points pay &7- Paga a um jogador");
            this.put("command-pay-usage", "&cUso: &e/points pay <jogador> <quantidade>");
            this.put("command-pay-sent", "&aTu pagaste a &b%player% %amount% &a%currency%.");
            this.put("command-pay-received", "&b%player%&e te pagou &b%amount% &e%currency%.");
            this.put("command-pay-lacking-funds", "&cTu não tens %currency% suficientes para isso.");

            this.put("#10", "Comando de Set");
            this.put("command-set-description", "&8 - &d/points set &7- Define os pontos de um jogador");
            this.put("command-set-usage", "&cUso: &e/points set <jogador> <quantidade>");
            this.put("command-set-success", "&a%currency% de &b%player% &aforam definidos para &b%amount%&a.");

            this.put("#11", "Comando de Reset");
            this.put("command-reset-description", "&8 - &d/points reset &7- Redefine os pontos de um jogador");
            this.put("command-reset-usage", "&cUso: &e/points reset <jogador>");
            this.put("command-reset-success", "&a%currency% de &b%player% &aforam redefinidos.");

            this.put("#12", "Comando de Me");
            this.put("command-me-description", "&8 - &d/points me &7- Ver os teus pontos");
            this.put("command-me-usage", "&cUso: &d/points me");
            this.put("command-me-success", "&eTu tens &b%amount% &e%currency%.");

            this.put("#13", "Comando de Lead");
            this.put("command-lead-description", "&8 - &d/points lead &7- Veja o ranking");
            this.put("command-lead-usage", "&cUso: &e/points lead [next|prev|#]");
            this.put("command-lead-title", "&eLeaderboard &7(Página #%page%/%pages%)");
            this.put("command-lead-entry", "&b%position%). &e%player% &7- &6%amount% %currency%");

            this.put("#14", "Comando de Broadcast");
            this.put("command-broadcast-description", "&8 - &d/points broadcast &7- Difunde a quantidade de pontos de um jogador");
            this.put("command-broadcast-usage", "&cUso: &e/points broadcast <jogador>");
            this.put("command-broadcast-message", "&b%player% &etem &b%amount% &e%currency%.");

            this.put("#15", "Comando de Reload");
            this.put("command-reload-description", "&8 - &d/points reload &7- Recarrega o plugin");
            this.put("command-reload-usage", "&cUso: &e/points reload");
            this.put("command-reload-success", "&aArquivos de configuração e linguagem foram recarregados.");

            this.put("#16", "Comando de Export");
            this.put("command-export-description", "&8 - &d/points export &7- Exporta os dados para storage.yml");
            this.put("command-export-usage", "&cUso: &e/points export");
            this.put("command-export-success", "&aDados foram exportados para storage.yml.");
            this.put("command-export-warning", "&cAviso: Um arquivo storage.yml já existe. Se gostarias de o sobrescrever, utiliza &b/points export confirm&c.");

            this.put("#17", "Comando de Import");
            this.put("command-import-description", "&8 - &d/points import &7- Importa os dados de storage.yml");
            this.put("command-import-usage", "&cUso: &e/points import");
            this.put("command-import-success", "&aDados foram importados de storage.yml.");
            this.put("command-import-no-backup", "&cNão é possível importar, storage.yml não existe. Tu podes gerar um com &b/points export &ce usá-lo para transferir dados entre tipos de bases de dados.");
            this.put("command-import-warning", "&cAviso: Esta operação vai apagar todos os dados da base de dados ativa e substitui-los pelo conteudo de storage.yml. " +
                    "&cO tipo de base de dados ativa é &b&o&l%type%&c. " +
                    "&cSe tu tens a certeza absoluta sobre isto, utiliza &b/points import confirm&c.");

            this.put("#18", "Comando de Convert");
            this.put("command-convert-description", "&8 - &d/points convert &7- Carrega os dados da moeda de outro plugin");
            this.put("command-convert-usage", "&cUso: &e/points convert <plugin>");
            this.put("command-convert-invalid", "&b%plugin% &cnão é o nome de um plugin de moeda convertível.");
            this.put("command-convert-success", "&aDados da moeda de &b%plugin% &aforam convertidos com sucesso.");
            this.put("command-convert-failure", "&cOcorreu um erro enquanto se tentava converter os dados. " +
                    "Por favor verifica na tua consola e informa quaisquer erros ao autor do plugin PlayerPoints.");
            this.put("command-convert-warning", "&cAviso: Esta operação vai apagar todos os dados da base de dados ativa e substitui-los pelos dados de &b%plugin%&c. " +
                    "&cSe tu tens a certeza absoluta sobre isto, utiliza &b/points convert %plugin% confirm&c.");

            this.put("#19", "Comando de Import Legacy");
            this.put("command-importlegacy-description", "&8 - &d/points importlegacy &7- Importa uma tabela antiga");
            this.put("command-importlegacy-usage", "&cUso: &e/points importlegacy <tabela>");
            this.put("command-importlegacy-success", "&aDados antigos importados com sucesso de &b%table%&a.");
            this.put("command-importlegacy-failure", "&cFalha para importar dados de &b%table%&c. A tabela existe?");
            this.put("command-importlegacy-only-mysql", "&cEste comando apenas se encontra disponível quando o MySQL está ativado.");

            this.put("#20", "Version Command");
            this.put("command-version-description", "&8 - &d/points version &7- Exibe informações sobre a versão de PlayerPoints");
        }};
    }

}
