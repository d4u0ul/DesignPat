# Solid Design Principles -  Useful principles of Objected Oriented Design

There are a set of Design principles(5) related to software Design introduced by RObert S.Martin(UncleBob)

## SRP - Single Responsability Principle 

A classe should have just a single reason to change or one primary responsability.
When you take a lot of responsabilities in some class, you created a "GodObject"

Let's suppose that i decide to make a class called journal and this class is going to store my most intimate thoughts.

So, what is the responsability of the journal?

Well, obviously it is to allow some sort of acess to the entries and maybe remove entries as well. 

to Look: Journal.java

the takeway from this example is that SRP basically tries to force you to put a just one responsability into any single class. 


## OCP - OPEN CLOSE PRINCIPLE

É um desing pattern utilizado pelas enterprises(Specification) não feito pela gangue of four

vamos supor que queiramos fazer um sistema que nos permite pesquisar coisas para comprar na amazon

pesquisamos então pela :

- Cor  - vermelho, verde e azul
- Tamanho - pequeno meio grande e enorme

Então seu chefe pede para vc fazer um filtro pela cor

    package com.activemesa.solid.ocp;

    import java.util.List;
    import java.util.stream.Stream;

    //enumerator de cores
    enum Color
    {
        RED, GREEN, BLUE
    }
    
    //enumerator de tamanho
    enum Size
    {
        SMALL, MEDIUM, LARGE, YUGE
    }
    
    //classe dos produtos
    class Product
    {
        public String name;
        public Color color;
        public Size size;
        
        public Product(String name, Color color, Size size) 
        {
        this.name = name;
        this.color = color;
        this.size = size;
        }
    }

    //Filtro pela cor pedido pelo chefe
    class ProductFilter
    {
        public Stream<Product> filterByColor(List<Product> products, Color color)
        {
            return products.stream().filter(p -> p.color == color);
        }
    }

Agora, seu chefe pede que vc faça um filtro pelo tamanho, então vc copia o filtro de cor e o cola modificando o de cor para o tamanho


    class ProductFilter
    {
        //Filtro pela cor pedido pelo chefe
        public Stream<Product> filterByColor(List<Product> products, Color color)
            {
                return products.stream().filter(p -> p.color == color);
            }
        //Filtro pelo tamanho pedido pelo chefe
        public Stream<Product> filterBySize(List<Product> products, Size size)
            {
                return products.stream().filter(p -> p.size == size);
            }
    }

Agora seu chefe pede para que vc faça um filtro de tamanho e cor

    class ProductFilter
    {
        //Filtro pela cor pedido pelo chefe
        public Stream<Product> filterByColor(List<Product> products, Color color)
            {
                return products.stream().filter(p -> p.color == color);
            }
        //Filtro pelo tamanho pedido pelo chefe
        public Stream<Product> filterBySize(List<Product> products, Size size)
            {
                return products.stream().filter(p -> p.size == size);
            }
        //Filtro pelo tamanho&Cor
        public Stream<Product> filterbySizeAndColor(List<Product> products, Color color, Size size)
            {
                return products.stream().filter(p -> p.size==size && p.color == color   )
            }
    }

Ok, mas qual o problema deste código???

O problema é  que tá tudo certo e fácil quando temos 2 critérios, mas quando temos 3 critérios teríamos 7 métodos.

O problema que o OCP tenta resolver é dizer que vc não deveria ficar indo no código que já está escrito e modificá-lo sempre que vc quiser add uma nova feature, visto que o código que já está lá implementado já foi testado e está em produção e não devemos ficar mexendo nele.

Logo, 

*O Código deve ficar sempre aberto para extensão, mas deve ficar fechado para modificações pq ele jã foi testado e entrou no ambiente de produção e/ou foi entregue para os clientes que o estão usando*

Então devemos dar algum jeito de add mais features sem necessariamente ficar mexendo no código, mas como fazer isso qual a arquitetura e o que esse pattern specification ajuda nisso???

    class OCPDemo
    {
        public static void main(String[] args) {
            Product apple = new Product("Apple", Color.GREEN, Size.SMALL);
            Product tree = new Product("Tree", Color.GREEN, Size.LARGE);
            Product house = new Product("House", Color.BLUE, Size.LARGE);

            List<Product> products = List.of(apple, tree, house); //lista de produtos para filtrar
            
            ProductFilter pf = new ProductFilter();

            pf.filterByColor(products, Color.GREEN).foreach(p -> System.out.println(" - " +p.name+ " is green"));
        }
    }
            
