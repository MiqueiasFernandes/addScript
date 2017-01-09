/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package addscript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Miquéias Fernandes
 */
public class AddScript {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String tmp = "C:\\Users\\Miquéias Fernandes\\Desktop\\export.html";

        String st = null;
        if (args != null && args.length > 0) {
            st = args[0];
        }

        if (st == null || st.isEmpty() || st.length() < 1) {
            System.err.println("chame com nome do arquivo por ex: java -jar \"" + tmp + "\"");
            System.exit(0);
        }

        Data datasource = new Data();
        Scanner s = null;
        boolean added = false;
        boolean parar = false;
        boolean ommit = false;
        String mdID = null, elID = null, docID = null, style = null;
        try {
            s = new Scanner(new FileReader(st));
            String next;

            StringBuilder sb = new StringBuilder();

            while (s.hasNextLine()) {
                next = s.nextLine();

                ///caso rbokeh2html
                if (parar) {
                    break;
                }

                if (next.startsWith("<link href='https://cdn.pydata.org/bokeh/release/bokeh-")) {
                    sb.append("\n" + datasource.getCss1() + "\n");
                    continue;
                }

                if (next.startsWith("<script src='https://cdn.pydata.org/bokeh/release/bokeh-")) {
                    sb.append("\n" + datasource.getJS1() + "\n");
                    sb.append("\n" + datasource.getJS2() + "\n");
                    continue;
                }

                if (next.startsWith("<div class='bk-root' class='plotdiv' ")) {
                    ommit = true;
                    style = next.substring(next.indexOf("style"));
                    continue;
                }

                if (next.equals("<script type='text/javascript'>")) {
                    sb.append("\n");
                    continue;
                }

                if (next.contains("var modelid = '")) {
                    mdID = next.substring(next.indexOf("'") + 1, next.length() - 2);
                    continue;
                }

                if (next.contains("var elementid = '")) {
                    elID = next.substring(next.indexOf("'") + 1, next.length() - 2);
                    continue;
                }

                if (next.contains("var docid = '")) {
                    docID = next.substring(next.indexOf("'") + 1, next.length() - 2);
                    continue;
                }

                if (next.contains("var docs_json = ")) {
                    sb.append("<div id=\"htmlwidget_container\">\n"
                            + "<div id=\"htmlwidget-"
                            + elID
                            + "\" class=\"rbokeh html-widget\" "
                            + style + "\n"
                            + "\n"
                            + "</div>\n"
                            + "</div>\n"
                            + "\n"
                            + "<div id=\"checkboxes\">\n"
                            + "\n"
                            + "</div>\n"
                            + "\n");
                    sb.append("<script type=\"application/json\" data-for=\"htmlwidget-");
                    next = elID
                            + "\">{\"x\":{\"elementid\":\""
                            + elID
                            + "\",\"modeltype\":\"Plot\",\"modelid\":\""
                            + mdID
                            + "\",\"docid\":\""
                            + docID
                            + "\",\"docs_json\":"
                            + next.substring(next.indexOf("{") - 1, next.length() - 1)
                            + ",\"debug\":false},\"evals\":[],\"jsHooks\":[]}</script>\n";
                    sb.append(next);
                    sb.append("</body>\n"
                            + "</html>");
                    parar = true;
                    continue;
                }

                ///caso rstudio export
                if (next.startsWith("<script src=\"data:application/x-javascript;base64,KGZ1bmN0aW9uKCkgewogIC8vIElmI")) {
                    sb.append(datasource.getJS1() + "\n");
                    continue;
                }

                if (next.startsWith("<script src=\"data:application/x-javascript;base64,SFRNTFdpZGdldHMud2lkZ2V")) {
                    continue;
                }

                ///ambos casos
                if (next.equals("</div>") && !added) {
                    sb.append(next + "\n" + "<div id=\"checkboxes\"></div> \n");
                    added = true;
                    continue;
                }

                if (!ommit) {
                    sb.append(next + "\n");
                }
            }

            try {
                String name = "new-" + new File(st).getName();
                if (!name.endsWith(".html")) {
                    name += ".html";
                }
                FileWriter fw = new FileWriter(name);
                fw.write(sb.toString());
                fw.close();
                System.out.println("executado com sucesso! salvo em " + name);
            } catch (IOException ex) {
                System.err.println("erro ao gravar arquivo: " + ex);
            }

            System.out.println("terminado.");

        } catch (FileNotFoundException ex) {
            System.err.println("arquivo nao encontrado: " + ex);
        }
    }

}
