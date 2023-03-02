package com.mohaeng.common.util;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

@Component
public class ExcelExporter {

    private static final int CONTENT_TITLE_ROW_INDEX = 0;
    private static final int FIRST_DATA_ROW_INDEX = 1;

    public void export(final List<ExcelRow> excelRows,
                       final HeaderTitles headerTitles,
                       final OutputStream os,
                       final String sheetName) {
        final Workbook workbook = new Workbook(os, "Mo-Haeng", "1.0");
        final Worksheet worksheet = workbook.newWorksheet(sheetName);
        makeTitle(worksheet, headerTitles);
        fillDatas(excelRows, worksheet);
        finish(workbook, worksheet);
    }

    private void makeTitle(final Worksheet worksheet, final HeaderTitles headerTitles) {
        final ListIterator<String> it = headerTitles.titles.listIterator();
        while (it.hasNext()) {
            worksheet.value(CONTENT_TITLE_ROW_INDEX, it.nextIndex(), it.next());
        }
    }

    private void fillDatas(final List<ExcelRow> excelRows, final Worksheet worksheet) {
        final ListIterator<ExcelRow> it = excelRows.listIterator();
        while (it.hasNext()) {
            fillRow(it.nextIndex() + FIRST_DATA_ROW_INDEX, it.next(), worksheet);
        }
    }

    private void fillRow(final int row, final ExcelRow excelRow, final Worksheet worksheet) {
        final List<String> datas = excelRow.datas;
        final ListIterator<String> it = datas.listIterator();
        while (it.hasNext()) {
            worksheet.value(row, it.nextIndex(), it.next());
        }
    }

    private void finish(final Workbook workbook, final Worksheet worksheet) {
        try {
            worksheet.flush();
            worksheet.finish();
            workbook.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public record ExcelRow(
            List<String> datas
    ) {
        public ExcelRow(String... datas) {
            this(Arrays.asList(datas));
        }
    }

    public record HeaderTitles(
            List<String> titles
    ) {
        public HeaderTitles(String... datas) {
            this(Arrays.asList(datas));
        }
    }
}