Primeiro de tudo vamos ver 2 novas interfaces:
Specifications que recebe um genérico T(Pode filtrar por qualquer coisa) e tem 1 método boolean que verifica se o critério é satisfeito isSatisfied() 


    interface Specification< T > //interface com generics(Recebe qualquer objeto)
    {
        boolean isSatisfied();
    }

A segunda interface se chama Filter<T> e também recebe um generics  que pode ser qualquer coisa não apenas um produto, mas como no nosso main estamo trabalhando com produtos , ela será obviamente um produto, mas o filter pode trabalhar com qualquer coisa. Em resumo, teremos apenas uma função Filter q  retornará um stream de objetos <T> e recebe uma lista de <T> e uma Specification de <T> definida com a ajuda da interface acima que satisfaça o que queremos filtrar.

    interface Filter<T>
    {
        Stream<T> filter(List<T> items, Specification<T> spec);
    }

Agora, supondo que queremo criar uma especificação de cor, então:

    class ColorSpecification implements Specification<Product>
    {
        private Color color;
        public ColorSpecification(Color color) 
        {
            this.color = color; //Parâmetro da cor q será especificada na construção da classe especificação pela cor   
        }
        
        @override
        public boolean isSatisfied(Product item)
        {
            return item.color == color;
        }
    }

O Size especification é idêntico, mas trocando o size pela cor.

Agora, precisamos de um novo filtro q tome conta dos filtro que especificados no Specifications , o que é fácil é só fazer uma classe de betterFIlter que eimplemente Filter<Product> 

    class BetterFilter implements Filter<Product>
        {
            @Override
            public Stream<Product> filter(List<Product> items, Specification<Product> spec) {
            return items.stream().filter(p -> spec.isSatisfied(p));
        }
    }

E no main:

    class OCPDemo
    {
        public static void main(String[] args) 
        {
            Product apple = new Product("Apple", Color.GREEN, Size.SMALL);
            Product tree = new Product("Tree", Color.GREEN, Size.LARGE);
            Product house = new Product("House", Color.BLUE, Size.LARGE);

            List<Product> products = List.of(apple, tree, house); //lista de produtos para filtrar
            
            ProductFilter pf = new ProductFilter();

            pf.filterByColor(products, Color.GREEN).foreach(p -> System.out.println(" - " +p.name+ " is green"));


            ///////////////////////////////// Filtro usando o Specifications com o Filter
            
            BetterFilter bf = new BetterFilter();
            System.out.println("Green products (new):");
            bf.filter(products, new ColorSpecification(Color.GREEN)).forEach(p -> System.out.println(" - " + p.name + " is green"));
            
            // Se vc quiser novos filtros vc não precisa voltar no Código e modificá-lo, é só usar herança e a implementação das interfaces que foram criadas
            
            System.out.println("Large products:");
            bf.filter(products, new SizeSpecification(Size.LARGE)).forEach(p -> System.out.println(" - " + p.name + " is large"));
            
            

        }
    }


E como que fazemos para o Size&&Color???

Precisamos fazer um Combinador que vai ser basicamente uma specification que combina 2 specifications, logo deve ser implementada uma classe dessas

    class AndSpecification<T> implements Specification<T>
        {
        private Specification<T> first, second; //2 especificações
        
        public AndSpecification(Specification<T> first, Specification<T> second) { //construtor
            this.first = first;
            this.second = second;
        }
        
        @Override
        public boolean isSatisfied(T item) { // mudança no isSatisfied() q deve checar ambos
            return first.isSatisfied(item) && second.isSatisfied(item);
        }
    }

Aí fica fácil no main:

    class OCPDemo
    {
        public static void main(String[] args) 
        {
            ...
            Código anterior
            ...
            
            // Se vc quiser novos filtros vc não precisa voltar no Código e modificá-lo, é só usar herança e a implementação das interfaces que foram criadas
            
            System.out.println("Large products:");
            bf.filter(products, new SizeSpecification(Size.LARGE)).forEach(p -> System.out.println(" - " + p.name + " is large"));
            
            System.out.println("Large blue items:");
            bf.filter(products, new AndSpecification<>( new ColorSpecification(Color.BLUE), new SizeSpecification(Size.LARGE))).forEach(p -> System.out.println(" - " + p.name + " is large and blue"));    
            

        }
    }

