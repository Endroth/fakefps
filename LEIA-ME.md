# FPS Fake – todas as versões

Comandos (iguais em todas): `/fpsfake on`, `/fpsfake off`, `/fps <min> <max>` (ex.: `/fps 200 300`).
O número muda 1 vez por segundo, andando aos poucos dentro da faixa. É só visual (F3).

## O que tem aqui
| Pasta | Versão | Loader | Estado |
|---|---|---|---|
| prebuilt/fabric-1.21.11 | 1.21.11 | Fabric | jar pronto (o mesmo que já te mandei) |
| fabric-1.20.1 | 1.20.1 | Fabric | código-fonte, NÃO testado |
| fabric-1.16.5 | 1.16.5 | Fabric | código-fonte, NÃO testado |
| forge-1.20.1 | 1.20.1 | Forge | código-fonte, NÃO testado |
| forge-1.16.5 | 1.16.5 | Forge | código-fonte, NÃO testado |
| forge-1.12.2 | 1.12.2 | Forge | código-fonte, NÃO testado |
| forge-1.8.9 | 1.8.9 | Forge | código-fonte, NÃO testado |

## Como gerar os .jar
1. Crie um repositório no GitHub e suba TODO o conteúdo desta pasta (inclusive `.github`).
2. Aba **Actions** → rode o workflow **build** (ou é só dar push).
3. Em cada job concluído, baixe o artefato (o .jar fica em `build/libs`).
   Use o jar que NÃO termina em `-sources` / `-dev`.

Se algum job falhar, abra o log, copie o erro e me mande: cada pasta é um projeto separado.

## Observações
- Fabric 1.21.11 já vem pronto; as outras versões Fabric usam nomes Yarn.
- Forge 1.21.x e Legacy Fabric (1.8.9/1.12.2 com Fabric) não estão incluídos.
- Forge 1.16.5 não tem comando de cliente: o mod lê o chat e cancela `/fps` e `/fpsfake` antes de enviar ao servidor.
