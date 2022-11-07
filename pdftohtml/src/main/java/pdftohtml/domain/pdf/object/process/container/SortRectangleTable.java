package pdftohtml.domain.pdf.object.process.container;

import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class SortRectangleTable<T> {

  private double threshold = 2.5f;

  private List<SortRectangleTableRow<T>> rows;

  public SortRectangleTable(double threshold) {
    this.threshold = threshold;
    this.rows = new ArrayList<>();
  }

  public void addValue(Double rowKey, Double valueKey, T value) {
    double prevKey = 0;
    int index = 0;
    for (SortRectangleTableRow<T> row: rows) {
      if (prevKey == 0 || prevKey < rowKey) {
        prevKey = row.getKeysAverage();
        index++;
      }
    }

    if (Math.abs(prevKey - rowKey) < threshold) {
      SortRectangleTableRow<T> prevRow = this.rows.get(index - 1);
      prevRow.addKey(rowKey);
      prevRow.addValue(new SortRectangleTableRowValue<>(valueKey, value));
    } else {
      SortRectangleTableRow<T> newRow = new SortRectangleTableRow<>();
      newRow.addKey(rowKey);
      newRow.addValue(new SortRectangleTableRowValue<>(valueKey, value));
      this.rows.add(index, newRow);
    }

  }

  public void addRow(SortRectangleTableRow<T> row) {
    this.rows.add(row);
  }

  public void sortByKeysAverage() {
    rows.sort(Comparator.comparing(SortRectangleTableRow::getKeysAverage));
  }

  @Data
  public static class SortRectangleTableRow<T> {

    private List<Double> keys;

    private List<SortRectangleTableRowValue<T>> values;

    public SortRectangleTableRow() {
      this.keys = new ArrayList<>();
      this.values = new ArrayList<>();
    }

    public void addKey(Double key) {
      this.keys.add(key);
    }

    public void addValue(SortRectangleTableRowValue<T> value) {
      this.values.add(value);
    }

    public Double getKeysAverage() {
      return keys.stream().mapToDouble(d -> d).average().orElse(0);
    }

    public void sortValues() {
      values.sort(Comparator.comparing(SortRectangleTableRowValue::getKey));
    }

  }

  @Data
  public static class SortRectangleTableRowValue<T> {

    private Double key;

    private T value;

    public SortRectangleTableRowValue(Double key, T value) {
      this.key = key;
      this.value = value;
    }

  }

}
