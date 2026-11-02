import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import com.j256.ormlite.table.TableUtils;
import java.util.List;
import java.util.ArrayList;

/**
 * Repositorio de produtos: concentra o acesso ao banco para a entidade Produto
 * (padrao Repository, conforme o tutorial de ORM).
 *
 * As operacoes CRUD sao delegadas ao Dao do ORMLite, de modo que nenhuma linha
 * de SQL precisa ser escrita. O repositorio tambem mantem em memoria os
 * objetos carregados nas ultimas operacoes (loadedProdutos e loadedProduto).
 */
public class ProdutoRepository
{
    private static Database database;
    private static Dao<Produto, Integer> dao;
    private List<Produto> loadedProdutos;
    private Produto loadedProduto;

    public ProdutoRepository(Database database) {
        ProdutoRepository.setDatabase(database);
        loadedProdutos = new ArrayList<Produto>();
    }

    public static void setDatabase(Database database) {
        ProdutoRepository.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), Produto.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Produto.class);
        }
        catch(SQLException e) {
            System.out.println(e);
        }
    }

    /** Create: grava um novo produto; o ORMLite preenche o id gerado pelo banco. */
    public Produto create(Produto produto) {
        int nrows = 0;
        try {
            nrows = dao.create(produto);
            if ( nrows == 0 )
                throw new SQLException("Error: object not saved");
            this.loadedProduto = produto;
            loadedProdutos.add(produto);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return produto;
    }

    /** Update: grava no banco os valores atuais de um produto ja cadastrado. */
    public void update(Produto produto) {
        int nrows = 0;
        try {
            nrows = dao.update(produto);
            if ( nrows == 0 )
                throw new SQLException("Error: object not updated (id " + produto.getId() + " not found)");
            removeFromLoaded(produto.getId());
            this.loadedProduto = produto;
            loadedProdutos.add(produto);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /** Delete: exclui do banco a linha correspondente ao id do produto. */
    public void delete(Produto produto) {
        int nrows = 0;
        try {
            nrows = dao.delete(produto);
            if ( nrows == 0 )
                throw new SQLException("Error: object not deleted (id " + produto.getId() + " not found)");
            removeFromLoaded(produto.getId());
            if (this.loadedProduto != null && this.loadedProduto.getId() == produto.getId())
                this.loadedProduto = null;
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /** Read: busca um produto pelo identificador; devolve null se nao existir. */
    public Produto loadFromId(int id) {
        try {
            this.loadedProduto = dao.queryForId(id);
            if (this.loadedProduto != null) {
                removeFromLoaded(id);
                this.loadedProdutos.add(this.loadedProduto);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedProduto;
    }

    /** Read: lista todos os produtos cadastrados. */
    public List<Produto> loadAll() {
        try {
            this.loadedProdutos =  dao.queryForAll();
            if (this.loadedProdutos.size() != 0)
                this.loadedProduto = this.loadedProdutos.get(0);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return this.loadedProdutos;
    }

    /** Tira do cache em memoria o produto com o id informado, se estiver la. */
    private void removeFromLoaded(int id) {
        for (int i = loadedProdutos.size() - 1; i >= 0; i--) {
            if (loadedProdutos.get(i).getId() == id)
                loadedProdutos.remove(i);
        }
    }

    public static Database getDatabase() {
        return database;
    }

    public List<Produto> getLoadedProdutos() {
        return loadedProdutos;
    }

    public Produto getLoadedProduto() {
        return loadedProduto;
    }
}