E conseguimos assim respeitar o OCP - *O Código deve ficar sempre aberto para extensão, mas deve ficar fechado para modificações pq ele jã foi testado e entrou no ambiente de produção e/ou foi entregue para os clientes que o estão usando*

## Liskov Substitution Principle(LSP)

Foi desenvolvido por Barbara Liskov

A ideia do risco do principio de Sustituição é que vc deveria se habilitado a substituir uma subclass pela classe base sem quebrar o sistema.

E como violar esse princípio? vamos criar a classe retângulo:

    class Rectangle
        {
        protected int width, height;
        
        public Rectangle() 
        {
        }
        
        public Rectangle(int width, int height) 
        {
            this.width = width;
            this.height = height;
        }
        
        public int getWidth() 
        {
            return width;
        }
        
        public void setWidth(int width) 
        {
            this.width = width;
        }
        
        public int getHeight() 
        {
            return height;
        }
        
        public void setHeight(int height) 
        {
            this.height = height;
        }
        
        public int getArea(){ return width*height; }
        
        @Override
        public String toString() 
        {
            return "Rectangle{" +
            "width=" + width +
            ", height=" + height +
            '}';
        }
    }

Agora, vamos criar um filha quadrado que é um retângulo de lados iguais:

    class Square extends Rectangle
        {
            public Square() {
            }
        
            public Square(int size) {
            width = height = size;
            }
        
            @Override
            public void setWidth(int width) {
                super.setWidth(width);
                super.setHeight(width);
            }
        
            @Override
            public void setHeight(int height) {
                super.setHeight(height);
                super.setWidth(height);
            }
    }

Dá impressão que está tudo ok, vamos ver no main:

    class LSPDemo
    {
        // maybe conform to ++
        static void useIt(Rectangle r)
        {
            int width = r.getWidth();
            r.setHeight(10);
            //A ÁREA DEVERIA SER WIDTH*10
            System.out.println("Expected area of " + (width*10) + ", got " + r.getArea());
        }
        
        public static void main(String[] args) 
        {
            Rectangle rc = new Rectangle(2, 3);
            useIt(rc);
            
            //Até agora, tudo certo o resultado dá Expected area of 20, got 20
            //LSP diz que se eu tentar com a classe filha quadrado, tudo tem q funcionar, vamos testar:
    
            Rectangle sq = new Square(); // todo quadrado é um retângulo
            sq.setHeight(5);
            useIt(sq);

            // A implementação acima é válida, já que rectangle é mãe de square, não tem nada de errado aqui, mas quando jogamos o output temos
            // Expected area of 50, got 100, isso pq a linha 343 (r.setHeight(10);) só faz sentido para um retângulo, mesmo os métodos funcionando perfeitamente eles violam o LSP
        }
    }

Como resolver esse problema? há várias maneiras simples de definir se um retângulo é um quadrado ou não. Por exemplo poderíamos ter um public boolean isSquare() que verifica se os lados são iguais. Mas, se vc quiser explicitamente construir retângulos e quadrados, então vc deveria usar um design pattern chamado factory pattern. Vamos ver como que ele funciona:

    class RectangleFactory
    {
        //Primeiro, vc faz uma classe tipo RectangleFactory e nela vc instancia uma função static newRectangle que basicamente cria e retorna um objeto rectangle.
        public static Rectangle newSquare(int side)
        {
            return new Rectangle(side, side);
        }
        //Depois, vc cria um novo método static newSquare que basicamente cria e retorna um Rectangle com as características do quadrado
        //OBS->Acredito que deve se retornar um new Square, visto q como queremos square da maneira que está não temos acesso aos métodos específicos de square, ficando >return new Square(width, height);<
        public static Rectangle newRectangle(int width, int height)
        {
            return new Rectangle(width, height);
        }
    }

## Interface Segregation Principle

É basicamente uma recomendação de como dividir as interfaces do seu sistema em pequenas interfaces

vamos pensar que queremos criar um class documentos e uma interface Machine para um objeto impressora multifuncional



    class Document
    {
    }
    
    interface Machine
    {
        void print(Document d);
        void fax(Document d) throws Exception;
        void scan(Document d) throws Exception;
    }
    
    // ok if you need a multifunction machine
    class MultiFunctionPrinter implements Machine
    {
        public void print(Document d)
        {
            //ensina como imprimir
        }
        public void fax(Document d)
        {
            //ensina como mandar fax
        }
        
        public void scan(Document d)
        {
            //ensina como scannear
        }
    }

