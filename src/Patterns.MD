# Gamma categorizations

Design Patterns are typically split into three categories that are called by ***Gamma Categorizations*** pq foi feito por Erich Gamma, um dos GoF

São Categorias:

1. Creational Patterns
   1. Lidam com criação(Construção de objetos)
   2. Explicitamente(Construtor) vs implicitamente (DI,Reflections, etc) - framework de injeção de dependências por exemplo 
   3. Há diferentes maneiras de se incializar um objeto, às vezes é simples WholeSale(Single Statement) e às vezes não piecewise(step-by-step).
4. Structural Patterns
   1. Como o nome sugere, se preocupa com a estrutura (como classes que estão aderentes a interfaces ou outras classes )
   2. Aqui temos muitos patterns que são invólucros que imitam uma interface de uma classe por baixo dos panos 
   3. Geralmente coloca um peso extra na importância de bons designs de API replicando a interface o máximo possível fazendo objetos e APis usáveis para outras pessoas
4. Behavioral Patterns
   1. Aqui os padrões são bem distintos, não há um tema central, normalmente a maioria é unica na sua abordagem o que significa que cada um resolve um problema em particular


## CREATIONAL

### BUILDER - Quando a construção do objeto é um pouco bastante complicada - 

#### Motivação 
-   Alguns objetos são simples e podem ser criados com uma simples chamada de construtor com ou sem argumentos

por exemplo, quando vc quer construir uma string de uma dezena de diferentes peças, o que vc normalmente faz é não chamar um construtor e nem concatenar onde vc tem 1000 sinais de +  , em vez disso, vc constroi um StringBuilder q na verdade faz vc construir um objeto peça por peça (step-by-step). Então ter um objeto com 10 argumentos no construtor não é produtivo pq pessoas cometem erros quando existem muitas variáveis. Mesmo com CodeCompletion nas IDEs são legais, mas não são perfeitos e às vezes podem dar uma falsa sensação de segurança. Então permitir que o usuário crontrua o objeto parte por parte gera uma boa API. Api é que entra o builder pattern que é essecinalmente um separador de componentes.

#### *When piecewise object construction is complicated, provide an API for doing it succintly.*


 Vamos dar uma olhada agora em como que seria um Builder no código BuilderDemo
 
às vezes a construção de um objeto pode ser tão complicada que tenhamos mais de 1 objeto para construir ao mesmo tempo
por exemplo uma classe pessoa que tenha informações de nome e endereço e que exija 2 Builders um para cada. Para resolver essa situação temos um design pattern q é o builder facade que é a união de FACADE e do BUILDER onde basicamente criamos 2 Builders e o passamos para o Builder principal que é a pessoa que interliga esses builders com herança.

#### RESUMO
- Um builder é um componente separado para construção de um objeto
- ELe pode ou dar Construir um Construtor ou retornar um via static function
- para fazer um builder fluente, retuorne com "return this"
- Diferentes faces de um objeto(Diferentes Builders no mesmo objeto) podem ser montados com diferentes builders trabalhando em conjuntodentro da classe base


### Factories - Factory method e Abstract factory

#### Motivação 

- Objet creation logic becomes too convoluted
- Constructor is not descriptive
  - Name mandated by name of containing type
  - cannot overload with same sets of arguments with diferent names
  - can turn into overloading hell
- Whole sale Creation(NON-PIECEWISE, unilike Builder) can be outsourced to

Pensando novamente na criação de objetos e no fato de que algumas vezes a lógica que envolve a criação de um objeto pode ser muito complicada e infelizmente os construtores são limitados em suas capacidades em termos de como eles constroem os objetos. Um dos Problemas é o Nome do Construtor ser o nome do tipo do Container,  na verdade é assim que  LPOO trabalha, mas é um problema pois desde que nós não tenhamos nenhuma flexibilidade do nome, o que acontece é nós não temos nenhuma habilidade para dar dicas adicionais como por exemplo o que os argumentos significam ou não podemos fazer uma sobrecarga(@overload) com a mesma quantidade de argumentos (ex; Construtores de retângulos passando 2ptos cartesianos ou 2 informações de coordenadas polares - isso é possível em algumas outras linguagens como swift e objective C ) então, nós precisaríamos de algo como uma overload house que fabrica diferentes tipos de construtores evitando essa duplicação da lógica  

#### *OBS: Nós não estamos falando mais em piecewise creations como nos Builders, estamos apenas com WholeSale Creations que é a criação do obejto por inteiro de uma única vez*

Aqui pe feita uma única chamada do objeto, mas essa chamada não precisa ser um construtor pode ser alguma coutra coisa e isso é onde o factory patterns começa. 

Então uma das opções é uma função separada ou seja a opção ***Factory Method*** que ´uma função static que pode ter um nome arbitrário, não é restrito como um construtor. Epor ter esse nome qualquer, vc pode ter muitas possibilidades de nomes de argumentos pq vc não precisa fazer overloads, diferente de um construtor.

Também temos a ***Factory*** que não foi mencionada pelo livro do GoF, mas é muito comum de se ver em projetos.

Há tbm um cenário q é um pouco mais complicado onde vc pode ter uma hierarquia de objetos q vc quer criar e vc quer ter uma hierarquia correspondente de factories relaciandas a esses objetos e isto é representada pela Abstract Facotory


Mas o que é uma Factory?

***Factory é um componente responsável unicamente pela inteira criação de objetos***






#### RESUMO