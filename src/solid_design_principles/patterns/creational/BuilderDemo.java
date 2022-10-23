package solid_design_principles.patterns.creational;

import java.util.ArrayList;
import java.util.Collections;

public class BuilderDemo {
    public static void main(String[] args) {
        //digamos que o nosso código deseje imprmir strings que criem um arquivo html
//        String hello = "hello world";
//        System.out.println("<p>"+ hello+ "</p>");
//        //até agora está tudo certo pq o nosso exemplo é pequeno, mas se quiséssemos colocar uma lista de strings que queremos colocar na nossa página html, teríamos que escrever todos as tags e fazer um loop, mas isso não é prático e nem eficiente
//        String [] words = {"hello", "world"};
//        //para isso precisaremos de um BUILDER, o já conhecido StringBuilder. O objeto String BUilder não é chamado com uma simples chamada, mas sim através de várias funções e operações que constrõem o objeto step-by-step
//
//        StringBuilder sb = new StringBuilder(); //INSTÂNCIA DO OBJETO QUE VAI SER CONSTRUIDO AOS POUCOS
//        sb.append("<ul>\n"); // abrindo a lista
//        for(String word: words)
//        {
//            sb.append(String.format("    <li>%s</li>\n",word)); // adding elementos
//        }
//        sb.append("</ul>\n"); // fechando a lista
//        //e no final chamamos uma função particular de cada builder que finaliza a ocnstrução do objeto
//        System.out.println(sb);
//        //Perceba que mesmo q estejamos trabalhando com html, ainda estamos usando Strings, podemos fazer classes ordinárias que guardam tags de html

        // we want to build a simple HTML paragraph
        System.out.println("Testing");
        String hello = "hello";
        System.out.println("<p>" + hello + "</p>");

        // now we want to build a list with 2 words
        String [] words = {"hello", "world"};
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>\n");
        for (String word: words)
        {
            // indentation management, line breaks and other evils
            sb.append(String.format("  <li>%s</li>\n", word));
        }
        sb.append("</ul>");
        System.out.println(sb);

        // ordinary non-fluent builder
        HtmlBuilder builder = new HtmlBuilder("ul");
        builder.addChild("li", "hello");
        builder.addChild("li", "world");
        System.out.println(builder);

        // fluent builder
        builder.clear();
        builder.addChildFluent("li", "hello")
                .addChildFluent("li", "world");
        System.out.println(builder);



    }



}

//Criação de uma classe ordinária que guarda tag html
class HtmlElement
{
    public String name; //exemplo li
    public String text; //exemplo <li>
    public ArrayList<HtmlElement> elements = new ArrayList<>();//Vc pode ter tags com abertura e fechamento de infinitas tags no seu interior
    private  final int indentSize = 2;  //identação
    private final String newLine = System.lineSeparator(); // pulo de linha

    public HtmlElement()
    {
    }
    public HtmlElement(String name, String text)
    {
        this.name = name;
        this.text = text;
    }

    private String toStringImpl(int indent)
    {
        //nível de profundidade de identação
        StringBuilder sb = new StringBuilder();
        String i = String.join("", Collections.nCopies(indent * indentSize, " "));
        sb.append(String.format("%s<%s>%s", i, name, newLine));
        if (text != null && !text.isEmpty())
        {
            sb.append(String.join("", Collections.nCopies(indentSize*(indent+1), " ")))
                    .append(text)
                    .append(newLine);
        }

        for (HtmlElement e : elements)
            sb.append(e.toStringImpl(indent + 1));

        sb.append(String.format("%s</%s>%s", i, name, newLine));
        return sb.toString();
    }
    //Mas quando que a gnt transforma tudo em string? numa classe separada HtmlBuilder (SRP)

    @Override
    public String toString() {return toStringImpl(0);
    }
}

class HtmlBuilder
{
    // A ideia aqui é que temos alguns elementos raizez que quando alguém coloca mais uma tag, na verdade só está aumentando este elemento raiz
    private String rootName;
    private HtmlElement root = new HtmlElement();

    public HtmlBuilder(String rootName)
    {
        this.rootName = rootName;
        root.name = rootName;
    }

    // not fluent
    public void addChild(String childName, String childText)
    {
        HtmlElement e = new HtmlElement(childName, childText);
        root.elements.add(e);
    }

    public HtmlBuilder addChildFluent(String childName, String childText)
    {
        HtmlElement e = new HtmlElement(childName, childText);
        root.elements.add(e);
        return this;
    }

    public void clear()
    {
        root = new HtmlElement();
        root.name = rootName;
    }

    // delegating
    @Override
    public String toString()
    {
        return root.toString();
    }
}