Até agora tudo certo(So far, so good), mas digamos que alguém q vai usar a interface Machine quer desenvolver um sistema de uma impressora antiga que só imprime

    class OldFashionedPrinter implements Machine
    {
        public void print(Document d)
        {
            //ensina como imprimir 
        }
        
        public void fax(Document d) throws Exception
        {
            //não ensina pq a impressora não tem
            throw new Exception();
        }
        
        public void scan(Document d) throws Exception
        {
            //não ensina pq a impressora não tem
            throw new Exception();
        }
    }

Aqui é onde o problema começa, o que fazemos com os outros métodos que não queremos implementar nada? deixá0los vazios trará problemas quando algum usuário da classe tentar utilizar os métdos não implementados. Uma outra alternativa é forçar uma exceção com o throw Exception q pode ser algum especial. Então, vc quer mexer na interface, mas não pode pq vc não pode controlá-la. Então vc não pode propagar a especificação de alguma exceção pq vc não é dono do código fonte da itnerface. 

Assim ,a palavra segregation basicamente significa colocar partes em diferentes categorias ou locais. Isto é o que devemos fazer colocando-ás em diferentes interfaces:

    interface Printer
    {
        void Print(Document d) throws Exception;
    }
    
    interface IScanner
    {
        void Scan(Document d) throws Exception;
    }

Aqui podemos então, instanciar uma impressora que somente imprime:

    class JustAPrinter implements Printer
    {
        public void Print(Document d)
        {
            //ensinar como imprimir
        }
    }

Esta abordagem traz a ideia de princípio YAGNI = You Ain't Going To Need It, o que siginifica , se vc só precisa imprimir vc deve implemetar uma interface que somente imprime

Se queremos uma fotocópia implementamos printer e scanner

    class Photocopier implements Printer, IScanner
    {
        public void Print(Document d) throws Exception
        {
            throw new Exception();
        }
        
        public void Scan(Document d) throws Exception
        {
            throw new Exception();
        }
    }

Mas, e se quisermos colocar várias funções das interfaces? é só criar  uma nova interface que extenda todas as interfaces que queremos as funções

    interface MultiFunctionDevice extends Printer, IScanner //
    {

    }

    class MultiFunctionMachine implements MultiFunctionDevice
    {
        // compose this out of several modules
        private Printer printer;
        private Scanner scanner;
    
        public MultiFunctionMachine(Printer printer, IScanner scanner)
        {
            this.printer = printer;
            this.scanner = scanner;
        }
        
        @override
        public void Print(Document d) throws Exception
        {
            printer.print(d);
        }
    
        @override
        public void Scan(Document d) throws Exception
        {
            scanner.scan(d);
        }
    }

Um pattern que será falado lá na frente que usa isso é o Decorator o que sigiinifica que vc pode reusar a funcionalidade de uma printer tendo um scanner se vc já tem essas coisas prontas para construir a sua impressora multifuncional.

## Dependency injection principle DIP

Diferente do que o nome diz, ela não está diretamente ligada com a ideia de injeção de dependências, é parecido, mas não são a mesma coisa. Eu vou escrever para ler pq não é muito intuitivo.

O DIP é dividido em 2 partes:

### 1-*High-level modules não deveriam depender de low-level modules, mas ambos deveriam depender de abstrações*
    - Abstrações são ou classes abstratas ou interfaces - ou seaja a assinatura ou açguma coisa que performa uma operação em particular mas vc não trabalha com a implementação concreta daquilo
    - 
### 2-*Abstrações não deveriam depender de detalhes, mas detalhes deveriam depender de abstrações*
    - Se vc pode usar interfaces em classe abstratas em vez de usar em classes concretas pq obviamente o benefícion aqui é vc pode substituir uma implemetação pela outra sem quebrar nada

Para entender melhor o que seria, high and low level modules, vamos imaginar um sistema que mapeia o tipo de relação entre pessoas 

    enum Relationship
        {
            PARENT,
            CHILD,
            SIBLING
        }
        
        class Person
        {
            public String name;
            // dob etc.
        
        
            public Person(String name) {
            this.name = name;
        }
    }


