import java.text.SimpleDateFormat;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Entidade Produto do Sistema de Vendas para Eventos Universitarios (PDV).
 *
 * Representa um item vendido no caixa do evento (pastel, refrigerante...).
 * As anotacoes do ORMLite mapeiam a classe para a tabela "produtos" e cada
 * atributo para uma coluna, com os mesmos nomes usados no schema SQL do
 * projeto (sistema-vendas-eventos/sql/01_schema.sql).
 *
 * Seguindo a convencao do projeto, o preco e guardado em centavos (int):
 * ponto flutuante acumula erro de arredondamento quando se trata de dinheiro.
 */
@DatabaseTable(tableName = "produtos")
public class Produto
{
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String nome;

    @DatabaseField
    private String categoria;

    @DatabaseField(columnName = "preco_centavos", canBeNull = false)
    private int precoCentavos;

    @DatabaseField(canBeNull = false)
    private boolean ativo;

    // DATE_STRING grava a data como texto legivel (2026-11-02 17:30:00) em vez
    // de um numero de milissegundos, o que facilita conferir a tabela no banco.
    @DatabaseField(columnName = "criado_em", dataType = DataType.DATE_STRING,
                   format = "yyyy-MM-dd HH:mm:ss")
    private Date criadoEm;

    /**
     * Construtor vazio, obrigatorio para o ORMLite: ao ler uma linha da tabela,
     * ele cria o objeto com este construtor e depois preenche os atributos.
     */
    public Produto() {
        this.ativo = true;
        this.criadoEm = new Date();
    }

    public Produto(String nome, String categoria, int precoCentavos) {
        this();
        this.nome = nome;
        this.categoria = categoria;
        this.precoCentavos = precoCentavos;
    }

    /** Preco no formato da moeda, por exemplo 850 centavos -> "R$ 8,50". */
    public String getPrecoFormatado() {
        return String.format("R$ %d,%02d", precoCentavos / 100, precoCentavos % 100);
    }

    public String printCriadoEm() {
        if (criadoEm == null) {
            return "";
        }
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(criadoEm);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getPrecoCentavos() {
        return precoCentavos;
    }

    public void setPrecoCentavos(int precoCentavos) {
        this.precoCentavos = precoCentavos;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return String.format("Produto #%d | %-18s | %-8s | %8s | %-7s | criado em %s",
                             id, nome, categoria, getPrecoFormatado(),
                             ativo ? "ativo" : "inativo", printCriadoEm());
    }
}
