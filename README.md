# DRL Support — plugin de IntelliJ para Drools

Plugin de IntelliJ Platform (IDEA Community/Ultimate) para editar arquivos
`.drl` (Drools Rule Language).

> **Status:** código completo e revisado contra as APIs reais do LSP4IJ e do
> kiegroup/drools-lsp (fontes lidas diretamente no GitHub), mas **não
> compilado neste ambiente** — o sandbox onde foi gerado não tem acesso ao
> Gradle Plugin Portal (`plugins.gradle.org`) nem ao repositório de
> plugins da JetBrains, então não deu pra rodar `./gradlew build` aqui.
> Rode `gradle build` (passo 2 abaixo) na sua máquina como primeiro passo —
> é o teste real.

## Como funciona (duas camadas)

1. **Camada própria, sempre ativa** — `DrlLanguage`/`DrlFileType` + um lexer
   escrito à mão (`lexer/DrlLexerAdapter.kt`) dão syntax highlighting,
   comentar linha/bloco (`Ctrl+/`, `Ctrl+Shift+/`) e correspondência de
   `(){}[]`. Não depende de nada externo.

2. **Camada inteligente, via [LSP4IJ](https://plugins.jetbrains.com/plugin/23257-lsp4ij)** —
   quando o plugin LSP4IJ (Red Hat, gratuito) está instalado, este plugin
   registra o **Drools Language Server** real
   ([kiegroup/drools-lsp](https://github.com/kiegroup/drools-lsp), o mesmo
   que roda por trás da extensão "DRL Editor" do VS Code) como servidor
   LSP para arquivos `*.drl`. Isso traz completion de palavras-chave,
   tipos Java e campos/propriedades, diagnósticos (falta de `end`,
   parênteses desbalanceados, tipos desconhecidos com quick-fix...), hover,
   go-to-definition, find references, rename, outline e inlay hints — tudo
   implementado pelo parser ANTLR4 real da Drools, não por este plugin.

   Essa dependência é **opcional** (`<depends optional="true" ...>`): sem o
   LSP4IJ instalado, o plugin ainda funciona, só sem a parte inteligente.

## Pré-requisitos

- **Para desenvolver/rodar este plugin**: JDK 17+ e o Gradle Wrapper
  (incluso; baixa o Gradle e o IntelliJ Platform sozinho — só precisa de
  rede na primeira execução).
- **Para a parte inteligente (LSP)**: Java 17+ e Maven, para gerar o jar
  do `drools-lsp-server` (ver passo 1 abaixo). Isso é a mesma exigência que
  a extensão do VS Code lista no seu README.

## Passo a passo

### 1. Gerar o jar do Drools Language Server

```bash
git clone https://github.com/kiegroup/drools-lsp.git
cd drools-lsp
mvn -pl drools-lsp-server -am clean package
```

Isso gera
`drools-lsp-server/target/drools-lsp-server-jar-with-dependencies.jar`
(jar "fat", com todas as dependências — é o que a linha de comando `java
-jar` precisa). Guarde o caminho completo desse arquivo.

### 2. Rodar este plugin em uma IDE de teste (sandbox)

Este projeto não vem com o Gradle Wrapper commitado (o `gradlew` baixa o
Gradle certo sozinho, mas isso exige gerar o wrapper uma vez com acesso à
internet). Se você já tem Gradle 8.x instalado, é só:

```bash
cd drl-intellij-plugin
gradle runIde
```

Se preferir usar o wrapper (recomendado para builds reprodutíveis /
CI), gere-o uma vez e depois use `./gradlew`:

```bash
gradle wrapper --gradle-version 8.14.3
./gradlew runIde
```

`runIde` abre uma instância "sandbox" do IntelliJ IDEA Community com o
plugin já instalado. Na primeira execução o Gradle baixa a plataforma
IntelliJ e as dependências (LSP4IJ incluso) — pode demorar alguns minutos.

Isso abre uma instância "sandbox" do IntelliJ IDEA Community com o plugin
já instalado. Na primeira vez o Gradle baixa a plataforma IntelliJ e as
dependências — pode demorar alguns minutos.

Dentro dessa instância sandbox:

1. Instale o **LSP4IJ** via `Settings/Preferences → Plugins → Marketplace`
   (busque "LSP4IJ").
2. Abra `Settings/Preferences → Tools → Drools LSP` e cole, em "Jar do
   servidor", o caminho do
   `drools-lsp-server-jar-with-dependencies.jar` do passo 1.
3. Abra `samples/sample.drl` (incluso neste repositório) — ou qualquer
   projeto com arquivos `.drl` — e confira: highlighting deve funcionar
   imediatamente; completion (`Ctrl+Space` dentro de um `when`), hover e
   diagnósticos devem aparecer assim que o servidor subir (acompanhe em
   `View → Tool Windows → LSP Consoles` se algo não aparecer).

### 3. Empacotar para instalar em uma IDE "de verdade"

```bash
gradle buildPlugin   # ou ./gradlew buildPlugin, se já gerou o wrapper
```

Gera `build/distributions/drl-support-0.1.0.zip`. Em qualquer IntelliJ
Platform IDE: `Settings/Preferences → Plugins → ⚙️ → Install Plugin from
Disk...` e selecione esse zip. Repita os passos 1 e 2 acima (instalar
LSP4IJ, gerar o jar, apontar o caminho em Settings) dentro dessa IDE.

## Configurações (Settings → Tools → Drools LSP)

| Campo | Corresponde a | Padrão |
|---|---|---|
| Jar do servidor | caminho local do jar (obrigatório p/ a parte LSP) | vazio |
| Nível de log | `-Ddrools.lsp.logLevel` | `INFO` |
| Lint: `end` faltando, separador, `;`, parênteses, tipos desconhecidos, estilo MVEL | `-Ddrools.lsp.lint.*` | `warning` (MVEL: `off`) |
| Inlay hints | `-Ddrools.lsp.inlayHints.enabled` | ativado |
| POM(s) Maven | `-Ddrools.lsp.maven.pomPath` — usado pelo servidor para resolver o classpath Java (tipos usados nas regras) | vazio → usa o `pom.xml` da raiz do projeto |

Essas configurações são por projeto (ficam em
`.idea/drlLspSettings.xml`) e são lidas de novo toda vez que o LSP4IJ
reinicia o servidor.

## Internacionalização (i18n)

Todas as strings da UI que vêm do nosso código Kotlin (label da tela de
settings, nomes na tela de cores, mensagem de erro quando o jar não está
configurado etc.) passam por `DrlBundle.message("chave")`
(`DrlBundle.kt`, padrão `DynamicBundle` oficial da JetBrains). O idioma
**padrão é inglês**:
`src/main/resources/messages/DrlBundle.properties`. Também incluí
`DrlBundle_pt_BR.properties` com as mesmas chaves em português — o
`ResourceBundle` padrão do Java escolhe esse arquivo automaticamente
quando a IDE roda sob o locale `pt_BR` (por exemplo, com o Language Pack
correspondente instalado); do contrário, todo mundo recebe o inglês.

Para adicionar outro idioma, basta criar
`messages/DrlBundle_<locale>.properties` com as mesmas chaves — não
precisa mexer em código.

Os textos do `plugin.xml`/`drl-lsp4ij.xml` (nome e descrição do plugin, a
descrição do language server) ficam como literais em inglês direto no XML,
e não passam pelo `DrlBundle`: extension points de terceiros geralmente
não resolvem chaves de resource-bundle automaticamente do jeito que
`<action>` resolve, então não faria diferença em runtime.

## Estrutura do projeto

```
build.gradle.kts, settings.gradle.kts, gradle.properties  — build Gradle
src/main/resources/META-INF/
  plugin.xml            — declaração principal do plugin
  drl-lsp4ij.xml         — extensões LSP4IJ (só carrega se LSP4IJ estiver instalado)
src/main/resources/messages/
  DrlBundle.properties        — strings de UI em inglês (padrão)
  DrlBundle_pt_BR.properties  — strings de UI em português (pt_BR)
src/main/kotlin/dev/leoluzh/drlsupport/
  DrlLanguage.kt, DrlFileType.kt, DrlIcons.kt, DrlBundle.kt
  lexer/                — lexer + tabela de tokens + palavras-chave
  psi/                  — parser mínimo (flat) + ParserDefinition + PsiFile
  highlighting/         — SyntaxHighlighter + ColorSettingsPage
  editor/                — Commenter + BraceMatcher
  settings/              — tela "Tools > Drools LSP" + estado persistido
  lsp/                   — LanguageServerFactory + ProcessStreamConnectionProvider
samples/sample.drl       — arquivo de exemplo para testar
```

## Por que não um parser DRL próprio?

Dava para escrever um parser DRL completo aqui, mas o grupo KIE
(mantenedor do Drools/Kogito) já publica e mantém um — com o parser ANTLR4
real, motor de completion (C3) e todas as validações — como um Language
Server reutilizável (Apache-2.0). Embrulhar esse servidor via LSP4IJ dá
resultado muito mais correto (mesma fonte de verdade usada no VS Code) com
uma fração do código, e continua recebendo melhorias do upstream sem
precisar tocar neste plugin.

## Limitações conhecidas

- O servidor Drools LSP ignora `workspace/didChangeConfiguration`; por
  isso as configurações são passadas como `-D...` na linha de comando e só
  valem a partir do próximo restart do servidor (o LSP4IJ reinicia
  automaticamente quando você muda o jar path; para as outras opções, use
  "Restart" no ícone do servidor em `LSP Consoles` depois de mudar).
- `drools-lsp` está em desenvolvimento ativo (v1.0.x); cheque o repositório
  upstream se algo parar de funcionar depois de uma atualização.
- O highlighting local (camada 1) é léxico, não semântico: colore por tipo
  de token, não por "isso é um tipo Java válido" — isso é papel do LSP.

## Licenças

- Este plugin: seu código é seu para usar/distribuir como quiser.
- [kiegroup/drools-lsp](https://github.com/kiegroup/drools-lsp): Apache
  License 2.0.
- [LSP4IJ](https://github.com/redhat-developer/lsp4ij): Eclipse Public
  License 2.0.