Então o que queremos é algum modelo de relação entre duas pessoas distintas e para isso devemos fazer uma classe

    class Relationships 
    {
        //vamos usar triplette que é uma tupla com 3 elementos
        private List<Triplet<Person, Relationship, Person>> relations;
        
        //implementando o get
        public List<Triplet<Person, Relationship, Person>> getRelations() 
        {
            return relations;
        }
        //colocando uma função que adiciona relaões entre pessoas
        public void addParentAndChild(Person parent, Person child)
        {
            relations.add(new Triplet<>(parent, Relationship.PARENT, child);
            relations.add(new Triplet<>(child, Relationship.CHILD, parent);
        }

        //como fazer um pesquisa nessa lista relations? para isso faremos uma nova classe Research
    }

Então implementando Research, poderemos verificar a questão de high-level models não depender de low-level models

    class Research
        {
        public Research(Relationships relationships)
        {
            // high-level: find all of john's children
            List<Triplet<Person, Relationship, Person>> relations = relationships.getRelations();
            relations.stream().filter(x -> x.getValue0().name.equals("John") && x.getValue1() == Relationship.PARENT)
            .forEach(ch -> System.out.println("John has a child called " + ch.getValue2().name));
        } 
    }

Testando no main:

    class DIPDemo
        {
        public static void main(String[] args)
        {
            Person parent = new Person("John");
            Person child1 = new Person("Chris");
            Person child2 = new Person("Matt");
        
            // low-level module
            Relationships relationships = new Relationships();
            relationships.addParentAndChild(parent, child1);
            relationships.addParentAndChild(parent, child2);
        
            new Research(relationships);
        }
    }

Tudo funciona, mas temos um problema: Estamo expondo a lista de relacionamentos para todos,  
Em nosso exemplo a class Relationships é low-level model pq está relacionado a um storage de dados, ou seja ele simplemente provê uma lista ou mantém essa lista e nos dá algum tipo de acesso a ela. Ela não têm nenhuma regra de negógio, é um simples manipulador da lista e essa é a responsabilidade dela. Por outro lado a classe Reasearch é uma Heigh-level model pq ela nos permite fazer um grupo de operações com estes low-level constructs, isto q high-level não se importa com a implementação do storage, mas sim com uma pesquisa e implementação das regras de negócio. Ele quer saber quem é o pai de quem e o filho de quem na história .

*Agora lembre-se, a primeira parte do principio de inversão de dependência diz que high-level modules não deveriam depender de low-level modules, mas é o que temos no construtor de Research e a segunda parte diz que em vez disso, vc deveria depender de abstrações  *

Logo primeiro de tudo, vamos introduzir uma abstração como uma interface

    interface RelationshipBrowser
    {
        List<Person> findAllChildrenOf(String name);
    }

e colocamos o low-level class Relationships module como dependente da abstração e colocamos o Research high level dentro do RelationShips(low-level)

    class Relationships implements RelationshipBrowser
    {
        public List<Person> findAllChildrenOf(String name) {
        
            return relations.stream()
              .filter(x -> Objects.equals(x.getValue0().name, name)
                      && x.getValue1() == Relationship.PARENT)
              .map(Triplet::getValue2)
              .collect(Collectors.toList());
        }
        
        // Triplet class requires javatuples
        private List<Triplet<Person, Relationship, Person>> relations =
        new ArrayList<>();
        
        
        
        public void addParentAndChild(Person parent, Person child)
        {
            relations.add(new Triplet<>(parent, Relationship.PARENT, child));
            relations.add(new Triplet<>(child, Relationship.CHILD, parent));
        }
    }
e mudamos o construtor de Research para deixar de depender de Relationships e começar a depender da abstraction

    class Research
        {
        //Este primeiro construtor pode ser comentado, já que não queremos mais depender de low level modules
        //public Research(Relationships relationships)
        //{
        //    // high-level: find all of john's children
        //    List<Triplet<Person, Relationship, Person>> relations = relationships.getRelations();
        //    relations.stream()
        //    .filter(x -> x.getValue0().name.equals("John")
        //    && x.getValue1() == Relationship.PARENT)
        //    .forEach(ch -> System.out.println("John has a child called " + ch.getValue2().name));
        //}
        
        //Agora dependemos da abstração
        public Research(RelationshipBrowser browser)
        {
            List<Person> children = browser.findAllChildrenOf("John");
            for (Person child : children)
            System.out.println("John has a child called " + child.name);
        }
    }



Antes dependemos demais das SRP.MDimplementações , mas agora dependemos das abstrações e não precisamos mudar nada no main, já que só mudamos os construtores de Research






