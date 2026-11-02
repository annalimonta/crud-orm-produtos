import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Le a tabela "produtos" diretamente com SQL, via JDBC puro, SEM passar pelo
 * ORMLite nem pela classe Produto.
 *
 * Serve como evidencia independente do ORM: o que aparece aqui e exatamente o
 * que esta gravado no arquivo .sqlite, e nao o que esta na memoria do programa.
 *
 * Execucao:  java -cp ".;+libs/*" ConsultaSQL produtos.sqlite
 */
public class ConsultaSQL
{
    public static void main(String[] args) {
        String arquivo = args.length > 0 ? args[0] : DemonstracaoCrud.BANCO;
        imprimirEstrutura(arquivo);
        imprimirTabela(arquivo);
    }

    /** Mostra o CREATE TABLE que o ORMLite gerou a partir das anotacoes de Produto. */
    public static void imprimirEstrutura(String arquivoBanco) {
        String sql = "SELECT sql FROM sqlite_master WHERE type = 'table' AND name = 'produtos'";
        try (Connection conexao = DriverManager.getConnection("jdbc:sqlite:" + arquivoBanco);
             Statement comando = conexao.createStatement();
             ResultSet resultado = comando.executeQuery(sql)) {
            System.out.println("Estrutura da tabela no arquivo " + arquivoBanco + ":");
            if (resultado.next()) {
                System.out.println("  " + resultado.getString(1));
            } else {
                System.out.println("  (tabela produtos nao existe)");
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /** Executa SELECT * FROM produtos e imprime o resultado em forma de tabela. */
    public static void imprimirTabela(String arquivoBanco) {
        String sql = "SELECT * FROM produtos ORDER BY id";
        try (Connection conexao = DriverManager.getConnection("jdbc:sqlite:" + arquivoBanco);
             Statement comando = conexao.createStatement();
             ResultSet resultado = comando.executeQuery(sql)) {

            ResultSetMetaData colunas = resultado.getMetaData();
            int total = colunas.getColumnCount();
            List<String[]> linhas = new ArrayList<String[]>();
            String[] cabecalho = new String[total];
            for (int c = 0; c < total; c++) {
                cabecalho[c] = colunas.getColumnName(c + 1);
            }
            linhas.add(cabecalho);
            while (resultado.next()) {
                String[] linha = new String[total];
                for (int c = 0; c < total; c++) {
                    linha[c] = String.valueOf(resultado.getString(c + 1));
                }
                linhas.add(linha);
            }

            System.out.println("sqlite> " + sql + ";   -- arquivo " + arquivoBanco);
            imprimirLinhas(linhas);
            System.out.println("(" + (linhas.size() - 1) + " linha(s) gravada(s) no banco)");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /** Conta quantas linhas existem na tabela produtos (usado nos testes). */
    public static int contarProdutos(String arquivoBanco) {
        String sql = "SELECT COUNT(*) FROM produtos";
        try (Connection conexao = DriverManager.getConnection("jdbc:sqlite:" + arquivoBanco);
             Statement comando = conexao.createStatement();
             ResultSet resultado = comando.executeQuery(sql)) {
            return resultado.next() ? resultado.getInt(1) : 0;
        } catch (SQLException e) {
            System.out.println(e);
            return -1;
        }
    }

    private static void imprimirLinhas(List<String[]> linhas) {
        int[] largura = new int[linhas.get(0).length];
        for (String[] linha : linhas) {
            for (int c = 0; c < linha.length; c++) {
                largura[c] = Math.max(largura[c], linha[c].length());
            }
        }
        StringBuilder separador = new StringBuilder("+");
        for (int l : largura) {
            separador.append("-".repeat(l + 2)).append("+");
        }
        System.out.println(separador);
        for (int i = 0; i < linhas.size(); i++) {
            StringBuilder texto = new StringBuilder("|");
            for (int c = 0; c < largura.length; c++) {
                texto.append(" ").append(String.format("%-" + largura[c] + "s", linhas.get(i)[c])).append(" |");
            }
            System.out.println(texto);
            if (i == 0) {
                System.out.println(separador);
            }
        }
        System.out.println(separador);
    }
}
