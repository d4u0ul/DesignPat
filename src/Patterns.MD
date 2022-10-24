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

Há tbm um cenário q é um pouco mais complicado onde vc pode ter uma hierarquia de objetos q vc quer criar e vc quer ter uma hierarquia correspondente de factories relaciandas a esses objetos e isto é representada pela ***Abstract Facotory***


Mas o que é uma Factory?

***Factory é um componente responsável unicamente pela inteira criação de objetos***

Mas onde que o Factory pode ser útil?

Digamos que queremos inicializar uma classe ponto com a posição exata no plano cartesiano e q queiramos ter outro construtor para especificar o ponto no plano polar


    class Point
    {
        private double x, y;
        
        protected Point(double x, double y)
        {
            this.x = x;
            this.y = y;
        }
        
        public Point(double a, double b, // names do not communicate intent CoordinateSystem cs)
        {
            switch (cs)
            {
                case CARTESIAN:
                    this.x = a;
                    this.y = b;
                    break;
                case POLAR:
                    this.x = a * Math.cos(b);
                    this.y = a * Math.sin(b);
                    break;
            }
        }
    }

Os dois construtores tem 2 argumentos double, mas quando chamamos o construtor com 2 parâmetros double qual queremos chamar???
Parar resolver, podemos especificar na construção um termo padrão q defina se queremos um construtor cartesiano ou POlar e modificamos o construtor

    enum CoordinateSystem
    {
        CARTESIAN,
        POLAR
    }

    class Point
    {
        private double x, y;

        public Point(double a, double b, // names do not communicate intent CoordinateSystem cs ==> CARTESIAN/POLAR )
        {
            switch (cs)
            {
                case CARTESIAN:
                    this.x = a;
                    this.y = b;
                    break;
                case POLAR:
                    this.x = a * Math.cos(b);
                    this.y = a * Math.sin(b);
                    break;
            }
        }
    }

Até agora tudo bem, mas as coisas podem ficar mais complicadas. Aqui temos uma lógica estranha no construtor e ele não está ão gerenciável já que as pessoas que vão usá-lo ficam obrigadas a escolher entre CARTESIAN ou POLAR, o que nos obrigaria a escrever uma documentação explicando tudo isso. Um jeito de resolver isso seria modificar o nome do construtor de uma maneira que fosse  instintivo reconhecer o que se está sendo construído, mas sem usar o overloading. Mas como fazer isso?
podemos explicitar colcoando o construtor em private impedindo que ele seja diretamente instanciado e através de métodos static instanciá-lo de aconrdo com o nome do método chamado como newCartesianPoint e newPolarPoint


    class Point
    {
        private double x, y;

        private Point(double a, double b) //construtor private fornçando a construção pelos pontos státicos
        {
            this.x = a;
            this.y = b;
        }
        public static Point newCartesianPoint(double x, double y)
        {
            return new Point(x,y);
        }
        
        public static Point newPolarPoint(double rho, double theta)
        {
            return new Point(rho*Math.cos(theta), rho*Math.sin(theta));
        }
    }
    class Demo
    {
        public statiic void (String[] args)
        {
            Point point = Point.newPolarPoint(2,3);
        }
    }

Vemos q static Methods são a maneira mais fácil de vc implementar o Factory.

Agora, vamos supor q vc tem vários destes factory methods, vc vai precisar juntá-los de alguma maneira e como agrupá-los?
Uma das maneiras mais simples é coloc=a-los em uma classe separada e isso é o que precisamente cria uma Factory. Para os GoF isso não é a definição de uma (***FACTORY***) fábrica, só existem para eles o factory method e o abstract factory, mas o Factory pe o núcleo do padrão por baixo dessa ideia inteira.
Então podemos criar a classe q será responsável apnas pela criação de objetos ponto que é uma fábrica de pontos ou ***PointFactory***  e aqui temos o nosso primeiro problema - se passamos os métodos que criam os pontos para a class ***PointFactory*** eles dão erro pq o construtor de Point está privado e se mudamos o construtor de Point para public de novo o usuário da classe pode instanciá-lo diretamente ficando 2 caminhos distintos para a criação do objeto, o q não necessariamente estaria errado, pois dependeria da regra de negócio do que está sendo construído.

Todavia, se queremos forçar o caminho único que é pela construção via a ***PointFactory***, devemos fazê-la acessar o Construtor privado de Point e a única maneira disso acotecer é trazendo-a para dentro de ***Point*** (o q nos permie até trocar o nome dela apenas para ***Factory***), mais ou menos assim:

    class Point
    {
        private double x, y;

        private Point(double a, double b) //construtor private fornçando a construção pelos pontos státicos
        {
            this.x = a;
            this.y = b;
        }
        
        public static class Factory // perceba que precisamos a class de uma maneira static
        {
            public static Point newCartesianPoint(double x, double y)
            {
                return new Point(x,y);
            }
            
            public static Point newPolarPoint(double rho, double theta)
            {
                return new Point(rho*Math.cos(theta), rho*Math.sin(theta));
            }
        }
    }
    class Demo
    {
        public statiic void (String[] args)
        {
            Point point = Point.Factory.newPolarPoint(2,3);
        }
    }


Por fim, falaremos sobre a abstract factory. Uma abstract Factory é essecialmente um padrão o qual faz uma correspondência entre a hierarquia dos objetos que vc têm. Por exemplo, digamos que temos uma máquina de bebidas e temos uma interface HotDrink que é implementada pelas classes coffee e Tea. Nesse caso, podemos fazer a correnspondência entre hierarquia de objetos em uma hierarquia de ***factory*** .

    interface IHotDrink
    {
        void consume();
    }
    
    class Tea implements IHotDrink
    {
        @Override
        public void consume()
        {
            System.out.println("This tea is nice but I'd prefer it with milk.");
        }
    }
    
    class Coffee implements IHotDrink
    {
        @Override
        public void consume()
        {
            System.out.println("This coffee is delicious");
        }
    }

    interface IHotDrinkFactory
    {
        IHotDrink prepare(int amount);
    }
    
    class TeaFactory implements IHotDrinkFactory
    {
        @Override
        public IHotDrink prepare(int amount)
        {
            System.out.println(
    
            "Put in tea bag, boil water, pour " + amount + "ml, add lemon, enjoy!"
            );
            return new Tea();
        }
    }
    
    class CoffeeFactory implements IHotDrinkFactory
        {
        @Override
        public IHotDrink prepare(int amount)
        {
            System.out.println(
            "Grind some beans, boil water, pour " + amount + " ml, add cream and sugar, enjoy!"
            );
            return new Coffee();
        }
    }

Veja que as classes não compartilham a mesma classe mãe, mas compartilham a mesma interface. Agora precisamos de uma HotDrinkMachine


#### RESUMO

Se vc tem uma hierarquia de tipos, vc pode ter uma hierarquia correspondente de  factory relacionadas aos tipos e quando vc as usa com o polimorfismo para operar essas factories vc etm o asbtract factory design pattern.

- A factory method is a static method that creates objects
- A Facotory can take care of object creation
- A Factory can be external or reside inside the object as an inner class
- Hierarchies of factories can be used to create related objects


## PROTOTYPE 0 When it's easier to copy an existing object to fully initialize a new one

### MOTIVATIONS

Objetos complicados como carros, por exemplo não são desenhados do zero, se pensarmos que a qlq momento um Factory faz um novo modelo de um carro do zero um novo model, seria um erro pegar o design existente de um carro ou iphone e fazê-lo do zero. O normal é pegar o modelo antigo e mudá-lo e colocar alguma coisa a mais fazendo-os melhor e mais eficientes e o PROTOTYPE design tem exatametne essa mesma abordagem, ou seja o prototype é um objeto existente que pode ser parcial ou completamente construído. Se vc já desenvolveu algum design complexo e ele já está definido pode ser muito difícil fazer um novo totalmente do zero, se não for tão complexo, vc pode usar por exemplo um Builder. Mas se vc faz algumas variações, vc precisa fazer uma cópia(clone) do prototipo e customizá-lo, n]ao apenas copiando o obejto, mas tbm todas as suas referências fazendo isso recursivamente.

Ebtão, nós temos que prover uma api, por exemplo uma factory que faça o clone de objetos existentes um caso de conversão.

Logo, ***prototype é um parcial ou completo objeto inicializado que vc copia(clone) e usa.***


Há alguns conceitos do prototype q ainda não foram discutidos, mas que tem a ver com a ideia de que prototype é tudo sobre copiar um objeto.

Mas como assim copiar? Digamos q queremos construir uma classe q guarda informações de um endereço e outra de uma pessoa com construtores e o toString

    class Address  {
        public String streetName;
        public int houseNumber;
    
        public Address(String streetName, int houseNumber)
        {
            this.streetName = streetName;
            this.houseNumber = houseNumber;
        }
        
        public String toString()
        {
            return "Address{" +
                "streetName='" + streetName + '\'' +
                ", houseNumber=" + houseNumber +
                '}';
        }
        
    }

    class Person 
    {
        public String [] names;
        public Address address;
        
        public Person(String[] names, Address address)
        {
            this.names = names;
            this.address = address;
        }
        
        public String toString()
        {
            return "Person{" +
                "names=" + Arrays.toString(names) +
                ", address=" + address +
                '}';
        }
    }

    class Demo
    {
        public static void main(String[] args)
        {
            Person john = new Person(new String[]{"John","Smith"}, new Address("London Road", 123));
        }
    } 

Até agora tudo certo, mas digamos q agora queremos criar uma pessoa que é a vizinha de porta de john o que podemos fazer é copiar a chamada acima, colar e modificar os nomes, mas isso não é tão simples quando usamos o operador '='

    ...Código de cima ...
    class Demo
    {
        public static void main(String[] args)
        {
            Person john = new Person(new String[]{"John","Smith"}, new Address("London Road", 123));
            Person jane = john;
            jane.names[0] = "jane";
            jane.address.houseNumber = 124;
            System.out.println(john);
            System.out.println(jane);
        }
    } 

A impressão vai sair igual para os dois pois eles estão apontando para a referência do tipo Person, ou seja o mesmo lugar na memória o q é um erro crasso.
era bom q pudéssemos utilizar um método que clonasse o objeto john, mas modificando a referência do apontamento na memória e depois só mudássemos os valores das variáveis.

    ...Código de cima ...
            Person jane = john.clone();
    ...

Primeiramente, nós podemos fazer uma interface(cloneable) q faça um contrato exigindo a criação do método clone para as classes que desejem poder serem clonadas.

    class Address implements Cloneable // a interface é mais dica de que este objeto pode ser clonável
    {
        ...Cód...
    }

Mas, definir a implementação de Cloneable não especifica se temos uma DeepCopy ou uma ShallowCopy(rasa).

OBS
- ShallowCopy é no nosso exemplo ao atribuir john a jane, apontameos para a mesma referência, não apenas de pessoa, mas tbm para a referência de adress e isso é o shallowcopy ou seja quando vc simplesmente pega as referências e copia as referências. O que não nos ajuda pq modificando os parâmetros de um objeto vc modifica automaticamente o do outro.

Depois devemos definir como executar a clonagem.
Lembre-se que todo objeto e em java tudo é um objeto então todas as classes implementam clone de uma maneira ou de outra  e por isso podem na verdade sobrescrever(@override) a implementação de um clone e automaticamente throws uma exception de CloneNotSupportException então vc pode jogar um exceção especificando que este objeto não pode ser clonado.

    class Address implements Cloneable // a interface é mais dica de que este objeto pode ser clonável
    {
    ...Cód...

        @override
        protected Object clone() throws CloneNotSupportedException
        {
            return super.clone()
        }
    }


Um dos problemas aqui é que a implementação de clone é protected por padrão, então basicamente não é disponível para todo mundo usar, aí a gnt pensa q é só mudar para public, já que sou eu que estou implementando. Além disso, em vez de chamarmos o super.clone() nós podemos replicar isto como endereço invocando uma instancia de address com streetName e HouseNumber

    class Address implements Cloneable // a interface é mais dica de que este objeto pode ser clonável
    {
    ...Cód...

        @override
        public Object clone() throws CloneNotSupportedException
        {
            return Addrress(streetName, HouseNumber)
        }
    }

Esta cópia executada acima é uma DeepCopy, onde pegamos todos os dados e escrevemos por cima. 

E agora, faremos o mesmo para Person, mas prestando atenção q os parâmetros names e address são referências:

    class Person implements Cloneable // a interface é mais dica de que este objeto pode ser clonável
    {
    ...Cód...

        @override
        public Object clone() throws CloneNotSupportedException
        {
            return Person(names, address) //errado está apontando para a mesma referência
        }
    }

primeiro, devemos resolver o throws dentro da classe Demo e o cast do john.clone() para q ele seja uma nova Person(visto q a API retorna um objeto clone em vez de um Address);

    ...Código de cima ...
    class Demo
    {
        public static void main(String[] args) throws Exception
        {
            Person john = new Person(new String[]{"John","Smith"}, new Address("London Road", 123));
            Person jane = (Person) john.clone();
            jane.names[0] = "jane";
            jane.address.houseNumber = 124;
            System.out.println(john);
            System.out.println(jane);
        }
    } 

Além disso, se vc testar vai ver q john e jane tem o mesmo endereço e nome, já que o names e address apontam para o mesmo lugar, apesar da referência person apontar para lugares distintos.

    class Person implements Cloneable // a interface é mais dica de que este objeto pode ser clonável
    {
    ...Cód...

        @override
        public Object clone() throws CloneNotSupportedException
        {
            return Person
                    (
                        names.clone(), // Em Baixo é só clonar , MAS COMO RESOLVER COM UM ARRAY? é só clonar já que o array já vem com essa implementação de clone() por isso não precisa do cast
                        (Address) address.clone() // clone()<-novo apontamento na memória e (Address) casting do tipo que volta como "clone"
                    )
        }
    }

Então, é assimq  basicamente, vc consertar o problema de implementar um obj clonável e expor o clone, Agora, normalmente , implementações de clones não são recomendadas, logo na literatura e nas empresas nunca lhe dizem q vc deve implementar um cloneable pq infelizmente clonar não é um real estado q o método clone faz , então para fazer um deepCopy é melhor usar outros mecanismos que não sejam o cloneable.

Outra maneira é faznendo o que é conhecido como copyCOnstructors que  nada mais é do que criar um outro construtor que recebe um objeto já construido como parâmetro e quando esse construtor é chamado ele é instanciado como new, logo apntando para um novo endereço de memória.

    class Address
    {
        public String streetAddress, city, country;
        
        public Address(String streetAddress, String city, String country)
        {
            this.streetAddress = streetAddress;
            this.city = city;
            this.country = country;
        }
        
        public Address(Address other) // NOVO CONSTRUTOR OUTRO
        {
            this(other.streetAddress, other.city, other.country);
        }
        
        @Override
        public String toString()
        {
            return "Address{" +
            "streetAddress='" + streetAddress + '\'' +
            ", city='" + city + '\'' +
            ", country='" + country + '\'' +
            '}';
        }
    }
    
    class Employee
    {
        public String name;
        public Address address;
        
        public Employee(String name, Address address)
        {
            this.name = name;
            this.address = address;
        }
        
        public Employee(Employee other) // NOVO CONSTRUTOR OUTRO
        {
            name = other.name;
            address = new Address(other.address); //NOVA INSTÂNCIA DE ADDRESS RECEBENDO UM OBJETO JÁ COSNTRUÍDO
        }
        
        @Override
        public String toString()
            {
            return "Employee{" +
            "name='" + name + '\'' +
            ", address=" + address +
            '}';
        }
    }
    
    class CopyConstructorDemo
    {
        public static void main(String[] args)
        {
            Employee john = new Employee("John",
            new Address("123 London Road", "London", "UK"));
            
            //Employee chris = john;
            Employee chris = new Employee(john); //NOVA INSTÂNCIA DE PERSON RECEBENDO UM OBJETO JÁ COSNTRUÍDO
            
            chris.name = "Chris";
            System.out.println(john);
            System.out.println(chris);
        }
    }

Agora, veremos a copia sob serialização

Um dos grandes problemas que encontramos quando olhamos um construtor copy é que vc tem q construir um copy constructor para cada tipo q vc tenha e se vc tem uma hierarquia de 20 tipos distintos vc tem q fazer 20 implementações diferentes pq um destes pode acabar não sendo copiado direito, ele será uma shallowcopy. 

Quando vc tem uma hierarquia de tipos, como copiar os objetos sem ter q criar toda a gama de construtores necessários?

Uma das maneira é com serializaçao, vamos suport q temos uma class Foo que implementa Serializable interface

    class Foo implements Serializable
    {
        public int stuff;
        public String whatever;
        
        public Foo(int stuff, String whatever)
        {
            this.stuff = stuff;
            this.whatever = whatever;
        }
        
        @Override
        public String toString()
        {
            return "Foo{" +
            "stuff=" + stuff +
            ", whatever='" + whatever + '\'' +
            '}';
        }
    }

    class Demo
    {
        public static void main(String[] args)
        {
            Foo foo = new Foo(42,"life);
            
            // SerializationUtils.roundtrip()este método serializa o objeto e deserializa fazendo a cópia completa de cada elemento, então se o seu objeto tem outros objetos elestbm serão serializados e deserializados e vc tem uma nova copia como uma cópia  pelo valor
            
            Foo foo2 = SerializationUtils.roundtrip(foo); 

            System.out.println(foo);
            System.out.println(foo2);
        }
    {

E a impressão dos 2 foo saem corretas e obviamente esta é uma simples abordagem q vc pode fazer e existem varios tipos de serializações há bibliotecas que usam reflections em vez e se a biblioteca usa reflection vc não precisa implementar o Serializable