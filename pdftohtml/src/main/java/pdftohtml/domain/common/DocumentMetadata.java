package pdftohtml.domain.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentMetadata {

    private String documentTitle;

    private int pageIndex;
}
