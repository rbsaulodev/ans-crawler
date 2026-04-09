# ANS Web Crawler (Nível ZG)

Este projeto é uma automação em Java para extração de dados e documentos do portal da Agência Nacional de Saúde Suplementar (ANS), referente ao padrão TISS.

## Funcionalidades

O bot executa as seguintes tarefas automatizadas:

* **Task 1:** Navegação até a versão mais recente do Padrão TISS e download do Componente de Comunicação.
* **Task 2:** Extração da tabela de Histórico de Versões (competências desde Jan/2016) para um arquivo .csv.
* **Task 3:** Navegação e download da Tabela de Erros no envio para a ANS.

## Estrutura de Downloads

Os arquivos são organizados automaticamente no diretório do projeto:

* ./Downloads/Arquivos_padrao_TISS/
* ./Downloads/Tabela_Erros/
* ./Downloads/historico_versoes.csv

## Tecnologias

* Java 17+
* Jsoup: Para parsing de HTML e navegação web.
* Maven: Para gerenciamento de dependências.

## Como Executar

1. Certifique-se de ter o Maven instalado.
2. Clone o repositório.
3. Execute a classe Main.java.
