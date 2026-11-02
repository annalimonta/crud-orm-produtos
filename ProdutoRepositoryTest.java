import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

/**
 * Testes das operacoes CRUD do ProdutoRepository.
 *
 * Cada teste comeca com um banco vazio (arquivo teste-produtos.sqlite), para
 * nao misturar seus dados com os da demonstracao (produtos.sqlite). Alem de
 * conferir os objetos devolvidos pelo repositorio, os testes reabrem a conexao
 * e contam as linhas com SQL puro, para garantir que a alteracao chegou ao
 * arquivo do banco e nao ficou apenas na memoria.
 */
public class ProdutoRepositoryTest
{
    private static final String BANCO_TESTE = "teste-produtos.sqlite";

    private Database database;
    private ProdutoRepository repositorio;

    /**
     * Define a 'fixture' do teste: banco novo e vazio antes de cada teste.
     */
    @BeforeEach
    public void setUp()
    {
        new File(BANCO_TESTE).delete();
        database = new Database(BANCO_TESTE);
        repositorio = new ProdutoRepository(database);
    }

    /**
     * Desfaz a 'fixture' do teste: fecha a conexao com o banco.
     */
    @AfterEach
    public void tearDown()
    {
        database.close();
    }

    /** Fecha a conexao atual e abre outra, como se o programa fosse reiniciado. */
    private void reabrirBanco()
    {
        database.close();
        database = new Database(BANCO_TESTE);
        repositorio = new ProdutoRepository(database);
    }

    @Test
    public void testCreate()
    {
        Produto produto = new Produto("Pastel de queijo", "Comidas", 800);
        assertEquals(0, produto.getId(), "Objeto transiente ainda nao deveria ter id");

        Produto produtoSalvo = repositorio.create(produto);

        System.out.println("ID do produto criado: " + produtoSalvo.getId());
        assertTrue(produtoSalvo.getId() > 0, "Identificador do produto nao foi gerado");
        assertEquals(1, ConsultaSQL.contarProdutos(BANCO_TESTE), "Linha nao foi gravada na tabela");
    }

    @Test
    public void testLoadFromId()
    {
        int id = repositorio.create(new Produto("Cachorro-quente", "Comidas", 1000)).getId();
        reabrirBanco();

        Produto carregado = repositorio.loadFromId(id);

        assertNotNull(carregado, "Produto gravado nao foi encontrado pelo id");
        assertEquals("Cachorro-quente", carregado.getNome());
        assertEquals("Comidas", carregado.getCategoria());
        assertEquals(1000, carregado.getPrecoCentavos());
        assertTrue(carregado.isAtivo());
        assertNotNull(carregado.getCriadoEm(), "Data de cadastro nao foi gravada");
        assertNull(repositorio.loadFromId(9999), "Id inexistente deveria devolver null");
    }

    @Test
    public void testLoadAll()
    {
        repositorio.create(new Produto("Pastel de queijo", "Comidas", 800));
        repositorio.create(new Produto("Refrigerante lata", "Bebidas", 500));
        repositorio.create(new Produto("Suco natural", "Bebidas", 700));
        reabrirBanco();

        List<Produto> produtos = repositorio.loadAll();

        assertEquals(3, produtos.size(), "loadAll deveria trazer os 3 produtos cadastrados");
        assertEquals("Pastel de queijo", produtos.get(0).getNome());
        assertEquals("Suco natural", produtos.get(2).getNome());
    }

    @Test
    public void testUpdate()
    {
        Produto produto = repositorio.create(new Produto("Refrigerante lata", "Bebidas", 500));
        produto.setNome("Refrigerante lata 350ml");
        produto.setPrecoCentavos(600);
        produto.setAtivo(false);

        repositorio.update(produto);
        reabrirBanco();

        Produto relido = repositorio.loadFromId(produto.getId());
        assertEquals("Refrigerante lata 350ml", relido.getNome(), "Nome nao foi atualizado no banco");
        assertEquals(600, relido.getPrecoCentavos(), "Preco nao foi atualizado no banco");
        assertFalse(relido.isAtivo(), "Situacao nao foi atualizada no banco");
        assertEquals(1, ConsultaSQL.contarProdutos(BANCO_TESTE), "Update nao pode criar linha nova");
    }

    @Test
    public void testDelete()
    {
        Produto pastel = repositorio.create(new Produto("Pastel de carne", "Comidas", 900));
        Produto suco = repositorio.create(new Produto("Suco natural", "Bebidas", 700));
        int idPastel = pastel.getId();

        repositorio.delete(pastel);
        reabrirBanco();

        assertNull(repositorio.loadFromId(idPastel), "Produto nao foi excluido do banco");
        assertNotNull(repositorio.loadFromId(suco.getId()), "O outro produto nao poderia ser excluido");
        assertEquals(1, repositorio.loadAll().size());
        assertEquals(1, ConsultaSQL.contarProdutos(BANCO_TESTE), "Linha continua na tabela");
    }

    @Test
    public void testPersistenciaAposFecharConexao()
    {
        Produto produto = repositorio.create(new Produto("Agua mineral", "Bebidas", 300));
        database.close();

        // Conexao totalmente nova com o mesmo arquivo: o dado tem de estar la.
        assertTrue(new File(BANCO_TESTE).exists(), "Arquivo do banco nao foi criado");
        reabrirBanco();
        Produto relido = repositorio.loadFromId(produto.getId());

        assertNotNull(relido, "Produto nao persistiu apos fechar a conexao");
        assertEquals("Agua mineral", relido.getNome());
        assertEquals(300, relido.getPrecoCentavos());
    }
}
