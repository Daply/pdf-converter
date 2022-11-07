package pdftohtml.domain.htmltags;

public class HtmlTag {

    /**
     * Supported tags:
     *  <a></a>  <b></b>  <br>  <div></div>
     *  <code></code>  <em></em>  <hr>  <i></i>
     *  <img>  <li></li>  <ol></ol>  <p></p>
     *  <pre></pre>  <small></small>  <span></span>
     *  <strong></strong>  <sup></sup>  <table></table>
     *  <tbody></tbody>  <td></td>  <th></th>  <thead></thead>
     *  <title></title>  <tr></tr>  <u></u>  <ul></ul>
     */

    public static String getParagraphOpen() {
        return "<p>";
    }

    public static String getParagraphClose() {
        return "</p>";
    }

    public static String getItalicOpen() {
        return "<i>";
    }

    public static String getItalicClose() {
        return "</i>";
    }

    public static String getBoldOpen() {
        return "<b>";
    }

    public static String getBoldClose() {
        return "</b>";
    }

    public static String getUnderlinedOpen() {
        return "<u>";
    }

    public static String getUnderlinedClose() {
        return "</u>";
    }

    public static String getStrikeThroughOpen() {
        return "<del>";
    }

    public static String getStrikeThroughClose() {
        return "</del>";
    }

    /**
     * Lists
     */

    public static String getOrderedListOpen() {
        return "<ol>";
    }

    public static String getOrderedListClose() {
        return "</ol>";
    }

    public static String getUnorderedListOpen() {
        return "<ul>";
    }

    public static String getUnorderedListClose() {
        return "</ul>";
    }

    public static String getListElementOpen() {
        return "<li>";
    }

    public static String getListElementClose() {
        return "</li>";
    }

    /**
     * Table
     */

    public static String getTableOpen() {
        return "<table>";
    }

    public static String getTableClose() {
        return "</table>";
    }

    public static String getTableBodyOpen() {
        return "<tbody>";
    }

    public static String getTableBodyClose() {
        return "</tbody>";
    }

    public static String getTableHeadOpen() {
        return "<th>";
    }

    public static String getTableHeadClose() {
        return "</th>";
    }

    public static String getTableRowOpen() {
        return "<tr>";
    }

    public static String getTableRowClose() {
        return "</tr>";
    }

    public static String getTableCellOpen() {
        return "<td>";
    }

    public static String getTableCellClose() {
        return "</td>";
    }


}
