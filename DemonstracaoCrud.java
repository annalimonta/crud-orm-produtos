import java.util.List;

/**
 * Demonstracao passo a passo das quatro operacoes CRUD da entidade Produto.
 *
 * Cada operacao pode ser executada separadamente, em execucoes diferentes do
 * programa. Assim fica comprovado que os dados ficam no arquivo produtos.sqlite
 * e nao apenas na memoria: a consulta feita numa nova execucao encontra os
 * produtos cadastrados na execucao anterior.
 *
 * Ao final de cada operacao a tabela e lida com SQL puro (classe ConsultaSQL),
 * sem o ORM, para mostrar o que de fato foi gravado no banco.
 *
 * Execucao (Windows):
 *   java -cp ".;+libs/*" DemonstracaoCrud criar
 *   java -cp ".;+libs/*" DemonstracaoCrud consultar
 *   java -cp ".;+libs/*" DemonstracaoCrud atualizar 3
 *   java -cp ".;+libs/*" DemonstracaoCrud excluir 4
 *
 * No BlueJ, os metodos estaticos criar(), consultar(), atualizar(int) e
 * excluir(int) podem ser chamados diretamente com o botao direito na classe.
 */
public class DemonstracaoCrud
{
    public static final String BANCO = "produtos.sqlite";

    public static void main(String[] args) {
        String operacao = args.length > 0 ? args[0] : "tudo";
        int id = args.length > 1 ? Integer.parseInt(args[1]) : 0;

        switch (operacao) {
            case "criar":
                criar();
                break;
            case "consultar":
                consultar();
                break;
            case "atualizar":
                atualizar(id > 0 ? id : 3);
                break;
            case "excluir":
                excluir(id > 0 ? id : 4);
                break;
            case "tudo":
                criar();
                consultar();
                atualizar(3);
                excluir(4);
                break;
            default:
                System.out.println("Operacao desconhecida: " + operacao);
                System.out.println("Use: criar | consultar | atualizar <id> | excluir <id> | tudo");
        }
    }

    /** CREATE: cadastra os produtos do cardapio do evento. */
    public static void criar() {
        titulo("CREATE - cadastrando produtos com repositorio.create()");
        Database database = new Database(BANCO);
        ProdutoRepository repositorio = new ProdutoRepository(database);

        Produto[] cardapio = {
            new Produto("Pastel de queijo", "Comidas", 800),
            new Produto("Cachorro-quente", "Comidas", 1000),
            new Produto("Refrigerante lata", "Bebidas", 500),
            new Produto("Suco natural", "Bebidas", 700)
        };
        for (Produto produto : cardapio) {
            int idAntes = produto.getId();
            repositorio.create(produto);
            System.out.printf("create(%-17s) -> id antes: %d (so em memoria) | id gerado pelo banco: %d%n",
                              produto.getNome(), idAntes, produto.getId());
        }

        database.close();
        conferirNoBanco();
    }

    /** READ: lista todos os produtos e busca um produto pelo identificador. */
    public static void consultar() {
        titulo("READ - consultando com loadAll() e loadFromId()");
        Database database = new Database(BANCO);
        ProdutoRepository repositorio = new ProdutoRepository(database);

        List<Produto> produtos = repositorio.loadAll();
        System.out.println("loadAll() -> " + produtos.size() + " produto(s) encontrado(s):");
        for (Produto produto : produtos) {
            System.out.println("  " + produto);
        }

        System.out.println();
        System.out.println("loadFromId(2) -> " + repositorio.loadFromId(2));
        System.out.println("loadFromId(99) -> " + repositorio.loadFromId(99)
                           + "   (nenhum produto com id 99)");

        database.close();
    }

    /** UPDATE: reajusta o preco e renomeia um produto ja cadastrado. */
    public static void atualizar(int id) {
        titulo("UPDATE - alterando o produto " + id + " com repositorio.update()");
        Database database = new Database(BANCO);
        ProdutoRepository repositorio = new ProdutoRepository(database);

        Produto produto = repositorio.loadFromId(id);
        if (produto == null) {
            System.out.println("Nenhum produto com id " + id + " para atualizar.");
            database.close();
            return;
        }
        System.out.println("Antes .....: " + produto);

        produto.setNome(produto.getNome() + " 350ml");
        produto.setPrecoCentavos(produto.getPrecoCentavos() + 100);
        repositorio.update(produto);

        // Nova leitura: queryForId vai ao banco e monta um objeto novo.
        Produto relido = repositorio.loadFromId(id);
        System.out.println("Relido ....: " + relido);

        database.close();
        conferirNoBanco();
    }

    /** DELETE: exclui um produto do banco. */
    public static void excluir(int id) {
        titulo("DELETE - excluindo o produto " + id + " com repositorio.delete()");
        Database database = new Database(BANCO);
        ProdutoRepository repositorio = new ProdutoRepository(database);

        Produto produto = repositorio.loadFromId(id);
        if (produto == null) {
            System.out.println("Nenhum produto com id " + id + " para excluir.");
            database.close();
            return;
        }
        System.out.println("Excluindo .........: " + produto);
        repositorio.delete(produto);

        System.out.println("loadFromId(" + id + ") apos o delete -> " + repositorio.loadFromId(id));
        System.out.println("loadAll() apos o delete -> " + repositorio.loadAll().size() + " produto(s)");

        database.close();
        conferirNoBanco();
    }

    private static void conferirNoBanco() {
        System.out.println();
        System.out.println("Conferencia direta no banco (SQL puro via JDBC, sem ORM):");
        ConsultaSQL.imprimirTabela(BANCO);
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=".repeat(88));
        System.out.println("  " + texto);
        System.out.println("=".repeat(88));
    }
}
