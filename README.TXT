------------------------------------------------------------------------
CRUD com ORM (ORMLite + SQLite) - entidade Produto
------------------------------------------------------------------------
Entidade Produto do projeto sistema-vendas-eventos (PDV para eventos
universitarios), persistida com ORMLite no banco SQLite, seguindo o
padrao Repository do tutorial de ORM.

Bibliotecas: pasta +libs (carregada automaticamente pelo BlueJ).

Classes:
  Database ............... conexao com o arquivo SQLite
  Produto ................ entidade mapeada para a tabela "produtos"
  ProdutoRepository ...... create, loadFromId, loadAll, update, delete
  ProdutoRepositoryTest .. testes JUnit 5 das operacoes CRUD
  DemonstracaoCrud ....... criar(), consultar(), atualizar(id), excluir(id)
  ConsultaSQL ............ confere a tabela com SQL puro (sem ORM)

Relatorio completo com os prints: README.md
